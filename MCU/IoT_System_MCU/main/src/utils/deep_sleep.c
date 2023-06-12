#include <esp_sleep.h>
#include <esp_log.h>
#include "driver/rtc_io.h"


#define uS_TO_S_FACTOR 1000000  /* Conversion factor for micro seconds to seconds */

const static char* TAG = "DEEP_SLEEP";

void start_deep_sleep(long sleep_time) {
    rtc_gpio_pulldown_en(GPIO_NUM_8);
    esp_sleep_enable_ext0_wakeup(GPIO_NUM_8, 1);

    esp_sleep_enable_timer_wakeup(sleep_time * uS_TO_S_FACTOR);
    ESP_LOGE(TAG, "Going to sleep for %ld seconds...", sleep_time);
    esp_deep_sleep_start();
}