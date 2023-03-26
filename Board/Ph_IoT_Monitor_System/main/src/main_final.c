#include <string.h>
#include <stdlib.h>
#include <driver/gpio.h>
#include <esp_sleep.h>
#include <nvs_flash.h>
#include <esp_log.h>

#include <ph_reader_fake.h>
#include <deep_sleep.h>

#define GPIO_RESET_PIN (CONFIG_GPIO_RESET_PIN)

const static char* TAG = "MAIN";

const static long SLEEP_TIME = 1000000 * 3; // 3 seconds

RTC_DATA_ATTR float ph_values[1000];

void store_ph_in_RTC_memory(struct ph_record* ph_record) {
    ESP_LOGE(TAG, "Storing pH...");
    for (int i = 0; i < 1000; i++) {
        if (ph_values[i] == 0) {
            ph_values[i] = ph_record->value;
            return
        }
    }
    ESP_LOGE(TAG, "No more space in RTC memory");
}

void print_ph_values() {
    ESP_LOGE(TAG, "Printing pH values...");
    for (int i = 0; i < 1000; i++) {
        if (ph_values[i] != 0) {
            ESP_LOGE(TAG, "pH value: %f", ph_values[i]);
        }
    }
}

void app_main(void) {
    ESP_ERROR_CHECK(nvs_flash_init());

    printDeepSleepWokeCause();

    print_ph_values();

    struct ph_record ph_record;
    read_ph(&ph_record);

    store_ph_in_RTC_memory(&ph_record);

    start_deep_sleep(SLEEP_TIME);
}