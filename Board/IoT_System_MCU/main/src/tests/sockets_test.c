#include <stdio.h>
#include <string.h>
#include <sys/param.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_wifi.h"
#include "esp_system.h"
#include "esp_event.h"
#include "nvs_flash.h"
#include "lwip/err.h"
#include "lwip/sys.h"
#include "lwip/netdb.h"
#include "lwip/sockets.h"
#include "wifi_connect_util.h"
#include "nvs_util.h"
#include <esp_log.h>
#include "esp_touch_util.h"

#define HOST_IP_ADDR "192.168.1.4" // IP address of the server
#define HOST_PORT 5555 // Port number of the server

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

void send_sensor_data() {
    ESP_LOGE(TAG, "Sending sensor data...");

    int sockfd;
    struct sockaddr_in server_addr;

    // Create a TCP socket
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) {
        printf("Failed to create socket\n");
        vTaskDelay(portMAX_DELAY);
    }

    // Set server address and port
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(HOST_PORT);
    server_addr.sin_addr.s_addr = inet_addr(HOST_IP_ADDR);

    // Connect to the server
    if (connect(sockfd, (struct sockaddr *)&server_addr, sizeof(server_addr)) != 0) {
        printf("Failed to connect to the server\n");
        vTaskDelay(portMAX_DELAY);
    }

    // Sensor data
    int sensorValue = 42; // Replace with your sensor data

    // Convert sensor value to a string
    char data[16];
    sprintf(data, "%d", sensorValue);

    // Send the sensor data
    if (send(sockfd, data, strlen(data), 0) < 0) {
        printf("Failed to send data\n");
    } else {
        printf("Data sent: %s\n", data);
    }

    // Close the socket
    close(sockfd);

    vTaskDelay(portMAX_DELAY);
}

void app_main() {
    // Initialize NVS
    esp_err_t ret = nvs_flash_init();
    if (ret == ESP_ERR_NVS_NO_FREE_PAGES || ret == ESP_ERR_NVS_NEW_VERSION_FOUND) {
        ESP_ERROR_CHECK(nvs_flash_erase());
        ret = nvs_flash_init();
    }
    ESP_ERROR_CHECK(ret);

    // Initialize WiFi
    setup_wifi();

    // Start sending sensor data
    xTaskCreate(send_sensor_data, "send_sensor_data", 4096, NULL, 5, NULL);
}
