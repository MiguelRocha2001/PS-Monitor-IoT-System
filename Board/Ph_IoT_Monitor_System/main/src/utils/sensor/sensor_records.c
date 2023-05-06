#include "sensor/sensor_records.h"
#include "sensor/ph_record.h"
#include "sensor/temp_record.h"

int read_sensor_records(sensor_records_struct *sensor_records) {
    int index = sensor_records->index;

    if (index < MAX_SENSOR_RECORDS) {
        read_ph_record(&sensor_records->ph_records[index]);
        read_temperature_record(&sensor_records->temperature_records[index]);
        //read_water_level_record(&sensor_records->water_level_records[index]);
        //read_water_flow_record(&sensor_records->water_flow_records[index]);
        //read_humidity_record(&sensor_records->humidity_records[index]);

        sensor_records->index = (index + 1) % MAX_SENSOR_RECORDS;
        return 0;
    }
    return -1;
}

int sensors_reading_is_complete(sensor_records_struct *sensor_records) {
    return sensor_records->index == MAX_SENSOR_RECORDS;
}