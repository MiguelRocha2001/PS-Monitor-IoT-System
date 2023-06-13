/* ADC1 Example

   This example code is in the Public Domain (or CC0 licensed, at your option.)

   Unless required by applicable law or agreed to in writing, this
   software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied.
*/
#include <stdio.h>
#include <stdlib.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "driver/gpio.h"
#include "driver/adc.h"
#include "esp_adc_cal.h"
#include "sensor/sensor_record.h"
#include "adc_reader.h"
#include <esp_log.h>

static const adc_channel_t initial_ph_channel = ADC_CHANNEL_0;
static const adc_channel_t final_ph_channel = ADC_CHANNEL_1;

const static char* TAG = "PH_READER_REAL";

/*
7955 - 4.0
7184 - 8.8

m = (8.8 - 4.0) / (7184 - 7955)
4.0 = 7955 * -(0.00622) + b
b = 4.0 - 7955 * -(0.00622) = +- 53.4801

ph = ADC * m + b

8.8 = 7184 * -0.00622 + 53.4801
*/

const double m = (8.8 - 4.0) / (7184 - 7955);
const double b = 4.0 - 7955 * m;

int read_initial_ph_record(struct sensor_record *sensor_record)
{
    ESP_LOGE(TAG, "Reading initial PH");
  
    int adc_reading = read_adc(initial_ph_channel);

    float ph = adc_reading * m + b;

    // sensor_record -> sensor_name = "ph";
    sensor_record -> value = ph;

    return 0;
}

int read_final_ph_record(struct sensor_record *sensor_record)
{
    ESP_LOGE(TAG, "Reading final PH");
  
    int adc_reading = read_adc(final_ph_channel);

    float ph = adc_reading * m + b;

    // sensor_record -> sensor_name = "ph";
    sensor_record -> value = ph;

    return 0;
}