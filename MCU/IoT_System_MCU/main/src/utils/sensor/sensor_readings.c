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

#define between_readings 2000 // 2 seconds

/**
 * Read sensor records and store them in the sensor_records_struct.
 * Return 0 if success, 1 if the sensor_records_struct is full, -1 if there is an error with some sensor reading.
*/
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

        vTaskDelay(pdMS_TO_TICKS(between_readings));
    }
    return 0;
}


/**
 * Check if all the sensors are working.
 * @param stores the sensors that are not working in the int array.
 * @returns 0 if all the sensors are working, -1 otherwise.
*/
/*
int check_if_sensors_are_working(int *sensors_not_working) {
    struct sensor_record record1;
    struct sensor_record record2;
    if (read_start_ph_record(&record1) == -1) {
        sensors_not_working[0] = START_PH_SENSOR_ERROR;
        ESP_LOGE(TAG, "Start PH sensor error");
    }
    if (read_end_ph_record(&record1) == -1) {
        sensors_not_working[1] = END_PH_SENSOR_ERROR;
        ESP_LOGE(TAG, "End PH sensor error");
    }
    if (read_water_level_record(&record2) == -1) {
        sensors_not_working[2] = WATER_LEVEL_SENSOR_ERROR;
        ESP_LOGE(TAG, "Water level sensor error");
    }
    if (read_water_flow_record(&record2) == -1) {
        sensors_not_working[3] = WATER_FLOW_SENSOR_ERROR;
        ESP_LOGE(TAG, "Water flow sensor error");
    }
    if (read_humidity_record(&record1) == -1) {
        sensors_not_working[4] = HUMIDITY_SENSOR_ERROR;
        ESP_LOGE(TAG, "Humidity sensor error");
    }
    if (read_temperature_record(&record2) == -1) {
        sensors_not_working[5] = TEMP_SENSOR_ERROR;
        ESP_LOGE(TAG, "Temperature sensor error");
    }
    if (
        sensors_not_working[0] == -1 || 
        sensors_not_working[1] == -1 || 
        sensors_not_working[2] == -1 || 
        sensors_not_working[3] == -1 || 
        sensors_not_working[4] == -1 || 
        sensors_not_working[5] == -1
    )
    {
        return -1;
    }
    else
    {
        return 0;
    }
    
    return 0;
}
*/

int sensors_reading_is_complete(sensor_records_struct *sensor_records) {
    return sensor_records->index == MAX_SENSOR_RECORDS;
}