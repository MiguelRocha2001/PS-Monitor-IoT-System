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

#define SENSOR_POWER_PIN GPIO_NUM_4


void app_main()
{
    ESP_ERROR_CHECK(nvs_flash_init());

     // Configure the GPIO pin as output
    gpio_set_direction(SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);

    /*
    while (1)
    {
        // Set the GPIO pin to HIGH
        gpio_set_level(SENSOR_POWER_PIN, 1);
        
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        
        // Set the GPIO pin to LOW
        gpio_set_level(SENSOR_POWER_PIN, 0);

        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }
    */
    
    

    // rtc_gpio_pullup_en(GPIO_NUM_5);
    esp_sleep_enable_ext0_wakeup(GPIO_NUM_2, 1);

    ESP_LOGI("main", "Going to sleep now");
    esp_deep_sleep_start();
    ESP_LOGI("main", "This will never be printed");
}