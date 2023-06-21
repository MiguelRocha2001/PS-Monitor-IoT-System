#ifndef NVS_UTIL_H
#define NVS_UTIL_H

#include <esp_wifi_types.h>
#include "nvs_flash.h"
#include "nvs.h"

esp_err_t init_nvs();
esp_err_t open_nvs(const char* namespace, nvs_handle_t* my_handle);

esp_err_t get_saved_wifi(wifi_config_t* wifi_config);
esp_err_t set_saved_wifi(wifi_config_t* wifi_config);
esp_err_t delete_saved_wifi();

esp_err_t get_device_id(char** deviceID);
esp_err_t set_device_id(char* deviceID);
esp_err_t delete_device_id();

esp_err_t set_saved_ph_calibration_timing(int timing);
esp_err_t get_saved_ph_calibration_timing(int* timing);
esp_err_t set_saved_dht11_calibration_timing(int timing);
esp_err_t get_saved_dht11_calibration_timing(int* timing);

#endif // AES_UTIL_H