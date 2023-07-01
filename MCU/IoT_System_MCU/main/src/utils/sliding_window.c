#include <string.h>
#include "sensor/sensor_reader.h"
#include "sensor/ph_reader.h"
#include "sensor/temp_reader.h"
#include "sensor/water_flow_reader.h"
#include "sensor/humidity_reader.h"
#include "sensor/sensor_record.h"
#include <esp_log.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "driver/gpio.h"
#include "sdkconfig.h"
#include "time_util.h"
#include <math.h>
#include <esp_system.h>
#include "nvs_util.h"
#include "dht11.h"

#define ph_window_size 5
#define ph_standard_deviation_threshold 3
#define dht11_window_size 10
#define dht11_standard_deviation_threshold 0.3

#define BETWEEN_READINGS_CALIBRATION 1000 // 1 second

#define DHT11_SENSOR_POWER_PIN GPIO_NUM_15
#define PH_SENSOR_POWER_PIN GPIO_NUM_13

static const char* TAG = "sliding_window";

void calibrate_ph_sensors() {
    gpio_set_direction(PH_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    gpio_set_level(PH_SENSOR_POWER_PIN, 1); // power on sensor

    int time_1_calculated = 0;
    int time_2_calculated = 0;
    sensor_record sensor_record1;
    sensor_record sensor_record2;
    double window1[ph_window_size];
    double window2[ph_window_size];
    int initial_timestamp1 = getNowTimestamp();
    int initial_timestamp2 = getNowTimestamp();
    while (true)
    {
        for(int i = 0; i < ph_window_size; i++)
        {
            if (!time_1_calculated)
            {
                read_initial_ph_record(&sensor_record1);
                window1[i] = sensor_record1.value;
                ESP_LOGW(TAG, "Final pH value 1: %f", sensor_record1.value);
            }
            if (!time_2_calculated)
            {
                read_final_ph_record(&sensor_record2);
                window2[i] = sensor_record2.value;
                ESP_LOGW(TAG, "Final pH value 2: %f", sensor_record2.value);
            }
            vTaskDelay(pdMS_TO_TICKS(BETWEEN_READINGS_CALIBRATION));
        }

        double sum1 = 0;
        double sum2 = 0;
        for (int j = 0; j < ph_window_size; j++) {
            sum1 += window1[j];
            sum2 += window2[j];
        }
        
        double mean1 = sum1 / ph_window_size;
        double mean2 = sum2 / ph_window_size;
        
        double squared_diff_sum1 = 0;
        double squared_diff_sum2 = 0;
        for (int j = 0; j < ph_window_size; j++) {
            double diff1 = window1[j] - mean1;
            squared_diff_sum1 += diff1 * diff1;
            double diff2 = window2[j] - mean2;
            squared_diff_sum2 += diff2 * diff2;
        }
        
        double std_dev1 = sqrt(squared_diff_sum1 / ph_window_size);
        if (std_dev1 <= ph_standard_deviation_threshold)
        {
            int final_timestamp = getNowTimestamp();
            int stabilization_time_in_ms = (final_timestamp - initial_timestamp1) * 1000;

            ESP_LOGW(TAG, "Standard deviation is below threshold %f", std_dev1);

            if (time_2_calculated)
            {
                ESP_ERROR_CHECK(set_saved_ph_calibration_timing(stabilization_time_in_ms));    
                ESP_LOGW(TAG, "Saved stabilization time: %d", stabilization_time_in_ms);
                break;
            }

            time_1_calculated = 1;
        }
        ESP_LOGW(TAG, "Standard deviation: %f", std_dev1);

        double std_dev2 = sqrt(squared_diff_sum2 / ph_window_size);
        if (std_dev2 <= ph_standard_deviation_threshold)
        {
            int final_timestamp = getNowTimestamp();
            int stabilization_time_in_ms = (final_timestamp - initial_timestamp2) * 1000;

            ESP_LOGW(TAG, "Standard deviation is below threshold %f", std_dev2);

            if (time_1_calculated)
            {
                ESP_ERROR_CHECK(set_saved_ph_calibration_timing(stabilization_time_in_ms));    
                ESP_LOGW(TAG, "Saved stabilization time: %d", stabilization_time_in_ms);
                break;
            }

            time_2_calculated = 1;
        }

        for (int i = 0; i < ph_window_size - 1; i++) {
            if (!time_1_calculated)
            {
                window1[i] = window1[i + 1];
            }
            if (!time_2_calculated)
            {
                window2[i] = window2[i + 1];
            }
        }
        read_initial_ph_record(&sensor_record1);
        read_final_ph_record(&sensor_record2);
        if (!time_1_calculated)
        {
            window1[ph_window_size - 1] = sensor_record1.value; // new value
        }
        if (!time_2_calculated)
        {
            window2[ph_window_size - 1] = sensor_record2.value; // new value
        }
    }
    gpio_set_level(PH_SENSOR_POWER_PIN, 0); // power off sensor
}

void calibrate_dht11_sensor() {
    gpio_set_direction(DHT11_SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    gpio_set_level(DHT11_SENSOR_POWER_PIN, 1); // power on sensor
    DHT11_init(GPIO_NUM_9);

    int time_1_calculated = 0;
    int time_2_calculated = 0;
    sensor_record sensor_record1;
    sensor_record sensor_record2;
    double window1[dht11_window_size];
    double window2[dht11_window_size];
    int initial_timestamp1 = getNowTimestamp();
    int initial_timestamp2 = getNowTimestamp();
    while (true)
    {
        for(int i = 0; i < dht11_window_size; i++)
        {
            if (!time_1_calculated)
            {
                read_temperature_record(&sensor_record1);
                window1[i] = sensor_record1.value;
                ESP_LOGW(TAG, "Temperature value 1: %f", sensor_record1.value);
            }
            if (!time_2_calculated)
            {
                read_humidity_record(&sensor_record2);
                window2[i] = sensor_record2.value;
                ESP_LOGW(TAG, "Humidity value 2: %f", sensor_record2.value);
            }
            vTaskDelay(pdMS_TO_TICKS(BETWEEN_READINGS_CALIBRATION));
        }

        double sum1 = 0;
        double sum2 = 0;
        for (int j = 0; j < dht11_window_size; j++) {
            sum1 += window1[j];
            sum2 += window2[j];
        }
        
        double mean1 = sum1 / dht11_window_size;
        double mean2 = sum2 / dht11_window_size;
        
        double squared_diff_sum1 = 0;
        double squared_diff_sum2 = 0;
        for (int j = 0; j < dht11_window_size; j++) {
            double diff1 = window1[j] - mean1;
            squared_diff_sum1 += diff1 * diff1;
            double diff2 = window2[j] - mean2;
            squared_diff_sum2 += diff2 * diff2;
        }
        
        double std_dev1 = sqrt(squared_diff_sum1 / dht11_window_size);
        if (std_dev1 <= dht11_standard_deviation_threshold)
        {
            int final_timestamp = getNowTimestamp();
            int stabilization_time_in_ms = (final_timestamp - initial_timestamp1) * 1000;

            ESP_LOGW(TAG, "Standard deviation is below threshold %f", std_dev1);

            if (time_2_calculated)
            {
                ESP_ERROR_CHECK(set_saved_dht11_calibration_timing(stabilization_time_in_ms));    
                ESP_LOGW(TAG, "Saved stabilization time: %d", stabilization_time_in_ms);
                break;
            }

            time_1_calculated = 1;
        }
        ESP_LOGW(TAG, "Standard deviation: %f", std_dev1);

        double std_dev2 = sqrt(squared_diff_sum2 / dht11_window_size);
        if (std_dev2 <= dht11_standard_deviation_threshold)
        {
            int final_timestamp = getNowTimestamp();
            int stabilization_time_in_ms = (final_timestamp - initial_timestamp2) * 1000;

            ESP_LOGW(TAG, "Standard deviation is below threshold %f", std_dev2);

            if (time_1_calculated)
            {
                ESP_ERROR_CHECK(set_saved_dht11_calibration_timing(stabilization_time_in_ms));    
                ESP_LOGW(TAG, "Saved stabilization time: %d", stabilization_time_in_ms);
                break;
            }

            time_2_calculated = 1;
        }

        for (int i = 0; i < dht11_window_size - 1; i++) {
            if (!time_1_calculated)
            {
                window1[i] = window1[i + 1];
            }
            if (!time_2_calculated)
            {
                window2[i] = window2[i + 1];
            }
        }
        read_temperature_record(&sensor_record1);
        read_humidity_record(&sensor_record2);
        if (!time_1_calculated)
        {
            window1[dht11_window_size - 1] = sensor_record1.value; // new value
        }
        if (!time_2_calculated)
        {
            window2[dht11_window_size - 1] = sensor_record2.value; // new value
        }
    }
    gpio_set_level(DHT11_SENSOR_POWER_PIN, 0); // power off sensor
}