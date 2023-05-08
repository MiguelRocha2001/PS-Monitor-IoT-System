#ifndef SENSOR_RECORDS_H
#define SENSOR_RECORDS_H

#include "sensor_record.h"

#define START_PH_SENSOR_ERROR -1
#define END_PH_SENSOR_ERROR -1
#define TEMP_SENSOR_ERROR -1
#define WATER_LEVEL_SENSOR_ERROR -1
#define WATER_FLOW_SENSOR_ERROR -1
#define HUMIDITY_SENSOR_ERROR -1

#define MAX_SENSOR_RECORDS 3

typedef struct sensor_records_struct {
    struct sensor_record1 start_ph_records[MAX_SENSOR_RECORDS];
    struct sensor_record1 end_ph_records[MAX_SENSOR_RECORDS];
    struct sensor_record2 temperature_records[MAX_SENSOR_RECORDS];
    struct sensor_record1 water_level_records[MAX_SENSOR_RECORDS];
    struct sensor_record1 water_flow_records[MAX_SENSOR_RECORDS];
    struct sensor_record1 humidity_records[MAX_SENSOR_RECORDS];
    int index;
} sensor_records_struct;

int read_sensor_records(sensor_records_struct *sensor_records);

int check_if_sensors_are_working(int *sensors_not_working);

int sensors_reading_is_complete(sensor_records_struct *sensor_records);

#endif