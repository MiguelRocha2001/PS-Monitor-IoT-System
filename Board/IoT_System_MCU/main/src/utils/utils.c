#include <stdint.h>
#include <string.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include <freertos/event_groups.h>
#include <sys/unistd.h>
#include "esp_system.h"
#include "esp_log.h"
#include "rsa_util.h"
#include "base64_util.h"
#include "time_util.h"
#include <esp_random.h>
#include <time.h>

static const char *TAG = "Utils";


void restart_esp(int delay_seconds) {
    if (delay_seconds < 0) delay_seconds = 0;

    if (delay_seconds == 0) {
        ESP_LOGI(TAG, "Restarting now!");
    } else if (delay_seconds == 1) {
        ESP_LOGI(TAG, "Restarting in 1 second...");
    } else {
        ESP_LOGI(TAG, "Restarting in %d seconds...", delay_seconds);
    }
    sleep(delay_seconds);
    esp_restart();
}


uint8_t* get_random_array(int len) {
    size_t size = sizeof(uint8_t) * len;
    uint8_t* buf = malloc(size);

    esp_fill_random(buf, size);

    return buf;
}

float generate_random_float()
{
    srand((unsigned int)time(NULL));

    float a = 5.0;
    return ((float)rand()/(float)(RAND_MAX)) * a;
}

int generate_random_int()
{
    srand((unsigned int)time(NULL));

    return rand();
}