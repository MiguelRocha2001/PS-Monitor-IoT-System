#include "esp_system.h"
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"

void app_main()
{
    // Read the Reset Reason Register
    uint32_t reset_reason = esp_reset_reason();

    // Check the reset reason flags
    if (reset_reason & ESP_RST_UNKNOWN) {
        printf("Reset reason: unknown\n");
    }
    if (reset_reason & ESP_RST_POWERON) {
        printf("Reset reason: power-on reset\n");
    }
    if (reset_reason & ESP_RST_SW) {
        printf("Reset reason: software reset\n");
    }
    if (reset_reason & ESP_RST_PANIC) {
        printf("Reset reason: exception/panic reset\n");
    }
    if (reset_reason & ESP_RST_BROWNOUT) {
        printf("Reset reason: brownout reset\n");
    }
    if (reset_reason & ESP_RST_DEEPSLEEP) {
        printf("Reset reason: deep sleep reset\n");
    }

    vTaskDelay(pdMS_TO_TICKS(2000));
    esp_restart();
}
