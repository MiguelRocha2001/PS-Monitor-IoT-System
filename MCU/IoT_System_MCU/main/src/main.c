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

const static long SLEEP_TIME = 6; // 6 seconds

RTC_DATA_ATTR struct sensor_records_struct sensor_records;
RTC_DATA_ATTR int ready_to_upload_records_to_server = 0; // indicates that records were read but not yet uploaded to server
RTC_DATA_ATTR int n_went_to_deep_sleep = 0; // used to fake timestamps
RTC_DATA_ATTR char action[100];
RTC_DATA_ATTR int check_all_sensors = 1; // if true, the mcu will read all sensors and not just the water sensor
RTC_DATA_ATTR int my_counter = 0;
RTC_DATA_ATTR int check_water_leakage_iteration = 0;
RTC_DATA_ATTR char* wake_up_reason;
esp_mqtt_client_handle_t mqtt_client;

void send_sensor_records(esp_mqtt_client_handle_t client, char* deviceID, struct sensor_records_struct* sensor_records)
{
    strcpy(action, "sending_sensor_records");

    ESP_LOGE(TAG, "Sending sensor records...");
    mqtt_send_sensor_record(client, &(sensor_records->initial_ph_record), deviceID, "initial-ph");
    vTaskDelay(1000 / portTICK_PERIOD_MS);

    mqtt_send_sensor_record(client, &(sensor_records->final_ph_record), deviceID, "final-ph");
    vTaskDelay(1000 / portTICK_PERIOD_MS);

    mqtt_send_sensor_record(client, &(sensor_records->temperature_record), deviceID, "temperature");
    vTaskDelay(1000 / portTICK_PERIOD_MS);

    mqtt_send_sensor_record(client, &(sensor_records->humidity_record), deviceID, "humidity");
    vTaskDelay(1000 / portTICK_PERIOD_MS);

    mqtt_send_sensor_record(client, &(sensor_records->water_flow_record), deviceID, "water-flow");
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
            start_deep_sleep(SLEEP_TIME);
    } else 
    {
        esp_touch_helper(&deviceID); // device id may be null
    }

    ESP_LOGI(TAG, "Finished setting up WiFi");

    char my_device_id[100] = "oxwWwa";
    set_device_id(&my_device_id);

    return deviceID;
}

char* setup_wifi_and_mqtt() {
    char* deviceID = setup_wifi(); // connects to wifi
    strcpy(action, "seting_up_mqtt");
    mqtt_client = setup_mqtt();

    if (try_to_connect_to_broker_if_necessary(mqtt_client) == 0) 
    {
        ESP_LOGE(TAG, "Cannot connect to broker. Going to deep sleep...");
        start_deep_sleep(SLEEP_TIME);
    }

    return deviceID;
}

void terminate_wifi_and_mqtt()
{
    mqtt_app_terminate(mqtt_client); // stops mqtt tasks
    terminate_wifi(); // disconnects from wifi
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

void save_previous_records(struct sensor_records_struct* previous_sensor_records)
{
    previous_sensor_records->initial_ph_record.timestamp = sensor_records.initial_ph_record.timestamp;
    previous_sensor_records->initial_ph_record.value = sensor_records.initial_ph_record.value;

    previous_sensor_records->final_ph_record.timestamp = sensor_records.final_ph_record.timestamp;
    previous_sensor_records->final_ph_record.value = sensor_records.final_ph_record.value;

    previous_sensor_records->temperature_record.timestamp = sensor_records.temperature_record.timestamp;
    previous_sensor_records->temperature_record.value = sensor_records.temperature_record.value;

    previous_sensor_records->humidity_record.timestamp = sensor_records.humidity_record.timestamp;
    previous_sensor_records->humidity_record.value = sensor_records.humidity_record.value;

    previous_sensor_records->water_flow_record.timestamp = sensor_records.water_flow_record.timestamp;
    previous_sensor_records->water_flow_record.value = sensor_records.water_flow_record.value;
}

void compute_sensors() 
{
    int read_all = check_all_sensors;
    check_all_sensors = !check_all_sensors;

    int pending_records = 0;
    struct sensor_records_struct previous_sensor_records;
    if (ready_to_upload_records_to_server == 1) // saves last readings in a local variable before reading new ones
    {
        pending_records = 1;
        save_previous_records(&previous_sensor_records);
    }

    if(read_all)
    {
        read_sensor_records(&sensor_records, &action);
        // fake_timestamps(&sensor_records);
        ready_to_upload_records_to_server = 1; // data needs to be uploaded
    }

    strcpy(action, "checking_water_leak");
    int leakage = read_water_leak_record(); // TODO: change name
    
    char* deviceID = "";
    if (read_all)
    {
        deviceID = setup_wifi_and_mqtt(); // turns on wifi
        send_sensor_records(mqtt_client, deviceID, &sensor_records);
        ready_to_upload_records_to_server = 0; // data uploaded

        if (pending_records == 1) // there were pending records
        {
            ESP_LOGW(TAG, "There were pending records. Sending now...");
            send_sensor_records(mqtt_client, deviceID, &previous_sensor_records);
        }
    }

    if(leakage)
    {
        if (!read_all) // wifi not started yet
        {
            
            deviceID = setup_wifi_and_mqtt(); // turns on wifi
            
            if (ready_to_upload_records_to_server) // data wasnt uploaded last time
            {
                ESP_LOGW(TAG, "Data was not uploaded last time. Sending now...");
                send_sensor_records(mqtt_client, deviceID, &sensor_records);
            }
        }

        ESP_LOGW(TAG, "Water leakage detected. Sending message...");
        mqtt_send_device_wake_up_reason_alert(mqtt_client, getNowTimestamp(), deviceID, "water-leak"); // TODO: send to water leak topic (create one)

        terminate_wifi_and_mqtt();
    }
    else 
    {
        ESP_LOGI(TAG, "No water leakage detected.");
    }

    if (read_all || leakage)
    {
        terminate_wifi_and_mqtt(); // turns off wifi
    }
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
 * Connects to the wifi just to sync the time.
*/
void sync_time() 
{
    ESP_LOGI(TAG, "Syncing time...");
    setup_wifi();
    getNowTimestamp();
    terminate_wifi();
    ESP_LOGI(TAG, "Time synced.");
}

/**
 * Checks the wake up reason. Returns 0 if it is the timer, 1 if is power on or software, and 2 otherwise.
 * If the code is 2, it sends immediately a message to the broker.
 * 
*/
int handle_wake_up() 
{
    //long start = get_unsynced_time_in_miliiseconds();

    ESP_LOGI(TAG, "Checking wake up reason...");

    // Read the Reset Reason Register
    uint32_t reset_reason = esp_reset_reason();
    esp_sleep_wakeup_cause_t sleep_wakeup_reason = esp_sleep_get_wakeup_cause();

    int codeToReturn = 0;
    // Check the reset reason flags
    if (reset_reason & ESP_RST_UNKNOWN) 
    {
        ESP_LOGE(TAG, "Reset reason: unknown");
        wake_up_reason = "unknown";
        codeToReturn = 2;
    }
    if (reset_reason & ESP_RST_POWERON) 
    {
        sync_time();
        ESP_LOGI(TAG, "Reset reason: power-on");
        n_went_to_deep_sleep = 0; // reset sleep counter
        wake_up_reason = "power-on";
        codeToReturn = 1;
    }
    if (reset_reason & ESP_RST_SW) 
    {
        ESP_LOGI(TAG, "Reset reason: software");
        wake_up_reason = "software";
        codeToReturn = 1;
    }
    if (reset_reason & ESP_RST_PANIC) 
    {
        ESP_LOGE(TAG, "Reset reason: exception/panic");
        char sensor[50];
        if (was_reading_from_sensor(action, sensor))
        {
            wake_up_reason = sensor;
        }
        else 
        {
            if (strcmp(action, "seting_up_wifi") != 0 &&  strcmp(action, "seting_up_mqtt") != 0)
            {
                printf("Action: %s", action);
                char msg[100];
                sprintf(msg, "exception/panic: %s", action);
                wake_up_reason = action;
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
        wake_up_reason = "timer";
        if (ready_to_upload_records_to_server == 1)
        {
            ESP_LOGW(TAG, "System detected pending records to be sent to server. Sending them now...");
        }
        codeToReturn = 0;
    }
    if(codeToReturn == 2)
    {   
        char* deviceID = setup_wifi_and_mqtt();
        mqtt_send_device_wake_up_reason_alert(mqtt_client, getNowTimestamp(), deviceID, wake_up_reason);
        terminate_wifi_and_mqtt();
    }

    //long end = get_unsynced_time_in_miliiseconds();
    //long time_spent = end - start;
    // ESP_LOGW(TAG, "Time spent in milliseconds: %ld", time_spent);

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
 * 
 * Consumption times:
 *  Wake up to make standard sensor readings: 1mAh and 6 mWh (2:36 min) - 1 cycle
 *  Wake up to check for water leakage: 1mAh and 5mWh (2:15 min) - 21 cycles
 *  Deep sleep: 1mAh and 5mWh (6:11 min) or 9.7mAh for each hour sleeping
 * 
 * 1 hour without leakage and deep sleep -> 29mAh and 149mWh and 60 cycles
 * 3 hours without leakage and deep sleep -> 80mAh and 409mWh and 149 cycles
 * 2:30 hours deep sleep: 25mAh
*/
void app_main(void) 
{
    // vTaskDelay(pdMS_TO_TICKS(3 * 1000)); // FIXME: get from NVS

    // start_deep_sleep(9999999);

    // long start = get_unsynced_time_in_microseconds();

    ESP_LOGI(TAG, "Starting app_main...");
    ESP_ERROR_CHECK(nvs_flash_init());

    check_if_woke_up_to_reset();

    // long end = get_unsynced_time_in_microseconds();
    // ESP_LOGW(TAG, "Time to check if woke up to reset: %ld", end - start);

    int res = handle_wake_up();
    if (res == 0 || res == 1) // timeout or power on
    {
        if (res == 10) // power on TODO: change back to 1 later
        {
            determine_sensor_calibration_timings(); // to set the calibration timings
        }
        
        compute_sensors();
        ++my_counter;
        ESP_LOGW(TAG, "Counter: %d", my_counter);
        start_deep_sleep(SLEEP_TIME);
    }
    else // other wake up reason
    {
        start_deep_sleep(SLEEP_TIME);
    }
}