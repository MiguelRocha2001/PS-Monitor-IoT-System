#include <string.h>
#include <stdlib.h>
#include <driver/gpio.h>
#include <esp_sleep.h>
#include <nvs_flash.h>
#include <esp_log.h>
#include "wifi_connect_util.h"
#include <mqtt_client.h>
#include <deep_sleep.h>
#include <time.h>
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

RTC_DATA_ATTR struct sensor_records_struct sensor_records;
RTC_DATA_ATTR int ready_to_upload_records_to_server = 0; // indicates that records were read but not yet uploaded to server
RTC_DATA_ATTR int n_went_to_deep_sleep = 0; // used to fake timestamps
RTC_DATA_ATTR char action[100];
esp_mqtt_client_handle_t mqtt_client;

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

char* setup_wifi(void) {
    strcpy(action, "seting_up_wifi");
    ESP_LOGI(TAG, "Setting up WiFi...");

    char* deviceID;
    wifi_config_t wifiConfig;

    if (get_saved_wifi(&wifiConfig) == ESP_OK && get_device_id(&deviceID) == ESP_OK) 
    {
        if(!connect_to_wifi(wifiConfig)) 
            start_deep_sleep(LONG_SLEEP_TIME);
    } else 
    {
        esp_touch_helper(&deviceID); // device id may be null
    }

    ESP_LOGI(TAG, "Finished setting up WiFi");

    return deviceID;
}

char* setup_wifi_and_mqtt() {
    char* deviceID = setup_wifi(); // connects to wifi
    strcpy(action, "seting_up_mqtt");
    mqtt_client = setup_mqtt();

    if (try_to_connect_to_broker_if_necessary(mqtt_client) == 0) 
    {
        ESP_LOGE(TAG, "Cannot connect to broker. Going to deep sleep...");
        start_deep_sleep(LONG_SLEEP_TIME);
    }

    return deviceID;
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

void compute_sensors() 
{
    strcpy(action, "checking_water_leak");
    int leakage = read_water_leak_record(); // TODO: change name
    
    read_sensor_records(&sensor_records, &action);
    ready_to_upload_records_to_server = 1; // data needs to be uploaded
    fake_timestamps(&sensor_records);

    char* deviceID = setup_wifi_and_mqtt(); // turns on wifi
    
    send_sensor_records(mqtt_client, deviceID);
    ready_to_upload_records_to_server = 0; // data uploaded
    if(leakage == 1) 
    {
        ESP_LOGW(TAG, "Water leakage detected. Sending message...");
        mqtt_send_device_wake_up_reason_alert(mqtt_client, getNowTimestamp(), deviceID, "water-leak"); // TODO: send to water leak topic (create one)
    }
    else 
    {
        ESP_LOGI(TAG, "No water leakage detected.");
    }

    mqtt_app_terminate(mqtt_client); // stops mqtt tasks
    terminate_wifi(); // disconnects from wifi
    
    start_deep_sleep(LONG_SLEEP_TIME);
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
int handle_wake_up() 
{
    char* deviceID = setup_wifi_and_mqtt();

    ESP_LOGI(TAG, "Checking wake up reason...");

    // Read the Reset Reason Register
    uint32_t reset_reason = esp_reset_reason();
    esp_sleep_wakeup_cause_t sleep_wakeup_reason = esp_sleep_get_wakeup_cause();

    int codeToReturn = 0;
    // Check the reset reason flags
    if (reset_reason & ESP_RST_UNKNOWN) 
    {
        ESP_LOGE(TAG, "Reset reason: unknown");
        mqtt_send_device_wake_up_reason_alert(mqtt_client, getNowTimestamp(), deviceID, "unknown");
        codeToReturn = 2;
    }
    if (reset_reason & ESP_RST_POWERON) 
    {
        ESP_LOGI(TAG, "Reset reason: power-on");
        n_went_to_deep_sleep = 0; // reset sleep counter
        mqtt_send_device_wake_up_reason_alert(mqtt_client, getNowTimestamp(), deviceID, "power on");
        codeToReturn = 1;
    }
    if (reset_reason & ESP_RST_SW) 
    {
        ESP_LOGI(TAG, "Reset reason: software");
        mqtt_send_device_wake_up_reason_alert(mqtt_client, getNowTimestamp(), deviceID, "software");
        codeToReturn = 2;
    }
    if (reset_reason & ESP_RST_PANIC) 
    {
        ESP_LOGE(TAG, "Reset reason: exception/panic");
        char sensor[50];
        if (was_reading_from_sensor(action, sensor))
        {
            mqtt_send_error_reading_sensor(mqtt_client, getNowTimestamp(), deviceID, sensor);
        }
        else 
        {
            if (strcmp(action, "seting_up_wifi") != 0 &&  strcmp(action, "seting_up_mqtt") != 0)
            {
                printf("Action: %s", action);
                char msg[100];
                sprintf(msg, "exception/panic: %s", action);
                mqtt_send_device_wake_up_reason_alert(mqtt_client, getNowTimestamp(), deviceID, action);
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
        ESP_LOGI(TAG, "Wake up reason: timer");
        if (ready_to_upload_records_to_server == 1) // means records were read last time but not sent to server
        {
            ESP_LOGI(TAG, "Records were read last time but not sent to server. Sending records...");
            send_sensor_records(mqtt_client, deviceID);
        }
        
        mqtt_send_device_wake_up_reason_alert(mqtt_client, getNowTimestamp(), deviceID, "Wake up by timer");
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

    mqtt_app_terminate(mqtt_client); // stops mqtt tasks
    terminate_wifi(); // disconnects from wifi

    return codeToReturn;
}

/**
 * Checks if the cause of wake up was an external signal using RTC_IO.
 * If so, resets wifi and device ID.
*/
void check_if_woke_up_to_reset() 
{
    ESP_LOGI(TAG, "Checking if woke up to reset...");
    // Read the Reset Reason Register
    esp_sleep_wakeup_cause_t sleep_wakeup_reason = esp_sleep_get_wakeup_cause();

    // Check the reset reason flags
    if (sleep_wakeup_reason == ESP_SLEEP_WAKEUP_EXT0) 
    {
        // TODO: maybe send log to broker
        ESP_LOGI(TAG, "Reset reason: external signal using RTC_IO");
        ESP_LOGI(TAG, "Resetting WiFi and device ID...");
        delete_saved_wifi();
        delete_device_id();
        ESP_LOGI(TAG, "Finished resetting WiFi and device ID");
        // mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "external-signal-using-rtc-io");
        return;
    }
    ESP_LOGI(TAG, "No external signal using RTC_IO");
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
    ESP_LOGI(TAG, "Starting app_main...");
    ESP_ERROR_CHECK(nvs_flash_init());

    check_if_woke_up_to_reset();

    int res = handle_wake_up();
    if (res == 0 || res == 1) // timeout or power on
    {
        if (res == 1) // power on
        {
            determine_sensor_calibration_timings(); // to set the calibration timings
        }
        
        compute_sensors();
    }
    else // other wake up reason
    {
        start_deep_sleep(LONG_SLEEP_TIME);
    }
}