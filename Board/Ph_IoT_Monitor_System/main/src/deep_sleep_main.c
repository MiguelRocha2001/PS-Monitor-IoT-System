#include <string.h>
#include <stdlib.h>
#include <driver/gpio.h>
#include <esp_sleep.h>
#include <nvs_flash.h>
#include <esp_log.h>

#define GPIO_RESET_PIN (CONFIG_GPIO_RESET_PIN)

const static char* TAG = "MAIN";

RTC_DATA_ATTR uint8_t counter = 0;

const static long SLEEP_TIME = 1000000 * 5; // 5 seconds

void app_main(void) {
    ESP_ERROR_CHECK(nvs_flash_init());

    esp_sleep_wakeup_cause_t cause = esp_sleep_get_wakeup_cause();
    if (cause == ESP_SLEEP_WAKEUP_TIMER) {
        ESP_LOGI(TAG, "Woke up from timer");
    } else {
        ESP_LOGI(TAG, "Woke up duo to unknown reason");
    }

    counter++;

    ESP_LOGI(TAG, "Counter: %d", counter);
    
    ESP_LOGI(TAG, "Starting up");
    esp_sleep_enable_timer_wakeup(SLEEP_TIME);

    ESP_LOGI(TAG, "Going to sleep");
    esp_deep_sleep_start();

    ESP_LOGI(TAG, "Should not be here");
}