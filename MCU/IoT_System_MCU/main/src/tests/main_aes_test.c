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

#include "aes_util.h"

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
    
    const uint8_t key_256[KEY_SIZE_BYTES] = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F};
    esp_aes_context ctx = init_AES(key_256);

    char *plaintext = "Hello World";
    ESP_LOGE(TAG, "Plaintext: %s", plaintext);

    char* res = encrypt_str_AES(ctx, plaintext);
    ESP_LOGE(TAG, "Encrypted: %s", res);

    char* res2 = decrypt_base64_AES(ctx, res);
    ESP_LOGE(TAG, "Decrypted: %s\n", res2);

    char* plaintext2 = "migasrocha1@hotmail.com";
    ESP_LOGE(TAG, "Plaintext: %s", plaintext2);

    char* res3 = encrypt_str_AES(ctx, plaintext2);
    ESP_LOGE(TAG, "Encrypted: %s", res3);

    char* res4 = decrypt_base64_AES(ctx, res3);
    ESP_LOGE(TAG, "Decrypted: %s", res4);

    free_AES(ctx);
}