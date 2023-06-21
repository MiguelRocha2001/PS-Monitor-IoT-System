#include <string.h>
#include <stdlib.h>
#include <driver/gpio.h>
#include <esp_sleep.h>
#include <nvs_flash.h>
#include <esp_log.h>
#include "wifi_connect_util.h"
#include <mqtt_client.h>

#include <deep_sleep.h>

#include "wifi_connect_util.h"
#include "nvs_util.h"
#include "esp_touch_util.h"
#include "time_util.h"

const static char* TAG = "MAIN";

void setup_wifi(void) {
    ESP_LOGE(TAG, "Setting up WiFi...");

    char* deviceID;
    wifi_config_t wifiConfig;

    if (get_saved_wifi(&wifiConfig) == ESP_OK && get_device_id(&deviceID) == ESP_OK ) {
        if(!connect_to_wifi(wifiConfig)) esp_touch_helper(&deviceID);
    } else {
        esp_touch_helper(&deviceID);
    }
    
    ESP_LOGE(TAG, "Device ID: %s", deviceID);

    ESP_LOGE(TAG, "Finished setting up WiFi");
}

void app_main(void) {
    ESP_ERROR_CHECK(nvs_flash_init());

    // setup_wifi();

    // delay
    for(int i = 0; i < 20; i++)
    {
        ESP_LOGI(TAG, "Doing nothing...");
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }
    
    int today_timestamp = getTodayTimestamp();
    ESP_LOGI(TAG, "Timestamp: %d", today_timestamp);

    int now_timestamp = getNowTimestamp();
    ESP_LOGI(TAG, "Timestamp: %d", now_timestamp);
    
    /*
    esp_sleep_enable_timer_wakeup(1000000 * 10);
    esp_deep_sleep_start();
    */
}