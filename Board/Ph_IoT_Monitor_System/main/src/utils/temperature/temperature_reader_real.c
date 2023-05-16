#include <stdio.h>
#include <stdlib.h>
#include <esp_log.h>
#include "time_util.h"
#include "sensor/sensor_record.h"
#include "utils.h"
#include "dht11.h"

const static char* TAG = "TEMP_READER_FAKE";

int read_temperature_record(struct sensor_record2 *temp_record)
{
    ESP_LOGE(TAG, "Reading temperature...");
    DHT11_init(GPIO_NUM_1);
    temp_record -> value = DHT11_read().temperature;
    temp_record -> timestamp = getNowTimestamp();
    return 0;
}