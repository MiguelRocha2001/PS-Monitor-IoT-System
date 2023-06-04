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

const static long SENSOR_MULTIPLE_READING_INTERVAL = 2; // 3 seconds
const static long LONG_SLEEP_TIME = 5; // 3 seconds

RTC_DATA_ATTR struct sensor_records_struct sensor_records;
RTC_DATA_ATTR int readings_started;
RTC_DATA_ATTR char action[100];
RTC_DATA_ATTR int time_to_wake_up = 0;

void continue_long_sleep() {
    int current_timestamp = getNowTimestamp();
    if(current_timestamp < time_to_wake_up) 
    {
        ESP_LOGE(TAG, "Going to long sleep...");
        start_deep_sleep(time_to_wake_up - current_timestamp);
    } 
    else 
    {
        ESP_LOGE(TAG, "Going to long sleep...");
        start_deep_sleep(LONG_SLEEP_TIME);
    }
}

void send_sensor_records(esp_mqtt_client_handle_t client, char* deviceID) {
    strcpy(action, "sending_sensor_records");
    ESP_LOGE(TAG, "Sending sensor records...");
    for (int i = 0; i < MAX_SENSOR_RECORDS; i++) {
        mqtt_send_sensor_record(client, &sensor_records.start_ph_records[i], deviceID, "initial-ph");
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        mqtt_send_sensor_record(client, &sensor_records.end_ph_records[i], deviceID, "final-ph");
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        mqtt_send_sensor_record(client, &sensor_records.temperature_records[i], deviceID, "temperature");
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        mqtt_send_sensor_record(client, &sensor_records.humidity_records[i], deviceID, "humidity");
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        mqtt_send_sensor_record(client, &sensor_records.humidity_records[i], deviceID, "humidity");
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        mqtt_send_sensor_record(client, &sensor_records.water_flow_records[i], deviceID, "water-flow");
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        mqtt_send_sensor_record(client, &sensor_records.water_level_records[i], deviceID, "water-level");
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        // TODO: send water level, water flow and humidity
    }
}

void erase_sensor_records() {
    strcpy(action, "erasing_sensor_records");
    ESP_LOGE(TAG, "Erasing sensor records...");
    for (int i = 0; i < MAX_SENSOR_RECORDS; i++) 
    {
        sensor_records.start_ph_records[i].value = 0;
        sensor_records.start_ph_records[i].timestamp = 0;
        sensor_records.end_ph_records[i].value = 0;
        sensor_records.end_ph_records[i].timestamp = 0;
        sensor_records.temperature_records[i].value = 0;
        sensor_records.temperature_records[i].timestamp = 0;
        sensor_records.humidity_records[i].value = 0;
        sensor_records.humidity_records[i].timestamp = 0;
        sensor_records.water_flow_records[i].value = 0;
        sensor_records.water_flow_records[i].timestamp = 0;
        sensor_records.water_level_records[i].value = 0;
        sensor_records.water_level_records[i].timestamp = 0;
    }
    sensor_records.index = 0; // resets the index
}

void setup_wifi(void) {
    strcpy(action, "seting_up_wifi");
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

void compute_sensors(char* deviceID, esp_mqtt_client_handle_t client) {
    readings_started = 1;

    strcpy(action, "reading_sensors");
    read_sensor_records(&sensor_records);
    if (sensors_reading_is_complete(&sensor_records)) 
    {
        ESP_LOGE(TAG, "Sensors reading is complete. Sending records...");
        readings_started = 0;
        send_sensor_records(client, deviceID);
        erase_sensor_records();

        int new_time_to_wake_up = getNowTimestamp() + LONG_SLEEP_TIME;
        ESP_LOGE(TAG, "Setting new time to wake up: %d", new_time_to_wake_up);
        time_to_wake_up = new_time_to_wake_up; // sets new time to wake up
        continue_long_sleep();
    } 
    else 
    {
        ESP_LOGE(TAG, "Sensors reading is not complete. Going to sleep...");
        start_deep_sleep(SENSOR_MULTIPLE_READING_INTERVAL);
    }
}

void printDeepSleepWokeCause(esp_sleep_wakeup_cause_t wakeup_reason) 
{
    switch(wakeup_reason)
    {
        case ESP_SLEEP_WAKEUP_EXT0 : ESP_LOGE(TAG, "Wakeup caused by external signal using RTC_IO"); break;
        case ESP_SLEEP_WAKEUP_EXT1 : ESP_LOGE(TAG, "Wakeup caused by external signal using RTC_CNTL"); break;
        case ESP_SLEEP_WAKEUP_TIMER : ESP_LOGE(TAG, "Wakeup caused by timer"); break;
        case ESP_SLEEP_WAKEUP_TOUCHPAD : ESP_LOGE(TAG, "Wakeup caused by touchpad"); break;
        case ESP_SLEEP_WAKEUP_ULP : ESP_LOGE(TAG, "Wakeup caused by ULP program"); break;
        default : ESP_LOGE(TAG, "Wakeup was not caused by deep sleep: %d", wakeup_reason); break;
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
int handle_wake_up_reason(char* deviceID, esp_mqtt_client_handle_t client) 
{
    ESP_LOGE(TAG, "Checking wake up reason...");

    // Read the Reset Reason Register
    uint32_t reset_reason = esp_reset_reason();

    // Check the reset reason flags
    if (reset_reason & ESP_RST_UNKNOWN) 
    {
        ESP_LOGE(TAG, "Reset reason: unknown");
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "unknown");
        return 1;
    }
    if (reset_reason & ESP_RST_POWERON) 
    {
        ESP_LOGE(TAG, "Reset reason: power-on");
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "power-on");
        return 1;
    }
    if (reset_reason & ESP_RST_SW) 
    {
        ESP_LOGE(TAG, "Reset reason: software");
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "software");
        return 1;
    }
    if (reset_reason & ESP_RST_PANIC) 
    {
        ESP_LOGE(TAG, "Reset reason: exception/panic");
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, action);
        return 1;
    }
    /*
    if (reset_reason & ESP_RST_BROWNOUT) 
    {
        ESP_LOGE(TAG, "Reset reason: brownout");
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "brownout");
        return 1;
    }
    */
    if (reset_reason & ESP_RST_DEEPSLEEP) 
    {
        esp_sleep_wakeup_cause_t cause = esp_sleep_get_wakeup_cause();
        printDeepSleepWokeCause(cause);
        if (cause == ESP_SLEEP_WAKEUP_EXT0)  // water leak sensor
        {
            mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "water-leak");
            esp_deep_sleep_start(); // goes to sleep
        }
        return 0;
    }
    return 0;
}

// IMPORTANT -> run with $ idf.py monitor or $ idf.py -p COM5 flash monitor 

/**
 * Program entry point.
 * It will read the pH value every 0.3 seconds and store it in RTC memory.
 * After 5 readings, it will send the values to the MQTT broker and go to deep sleep for 3 seconds.
 * For some unknown reason, the MQTT broker does not receive all messages, the first reading round.
*/
void app_main(void) 
{
    ESP_LOGE(TAG, "Starting app_main...");
    ESP_ERROR_CHECK(nvs_flash_init());

    char* deviceID;
    get_device_id(&deviceID);

    setup_wifi();
    esp_mqtt_client_handle_t client = setup_mqtt();

    int res = handle_wake_up_reason(deviceID, client);
    if (res == 0) // timer wake up
    {
        compute_sensors(deviceID, client);
    }
    else // other wake up reason
    {
        if (readings_started)
        {
            start_deep_sleep(SENSOR_MULTIPLE_READING_INTERVAL);
        }
        else
        {
            continue_long_sleep(LONG_SLEEP_TIME);
        }
    }
}