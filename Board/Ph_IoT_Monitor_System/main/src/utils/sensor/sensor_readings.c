#include "sensor/sensor_reader.h"
#include "sensor/ph_reader.h"
#include "sensor/temp_reader.h"

const static char* TAG = "sensor_readings";

/**
 * Read sensor records and store them in the sensor_records_struct.
 * Return 0 if success, 1 if the sensor_records_struct is full, -1 if there is an error with some sensor reading.
*/
int read_sensor_records(sensor_records_struct *sensor_records) {
    int index = sensor_records->index;

    if (index < MAX_SENSOR_RECORDS) {
        if (read_ph_record(&sensor_records->ph_records[index]) == -1) return PH_SENSOR_ERROR;
        if (read_temp_record(&sensor_records->temperature_records[index]) == -1) return TEMP_SENSOR_ERROR;
        if (read_water_level_record(&sensor_records->water_level_records[index]) == -1) return WATER_LEVEL_SENSOR_ERROR;
        if (read_water_flow_record(&sensor_records->water_flow_records[index]) == -1) return WATER_FLOW_SENSOR_ERROR;
        if (read_humidity_record(&sensor_records->humidity_records[index]) == -1) return HUMIDITY_SENSOR_ERROR;
    
        sensor_records->index = (index + 1) % MAX_SENSOR_RECORDS;
        return 0;
    }
    return 1;
}

int sensors_reading_is_complete(sensor_records_struct *sensor_records) {
    return sensor_records->index == MAX_SENSOR_RECORDS;
}