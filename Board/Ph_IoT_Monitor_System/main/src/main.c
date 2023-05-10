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
#include "sensor/sensor_reader.h"
#include "time_util.h"

const static char* TAG = "MAIN";

const static long SENSOR_MULTIPLE_READING_INTERVAL = 1000000 * 3; // 3 seconds
const static long LONG_SLEEP_TIME = 1000000 * 3; // 3 seconds

RTC_DATA_ATTR struct sensor_records_struct sensor_records;

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

void send_sensor_records(esp_mqtt_client_handle_t client, char* deviceID) {
    ESP_LOGE(TAG, "Sending sensor records...");
    for (int i = 0; i < MAX_SENSOR_RECORDS; i++) {
        mqtt_send_sensor_record1(client, &sensor_records.start_ph_records[i], deviceID, "ph");
        mqtt_send_sensor_record1(client, &sensor_records.end_ph_records[i], deviceID, "ph");
        mqtt_send_sensor_record2(client, &sensor_records.temperature_records[i], deviceID, "temperature");
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

    esp_touch_helper(&deviceID);

    ESP_LOGE(TAG, "Finished setting up WiFi");
}

void sendWaterAlert(esp_mqtt_client_handle_t client, int timestamp, char* deviceID) {
    ESP_LOGE(TAG, "Sending water alert...");
    mqtt_send_water_alert(client, timestamp, deviceID);
}

void compute_sensors(char* deviceID) {
    if (sensors_reading_is_complete(&sensor_records)) {
        setup_wifi();
        esp_mqtt_client_handle_t client = setup_mqtt();

        send_sensor_records(client, deviceID);
        erase_sensor_records();

        start_deep_sleep(LONG_SLEEP_TIME);
    } else {
        setup_wifi();
        read_sensor_records(&sensor_records);
        start_deep_sleep(SENSOR_MULTIPLE_READING_INTERVAL);
    }
}

int check_sensors_status(char* deviceID) {
    ESP_LOGE(TAG, "Checking sensors...");

    int sensors_not_working[6]; // 6 sensors
    int res = check_if_sensors_are_working(sensors_not_working);

    if (res == -1)
    {
        esp_mqtt_client_handle_t client = setup_mqtt();
        ESP_LOGE(TAG, "Sending sensor not working alert...");
        int timestamp = getNowTimestamp();
        mqtt_send_sensor_not_working_alert(client, deviceID, timestamp, sensors_not_working);
    }
    return res;
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

    esp_sleep_wakeup_cause_t cause = esp_sleep_get_wakeup_cause();
    printDeepSleepWokeCause(cause);
   if(cause == ESP_SLEEP_WAKEUP_UNDEFINED) { // maybe duo to an abort
        sendUnknowWokeUpReasonAlert();
    }

    char* deviceID;
    get_device_id(&deviceID);

    
    // needs wifi to ajust time, because the fake readings will get the real time
    int sensor_status_result = check_sensors_status(deviceID);
    if (sensor_status_result == -1) {
        start_deep_sleep(LONG_SLEEP_TIME);
    }

    // Woker duo to water leak sensor
    if (cause == ESP_SLEEP_WAKEUP_EXT0) {
        ESP_LOGE(TAG, "Woke up from water leak sensor");

        setup_wifi();
        esp_mqtt_client_handle_t client = setup_mqtt();

        int current_timestamp = getNowTimestamp(); // get current time
        sendWaterAlert(client, current_timestamp, deviceID);
    } else { // Normal woke up
        compute_sensors(deviceID);
    }
}