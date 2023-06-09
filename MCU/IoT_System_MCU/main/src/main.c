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

#define GPIO_RESET_PIN (CONFIG_GPIO_RESET_PIN)
#define SENSOR_POWER_PIN GPIO_NUM_15
#define sensor_stabilization_time 1000 * 5 // 1 minute

const static long LONG_SLEEP_TIME = 6; // 5 seconds

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
        mqtt_send_sensor_record(client, &sensor_records.initial_ph_records[i], deviceID, "initial-ph");
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        mqtt_send_sensor_record(client, &sensor_records.final_ph_records[i], deviceID, "final-ph");
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        mqtt_send_sensor_record(client, &sensor_records.temperature_records[i], deviceID, "temperature");
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        mqtt_send_sensor_record(client, &sensor_records.humidity_records[i], deviceID, "humidity");
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        mqtt_send_sensor_record(client, &sensor_records.humidity_records[i], deviceID, "humidity");
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        mqtt_send_sensor_record(client, &sensor_records.water_flow_records[i], deviceID, "water-flow");
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        // TODO: send water flow and humidity
    }
}

void erase_sensor_records() {
    strcpy(action, "erasing_sensor_records");
    ESP_LOGE(TAG, "Erasing sensor records...");
    for (int i = 0; i < MAX_SENSOR_RECORDS; i++) 
    {
        sensor_records.initial_ph_records[i].value = 0;
        sensor_records.initial_ph_records[i].timestamp = 0;
        sensor_records.final_ph_records[i].value = 0;
        sensor_records.final_ph_records[i].timestamp = 0;
        sensor_records.temperature_records[i].value = 0;
        sensor_records.temperature_records[i].timestamp = 0;
        sensor_records.humidity_records[i].value = 0;
        sensor_records.humidity_records[i].timestamp = 0;
        sensor_records.water_flow_records[i].value = 0;
        sensor_records.water_flow_records[i].timestamp = 0;
    }
    sensor_records.index = 0; // resets the index
}

void setup_wifi(void) {
    strcpy(action, "seting_up_wifi");
    ESP_LOGE(TAG, "Setting up WiFi...");

    char* deviceID;
    wifi_config_t wifiConfig;

    gpio_set_direction(GPIO_NUM_8, GPIO_MODE_INPUT);
    int gpio_value = gpio_get_level(GPIO_NUM_8);
    if (gpio_value == 1) // wifi and device id reset
    {
        ESP_LOGE(TAG, "Resetting WiFi and device ID...");
        delete_saved_wifi();
        delete_device_id();
        ESP_LOGE(TAG, "Finished resetting WiFi and device ID");
    }

    if (get_saved_wifi(&wifiConfig) == ESP_OK && get_device_id(&deviceID) == ESP_OK) 
    {
        if(!connect_to_wifi(wifiConfig)) esp_touch_helper(&deviceID);
    } else 
    {
        esp_touch_helper(&deviceID); // device id may be null
    }

    ESP_LOGE(TAG, "Finished setting up WiFi");
}

void compute_sensors(char* deviceID, esp_mqtt_client_handle_t client) 
{
    ESP_LOGE(TAG, "Waiting for sensor stability...");
    vTaskDelay(pdMS_TO_TICKS(sensor_stabilization_time));

    ESP_LOGE(TAG, "Powering sensors...");
    // power sensors
    gpio_set_direction(SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    gpio_set_level(SENSOR_POWER_PIN, 1);

    strcpy(action, "checking_water_leak");
    int leakage = read_water_leak_record();
    if(leakage == 1) 
    {
        ESP_LOGE(TAG, "Water leakage detected. Sending message...");
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "water-leak");       
    }
    else 
    {
        ESP_LOGE(TAG, "No water leakage detected.");
    }
    
    read_sensor_records(&sensor_records, &action);

    gpio_set_level(SENSOR_POWER_PIN, 0);

    ESP_LOGE(TAG, "Sensors reading is complete. Sending records...");
    send_sensor_records(client, deviceID);
    erase_sensor_records();
    
    int new_time_to_wake_up = getNowTimestamp() + LONG_SLEEP_TIME;
    ESP_LOGE(TAG, "Setting new time to wake up: %d", new_time_to_wake_up);
    time_to_wake_up = new_time_to_wake_up; // sets new time to wake up
    continue_long_sleep();
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

int was_reading_from_sensor(char* action, char* sensor) 
{
    if (strcmp(action, "reading_initial_ph") == 0) 
    {
        strcpy(sensor, "ph");
        return 1;
    }
    if (strcmp(action, "reading_final_ph") == 0) 
    {
        strcpy(sensor, "tds");
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
        return 0;
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
    
    strcpy(action, "seting_up_mqtt");
    esp_mqtt_client_handle_t client = setup_mqtt();

    int res = handle_wake_up_reason(deviceID, client);
    if (res == 0) // timer wake up
    {
        mqtt_send_device_wake_up_reason_alert(client, getNowTimestamp(), deviceID, "Wake up by timer");
        compute_sensors(deviceID, client);
    }
    else // other wake up reason
    {
        continue_long_sleep(LONG_SLEEP_TIME);
    }
}