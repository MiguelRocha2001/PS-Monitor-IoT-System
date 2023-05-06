#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <esp_log.h>
#include "time_util.h"
#include "sensor/sensor_record.h"

const static char* TAG = "PH_READER_FAKE";

float generate_random_float()
{
    srand((unsigned int)time(NULL));

    float a = 5.0;
    return ((float)rand()/(float)(RAND_MAX)) * a;
}

int read_ph_record(struct sensor_record1 *sensor_record)
{
    ESP_LOGE(TAG, "Reading pH...");
    float ph_value = generate_random_float();
    // int timestamp = generate_random_int();
    int timestamp = getNowTimestamp();
    sensor_record -> value = ph_value;
    sensor_record -> timestamp = timestamp;
}