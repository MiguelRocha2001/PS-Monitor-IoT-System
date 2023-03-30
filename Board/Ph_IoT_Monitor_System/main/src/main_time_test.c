#include <string.h>
#include <stdlib.h>
#include <driver/gpio.h>
#include <esp_sleep.h>
#include <nvs_flash.h>
#include <esp_log.h>
#include "wifi_connect_util.h"
#include <mqtt_client.h>

#include <ph_reader_fake.h>
#include <deep_sleep.h>

#include "time_utils.h"

const static char* TAG = "MAIN";

void app_main(void) {
    ESP_ERROR_CHECK(nvs_flash_init());

    char strftime_buf[64];
    get_current_time(strftime_buf);

    ESP_LOGI(TAG, "The current date/time in Lisbon is: %s", strftime_buf);
}