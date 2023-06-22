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

#define DHT11_SENSOR_POWER_PIN GPIO_NUM_15
#define PH_SENSOR_POWER_PIN GPIO_NUM_13
#define WATER_FLOW_SENSOR_POWER_PIN GPIO_NUM_12
#define WATER_SENSOR_POWER_PIN GPIO_NUM_11

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

void active_mode_without_wifi()
{
    ESP_LOGW(TAG, "Starting iterations...");
    for(int i = 0; i < 10; i++)
    {
        // ESP_LOGI(TAG, "Doing nothing...");
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }
    ESP_LOGW(TAG, "Iterations completed");
}

void active_mode_with_wifi()
{
    setup_wifi();
    ESP_LOGW(TAG, "Starting iterations...");
    for(int i = 0; i < 10; i++)
    {
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }
    ESP_LOGW(TAG, "Iterations completed");
}

void active_mode_with_wifi_and_sending_mqtt()
{
    setup_wifi();
    esp_mqtt_client_handle_t mqtt_client = setup_mqtt();
    struct sensor_record record;
    record.value = 30.0f;
    record.timestamp = 1234567;
    char* device_id = "test-device-id";

    ESP_LOGW(TAG, "Starting iterations...");
    for(int i = 0; i < 10; i++)
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

    ESP_LOGW(TAG, "Starting iterations...");
    for(int i = 0; i < 10; i++)
    {
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }
    ESP_LOGW(TAG, "Iterations completed");
}

void power_on_dht11_and_make_readings() 
{
    // Configure the GPIO pin as output
    gpio_set_direction(DHT11_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    // Set the GPIO pin to HIGH
    gpio_set_level(DHT11_SENSOR_POWER_PIN, 1);

    DHT11_init(GPIO_NUM_9);

    ESP_LOGW(TAG, "Starting iterations...");
    for(int i = 0; i < 10; i++)
    {
        DHT11_read();
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }
    ESP_LOGW(TAG, "Iterations completed");
}

void power_on_water_sensor()
{
    gpio_set_direction(WATER_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    gpio_set_level(WATER_SENSOR_POWER_PIN, 1); // power on sensors

    ESP_LOGW(TAG, "Starting iterations...");
    for(int i = 0; i < 10; i++)
    {
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }
    ESP_LOGW(TAG, "Iterations completed");
}

void power_on_and_read_from_water_sensor()
{
    gpio_set_direction(WATER_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    gpio_set_level(WATER_SENSOR_POWER_PIN, 1); // power on sensors

    static const adc_channel_t channel = ADC_CHANNEL_2;

    ESP_LOGW(TAG, "Starting iterations...");
    for(int i = 0; i < 10; i++)
    {
        read_adc(channel);
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }
    ESP_LOGW(TAG, "Iterations completed");
}

void read_from_dht11()
{
    int i = 0;
    while(1) {
        printf("%d,%d,%d\n", i, DHT11_read().temperature, DHT11_read().humidity);
        /*
        printf("Temperature is %d \n", DHT11_read().temperature);
        printf("Humidity is %d\n", DHT11_read().humidity);
        printf("Status code is %d\n", DHT11_read().status);
        */
        
        // sleep for 1 second
        vTaskDelay(100 / portTICK_PERIOD_MS);
        i = i + 1;
    }
}

void app_main(void) 
{
    // start_deep_sleep(10);
    ESP_ERROR_CHECK(nvs_flash_init());

    ESP_LOGW(TAG, "Starting...");
    vTaskDelay(2000 / portTICK_PERIOD_MS);
    
    time_t start;
    time_t end;

    //time(&start);
    //ESP_LOGI(TAG, "Start: %d", start);
    power_on_and_read_from_water_sensor();
    //time(&end);
    //ESP_LOGI(TAG, "End: %d", end);

    
    /*
    int today_timestamp = getTodayTimestamp();
    ESP_LOGI(TAG, "Timestamp: %d", today_timestamp);

    int now_timestamp = getNowTimestamp();
    ESP_LOGI(TAG, "Timestamp: %d", now_timestamp);
    */
   // ESP_LOGI(TAG, "Time taken: %d", end - start);
   //ESP_LOGW(TAG, "Programm ended !!!");

    // active_mode_with_wifi_and_sending_mqtt();
    // active_mode_with_wifi();
    // active_mode_without_wifi();
    // power_dht11();
    // read_from_dht11();
    // power_dht11_off();
   start_deep_sleep(2);
}