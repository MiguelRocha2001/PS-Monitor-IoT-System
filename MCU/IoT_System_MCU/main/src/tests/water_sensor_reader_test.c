/* Simple Read Example

   This example code is in the Public Domain (or CC0 licensed, at your option.)

   Unless required by applicable law or agreed to in writing, this
   software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied.
*/
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "driver/gpio.h"
#include "sdkconfig.h"
#include "esp_log.h"
#include <nvs_flash.h>
#include <esp_sleep.h>
#include <esp_log.h>
#include "adc_reader.h"

#define WATER_SENSOR_POWER_PIN GPIO_NUM_11
#define WATER_SENSOR_STABILIZATION_TIME 1000 * 3 // 1 minute
#define TAG "WATER_SENSOR_READER_TEST"

static const adc_channel_t channel = ADC_CHANNEL_2;


void app_main()
{
    ESP_ERROR_CHECK(nvs_flash_init());

    gpio_set_direction(WATER_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    gpio_set_level(WATER_SENSOR_POWER_PIN, 1); // power on sensors
    ESP_LOGE(TAG, "Water sensor powered on. Waiting for stabilization...");
    vTaskDelay(pdMS_TO_TICKS(WATER_SENSOR_STABILIZATION_TIME));

    float i = 0;
    while (1)
    {
        int adc_reading = read_adc(channel);
        int Vout = adc_reading * 2500 / 8191; // it's not necessary to check Voltage

        printf("%f, %d, %d\n", i, adc_reading, Vout);

        vTaskDelay(pdMS_TO_TICKS(100));
        i = i + 0.1;
    }

}