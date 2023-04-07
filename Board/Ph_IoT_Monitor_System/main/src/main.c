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
#include "time_util.h"

const static char* TAG = "MAIN";

const static long READ_PH_INTERVAL = 1000000 * 0.3; // 3 seconds
const static long LONG_SLEEP_TIME = 1000000 * 3; // 10 seconds

RTC_DATA_ATTR struct ph_records_struct ph_records;

void printDeepSleepWokeCause(esp_sleep_wakeup_cause_t cause) {
    if (cause == ESP_SLEEP_WAKEUP_TIMER) {
        ESP_LOGI(TAG, "Woke up from timer");
    } else {
        ESP_LOGI(TAG, "Woke up duo to unknown reason");
    }
}

void store_ph_in_RTC_memory(struct ph_record* ph_record) {
    ESP_LOGE(TAG, "Storing pH...");
    if (ph_records.index < MAX_PH_VALUES) {
        ph_records.ph_values[ph_records.index].value = ph_record->value;
        ph_records.ph_values[ph_records.index].timestamp = ph_record->timestamp;

        ph_records.index++;
    } else {
        ESP_LOGE(TAG, "No more space in RTC memory");
    }
}

int is_ph_reading_complete() {
    ESP_LOGE(TAG, "Checking if pH reading is complete...");
    return ph_records.index == MAX_PH_VALUES;
}

void print_ph_values() {
    ESP_LOGE(TAG, "Printing pH values...");
    for (int i = 0; i < MAX_PH_VALUES; i++) {
        if (ph_records.ph_values[i].value != 0) {
            ESP_LOGE(TAG, "pH value: %f", ph_records.ph_values[i].value);
            ESP_LOGE(TAG, "Timestamp: %d", ph_records.ph_values[i].timestamp);
        }
    }
}

void send_ph_values(esp_mqtt_client_handle_t client, char* deviceID) {
    ESP_LOGE(TAG, "Sending pH values...");
    for (int i = 0; i < MAX_PH_VALUES; i++) {
        if (ph_records.ph_values[i].value != 0) {
            mqtt_send_ph(client, &ph_records.ph_values[i], deviceID);
            // delay necessary. Without it, the Backend server will not receive all messages. Not sure why...
            vTaskDelay(300 / portTICK_PERIOD_MS);
        }
    }
}

void erase_ph_values() {
    ESP_LOGE(TAG, "Erasing pH values...");
    for (int i = 0; i < MAX_PH_VALUES; i++) {
        ph_records.ph_values[i].value = 0;
        ph_records.ph_values[i].timestamp = 0;
    }
    ph_records.index = 0;
}

void setup_wifi(void) {
    ESP_LOGE(TAG, "Setting up WiFi...");

    char* deviceID;
    wifi_config_t wifiConfig;

    esp_touch_helper(&deviceID);

    ESP_LOGE(TAG, "Finished setting up WiFi");
}

void sendWaterAlert(esp_mqtt_client_handle_t client, int timestamp, char* deviceID) {
    ESP_LOGE(TAG, "Sending water alert...");
    mqtt_send_water_alert(client, timestamp, deviceID);
}

/**
 * The program starts here.
 * It will read the pH value every 0.3 seconds and store it in RTC memory.
 * After 5 readings, it will send the values to the MQTT broker and go to deep sleep for 3 seconds.
 * For some unknown reason, the MQTT broker does not receive all messages, the first reading round.
*/
void app_main(void) {
    ESP_LOGE(TAG, "Starting app_main...");
    ESP_ERROR_CHECK(nvs_flash_init());

    char* deviceID;
    get_device_id(&deviceID);

    esp_sleep_wakeup_cause_t cause = esp_sleep_get_wakeup_cause();
    printDeepSleepWokeCause(cause);

    // Woker duo to water sensor
    if (cause == ESP_SLEEP_WAKEUP_EXT0)
    {
        ESP_LOGE(TAG, "Woke up from water sensor");

        setup_wifi();
        esp_mqtt_client_handle_t client = setup_mqtt();

        int current_timestamp = getNowTimestamp(); // get current time
        sendWaterAlert(client, current_timestamp, deviceID);
    } 
    // Normal woke up
    else {
        print_ph_values();

        if (is_ph_reading_complete()) {
            setup_wifi();
            esp_mqtt_client_handle_t client = setup_mqtt();

            send_ph_values(client, deviceID);
            erase_ph_values();

            start_deep_sleep(LONG_SLEEP_TIME);
        } else {
            setup_wifi(); // to get the current time

            struct ph_record ph_record;
            read_ph(&ph_record);

            store_ph_in_RTC_memory(&ph_record);

            start_deep_sleep(READ_PH_INTERVAL);
        }
    }
}