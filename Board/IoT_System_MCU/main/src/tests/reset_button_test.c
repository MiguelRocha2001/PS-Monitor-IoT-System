#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "driver/gpio.h"
#include "sdkconfig.h"

void app_main() {
    while (1)
    {
        esp_rom_gpio_pad_select_gpio(GPIO_NUM_3);
        gpio_set_direction(GPIO_NUM_3, GPIO_MODE_INPUT);
        int gpio_value = gpio_get_level(GPIO_NUM_3);
        printf("GPIO 3 value: %d\n", gpio_value);
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }
}
