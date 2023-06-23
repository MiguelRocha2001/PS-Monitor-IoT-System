#include <string.h>
#include <stdlib.h>
#include <driver/gpio.h>
#include <esp_sleep.h>
#include <nvs_flash.h>
#include <esp_log.h>
#include "wifi_connect_util.h"
#include <mqtt_client.h>
#include <sys/time.h>
#include <deep_sleep.h>
#include "wifi_connect_util.h"
#include "nvs_util.h"
#include "esp_touch_util.h"
#include "time_util.h"
#include "sensor/sensor_reader.h"
#include "mqtt_util.h"
#include "dht11.h"
#include "adc_reader.h"

#define TIME 10 // in seconds
#define TIME_IN_MS TIME * 1000

#define DHT11_SENSOR_POWER_PIN GPIO_NUM_15
#define PH_SENSOR_POWER_PIN GPIO_NUM_13
#define WATER_FLOW_SENSOR_POWER_PIN GPIO_NUM_12
#define WATER_SENSOR_POWER_PIN GPIO_NUM_11

RTC_DATA_ATTR int action = 0;

const static char* TAG = "MAIN";

void setup_wifi(void) 
{
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

void active_mode_iddle()
{
    ESP_LOGW(TAG, "Active mode iddle");
    vTaskDelay(TIME_IN_MS / portTICK_PERIOD_MS);
}

void active_mode_with_wifi_iddle()
{
    setup_wifi();
    ESP_LOGW(TAG, "Active mode with wifi iddle");
    vTaskDelay(TIME_IN_MS / portTICK_PERIOD_MS);
}

// assumes wifi is already connected
void active_mode_with_wifi_and_sending_mqtt()
{
    setup_wifi();
    esp_mqtt_client_handle_t mqtt_client = setup_mqtt();
    struct sensor_record record;
    record.value = 30.0f;
    record.timestamp = 1234567;
    char* device_id = "test-device-id";

    ESP_LOGW(TAG, "Starting iterations...");
    for(int i = 0; i < TIME; i++)
    {
        mqtt_send_sensor_record(mqtt_client, &record, device_id, "initial-ph");
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }
    ESP_LOGW(TAG, "Iterations completed");
}

void power_on_dht11() 
{
    // Configure the GPIO pin as output
    gpio_set_direction(DHT11_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    // Set the GPIO pin to HIGH
    gpio_set_level(DHT11_SENSOR_POWER_PIN, 1);

    ESP_LOGW(TAG, "Powering on DHT11...");
    vTaskDelay(TIME_IN_MS / portTICK_PERIOD_MS);

    gpio_set_level(DHT11_SENSOR_POWER_PIN, 0);
}

void power_on_dht11_and_make_readings() 
{
    // Configure the GPIO pin as output
    gpio_set_direction(DHT11_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    // Set the GPIO pin to HIGH
    gpio_set_level(DHT11_SENSOR_POWER_PIN, 1);

    DHT11_init(GPIO_NUM_9);

    ESP_LOGW(TAG, "Reading from DHT11...");
    for(int i = 0; i < TIME; i++)
    {
        DHT11_read();
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }
    gpio_set_level(DHT11_SENSOR_POWER_PIN, 0);
}

void power_on_water_sensor()
{
    gpio_set_direction(WATER_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    gpio_set_level(WATER_SENSOR_POWER_PIN, 1); // power on sensors

    ESP_LOGW(TAG, "Powering on water sensor...");
    vTaskDelay(TIME_IN_MS / portTICK_PERIOD_MS);

    gpio_set_level(WATER_SENSOR_POWER_PIN, 0);
}

void power_on_and_read_from_water_sensor()
{
    gpio_set_direction(WATER_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    gpio_set_level(WATER_SENSOR_POWER_PIN, 1); // power on sensors

    static const adc_channel_t channel = ADC_CHANNEL_2;

    ESP_LOGW(TAG, "Reading from water sensor...");
    for(int i = 0; i < TIME; i++)
    {
        read_adc(channel);
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }

    gpio_set_level(WATER_SENSOR_POWER_PIN, 0);
}

/**
 * Active mode iddle;
 * Active mode wifi iddle;
 * Active mode wifi sending mqtt;
 * Power on DHT11;
 * Power on DHT11 and make readings;
 * Power on water sensor;
 * Power on and read from water sensor;
 * Deep sleep
*/
void app_main(void) 
{
    // start_deep_sleep(23);

    switch (action)
    {
    case 0:
        active_mode_iddle();
        break;
    case 1:
        active_mode_with_wifi_iddle();
        break;
    case 2:
        active_mode_with_wifi_and_sending_mqtt();
        break;
    case 3:
        power_on_dht11();
        break;
    case 4:
        power_on_dht11_and_make_readings();
        break;
    case 5:
        power_on_water_sensor();
        break;
    case 6:
        power_on_and_read_from_water_sensor();
        break;
    default:
        break;
    }

    action++;
    
    start_deep_sleep(3);
}