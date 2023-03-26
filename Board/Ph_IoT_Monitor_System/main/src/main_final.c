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

#define GPIO_RESET_PIN (CONFIG_GPIO_RESET_PIN)

const static char* TAG = "MAIN";

const static long READ_PH_INTERVAL = 1000000 * 3; // 3 seconds
const static long LONG_SLEEP_TIME = 1000000 * 10; // 10 seconds

#define MAX_PH_VALUES 5
RTC_DATA_ATTR struct ph_record* ph_values[MAX_PH_VALUES];

void store_ph_in_RTC_memory(struct ph_record* ph_record) {
    ESP_LOGE(TAG, "Storing pH...");
    for (int i = 0; i < MAX_PH_VALUES; i++) {
        if (ph_values[i] == 0) {
            ph_values[i] = ph_record;
            return;
        }
    }
    ESP_LOGE(TAG, "No more space in RTC memory");
}

int is_ph_reading_complete() {
    for (int i = 0; i < MAX_PH_VALUES; i++) {
        if (ph_values[i] == 0) {
            return 0;
        }
    }
    return 1;
}

void print_ph_values() {
    ESP_LOGE(TAG, "Printing pH values...");
    for (int i = 0; i < MAX_PH_VALUES; i++) {
        if (ph_values[i] != 0) {
            ESP_LOGE(TAG, "pH value: %f; timestamp: %d", ph_values[i]->value, ph_values[i]->timestamp);
        }
    }
}

void send_ph_values(esp_mqtt_client_handle_t client) {
    ESP_LOGE(TAG, "Sending pH values...");
    for (int i = 0; i < MAX_PH_VALUES; i++) {
        if (ph_values[i] != 0) {
            mqtt_send_ph(client, ph_values[i]);
        }
    }
}

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

    printDeepSleepWokeCause();

    print_ph_values();

    struct ph_record ph_record;
    read_ph(&ph_record);

    store_ph_in_RTC_memory(&ph_record);

    if (is_ph_reading_complete())
    {
        setup_wifi();

        esp_mqtt_client_handle_t client = setup_mqtt();

        send_ph_values(client);

        // TODO: send data to MQTT broker
        start_deep_sleep(LONG_SLEEP_TIME);
    } else {
        start_deep_sleep(READ_PH_INTERVAL);
    }
}