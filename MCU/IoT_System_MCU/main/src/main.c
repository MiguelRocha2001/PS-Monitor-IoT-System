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
RTC_DATA_ATTR int sensor_readings_not_yet_uploaded = 0; // indicates that records were read but not yet uploaded to server
RTC_DATA_ATTR int n_went_to_deep_sleep = 0; // used to fake timestamps
RTC_DATA_ATTR int check_all_sensors = 1; // if true, the mcu will read all sensors and not just the water sensor
RTC_DATA_ATTR int my_counter = 0;
RTC_DATA_ATTR char* wake_up_reason;
esp_mqtt_client_handle_t mqtt_client;

/**
 * Uses WiFi and MQTT to send the stores sensor records to the server.
 * This records are stores in the global RTC variable sensor_records.
*/
void send_sensor_records(esp_mqtt_client_handle_t client, char* deviceID, struct sensor_records_struct* sensor_records)
{
    set_last_action_performed("sending_sensor_records");

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

/**
 * Tries to start WiFi, based on saved credentials (wifi credentials are saved in flash memory).
 * If the device id or wifi credentials are not saved, or the wifi connection fails the maximum number of tries,
 * the device will enter in the esp touch mode.
 * This function will block until the user uses his personal phone to send the WiFi credentials to the device.
 * @return the saved device id.
*/
char* setup_wifi(void) {
    char action[100] = "seting_up_wifi";
    set_last_action_performed(&action);
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

    char my_device_id[100] = "QJlcPa";
    //set_device_id(&my_device_id);

    return deviceID;
}

/**
 * Tries to connect to the WiFi. 
 * @see setup_wifi.
 * If successful, tries to connect to the MQTT broker, for a maximum number of tries.
 * @see try_to_connect_to_broker_if_necessary.
 * If cannot connect to the broker, the device will enter in deep sleep mode, for SLEEP_TIME seconds.
 * 
*/
char* setup_wifi_and_mqtt() {
    char* deviceID = setup_wifi(); // connects to wifi

    char action[100] = "seting_up_mqtt";
    set_last_action_performed(&action);

    mqtt_client = setup_mqtt();

    if (try_to_connect_to_broker_if_necessary(mqtt_client) == -1) 
    {
        ESP_LOGE(TAG, "Cannot connect to broker. Going to deep sleep...");
        start_deep_sleep(SLEEP_TIME);
    }

    return deviceID;
}

void terminate_wifi_and_mqtt()
{
    char action[100] = "terminating_mqtt";

    set_last_action_performed(&action);
    mqtt_app_terminate(mqtt_client); // stops mqtt tasks
    
    strcpy(action, "terminating_wifi");
    set_last_action_performed(&action);
    terminate_wifi(); // disconnects from wifi
}

/**
 * Overrides timestamps, based on how many times the mcu woke up.
*/
void fake_timestamps(struct sensor_records_struct *sensor_records) {
    ESP_LOGE(TAG, "Faking timestamps...");
    int n_seconds_in_day = 24 * 60 * 60;
    sensor_records -> initial_ph_record.timestamp -= n_went_to_deep_sleep * n_seconds_in_day;
    sensor_records -> final_ph_record.timestamp -= n_went_to_deep_sleep * n_seconds_in_day;
    sensor_records -> temperature_record.timestamp -= n_went_to_deep_sleep * n_seconds_in_day;
    sensor_records -> humidity_record.timestamp -= n_went_to_deep_sleep * n_seconds_in_day;
    sensor_records -> water_flow_record.timestamp -= n_went_to_deep_sleep * n_seconds_in_day;
    n_went_to_deep_sleep++;
}

/**
 * Copies all sensor records from RTC sensor_records global variable to previous_sensor_records strcut.
*/
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

/**
 * Makes the apropriate actions based on the current wake up iteration.
 * If the global variable "check_all_sensors" is true, it will read all sensors, and check for water leakage.
 * In this case, it will also try to upload the last readings, if they are not uploaded yet.
 * If the global variable "check_all_sensors" is false, it will only check for water leakage. If water is detected,
 * it will try to upload the last readings, if they are not uploaded yet.
*/
void compute_sensors() 
{
    int read_all = check_all_sensors;
    check_all_sensors = !check_all_sensors;

    int pending_records = 0;
    struct sensor_records_struct previous_sensor_records;
    if (sensor_readings_not_yet_uploaded == 1) // saves last readings in a local variable before reading new ones
    {
        pending_records = 1;
        save_previous_records(&previous_sensor_records);
    }

    if(read_all)
    {
        read_sensor_records(&sensor_records);
        fake_timestamps(&sensor_records);
        sensor_readings_not_yet_uploaded = 1; // data needs to be uploaded
    }

    char action[100] = "checking_water_leak";
    set_last_action_performed(&action);
    int leakage = read_water_leak_record(); // TODO: change name
    
    char* deviceID = "";
    if (read_all)
    {
        deviceID = setup_wifi_and_mqtt(); // turns on wifi
        send_sensor_records(mqtt_client, deviceID, &sensor_records);
        sensor_readings_not_yet_uploaded = 0; // data uploaded

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
            ESP_LOGW(TAG, "Water leakage detected. Sending message...");
            
            deviceID = setup_wifi_and_mqtt(); // turns on wifi
            
            if (sensor_readings_not_yet_uploaded) // data wasnt uploaded last time
            {
                ESP_LOGW(TAG, "Data was not uploaded last time. Sending now...");
                send_sensor_records(mqtt_client, deviceID, &sensor_records);
            }
        }
        mqtt_send_water_leak_alert(mqtt_client, getNowTimestamp(), deviceID);
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

/**
 * Determines what sensor the device was reading from based on the action.
 * @param sensor The sensor that the device was reading from.
 * Returns 1 if the device was reading from a sensor, 0 otherwise.
*/
int was_reading_from_sensor(char* sensor) 
{
    char* action;
    get_last_action_performed(&action);
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
    // terminate_wifi();
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

    int problem_with_network = 0; // if there was a problem with wifi or mqtt
    if (reset_reason & ESP_RST_PANIC) 
    {
        ESP_LOGE(TAG, "Reset reason: exception/panic");
        char sensor[50];
        if (was_reading_from_sensor(sensor))
        {
            wake_up_reason = sensor;
        }
        else 
        {
            char *action;
            ESP_ERROR_CHECK(get_last_action_performed(&action));
            // last action was a problem with wifi or mqtt
            if (strcmp(action, "seting_up_wifi") != 0 &&  strcmp(action, "seting_up_mqtt") != 0)
            {
                ESP_LOGE(TAG, "Error: exception/panic - %s", action);
                char msg[100];
                sprintf(msg, "exception/panic - %s", action);
                ESP_LOGE(TAG, "msg: %s", msg);
                wake_up_reason = msg;
            }
            else
            {
                problem_with_network = 1;
                ESP_LOGE(TAG, "Error setting up wifi or mqtt. Not sending message to broker");
            }
        }
        codeToReturn = 2;
    }
    if (sleep_wakeup_reason == ESP_SLEEP_WAKEUP_TIMER)
    {
        ESP_LOGI(TAG, "Wake up reason: timer");
        wake_up_reason = "timer";
        codeToReturn = 0;
    }
    if(codeToReturn == 2 && !problem_with_network)
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
        ESP_ERROR_CHECK(set_saved_ph_calibration_timing(-1)); 
        ESP_ERROR_CHECK(set_saved_dht11_calibration_timing(-1));
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
 * The device will wake up every 6 hours to check for water leakage.
 * Also, each two iterations (12 hours interval), apart from the water sensor, 
 * the device will also make readings using the other sensors:
 * - ph;
 * - temperature;
 * - humidity;
 * - water flow (driver not implemented yet)
 * 
 * Consumption times:
 *  Wake up to make standard sensor readings: 1mAh and 6 mWh (2:36 min) - 1 cycle
 *  Wake up to check for water leakage: 1mAh and 5mWh (2:15 min) - 21 cycles
 *  Deep sleep: 1mAh and 5mWh (6:11 min) or 9.7mAh for each hour sleeping
 * 
 * 3 hours without leakage and deep sleep -> 80mAh and 409mWh and 150 wakeups (75 cycles)
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
        if (res == 1)
        {
            ESP_ERROR_CHECK(set_saved_ph_calibration_timing(-1)); // only for tests
            ESP_ERROR_CHECK(set_saved_dht11_calibration_timing(-1)); // only for tests

            determine_sensor_calibration_timings(); // to set the calibration timings

            start_deep_sleep(1);
        }
        
        compute_sensors();
        ++my_counter;
        // ESP_LOGW(TAG, "Counter: %d", my_counter);
        start_deep_sleep(SLEEP_TIME);
    }
    else // other wake up reason
    {
        start_deep_sleep(SLEEP_TIME);
    }
}