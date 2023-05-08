#include <stdio.h>
#include <stdlib.h>
#include <esp_log.h>
#include "time_util.h"
#include "sensor/sensor_record.h"
#include "utils.h"

const static char* TAG = "PH_READER_FAKE";

int read_start_ph_record(struct sensor_record1 *sensor_record)
{
    ESP_LOGE(TAG, "Reading start pH...");
    float ph_value = generate_random_float();
    // int timestamp = generate_random_int();
    int timestamp = getNowTimestamp();
    sensor_record -> value = ph_value;
    sensor_record -> timestamp = timestamp;
    return 0;
}

int read_end_ph_record(struct sensor_record1 *sensor_record)
{
    ESP_LOGE(TAG, "Reading end pH...");
    float ph_value = generate_random_float();
    // int timestamp = generate_random_int();
    int timestamp = getNowTimestamp();
    sensor_record -> value = ph_value;
    sensor_record -> timestamp = timestamp;
    return 0;
}