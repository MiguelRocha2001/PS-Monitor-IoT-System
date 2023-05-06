#include "sensor/sensor_records.h"

esp_mqtt_client_handle_t setup_mqtt();

void mqtt_send_sensor_record1(esp_mqtt_client_handle_t client, struct sensor_record1 *sensor_record, char* deviceID, char* topic);
void mqtt_send_sensor_record2(esp_mqtt_client_handle_t client, struct sensor_record2 *sensor_record, char* deviceID, char* topic);

void mqtt_send_water_alert(esp_mqtt_client_handle_t client, int timestamp, char* deviceID);