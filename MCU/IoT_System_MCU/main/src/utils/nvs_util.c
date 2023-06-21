#include <stdio.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_system.h"
#include "nvs_flash.h"
#include "nvs.h"
#include "nvs_util.h"
#include <string.h>
#include <esp_wifi_types.h>
#include "esp_log.h"

static const char TAG[] = "NVS_UTIL";


esp_err_t init_nvs() {
    // Initialize NVS
    esp_err_t err = nvs_flash_init();
    if (err == ESP_ERR_NVS_NO_FREE_PAGES || err == ESP_ERR_NVS_NEW_VERSION_FOUND) {
        // NVS partition was truncated and needs to be erased
        // Retry nvs_flash_init
        ESP_ERROR_CHECK(nvs_flash_erase());
        err = nvs_flash_init();
    }
    ESP_ERROR_CHECK( err );
    return err;
}

esp_err_t open_nvs(const char* namespace, nvs_handle_t* my_handle) {

    // Open
    esp_err_t err = nvs_open(namespace, NVS_READWRITE, my_handle);

    if (err != ESP_OK) {
        ESP_LOGE(TAG, "Error (%s) opening NVS!", esp_err_to_name(err));
        return err;
    }

    return err;
}


esp_err_t get_saved_wifi(wifi_config_t* wifi_config) {
    esp_err_t err = init_nvs();

    if (err != ESP_OK) return err;

    nvs_handle_t my_handle;
    err = open_nvs("saved_params", &my_handle);

    if (err != ESP_OK) return err;


    size_t required_size = 0;  // value will default to 0, if not set yet in NVS
    err = nvs_get_blob(my_handle, "saved_wifi", NULL, &required_size);

    if (err != ESP_OK && err != ESP_ERR_NVS_NOT_FOUND) return err;


    if (required_size == 0) {
        ESP_LOGE(TAG, "There is no saved wifi!");
        return err;
    } else {

        err = nvs_get_blob(my_handle, "saved_wifi", wifi_config, &required_size);

        if (err != ESP_OK) {
            return err;
        }

        return err;
    }
}

esp_err_t set_saved_wifi(wifi_config_t* wifi_config) {
    esp_err_t err = init_nvs();
    nvs_handle_t my_handle;

    if (err != ESP_OK) return err;

    err = open_nvs("saved_params", &my_handle);

    if (err != ESP_OK) return err;

    size_t required_size = sizeof(wifi_config_t);

    err = nvs_set_blob(my_handle, "saved_wifi", wifi_config, required_size);

    if (err != ESP_OK) return err;

    // Commit
    err = nvs_commit(my_handle);
    if (err != ESP_OK) return err;

    // Close
    nvs_close(my_handle);
    return err;
}

esp_err_t delete_saved_wifi() {
    esp_err_t err = init_nvs();
    nvs_handle_t my_handle;

    if (err != ESP_OK) return err;

    err = open_nvs("saved_params", &my_handle);

    if (err != ESP_OK) return err;

    err = nvs_erase_key(my_handle, "saved_wifi");
    if (err != ESP_OK) return err;

    // Commit
    err = nvs_commit(my_handle);
    if (err != ESP_OK) return err;

    // Close
    nvs_close(my_handle);
    return err;
}

esp_err_t get_saved_ph_calibration_timing(int* timing) {
    esp_err_t err = init_nvs();

    if (err != ESP_OK) return err;

    nvs_handle_t my_handle;
    err = open_nvs("saved_params", &my_handle);

    if (err != ESP_OK) return err;


    size_t required_size = 0;  // value will default to 0, if not set yet in NVS
    err = nvs_get_blob(my_handle, "ph_cal_time", NULL, &required_size);

    if (err != ESP_OK && err != ESP_ERR_NVS_NOT_FOUND) return err;


    if (required_size == 0) {
        ESP_LOGE(TAG, "There is no saved ph calibration timing!");
        return err;
    } else {

        err = nvs_get_blob(my_handle, "ph_cal_time", timing, &required_size);

        if (err != ESP_OK) {
            return err;
        }

        return err;
    }
}

esp_err_t set_saved_ph_calibration_timing(int timing) {
    esp_err_t err = init_nvs();
    nvs_handle_t my_handle;

    if (err != ESP_OK) return err;

    err = open_nvs("saved_params", &my_handle);

    if (err != ESP_OK) return err;

    size_t required_size = sizeof(int);

    err = nvs_set_blob(my_handle, "ph_cal_time", &timing, required_size);

    if (err != ESP_OK) return err;

    // Commit
    err = nvs_commit(my_handle);
    if (err != ESP_OK) return err;

    // Close
    nvs_close(my_handle);
    return err;
}

esp_err_t set_saved_dht11_calibration_timing(int timing) {
    esp_err_t err = init_nvs();
    nvs_handle_t my_handle;

    if (err != ESP_OK) return err;

    err = open_nvs("saved_params", &my_handle);

    if (err != ESP_OK) return err;

    size_t required_size = sizeof(int);

    err = nvs_set_blob(my_handle, "dht11_cal_time", &timing, required_size);

    if (err != ESP_OK) return err;

    // Commit
    err = nvs_commit(my_handle);
    if (err != ESP_OK) return err;

    // Close
    nvs_close(my_handle);
    return err;
}

esp_err_t get_saved_dht11_calibration_timing(int* timing) {
    esp_err_t err = init_nvs();

    if (err != ESP_OK) return err;

    nvs_handle_t my_handle;
    err = open_nvs("saved_params", &my_handle);

    if (err != ESP_OK) return err;


    size_t required_size = 0;  // value will default to 0, if not set yet in NVS
    err = nvs_get_blob(my_handle, "dht11_cal_time", NULL, &required_size);

    if (err != ESP_OK && err != ESP_ERR_NVS_NOT_FOUND) return err;


    if (required_size == 0) {
        ESP_LOGE(TAG, "There is no saved ph calibration timing!");
        return err;
    } else {

        err = nvs_get_blob(my_handle, "dht11_cal_time", timing, &required_size);

        if (err != ESP_OK) {
            return err;
        }

        return err;
    }
}


esp_err_t get_device_id(char** deviceID) {
    esp_err_t err = init_nvs();

    if (err != ESP_OK) return err;

    nvs_handle_t my_handle;
    err = open_nvs("saved_params", &my_handle);

    if (err != ESP_OK) return err;


    size_t required_size = 0;  // value will default to 0, if not set yet in NVS
    err = nvs_get_str(my_handle, "device_id", NULL, &required_size);

    if (err != ESP_OK && err != ESP_ERR_NVS_NOT_FOUND) return err;


    if (required_size == 0) {
        ESP_LOGE(TAG, "There is no saved deviceID!");
        return err;
    } else {
        *deviceID = malloc(required_size);

        err = nvs_get_str(my_handle, "device_id", *deviceID, &required_size);

        if (err != ESP_OK) {
            return err;
        }

        return err;
    }
}

esp_err_t set_device_id(char* deviceID) {
    esp_err_t err = init_nvs();
    nvs_handle_t my_handle;

    if (err != ESP_OK) return err;

    err = open_nvs("saved_params", &my_handle);

    if (err != ESP_OK) return err;

    err = nvs_set_str(my_handle, "device_id", deviceID);

    if (err != ESP_OK) return err;

    // Commit
    err = nvs_commit(my_handle);
    if (err != ESP_OK) return err;

    // Close
    nvs_close(my_handle);
    return err;
}

esp_err_t delete_device_id() {
    esp_err_t err = init_nvs();
    nvs_handle_t my_handle;

    if (err != ESP_OK) return err;

    err = open_nvs("saved_params", &my_handle);

    if (err != ESP_OK) return err;

    err = nvs_erase_key(my_handle, "device_id");
    if (err != ESP_OK) return err;

    // Commit
    err = nvs_commit(my_handle);
    if (err != ESP_OK) return err;

    // Close
    nvs_close(my_handle);
    return err;
}