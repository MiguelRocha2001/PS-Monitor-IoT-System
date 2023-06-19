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
#include <esp_log.h>
#include "time_util.h"
#include "sensor/sensor_record.h"
#include "adc_reader.h"

#define WATER_SENSOR_POWER_PIN GPIO_NUM_11
#define WATER_SENSOR_STABILIZATION_TIME 1000 * 3 // 1 minute

static const adc_channel_t channel = ADC_CHANNEL_2;

const static char* TAG = "WATER_LEAK_READER_REAL";

int read_water_leak_record()
{
    gpio_set_direction(WATER_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    gpio_set_level(WATER_SENSOR_POWER_PIN, 1); // power on sensors
    ESP_LOGI(TAG, "Water sensor powered on. Waiting for stabilization...");
    vTaskDelay(pdMS_TO_TICKS(WATER_SENSOR_STABILIZATION_TIME));

    ESP_LOGI(TAG, "Reading water leak..."); 

    int adc_reading = read_adc(channel);
    int Vout = adc_reading * 2500 / 8191; // it's not necessary to check Voltage

    gpio_set_level(WATER_SENSOR_POWER_PIN, 0); // power off sensors
    ESP_LOGI(TAG, "Water sensor powered off.");
    
    return Vout > 500; // just check if adc is greater than 1638
}