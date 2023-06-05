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
#include <esp_sleep.h>
#include <nvs_flash.h>

#include "dht11.h"

#define SENSOR_POWER_PIN GPIO_NUM_15

void app_main()
{
    ESP_ERROR_CHECK(nvs_flash_init());
    ESP_LOGI("DHT11", "DHT11 test!");

    // Configure the GPIO pin as output
    gpio_set_direction(SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    // Set the GPIO pin to HIGH
    gpio_set_level(SENSOR_POWER_PIN, 1);

    DHT11_init(GPIO_NUM_9);
    ESP_LOGI("DHT11", "DHT11 initialized!");

    // ESP_LOGI("main", "Going to sleep now");
    // esp_deep_sleep_start();

    int i = 0;
    while(i < 100) {
        printf("%d,%d,%d\n", i, DHT11_read().temperature, DHT11_read().humidity);
        /*
        printf("Temperature is %d \n", DHT11_read().temperature);
        printf("Humidity is %d\n", DHT11_read().humidity);
        printf("Status code is %d\n", DHT11_read().status);
        */
        
        // sleep for 1 second
        vTaskDelay(1000 / portTICK_PERIOD_MS);
        i = i + 1;
    }
}