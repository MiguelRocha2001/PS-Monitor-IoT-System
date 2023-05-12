#include <stdio.h>
#include <stdlib.h>
#include <esp_log.h>
#include "time_util.h"
#include "sensor/sensor_record.h"
#include "utils.h"

const static char* TAG = "TEMP_READER_FAKE";

int read_temperature_record(struct sensor_record2 *temp_record)
{
    ESP_LOGE(TAG, "Reading temperature...");
    int temp_value = generate_random_int();
    int timestamp = getNowTimestamp();
    temp_record -> value = temp_value;
    temp_record -> timestamp = timestamp;

    return 0;
}