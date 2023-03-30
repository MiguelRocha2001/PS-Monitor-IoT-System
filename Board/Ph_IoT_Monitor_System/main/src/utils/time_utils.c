#include <time.h>
#include "esp_log.h"

static const char *TAG = "MAIN";
#define STRFTIME_BUF_SIZE 64

int get_current_time(char *strftime_buf) {
    time_t now;
    struct tm timeinfo;

    time(&now);

    // Set timezone to Lisbon, Portugal Standard Time
    setenv("TZ", "WET0WEST,M3.5.0/01,M10.5.0/02", 1);
    tzset();

    localtime_r(&now, &timeinfo);

    size_t strftime_len = strftime(strftime_buf, STRFTIME_BUF_SIZE, "%Y-%m-%d %H:%M:%S", &timeinfo);
    if (strftime_len == 0) {
        ESP_LOGE(TAG, "strftime failed");
        return -1;
    }
    return 0;
}
