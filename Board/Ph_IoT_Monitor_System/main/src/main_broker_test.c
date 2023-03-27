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

void app_main(void) {
    ESP_ERROR_CHECK(nvs_flash_init());

    esp_mqtt_client_handle_t client =  mqtt_app_start();
    
    struct ph_record ph_record;
    while (1)
    {
        read_ph(&ph_record);
        mqtt_send_ph(client, &ph_record);

        deep_sleep(1000 * 2);
    }
}