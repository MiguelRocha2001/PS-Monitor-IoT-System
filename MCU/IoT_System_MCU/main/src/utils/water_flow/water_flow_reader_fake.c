#include <stdio.h>
#include <stdlib.h>
#include <esp_log.h>
#include "time_util.h"
#include "sensor/sensor_record.h"
#include "utils.h"

const static char* TAG = "WATER_FLOW_READER_FAKE";

int read_water_flow_record(struct sensor_record *sensor_record)
{
    ESP_LOGI(TAG, "Reading water flow...");
    float water_flow = (float) (int) generate_random_float();
    // int timestamp = generate_random_int();
    int timestamp = getNowTimestamp();
    sensor_record -> value = water_flow;
    sensor_record -> timestamp = timestamp;
    return 0;
}