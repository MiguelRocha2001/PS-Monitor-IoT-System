#ifndef SENSOR_RECORD_H
#define SENSOR_RECORD_H

typedef struct sensor_record1 {
    char* sensor_name;
    float value;
    int timestamp;
} sensor_record1;

typedef struct sensor_record2 {
    char* sensor_name;
    int value;
    int timestamp;
} sensor_record2;

#endif
