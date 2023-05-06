#ifndef SENSOR_RECORDS_H
#define SENSOR_RECORDS_H

#include "sensor_record.h"

#define MAX_SENSOR_RECORDS 3

typedef struct sensor_records_struct {
    struct sensor_record1 ph_records[MAX_SENSOR_RECORDS];
    struct sensor_record2 temperature_records[MAX_SENSOR_RECORDS];
    struct sensor_record1 water_level_records[MAX_SENSOR_RECORDS];
    struct sensor_record1 water_flow_records[MAX_SENSOR_RECORDS];
    struct sensor_record1 humidity_records[MAX_SENSOR_RECORDS];
    int index;
} sensor_records_struct;

int read_sensor_records(sensor_records_struct *sensor_records);

int sensors_reading_is_complete(sensor_records_struct *sensor_records);

#endif