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

const static char* TAG = "sensor_readings";

#define BETWEEN_READINGS 2000 // 2 seconds

#define PH_SENSOR_STABILIZATION_TIME 1000 * 5 // 1 minute
#define DHT11_STABILIZATION_TIME 1000 * 5 // 1 minute
#define WATER_FLOW_STABILIZATION_TIME 0 * 5 // 1 minute

#define DHT11_SENSOR_POWER_PIN GPIO_NUM_15
#define PH_SENSOR_POWER_PIN GPIO_NUM_13
#define WATER_FLOW_SENSOR_POWER_PIN GPIO_NUM_12

/**
 * Read sensor records and store them in the sensor_records_struct.
 * Return 0 if success, 1 if the sensor_records_struct is full, -1 if there is an error with some sensor reading.
*/
/*
int read_sensor_records(sensor_records_struct *sensor_records, char* action) 
{
    ESP_LOGE(TAG, "Reading sensor records");
    for(int i = 0; i < MAX_SENSOR_RECORDS; i++) 
    {
        strcpy(action, "reading_initial_ph");
        read_initial_ph_record(&sensor_records->initial_ph_records[i]);
        strcpy(action, "reading_final_ph");
        read_final_ph_record(&sensor_records->final_ph_records[i]);
        strcpy(action, "reading_temperature");
        read_temperature_record(&sensor_records->temperature_records[i]);
        strcpy(action, "reading_water_flow");
        read_water_flow_record(&sensor_records->water_flow_records[i]);
        strcpy(action, "reading_humidity");
        read_humidity_record(&sensor_records->humidity_records[i]);
        sensor_records->index = i + 1; // increment index

        vTaskDelay(pdMS_TO_TICKS(BETWEEN_READINGS));
    }
    return 0;
}
*/

/**
 * Read sensor records and store them in the sensor_records_struct.
 * Return 0 if success, 1 if the sensor_records_struct is full, -1 if there is an error with some sensor reading.
*/
int read_sensor_records(sensor_records_struct *sensor_records, char* action) 
{
    ESP_LOGE(TAG, "Reading sensor records");

    gpio_set_direction(PH_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    gpio_set_level(PH_SENSOR_POWER_PIN, 1); // power on sensors
    vTaskDelay(pdMS_TO_TICKS(PH_SENSOR_STABILIZATION_TIME));

    for(int i = 0; i < MAX_SENSOR_RECORDS; i++) 
    {
        strcpy(action, "reading_initial_ph");
        read_initial_ph_record(&sensor_records->initial_ph_records[i]);

        strcpy(action, "reading_final_ph");
        read_initial_ph_record(&sensor_records->final_ph_records[i]);

        sensor_records->index = i + 1; // increment index
        vTaskDelay(pdMS_TO_TICKS(BETWEEN_READINGS));
    }

    gpio_set_level(PH_SENSOR_POWER_PIN, 0); // power off sensors
    ESP_LOGE(TAG, "Powering off ph sensor...");

    ESP_LOGE(TAG, "Powering on dht11 sensor...");
    gpio_set_direction(DHT11_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    gpio_set_level(DHT11_SENSOR_POWER_PIN, 1); // power on sensors
    vTaskDelay(pdMS_TO_TICKS(DHT11_STABILIZATION_TIME));

    for(int i = 0; i < MAX_SENSOR_RECORDS; i++) 
    {
        strcpy(action, "reading_temperature");
        read_initial_ph_record(&sensor_records->temperature_records[i]);

        strcpy(action, "reading_humidity");
        read_humidity_record(&sensor_records->humidity_records[i]);

        sensor_records->index = i + 1; // increment index
        vTaskDelay(pdMS_TO_TICKS(BETWEEN_READINGS));
    }

    gpio_set_level(DHT11_SENSOR_POWER_PIN, 0); // power off sensors
    ESP_LOGE(TAG, "Powering off dht11 sensor...");

    ESP_LOGE(TAG, "Powering on water flow sensor...");
    gpio_set_direction(WATER_FLOW_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    gpio_set_level(WATER_FLOW_SENSOR_POWER_PIN, 1); // power on sensors
    vTaskDelay(pdMS_TO_TICKS(DHT11_STABILIZATION_TIME));

    for(int i = 0; i < MAX_SENSOR_RECORDS; i++) 
    {
        strcpy(action, "reading_water_flow");
        read_initial_ph_record(&sensor_records->water_flow_records[i]);

        sensor_records->index = i + 1; // increment index
        vTaskDelay(pdMS_TO_TICKS(BETWEEN_READINGS));
    }

    gpio_set_level(WATER_FLOW_SENSOR_POWER_PIN, 0); // power off sensors
    ESP_LOGE(TAG, "Powering off water flow sensor...");

    return 0;
}

int sensors_reading_is_complete(sensor_records_struct *sensor_records) {
    return sensor_records->index == MAX_SENSOR_RECORDS;
}