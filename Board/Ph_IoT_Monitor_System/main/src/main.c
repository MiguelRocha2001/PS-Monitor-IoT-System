#include <string.h>
#include <stdlib.h>
#include <driver/gpio.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "freertos/event_groups.h"
#include "esp_log.h"
#include "nvs_flash.h"

#include "esp_touch_util.h"
#include "wifi_connect_util.h"
#include "nvs_util.h"
#include "pushingbox_util.h"
#include "sdkconfig.h"

#include "ph_reader_fake.h"
#include "broker_util.h"


#define GPIO_RESET_PIN (CONFIG_GPIO_RESET_PIN)

const static char* TAG = "MAIN";

void app_main(void) {

//    delete_saved_wifi();
//    delete_device_id();

    ESP_ERROR_CHECK(nvs_flash_init());
    gpio_set_direction(GPIO_RESET_PIN, GPIO_MODE_DEF_INPUT);

    char* deviceID;

    if (gpio_get_level(GPIO_RESET_PIN) == 0) { // if LOW normal behavior, if HIGH reset memory
        ESP_LOGE(TAG, "normal behavior");

        wifi_config_t wifiConfig;

        if (get_saved_wifi(&wifiConfig) == ESP_OK && get_device_id(&deviceID) == ESP_OK ) {
            if(!connect_to_wifi(wifiConfig)) esp_touch_helper(&deviceID);
        } else {
            esp_touch_helper(&deviceID);
        }

    } else {
        ESP_LOGE(TAG, "Resetting params");
        delete_saved_wifi();
        delete_device_id();

        esp_touch_helper(&deviceID);
    }

    struct ph_record ph_record;
    while (1) {
        read_ph(&ph_record);
        send_ph_value(&ph_record);
    }
    
    send_notification(deviceID);
}