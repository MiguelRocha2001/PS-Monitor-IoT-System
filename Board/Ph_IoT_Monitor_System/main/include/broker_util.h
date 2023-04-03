#include "ph_reader_fake.h"


esp_mqtt_client_handle_t setup_mqtt();

void mqtt_send_ph(esp_mqtt_client_handle_t client, struct ph_record *ph_record, char* deviceID);

void mqtt_send_water_alert(esp_mqtt_client_handle_t client, int timestamp, char* deviceID);