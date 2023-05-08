#include "sensor/sensor_reader.h"
#include "sensor/ph_reader.h"
#include "sensor/temp_reader.h"
#include "sensor/water_level_reader.h"
#include "sensor/water_flow_reader.h"
#include "sensor/humidity_reader.h"
#include "sensor/sensor_record.h"
#include <esp_log.h>

const static char* TAG = "sensor_readings";

/**
 * Read sensor records and store them in the sensor_records_struct.
 * Return 0 if success, 1 if the sensor_records_struct is full, -1 if there is an error with some sensor reading.
*/
int read_sensor_records(sensor_records_struct *sensor_records) {
    int index = sensor_records->index;
    if (index < MAX_SENSOR_RECORDS) {
        if (read_start_ph_record(&sensor_records->start_ph_records[index]) == -1) return START_PH_SENSOR_ERROR;
        if (read_end_ph_record(&sensor_records->end_ph_records[index]) == -1) return END_PH_SENSOR_ERROR;
        if (read_temperature_record(&sensor_records->temperature_records[index]) == -1) return TEMP_SENSOR_ERROR;
        if (read_water_level_record(&sensor_records->water_level_records[index]) == -1) return WATER_LEVEL_SENSOR_ERROR;
        if (read_water_flow_record(&sensor_records->water_flow_records[index]) == -1) return WATER_FLOW_SENSOR_ERROR;
        if (read_humidity_record(&sensor_records->humidity_records[index]) == -1) return HUMIDITY_SENSOR_ERROR;
    
        sensor_records->index = (index + 1) % MAX_SENSOR_RECORDS;
        return 0;
    }
    return 1;
}

/**
 * Check if all the sensors are working.
 * @param stores the sensors that are not working in the int array.
 * @returns 0 if all the sensors are working, -1 otherwise.
*/
int check_if_sensors_are_working(int *sensors_not_working) {
    struct sensor_record1 record1;
    struct sensor_record2 record2;
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

int sensors_reading_is_complete(sensor_records_struct *sensor_records) {
    return sensor_records->index == MAX_SENSOR_RECORDS;
}