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

const static char* TAG = "sensor_readings";

#define window_size 5
#define standard_deviation_threshold 3
#define BETWEEN_READINGS_CALIBRATION 1000 // 1 second

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
 * Read sensor records and store them in the sensor_records_struct.
 * Return 0 if success, 1 if the sensor_records_struct is full, -1 if there is an error with some sensor reading.
*/
int read_sensor_records(sensor_records_struct *sensor_records, char* action) 
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

void sliding_window(int sensor_power_pin, int (*read_function)(struct sensor_record*), esp_err_t (*nvs_function)(int), int is_dht11) {
    gpio_set_direction(sensor_power_pin, GPIO_MODE_OUTPUT);
    gpio_set_level(sensor_power_pin, 1); // power on sensor

    if (is_dht11) { // initialize DHT11 sensor
        DHT11_init(GPIO_NUM_9);
    }
    
    sensor_record sensor_record;
    double window[window_size];
    int initial_timestamp = getNowTimestamp();
    while (true)
    {
        for(int i = 0; i < window_size; i++)
        {
            read_function(&sensor_record);
            window[i] = sensor_record.value;
            ESP_LOGW(TAG, "Value: %f", sensor_record.value);
            vTaskDelay(pdMS_TO_TICKS(BETWEEN_READINGS_CALIBRATION));
        }

        double sum = 0;
        for (int j = 0; j < window_size; j++) {
            sum += window[j];
        }
        
        double mean = sum / window_size;
        
        double squared_diff_sum = 0;
        for (int j = 0; j < window_size; j++) {
            double diff = window[j] - mean;
            squared_diff_sum += diff * diff;
        }
        
        double std_dev = sqrt(squared_diff_sum / window_size);
        if (std_dev <= standard_deviation_threshold)
        {
            int final_timestamp = getNowTimestamp();
            int stabilization_time_in_ms = (final_timestamp - initial_timestamp) * 1000;

            ESP_LOGW(TAG, "Standard deviation is below threshold %f", std_dev);

            ESP_ERROR_CHECK(nvs_function(stabilization_time_in_ms));
            ESP_LOGW(TAG, "Saved stabilization time: %d", stabilization_time_in_ms);

            break;
        }
        ESP_LOGW(TAG, "Standard deviation: %f", std_dev);

        for (int i = 0; i < window_size - 1; i++) {
            window[i] = window[i + 1];
        }
        read_function(&sensor_record);
        window[window_size - 1] = sensor_record.value; // new value

        gpio_set_level(sensor_power_pin, 0); // power off sensor
    }
}

void determine_sensor_calibration_timings()
{
    ESP_LOGI(TAG, "Determining pH sensor calibration time");
    sliding_window(PH_SENSOR_POWER_PIN, read_initial_ph_record, set_saved_ph_calibration_timing, 0);

    ESP_LOGI(TAG, "Determining DHT11 sensor calibration time");
    sliding_window(DHT11_SENSOR_POWER_PIN, read_humidity_record, set_saved_dht11_calibration_timing, 1);
}
