#include <string.h>
#include <stdlib.h>
#include <driver/gpio.h>
#include <esp_sleep.h>
#include <nvs_flash.h>
#include <esp_log.h>
#include "wifi_connect_util.h"
#include <mqtt_client.h>

#include <deep_sleep.h>

#include "broker_util.h"
#include "wifi_connect_util.h"
#include "nvs_util.h"
#include "esp_touch_util.h"
#include "sensor_records.h"

const static char* TAG = "MAIN";

const static long READ_PH_INTERVAL = 1000000 * 0.3; // 3 seconds
const static long LONG_SLEEP_TIME = 1000000 * 3; // 10 seconds

RTC_DATA_ATTR struct sensor_records_struct sensor_records;

void printDeepSleepWokeCause(esp_sleep_wakeup_cause_t cause) {
    if (cause == ESP_SLEEP_WAKEUP_TIMER) {
        ESP_LOGI(TAG, "Woke up from timer");
    } else {
        ESP_LOGI(TAG, "Woke up duo to unknown reason");
    }
}

void send_sensor_records(esp_mqtt_client_handle_t client, char* deviceID) {
    ESP_LOGE(TAG, "Sending sensor records...");
    for (int i = 0; i < MAX_SENSOR_RECORDS; i++) {
        mqtt_send_sensor_record(client, &sensor_records.ph_records[i], deviceID, "ph");
        mqtt_send_sensor_record(client, &sensor_records.temperature_records[i], deviceID, "temperature");
        // TODO: send water level, water flow and humidity
    }
    
}

void erase_sensor_records() {
    ESP_LOGE(TAG, "Erasing sensor records...");
    for (int i = 0; i < MAX_SENSOR_RECORDS; i++) {
        sensor_records.ph_records[i].value = 0;
        sensor_records.ph_records[i].timestamp = 0;
        sensor_records.temperature_records[i].value = 0;
        sensor_records.temperature_records[i].timestamp = 0;
    }
}

void setup_wifi(void) {
    ESP_LOGE(TAG, "Setting up WiFi...");

    char* deviceID;
    wifi_config_t wifiConfig;

    esp_touch_helper(&deviceID);

    ESP_LOGE(TAG, "Finished setting up WiFi");
}

void sendWaterAlert(esp_mqtt_client_handle_t client, int timestamp, char* deviceID) {
    ESP_LOGE(TAG, "Sending water alert...");
    mqtt_send_water_alert(client, timestamp, deviceID);
}

void compute_sensors() {
    if (sensors_reading_is_complete()) {
            setup_wifi();
            esp_mqtt_client_handle_t client = setup_mqtt();

            send_sensor_records(client, deviceID);
            erase_sensor_records();

            start_deep_sleep(LONG_SLEEP_TIME);
        } else {
            setup_wifi();
            read_sensors();
            start_deep_sleep(READ_PH_INTERVAL);
        }
}

/**
 * Program entry point.
 * It will read the pH value every 0.3 seconds and store it in RTC memory.
 * After 5 readings, it will send the values to the MQTT broker and go to deep sleep for 3 seconds.
 * For some unknown reason, the MQTT broker does not receive all messages, the first reading round.
*/
void app_main(void) {
    ESP_LOGE(TAG, "Starting app_main...");
    ESP_ERROR_CHECK(nvs_flash_init());

    char* deviceID;
    get_device_id(&deviceID);

    esp_sleep_wakeup_cause_t cause = esp_sleep_get_wakeup_cause();
    printDeepSleepWokeCause(cause);

    // Woker duo to water leak sensor
    if (cause == ESP_SLEEP_WAKEUP_EXT0) {
        ESP_LOGE(TAG, "Woke up from water leak sensor");

        setup_wifi();
        esp_mqtt_client_handle_t client = setup_mqtt();

        int current_timestamp = getNowTimestamp(); // get current time
        sendWaterAlert(client, current_timestamp, deviceID);
    } 
    // Normal woke up
    else {
        // print_ph_values();
        compute_sensors():

        if (is_ph_reading_complete()) {
            setup_wifi();
            esp_mqtt_client_handle_t client = setup_mqtt();

            send_ph_values(client, deviceID);
            erase_ph_values();

            start_deep_sleep(LONG_SLEEP_TIME);
        } else {
            setup_wifi(); // to get the current time

            struct ph_record ph_record;
            read_ph(&ph_record);

            store_ph_in_RTC_memory(&ph_record);

            start_deep_sleep(READ_PH_INTERVAL);
        }
    }
}