
esp_mqtt_client_handle_t setup_mqtt();

void mqtt_send_sensor_record(esp_mqtt_client_handle_t client, struct sensor_record1 *sensor_record, char* deviceID, char* topic);

void mqtt_send_water_alert(esp_mqtt_client_handle_t client, int timestamp, char* deviceID);

void mqtt_send_sensor_not_working_alert(esp_mqtt_client_handle_t client, int timestamp, char* deviceID, char** sensors);

void mqtt_send_unknown_woke_up_reason_alert(esp_mqtt_client_handle_t client, int timestamp, char* deviceID);


void mqtt_send_device_wake_up_reason_alert(esp_mqtt_client_handle_t client, int timestamp, char* deviceID, char* wake_up_reason);