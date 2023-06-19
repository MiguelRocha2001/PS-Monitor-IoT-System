#include <stdio.h>
#include <stdlib.h>
#include <esp_log.h>
#include "time_util.h"
#include "sensor/sensor_record.h"
#include "utils.h"

const static char* TAG = "WATER_LEAK_READER_FAKE";

int read_water_leak_record()
{
    ESP_LOGI(TAG, "Reading water leak...");
    return 0; // no water leak
}