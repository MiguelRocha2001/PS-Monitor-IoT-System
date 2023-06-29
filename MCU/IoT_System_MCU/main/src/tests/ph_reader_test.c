#include <string.h>
#include "sensor/sensor_reader.h"
#include "sensor/ph_reader.h"
#include "sensor/sensor_record.h"
#include <esp_log.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "driver/gpio.h"
#include "sdkconfig.h"
#include "time_util.h"
#include <esp_system.h>

#define PH_SENSOR_POWER_PIN GPIO_NUM_13

const static char* TAG = "MAIN";

void app_main(void) {
    // sleep
    // vTaskDelay(7000 / portTICK_PERIOD_MS);

    gpio_set_direction(PH_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    gpio_set_level(PH_SENSOR_POWER_PIN, 1); // power on sensors
    ESP_LOGI(TAG, "pH sensor powered on. Waiting for stabilization...");

    // vTaskDelay(7000 / portTICK_PERIOD_MS);

    struct sensor_record sensor_record;
    while(1)
    {
        read_initial_ph_record(&sensor_record);
        ESP_LOGI(TAG, "Value: %f", sensor_record.value);
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }
}