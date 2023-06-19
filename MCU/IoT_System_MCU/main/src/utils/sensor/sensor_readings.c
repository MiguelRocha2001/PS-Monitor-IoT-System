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

const static char* TAG = "sensor_readings";

#define BETWEEN_READINGS 2000 // 2 seconds

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
    int PH_SENSOR_STABILIZATION_TIME = 0;
    ESP_ERROR_CHECK(get_saved_ph_calibration_timing(&PH_SENSOR_STABILIZATION_TIME));
    vTaskDelay(pdMS_TO_TICKS(PH_SENSOR_STABILIZATION_TIME));

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
    ESP_LOGI(TAG, "DHT11 sensor powered on. Waiting for stabilization...");
    vTaskDelay(pdMS_TO_TICKS(1)); // FIXME: DHT11 stabilization time

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
    vTaskDelay(pdMS_TO_TICKS(1)); // FIXME: Water flow sensor stabilization time

    for(int i = 0; i < MAX_SENSOR_RECORDS; i++) 
    {
        read_water_flow_record(&sensor_records_temp.water_flow_records[i]);
        vTaskDelay(pdMS_TO_TICKS(BETWEEN_READINGS));
    }

    gpio_set_level(WATER_FLOW_SENSOR_POWER_PIN, 0); // power off sensors
    ESP_LOGI(TAG, "Water flow sensor powered off.");

    compute_average(sensor_records, sensor_records_temp);    

    ESP_LOGI(TAG, "Sensor records read successfully");

    return 0;
}

int sensors_reading_is_complete(sensor_records_struct *sensor_records) {
    return sensor_records->index == MAX_SENSOR_RECORDS;
}

// Function to calculate standard deviation
double calculate_standard_deviation(uint32_t* data, size_t data_length) {
    double mean = 0.0;
    double squared_deviation_sum = 0.0;

    // Calculate mean
    for (size_t i = 0; i < data_length; i++) {
        mean += data[i];
    }
    mean /= data_length;

    // Calculate squared deviations
    for (size_t i = 0; i < data_length; i++) {
        double deviation = data[i] - mean;
        squared_deviation_sum += deviation * deviation;
    }

    // Calculate variance and standard deviation
    double variance = squared_deviation_sum / data_length;
    double standard_deviation = sqrt(variance);

    return standard_deviation;
}

void determine_ph_sensors_calibration_time()
{
    ESP_LOGI(TAG, "Determining sensor calibration timings");

    gpio_set_direction(PH_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    gpio_set_level(PH_SENSOR_POWER_PIN, 1); // power on sensors
    ESP_LOGI(TAG, "pH sensor powered on. Waiting for stabilization...");

    struct sensor_record sensor_record[10];

    int initial_timestamp = getNowTimestamp();
    double standard_deviation = -1;
    while (standard_deviation != -1 && standard_deviation <= 2)
    {
        for(int i = 0; i < 10; i++)
        {
            read_initial_ph_record(&sensor_record[i]);
            ESP_LOGI(TAG, "Value: %d", sensor_record[i].value);
            vTaskDelay(pdMS_TO_TICKS(1000));
        }

        uint32_t data[10];
        for(int i = 0; i < 10; i++)
        {
            data[i] = sensor_record[i].value;
        }

        double standard_deviation = calculate_standard_deviation(data, 10);
        ESP_LOGI(TAG, "Standard deviation: %f", standard_deviation);
    }

    int final_timestamp = getNowTimestamp();
    int stabilization_time = 1000 * (final_timestamp - initial_timestamp);
    ESP_ERROR_CHECK(set_saved_ph_calibration_timing(stabilization_time));

    gpio_set_level(PH_SENSOR_POWER_PIN, 0); // power off sensors
    ESP_LOGI(TAG, "pH sensor powered off.");
}

void determine_sensor_calibration_timings()
{
    determine_ph_sensors_calibration_time();
    // TODO: determine other sensors calibration timings
}
