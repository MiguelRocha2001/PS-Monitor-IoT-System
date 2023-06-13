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
    struct sensor_record initial_ph_record;
    struct sensor_record final_ph_record;
    struct sensor_record temperature_record;
    struct sensor_record water_flow_record;
    struct sensor_record humidity_record;
    int index;
} sensor_records_struct;

int read_sensor_records(sensor_records_struct *sensor_records, char* action);

int check_if_sensors_are_working(int *sensors_not_working);

int sensors_reading_is_complete(sensor_records_struct *sensor_records);

#endif