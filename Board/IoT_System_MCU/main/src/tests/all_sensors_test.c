#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "driver/gpio.h"
#include "sdkconfig.h"
#include "esp_log.h"
#include <nvs_flash.h>
#include "sensor/sensor_reader.h"
#include "sensor/ph_reader.h"
#include "sensor/temp_reader.h"
#include "sensor/humidity_reader.h"
#include "sensor/sensor_record.h"
#include "sensor/water_level_reader.h"
#include <esp_sleep.h>

#define SENSOR_POWER_PIN GPIO_NUM_15
#define stability_time 1000 * 3 // 1 minute

/**
 * Read sensor records and store them in the sensor_records_struct.
 * Return 0 if success, 1 if the sensor_records_struct is full, -1 if there is an error with some sensor reading.
*/
int read_sensor_records(sensor_records_struct *sensor_records) {
    vTaskDelay(pdMS_TO_TICKS(stability_time));

    int index = sensor_records->index;
    if (index < MAX_SENSOR_RECORDS) {
        read_start_ph_record(&sensor_records->start_ph_records[index]);
        read_temperature_record(&sensor_records->temperature_records[index]);
        read_humidity_record(&sensor_records->humidity_records[index]);
        read_water_level_record(&sensor_records->water_level_records[index]);
        sensor_records->index = index + 1; // increment index
        return 0;
    }
    return -1;
}

void print_sensor_record(int time, sensor_record *sensor_record) {
    printf("%d, %f\n", time, sensor_record->value);
}

void print_sensor_records(int time, sensor_records_struct *sensor_records) {
    print_sensor_record(time, &sensor_records->start_ph_records[sensor_records->index - 1]);
    print_sensor_record(time, &sensor_records->temperature_records[sensor_records->index - 1]);
    print_sensor_record(time, &sensor_records->humidity_records[sensor_records->index - 1]);
    print_sensor_record(time, &sensor_records->water_level_records[sensor_records->index - 1]);
    printf("\n");
}


void app_main()
{
    ESP_ERROR_CHECK(nvs_flash_init());

    // Configure the GPIO pin as output
    gpio_set_direction(SENSOR_POWER_PIN, GPIO_MODE_OUTPUT);
    // Set the GPIO pin to HIGH
    gpio_set_level(SENSOR_POWER_PIN, 1);

    
    sensor_records_struct sensor_records;

    sensor_records.index = 0;

    int i = 0;
    while(i < 100) {
        read_sensor_records(&sensor_records);
        print_sensor_records(i, &sensor_records);
        
        // sleep for 1 second
        vTaskDelay(3000 / portTICK_PERIOD_MS);
        i = i + 1;
    }
}