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

#include "ph_reader_fake.h"
#include "broker_util.h"
#include "wifi_connect_util.h"
#include "nvs_util.h"
#include "esp_touch_util.h"
#include "ph_values_struct.h"

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

    ESP_LOGE(TAG, "Finished setting up WiFi");
}

void app_main(void) {
    ESP_ERROR_CHECK(nvs_flash_init());

    setup_wifi();
    esp_mqtt_client_handle_t client = setup_mqtt();
    
    struct ph_record ph_record;
    while (1) {
        read_ph(&ph_record);
        ESP_LOGI(TAG, "Value: %f, Timestamp: %u", ph_record.value, ph_record.timestamp);
        mqtt_send_ph(client, &ph_record);

        // sleep for 2 seconds
        vTaskDelay(2000 / portTICK_PERIOD_MS);
    }
}