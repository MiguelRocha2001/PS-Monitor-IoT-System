#include <stdio.h>
#include <stdlib.h>
#include <esp_log.h>
#include "time_util.h"
#include "sensor/sensor_record.h"
#include "utils.h"
#include "dht11.h"

const static char* TAG = "TEMP_READER_REAL";

int read_temperature_record(struct sensor_record *temp_record)
{
    DHT11_init(GPIO_NUM_9);
    // sensor_record -> sensor_name = "temperature";
    temp_record -> value = DHT11_read().temperature;
    temp_record -> timestamp = getNowTimestamp();
    return 0;
}