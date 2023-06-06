#include <stdio.h>
#include <stdlib.h>
#include <esp_log.h>
#include "time_util.h"
#include "sensor/sensor_record.h"
#include "utils.h"

const static char* TAG = "PH_READER_FAKE";

int read_initial_ph_record(struct sensor_record *sensor_record)
{
    ESP_LOGE(TAG, "Reading start pH...");
    float ph_value = generate_random_float();
    int timestamp = getNowTimestamp();
    sensor_record -> value = ph_value;
    sensor_record -> timestamp = timestamp;
    return 0;
}

int read_final_ph_record(struct sensor_record *sensor_record)
{
    ESP_LOGE(TAG, "Reading final pH...");
    float ph_value = generate_random_float();
    int timestamp = getNowTimestamp();
    sensor_record -> value = ph_value;
    sensor_record -> timestamp = timestamp;
    return 0;
}