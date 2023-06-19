#include <stdio.h>
#include <stdlib.h>
#include <esp_log.h>
#include "time_util.h"
#include "sensor/sensor_record.h"
#include "utils.h"

const static char* TAG = "TEMP_READER_FAKE";

int read_temperature_record(struct sensor_record *temp_record)
{
    ESP_LOGI(TAG, "Reading temperature...");
    float temp_value = (float) (int) generate_random_float();
    int timestamp = getNowTimestamp();
    temp_record -> value = temp_value;
    temp_record -> timestamp = timestamp;
    return 0;
}