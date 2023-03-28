#include <esp_sleep.h>
#include <esp_log.h>

const static char* TAG = "DEEP_SLEEP";

void start_deep_sleep(long sleep_time) {
    esp_sleep_enable_timer_wakeup(sleep_time);
    esp_deep_sleep_start();
}