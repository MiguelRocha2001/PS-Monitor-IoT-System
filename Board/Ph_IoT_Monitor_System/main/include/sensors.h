#include "ph_values_struct.h"

#ifndef SENSOR_RECORDS_H
#define SENSOR_RECORDS_H

#define MAX_PH_VALUES 1

typedef struct sensors_records_struct {
    struct ph_records_struct ph_readings;
    struct water_flow_records_struct water_flow_readings;
    struct water_level_records_struct water_level_readings;
    struct temp_records_struct temp_readings;
    struct humidity_records_struct humidity_readings;
} sensor_records_struct;

#endif