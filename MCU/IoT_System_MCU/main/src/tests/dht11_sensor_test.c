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

#include "dht11.h"

void app_main()
{
    ESP_ERROR_CHECK(nvs_flash_init());
    ESP_LOGI("DHT11", "DHT11 test!");

    DHT11_init(GPIO_NUM_0);
    ESP_LOGI("DHT11", "DHT11 initialized!");

    float i = 0;
    while(i < 100) {
        printf("%d,%d,%f\n", DHT11_read().temperature, DHT11_read().humidity, i);
        /*
        printf("Temperature is %d \n", DHT11_read().temperature);
        printf("Humidity is %d\n", DHT11_read().humidity);
        printf("Status code is %d\n", DHT11_read().status);
        */
        
        // sleep for 1 second
        vTaskDelay(100 / portTICK_PERIOD_MS);
        i = i + 0.1;
    }
}