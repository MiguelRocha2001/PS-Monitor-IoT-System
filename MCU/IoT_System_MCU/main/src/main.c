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
#include "sensor/water_leak_reader.h"

const static char* TAG = "MAIN";

const static long LONG_SLEEP_TIME = 6; // 6 seconds

RTC_DATA_ATTR struct sensor_records_struct sensor_records; // TODO: maybe not necessary anymore
RTC_DATA_ATTR int n_went_to_deep_sleep = 0; // used to fake timestamps
RTC_DATA_ATTR char action[100];
RTC_DATA_ATTR int time_to_wake_up = 0; // TODO: maybe not necessary anymore
esp_mqtt_client_handle_t client;

void continue_long_sleep() {
    int current_timestamp = getNowTimestamp();
    n_went_to_deep_sleep += 1;
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

    mqtt_send_sensor_record(client, &sensor_records.initial_ph_record, deviceID, "initial-ph");
    vTaskDelay(1000 / portTICK_PERIOD_MS);

    mqtt_send_sensor_record(client, &sensor_records.final_ph_record, deviceID, "final-ph");
    vTaskDelay(1000 / portTICK_PERIOD_MS);

    mqtt_send_sensor_record(client, &sensor_records.temperature_record, deviceID, "temperature");
    vTaskDelay(1000 / portTICK_PERIOD_MS);

    mqtt_send_sensor_record(client, &sensor_records.humidity_record, deviceID, "humidity");
    vTaskDelay(1000 / portTICK_PERIOD_MS);

    mqtt_send_sensor_record(client, &sensor_records.water_flow_record, deviceID, "water-flow");
    vTaskDelay(1000 / portTICK_PERIOD_MS);
}

void setup_wifi(void) {
    strcpy(action, "seting_up_wifi");
    ESP_LOGE(TAG, "Setting up WiFi...");

    char* deviceID;
    wifi_config_t wifiConfig;

    if (get_saved_wifi(&wifiConfig) == ESP_OK && get_device_id(&deviceID) == ESP_OK) 
    {
        if(!connect_to_wifi(wifiConfig)) esp_touch_helper(&deviceID);
    } else 
    {
        esp_touch_helper(&deviceID); // device id may be null
    }

    ESP_LOGE(TAG, "Finished setting up WiFi");
}

void setup_wifi_and_mqtt() {
    setup_wifi(); // connects to wifi
    strcpy(action, "seting_up_mqtt");
    client = setup_mqtt();
}

void fake_timestamps(struct sensor_records_struct *sensor_records) {
    ESP_LOGE(TAG, "Faking timestamps...");
    int n_seconds_in_day = 24 * 60 * 60;
    sensor_records -> initial_ph_record.timestamp -= n_went_to_deep_sleep * n_seconds_in_day;
    sensor_records -> final_ph_record.timestamp -= n_went_to_deep_sleep * n_seconds_in_day;
    sensor_records -> temperature_record.timestamp -= n_went_to_deep_sleep * n_seconds_in_day;
    sensor_records -> humidity_record.timestamp -= n_went_to_deep_sleep * n_seconds_in_day;
    sensor_records -> water_flow_record.timestamp -= n_went_to_deep_sleep * n_seconds_in_day;
}

void compute_sensors(char* deviceID) 
{
    strcpy(action, "checking_water_leak");
    int leakage = read_water_leak_record(); // TODO: change name
    
    read_sensor_records(&sensor_records, &action);
    fake_timestamps(&sensor_records);

    setup_wifi(); // connects to wifi
    mqtt_app_start(client);
    
    ESP_LOGE(TAG, "Sensors reading is complete. Sending records...");
    send_sensor_records(client, deviceID);
    if(leakage == 1) 
    {
        ESP_LOGE(TAG, "Water leakage detected. Sending message...");
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "water-leak"); // TODO: send to water leak topic (create one)
    }
    else 
    {
        ESP_LOGE(TAG, "No water leakage detected.");
    }

    mqtt_app_stop(client);
    terminate_wifi(); // disconnects from wifi
    
    int new_time_to_wake_up = getNowTimestamp() + LONG_SLEEP_TIME;
    ESP_LOGE(TAG, "Setting new time to wake up: %d", new_time_to_wake_up);
    time_to_wake_up = new_time_to_wake_up; // sets new time to wake up
    continue_long_sleep();
}

int was_reading_from_sensor(char* action, char* sensor) 
{
    if (strcmp(action, "reading_initial_ph") == 0) 
    {
        strcpy(sensor, "initial-ph");
        return 1;
    }
    if (strcmp(action, "reading_final_ph") == 0) 
    {
        strcpy(sensor, "final-ph");
        return 1;
    }
    if (strcmp(action, "reading_temperature") == 0) 
    {
        strcpy(sensor, "temperature");
        return 1;
    }
    if (strcmp(action, "reading_water_flow") == 0) 
    {
        strcpy(sensor, "water-flow");
        return 1;
    }
    if (strcmp(action, "reading_humidity") == 0) 
    {
        strcpy(sensor, "humidity");
        return 1;
    }
    if (strcmp(action, "checking_water_leak") == 0) 
    {
        strcpy(sensor, "water-leak");
        return 1;
    }
    return 0;
}

/**
 * Checks the wake up reason. Returns 0 if it is the timer, 1 if is power on and 2 otherwise.
 * If it is not the timer, it sends an alert to the broker:
 * - Power up;
 * - Software reset;
 * - Exception/panic;
 * - Brownout;
 * - Unknown.
*/
int handle_wake_up_reason(char* deviceID) 
{
    setup_wifi_and_mqtt();

    ESP_LOGE(TAG, "Checking wake up reason...");

    // Read the Reset Reason Register
    uint32_t reset_reason = esp_reset_reason();
    esp_sleep_wakeup_cause_t sleep_wakeup_reason = esp_sleep_get_wakeup_cause();

    int codeToReturn = 0;
    // Check the reset reason flags
    if (reset_reason & ESP_RST_UNKNOWN) 
    {
        ESP_LOGE(TAG, "Reset reason: unknown");
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "unknown");
        codeToReturn = 2;
    }
    if (reset_reason & ESP_RST_POWERON) 
    {
        ESP_LOGE(TAG, "Reset reason: power-on");
        n_went_to_deep_sleep = 0; // reset sleep counter
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "power on");
        codeToReturn = 1;
    }
    if (reset_reason & ESP_RST_SW) 
    {
        ESP_LOGE(TAG, "Reset reason: software");
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "software");
        codeToReturn = 2;
    }
    if (reset_reason & ESP_RST_PANIC) 
    {
        ESP_LOGE(TAG, "Reset reason: exception/panic");
        char sensor[50];
        if (was_reading_from_sensor(action, sensor))
        {
            mqtt_send_error_reading_sensor(client, getNowTimestamp(), deviceID, sensor);
        }
        else 
        {
            if (strcmp(action, "seting_up_wifi") != 0 &&  strcmp(action, "seting_up_mqtt") != 0)
            {
                printf("Action: %s", action);
                char msg[100];
                sprintf(msg, "exception/panic: %s", action);
                mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, action);
            }
            else
            {
                ESP_LOGE(TAG, "Error setting up wifi or mqtt. Not sending message to broker");
            }
        }
        codeToReturn = 2;
    }
    if (sleep_wakeup_reason == ESP_SLEEP_WAKEUP_TIMER)
    {
        ESP_LOGE(TAG, "Wake up reason: timer");
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "Wake up by timer");
        codeToReturn = 0;
    }
    /*
    if (reset_reason & ESP_RST_BROWNOUT) 
    {
        ESP_LOGE(TAG, "Reset reason: brownout");
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "brownout");
        return 1;
    }
    */

    mqtt_app_stop(client);
    terminate_wifi(); // disconnects from wifi

    return codeToReturn;
}

/**
 * Checks if the cause of wake up was an external signal using RTC_IO.
 * If so, resets wifi and device ID.
*/
void check_if_woke_up_to_reset() 
{
    ESP_LOGE(TAG, "Checking if woke up to reset...");
    // Read the Reset Reason Register
    esp_sleep_wakeup_cause_t sleep_wakeup_reason = esp_sleep_get_wakeup_cause();

    // Check the reset reason flags
    if (sleep_wakeup_reason == ESP_SLEEP_WAKEUP_EXT0) 
    {
        // TODO: maybe send log to broker
        ESP_LOGE(TAG, "Reset reason: external signal using RTC_IO");
        ESP_LOGE(TAG, "Resetting WiFi and device ID...");
        delete_saved_wifi();
        delete_device_id();
        ESP_LOGE(TAG, "Finished resetting WiFi and device ID");
        // mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "external-signal-using-rtc-io");
        return;
    }
    ESP_LOGE(TAG, "No external signal using RTC_IO");
    return;
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

    check_if_woke_up_to_reset();

    char* deviceID;
    get_device_id(&deviceID);

    int res = handle_wake_up_reason(deviceID);
    if (res == 0 || res == 1) // timerout or power on
    {
        compute_sensors(deviceID);
    }
    else // other wake up reason
    {
        continue_long_sleep(LONG_SLEEP_TIME);
    }
}