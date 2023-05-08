#include <stdio.h>
#include <stdlib.h>
#include <esp_log.h>
#include "time_util.h"
#include "sensor/sensor_record.h"
#include "utils.h"

const static char* TAG = "HUMIDITY_READER_FAKE";

int read_humidity_record(struct sensor_record1 *sensor_record)
{
    ESP_LOGE(TAG, "Reading start pH...");
    float ph_value = generate_random_float();
    // int timestamp = generate_random_int();
    int timestamp = getNowTimestamp();
    sensor_record -> value = ph_value;
    sensor_record -> timestamp = timestamp;
    return 0;
}