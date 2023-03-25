#include <string.h>
#include <stdlib.h>
#include <driver/gpio.h>
#include <esp_sleep.h>
#include <nvs_flash.h>
#include <esp_log.h>

#define GPIO_RESET_PIN (CONFIG_GPIO_RESET_PIN)

const static char* TAG = "MAIN";

RTC_DATA_ATTR float ph_values[1000];

struct ph_record ph_record;
void app_main(void) {
    ESP_ERROR_CHECK(nvs_flash_init());

    printDeepSleepWokeCause();

    ESP_LOGE(TAG, "Reading pH...");
    read_ph(&ph_record);

    ESP_LOGE(TAG, "Storing pH...");
    store_ph_in_RTC_memory(&ph_record);
}

void store_ph_in_RTC_memory(struct ph_record* ph_record) {
    ESP_LOGE(TAG, "Storing pH...");
    for (int i = 0; i < 1000; i++) {
        if (ph_values[i] == 0) {
            ph_values[i] = ph_record->value;
            break;
        }
    }
}