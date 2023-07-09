#include <string.h>
#include "sensor/sensor_reader.h"
#include "sensor/ph_reader.h"
#include "sensor/temp_reader.h"
#include "sensor/water_flow_reader.h"
#include "sensor/humidity_reader.h"
#include "sensor/sensor_record.h"
#include <esp_log.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "driver/gpio.h"
#include "sdkconfig.h"
#include "time_util.h"
#include <math.h>
#include <esp_system.h>
#include "nvs_util.h"
#include "dht11.h"
#include "sliding_window.h"

const static char* TAG = "sensor_readings";

#define BETWEEN_READINGS 1000 // 1 second

#define DHT11_SENSOR_POWER_PIN GPIO_NUM_15
#define PH_SENSOR_POWER_PIN GPIO_NUM_13
#define WATER_FLOW_SENSOR_POWER_PIN GPIO_NUM_12


typedef struct sensor_records_temporary_struct {
    struct sensor_record initial_ph_records[MAX_SENSOR_RECORDS];
    struct sensor_record final_ph_records[MAX_SENSOR_RECORDS];
    struct sensor_record temperature_records[MAX_SENSOR_RECORDS];
    struct sensor_record water_flow_records[MAX_SENSOR_RECORDS];
    struct sensor_record humidity_records[MAX_SENSOR_RECORDS];
    int index;
} sensor_records_temporary_struct;

/**
 * Computes the average of each sensor readings and saves them in the sensor_records struct.
*/
void compute_average(sensor_records_struct *sensor_records, sensor_records_temporary_struct sensor_records_temp) {
    int timestamp = getNowTimestamp();

    sensor_records->initial_ph_record.value = 0;
    for (int i = 0; i < MAX_SENSOR_RECORDS; i++) {
        sensor_records->initial_ph_record.value += sensor_records_temp.initial_ph_records[i].value;
    }
    sensor_records->initial_ph_record.value /= MAX_SENSOR_RECORDS;
    sensor_records->initial_ph_record.timestamp = timestamp;

    sensor_records->final_ph_record.value = 0;
    for (int i = 0; i < MAX_SENSOR_RECORDS; i++) {
        sensor_records->final_ph_record.value += sensor_records_temp.final_ph_records[i].value;
    }
    sensor_records->final_ph_record.value /= MAX_SENSOR_RECORDS;
    sensor_records->final_ph_record.timestamp = timestamp;

    sensor_records->temperature_record.value = 0;
    for (int i = 0; i < MAX_SENSOR_RECORDS; i++) {
        sensor_records->temperature_record.value += sensor_records_temp.temperature_records[i].value;
    }
    sensor_records->temperature_record.value /= MAX_SENSOR_RECORDS;
    sensor_records->temperature_record.timestamp = timestamp;

    sensor_records->humidity_record.value = 0;
    for (int i = 0; i < MAX_SENSOR_RECORDS; i++) {
        sensor_records->humidity_record.value += sensor_records_temp.humidity_records[i].value;
    }
    sensor_records->humidity_record.value /= MAX_SENSOR_RECORDS;
    sensor_records->humidity_record.timestamp = timestamp;

    sensor_records->water_flow_record.value = 0;
    for (int i = 0; i < MAX_SENSOR_RECORDS; i++) {
        sensor_records->water_flow_record.value += sensor_records_temp.water_flow_records[i].value;
    }
    sensor_records->water_flow_record.value /= MAX_SENSOR_RECORDS;
    sensor_records->water_flow_record.timestamp = timestamp;
}

/**
 * Makes multiple readings using each sensor, computes the average and saves the result in the sensor_records struct.
 * The sensors are the following:
 * - initial pH sensor;
 * - final pH sensor;
 * - temperature sensor;
 * - humidity sensor;
 * - water flow sensor (not implemented yet)
 * 
 * For each sensor, the thread will block until the sensor has stabilized.
 * The maximum number of sensor readings is MAX_SENSOR_RECORDS.
 * Between each reading, the thread will wait for BETWEEN_READINGS milliseconds. This help avoiding sensor noise.
 * 
 * @return 0 if success.
*/
int read_sensor_records(sensor_records_struct *sensor_records) 
{
    sensor_records_temporary_struct sensor_records_temp;

    ESP_LOGI(TAG, "Reading sensor records");

    gpio_set_direction(PH_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    gpio_set_level(PH_SENSOR_POWER_PIN, 1); // power on sensors
    ESP_LOGI(TAG, "pH sensor powered on. Waiting for stabilization...");
    int ph_stabilization_time;
    ESP_ERROR_CHECK(get_saved_ph_calibration_timing(&ph_stabilization_time));
    vTaskDelay(pdMS_TO_TICKS(ph_stabilization_time)); // FIXME: get from NVS

    for(int i = 0; i < MAX_SENSOR_RECORDS; i++) 
    {
        read_initial_ph_record(&sensor_records_temp.initial_ph_records[i]);
        read_final_ph_record(&sensor_records_temp.final_ph_records[i]);
        vTaskDelay(pdMS_TO_TICKS(BETWEEN_READINGS));
    }

    gpio_set_level(PH_SENSOR_POWER_PIN, 0); // power off sensors
    ESP_LOGI(TAG, "pH sensor powered off.");

    gpio_set_direction(DHT11_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    gpio_set_level(DHT11_SENSOR_POWER_PIN, 1); // power on sensors
    DHT11_init(GPIO_NUM_9);
    ESP_LOGI(TAG, "DHT11 sensor powered on. Waiting for stabilization...");
    int dht11_stabilization_time;
    ESP_ERROR_CHECK(get_saved_dht11_calibration_timing(&dht11_stabilization_time));
    vTaskDelay(pdMS_TO_TICKS(dht11_stabilization_time)); // FIXME: get from NVS

    for(int i = 0; i < MAX_SENSOR_RECORDS; i++) 
    {
        read_temperature_record(&sensor_records_temp.temperature_records[i]);
        read_humidity_record(&sensor_records_temp.humidity_records[i]);
        vTaskDelay(pdMS_TO_TICKS(BETWEEN_READINGS));
    }

    gpio_set_level(DHT11_SENSOR_POWER_PIN, 0); // power off sensors
    ESP_LOGI(TAG, "DHT11 sensor powered off.");

    gpio_set_direction(WATER_FLOW_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    gpio_set_level(WATER_FLOW_SENSOR_POWER_PIN, 1); // power on sensors
    ESP_LOGI(TAG, "Water flow sensor powered on. Waiting for stabilization...");
    vTaskDelay(pdMS_TO_TICKS(0)); // FIXME: Water flow sensor stabilization time

    for(int i = 0; i < MAX_SENSOR_RECORDS; i++) 
    {
        read_water_flow_record(&sensor_records_temp.water_flow_records[i]);
        vTaskDelay(pdMS_TO_TICKS(BETWEEN_READINGS));
    }

    gpio_set_level(WATER_FLOW_SENSOR_POWER_PIN, 0); // power off sensors
    ESP_LOGI(TAG, "Water flow sensor powered off.");

    compute_average(sensor_records, sensor_records_temp);    

    ESP_LOGI(TAG, "Sensor readings complete");

    return 0;
}

/**
 * If not saved yet, uses Slinding Window Algorithm to determine the stabilization time of the initial ph, 
 * final ph and dht11 sensors, and saves the result in the NVS.
*/
void determine_sensor_calibration_timings()
{
    ESP_LOGI(TAG, "Determining pH sensor calibration time");
    int stabilization_time_in_ms;
    if (get_saved_ph_calibration_timing(&stabilization_time_in_ms) && stabilization_time_in_ms > 0)
    {
        calibrate_ph_sensors();
        ESP_LOGI(TAG, "pH sensor calibration complete");
    }
    else
    {
        ESP_LOGI(TAG, "pH sensor calibration not needed");
    }
    
    ESP_LOGI(TAG, "Determining DHT11 sensor calibration time");
    if (get_saved_dht11_calibration_timing(&stabilization_time_in_ms) && stabilization_time_in_ms > 0)
    {
        calibrate_dht11_sensor();
        ESP_LOGI(TAG, "DHT11 sensor calibration complete");
    }
    else
    {
        ESP_LOGI(TAG, "DHT11 sensor calibration not needed");
    }
}
