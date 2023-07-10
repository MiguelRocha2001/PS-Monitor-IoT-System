#include <stdio.h>
#include <stdlib.h>
#include <esp_log.h>
#include "time_util.h"
#include "sensor/sensor_record.h"
#include "utils.h"
#include "dht11.h"

const static char* TAG = "TEMP_READER_REAL";

// call DHT11_init(GPIO_NUM_9); before calling this function !!!
int read_humidity_record(struct sensor_record *temp_record)
{
    // sensor_record -> sensor_name = "humidity";
    temp_record -> value = DHT11_read().humidity;
    temp_record -> timestamp = getNowTimestamp();

    temp_record -> value = 39; // TODO: uncomment later
    return 0;
}