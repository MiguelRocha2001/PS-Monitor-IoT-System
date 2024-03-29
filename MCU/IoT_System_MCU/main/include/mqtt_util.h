
esp_mqtt_client_handle_t setup_mqtt();

int try_to_connect_to_broker_if_necessary(esp_mqtt_client_handle_t mqtt_client);

void mqtt_app_terminate(esp_mqtt_client_handle_t client);

void mqtt_send_sensor_record(esp_mqtt_client_handle_t client, struct sensor_record1 *sensor_record, char* deviceID, char* topic);

void mqtt_send_device_wake_up_reason_alert(esp_mqtt_client_handle_t client, int timestamp, char* deviceID, char* wake_up_reason);

void mqtt_send_error_reading_sensor(esp_mqtt_client_handle_t client, int timestamp, char* deviceID, char* sensor_type);

void mqtt_send_water_leak_alert(esp_mqtt_client_handle_t client, int timestamp, char* deviceID);