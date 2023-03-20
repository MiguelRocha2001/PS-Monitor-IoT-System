#include "ph_reader_fake.h"


esp_mqtt_client_handle_t setup_mqtt(struct ph_record *ph_record);

void mqtt_send_ph(esp_mqtt_client_handle_t client, struct ph_record *ph_record);