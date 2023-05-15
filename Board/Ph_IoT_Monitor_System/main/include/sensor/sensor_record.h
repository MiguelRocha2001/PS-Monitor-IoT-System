#ifndef SENSOR_RECORD_H
#define SENSOR_RECORD_H

// float value
typedef struct sensor_record {
    char* sensor_name;
    float value;
    int timestamp;
} sensor_record;

#endif
