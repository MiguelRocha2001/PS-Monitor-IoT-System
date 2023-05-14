#include <string.h>
#include <stdlib.h>
#include <driver/gpio.h>
#include <esp_sleep.h>
#include <nvs_flash.h>
#include <esp_log.h>
#include "wifi_connect_util.h"
#include <mqtt_client.h>
#include <deep_sleep.h>
#include "mqtt_util.h"
#include "wifi_connect_util.h"
#include "nvs_util.h"
#include "esp_touch_util.h"
#include "sensor/sensor_reader.h"
#include "time_util.h"
#include "esp_system.h"
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"

const static char* TAG = "MAIN";

#define GPIO_RESET_PIN (CONFIG_GPIO_RESET_PIN)

const static long SENSOR_MULTIPLE_READING_INTERVAL = 1000000 * 3; // 3 seconds
const static long LONG_SLEEP_TIME = 1000000 * 3; // 3 seconds

RTC_DATA_ATTR struct sensor_records_struct sensor_records;
RTC_DATA_ATTR int readings_started;

void send_sensor_records(esp_mqtt_client_handle_t client, char* deviceID) {
    ESP_LOGE(TAG, "Sending sensor records...");
    for (int i = 0; i < MAX_SENSOR_RECORDS; i++) {
        mqtt_send_sensor_record(client, &sensor_records.start_ph_records[i], deviceID, "ph");
        mqtt_send_sensor_record(client, &sensor_records.end_ph_records[i], deviceID, "ph");
        mqtt_send_sensor_record(client, &sensor_records.temperature_records[i], deviceID, "temperature");
        // TODO: send water level, water flow and humidity
    }
}

void erase_sensor_records() {
    ESP_LOGE(TAG, "Erasing sensor records...");
    for (int i = 0; i < MAX_SENSOR_RECORDS; i++) {
        sensor_records.start_ph_records[i].value = 0;
        sensor_records.start_ph_records[i].timestamp = 0;
        sensor_records.end_ph_records[i].value = 0;
        sensor_records.end_ph_records[i].timestamp = 0;
        sensor_records.temperature_records[i].value = 0;
        sensor_records.temperature_records[i].timestamp = 0;
    }
}

void setup_wifi(void) {
    ESP_LOGE(TAG, "Setting up WiFi...");

    char* deviceID;
    wifi_config_t wifiConfig;

    if (get_saved_wifi(&wifiConfig) == ESP_OK && get_device_id(&deviceID) == ESP_OK ) 
    {
        if(!connect_to_wifi(wifiConfig)) esp_touch_helper(&deviceID);
    } else 
    {
        esp_touch_helper(&deviceID);
    }

    ESP_LOGE(TAG, "Finished setting up WiFi");
}

void compute_sensors(char* deviceID) {
    setup_wifi();
    readings_started = 1;
    read_sensor_records(&sensor_records);
    if (sensors_reading_is_complete(&sensor_records)) 
    {
        readings_started = 0;
        setup_wifi();
        esp_mqtt_client_handle_t client = setup_mqtt();

        send_sensor_records(client, deviceID);
        erase_sensor_records();

        start_deep_sleep(LONG_SLEEP_TIME);
    } 
    else 
    {
        start_deep_sleep(SENSOR_MULTIPLE_READING_INTERVAL);
    }
}

void printDeepSleepWokeCause(esp_sleep_wakeup_cause_t cause) {
    if (cause == ESP_SLEEP_WAKEUP_TIMER) {
        ESP_LOGI(TAG, "Woke up from timer");
    } if(cause == ESP_SLEEP_WAKEUP_UNDEFINED) {
        ESP_LOGI(TAG, "Woke up from undefined");
    }
    else {
        ESP_LOGI(TAG, "Woke up duo to unknown reason");
    }
}

/**
 * Checks the wake up reason. Returns 0 if it is the timer, 1 otherwise.
 * If it is not the timer, it sends an alert to the broker:
 * - Power up;
 * - Software reset;
 * - Exception/panic;
 * - Brownout;
 * - Unknown.
*/
int handle_wake_up_reason(char* deviceID) {
    ESP_LOGE(TAG, "Checking wake up reason...");

     // Read the Reset Reason Register
    uint32_t reset_reason = esp_reset_reason();

    setup_wifi();
    esp_mqtt_client_handle_t client = setup_mqtt();

    // Check the reset reason flags
    if (reset_reason & ESP_RST_UNKNOWN) {
        ESP_LOGE(TAG, "Reset reason: unknown");
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "unknown");
        return 1;
    }
    if (reset_reason & ESP_RST_POWERON) {
        ESP_LOGE(TAG, "Reset reason: power-on");
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "power-on");
        return 1;
    }
    if (reset_reason & ESP_RST_SW) {
        ESP_LOGE(TAG, "Reset reason: software");
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "software");
        return 1;
    }
    if (reset_reason & ESP_RST_PANIC) {
        ESP_LOGE(TAG, "Reset reason: exception/panic");
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "exception-panic");
        return 1;
    }
    if (reset_reason & ESP_RST_BROWNOUT) {
        ESP_LOGE(TAG, "Reset reason: brownout");
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "brownout");
        return 1;
    }
    if (reset_reason & ESP_RST_DEEPSLEEP) {
        esp_sleep_wakeup_cause_t cause = esp_sleep_get_wakeup_cause();
        printDeepSleepWokeCause(cause);
        if (cause == ESP_SLEEP_WAKEUP_EXT0) { // water leak sensor
            ESP_LOGE(TAG, "Sending water alert...");
            mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "water-leak");
            return 1;
        }
        return 0;
    }
    return 0;
}

// IMPORTANT -> run with $idf.py monitor

/**
 * Program entry point.
 * It will read the pH value every 0.3 seconds and store it in RTC memory.
 * After 5 readings, it will send the values to the MQTT broker and go to deep sleep for 3 seconds.
 * For some unknown reason, the MQTT broker does not receive all messages, the first reading round.
*/
void app_main(void) {
    ESP_LOGE(TAG, "Starting app_main...");
    ESP_ERROR_CHECK(nvs_flash_init());

    gpio_set_direction(GPIO_RESET_PIN, GPIO_MODE_DEF_INPUT);
    if (gpio_get_level(GPIO_RESET_PIN) == 0) // if LOW normal behavior, if HIGH reset memory
    {
        ESP_LOGE(TAG, "normal behavior");
        char* deviceID;
        get_device_id(&deviceID);
        int not_timer = handle_wake_up_reason(deviceID);

        if (not_timer == 1) // timer wake up
        {
                if (readings_started)
                {
                    start_deep_sleep(SENSOR_MULTIPLE_READING_INTERVAL);
                }
                else
                {
                    start_deep_sleep(LONG_SLEEP_TIME);
                }
        }
        else // other wake up reason
        {
            compute_sensors(deviceID);
        }
    }
    else
    {
        ESP_LOGE(TAG, "Resetting params");
        delete_saved_wifi();
        delete_device_id();

        start_deep_sleep(SENSOR_MULTIPLE_READING_INTERVAL);        
    }
}