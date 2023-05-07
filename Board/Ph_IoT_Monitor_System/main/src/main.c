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
const static long LONG_SLEEP_TIME = 1000000 * 30; // 10 seconds

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
        mqtt_send_sensor_record1(client, &sensor_records.ph_records[i], deviceID, "ph");
        mqtt_send_sensor_record2(client, &sensor_records.temperature_records[i], deviceID, "temperature");
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

int check_sensors() {
    ESP_LOGE(TAG, "Checking sensors...");
    int res = read_sensor_records(&sensor_records);
    switch (res)
    {
    case PH_SENSOR_ERROR:
        ESP_LOGE(TAG, "PH sensor error");
        break;
    case TEMP_SENSOR_ERROR:
        ESP_LOGE(TAG, "Temperature sensor error");
        break;
    case WATER_LEVEL_SENSOR_ERROR:
        ESP_LOGE(TAG, "Water level sensor error");
        break;
    case WATER_FLOW_SENSOR_ERROR:
        ESP_LOGE(TAG, "Water flow sensor error");
        break;
    case HUMIDITY_SENSOR_ERROR:
        ESP_LOGE(TAG, "Humidity sensor error");
        break;
    default:
        ESP_LOGE(TAG, "All sensors are working");
        break;
    }
    return res;
}

/**
 * Publishes a sensor not working alert to the MQTT broker,
 * with the deviceID and the sensor that is not working.
*/
void send_sensor_not_working_alert(esp_mqtt_client_handle_t client, char* deviceID, int sensor_error) {
    ESP_LOGE(TAG, "Sending sensor not working alert...");
    char sensorName[20];
    switch (sensor_error)
    {
    case PH_SENSOR_ERROR:
        sensorName = "PH";
        break;
    case TEMP_SENSOR_ERROR:
        sensorName = "Temperature";
        break;
    case WATER_LEVEL_SENSOR_ERROR:
        sensorName = "Water level";
        break;
    case WATER_FLOW_SENSOR_ERROR:
        sensorName = "Water flow";
        break;
    case HUMIDITY_SENSOR_ERROR:
        sensorName = "Humidity";
        break;
    default:
        // TODO: should not happen
    }
    int timestamp = getNowTimestamp();
    mqtt_send_sensor_not_working_alert(client, deviceID, timestamp, sensorName);
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

    if (int error = check_sensors() == -1) {
        send_sensor_not_working_alert(error);
        start_deep_sleep(LONG_SLEEP_TIME);
    }

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
    } else { // Normal woke up
        compute_sensors(deviceID);
    }
}