#include <stdio.h>
#include <stdint.h>
#include <stddef.h>
#include <string.h>
#include "esp_wifi.h"
#include "esp_system.h"
#include "nvs_flash.h"
#include "esp_event.h"
#include "esp_netif.h"

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "freertos/semphr.h"
#include "freertos/queue.h"

#include "lwip/sockets.h"
#include "lwip/dns.h"
#include "lwip/netdb.h"

#include "esp_log.h"
#include "mqtt_client.h"

#include "sensor/sensor_reader.h"

// see: https://docs.espressif.com/projects/esp-idf/en/v5.0.1/esp32s2/api-reference/protocols/mqtt.html

static const char *TAG = "MQTT_MODULE";

static const char *CONFIG_BROKER_URL = "mqtt://2.tcp.eu.ngrok.io:14133/";

static void log_error_if_nonzero(const char *message, int error_code)
{
    if (error_code != 0) {
        ESP_LOGE(TAG, "Last error %s: 0x%x", message, error_code);
    }
}

/*
 * @brief Event handler registered to receive MQTT events
 *
 *  This function is called by the MQTT client event loop.
 *
 * @param handler_args user data registered to the event.
 * @param base Event base for the handler(always MQTT Base in this example).
 * @param event_id The id for the received event.
 * @param event_data The data for the event, esp_mqtt_event_handle_t.
 */
static void mqtt_event_handler(void *handler_args, esp_event_base_t base, int32_t event_id, void *event_data)
{
    ESP_LOGD(TAG, "Event dispatched from event loop base=%s, event_id=%d", base, event_id);
    esp_mqtt_event_handle_t event = event_data;
    esp_mqtt_client_handle_t client = event->client;
    int msg_id;
    switch ((esp_mqtt_event_id_t)event_id) {
        case MQTT_EVENT_CONNECTED:
            ESP_LOGI(TAG, "MQTT_EVENT_CONNECTED");
            // msg_id = esp_mqtt_client_publish(client, "/ph", "data_10", 0, 1, 0);
            // ESP_LOGI(TAG, "sent publish successful, msg_id=%d", msg_id);
            break;
        case MQTT_EVENT_DISCONNECTED:
            ESP_LOGI(TAG, "MQTT_EVENT_DISCONNECTED");
            break;

        case MQTT_EVENT_SUBSCRIBED:
            ESP_LOGI(TAG, "MQTT_EVENT_SUBSCRIBED, msg_id=%d", event->msg_id);
            msg_id = esp_mqtt_client_publish(client, "/topic/qos0", "data", 0, 0, 0);
            ESP_LOGI(TAG, "sent publish successful, msg_id=%d", msg_id);
            break;
        case MQTT_EVENT_UNSUBSCRIBED:
            ESP_LOGI(TAG, "MQTT_EVENT_UNSUBSCRIBED, msg_id=%d", event->msg_id);
            break;
        case MQTT_EVENT_PUBLISHED:
            ESP_LOGI(TAG, "MQTT_EVENT_PUBLISHED, msg_id=%d", event->msg_id);
            break;
        case MQTT_EVENT_DATA:
            ESP_LOGI(TAG, "MQTT_EVENT_DATA");
            printf("TOPIC=%.*s\r\n", event->topic_len, event->topic);
            printf("DATA=%.*s\r\n", event->data_len, event->data);
            break;
        case MQTT_EVENT_ERROR:
            ESP_LOGI(TAG, "MQTT_EVENT_ERROR");
            if (event->error_handle->error_type == MQTT_ERROR_TYPE_TCP_TRANSPORT) {
                log_error_if_nonzero("reported from esp-tls", event->error_handle->esp_tls_last_esp_err);
                log_error_if_nonzero("reported from tls stack", event->error_handle->esp_tls_stack_err);
                log_error_if_nonzero("captured as transport's socket errno",  event->error_handle->esp_transport_sock_errno);
                ESP_LOGI(TAG, "Last errno string (%s)", strerror(event->error_handle->esp_transport_sock_errno));

            }
            break;
        default:
            ESP_LOGI(TAG, "Other event id:%d", event->event_id);
            break;
    }
}

esp_mqtt_client_handle_t mqtt_app_start(void)
{
    esp_mqtt_client_config_t mqtt_cfg = {
        .broker.address.uri = CONFIG_BROKER_URL,
    };
    
    esp_mqtt_client_handle_t client = esp_mqtt_client_init(&mqtt_cfg);
    /* The last argument may be used to pass data to the event handler, in this example mqtt_event_handler */
    esp_mqtt_client_register_event(client, ESP_EVENT_ANY_ID, mqtt_event_handler, NULL);
    esp_mqtt_client_start(client);

    return client;
}

esp_mqtt_client_handle_t setup_mqtt()
{
    ESP_LOGE(TAG, "Setting up MQTT...");

    ESP_LOGI(TAG, "[APP] Startup..");
    ESP_LOGI(TAG, "[APP] Free memory: %d bytes", esp_get_free_heap_size());
    ESP_LOGI(TAG, "[APP] IDF version: %s", esp_get_idf_version());

    esp_log_level_set("*", ESP_LOG_INFO);
    esp_log_level_set("mqtt_client", ESP_LOG_VERBOSE);
    esp_log_level_set("MQTT_EXAMPLE", ESP_LOG_VERBOSE);
    esp_log_level_set("TRANSPORT_BASE", ESP_LOG_VERBOSE);
    esp_log_level_set("esp-tls", ESP_LOG_VERBOSE);
    esp_log_level_set("TRANSPORT", ESP_LOG_VERBOSE);
    esp_log_level_set("outbox", ESP_LOG_VERBOSE);
    esp_log_level_set("*", ESP_LOG_INFO);

    // ESP_ERROR_CHECK(nvs_flash_init());
    // ESP_ERROR_CHECK(esp_netif_init());
    // ESP_ERROR_CHECK(esp_event_loop_create_default());

    esp_mqtt_client_handle_t client = mqtt_app_start();
    return client;
}

/*
void mqtt_send_encrypted_data(esp_mqtt_client_handle_t client, char* buf, char* topic)
{
    const uint8_t key_256[KEY_SIZE_BYTES] = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F};
    esp_aes_context ctx = init_AES(key_256);

    char* res = encrypt_str_AES(ctx, buf);

    int msg_id = esp_mqtt_client_publish(client, topic, res, 0, 1, 0);
}
*/

void mqtt_send_sensor_record1(esp_mqtt_client_handle_t client, struct sensor_record1 *sensor_record, char* deviceID, char* topic)
{
    char buf[100];
    sprintf(buf, "{deviceId: %s, value: %f, timestamp: %d}", deviceID, sensor_record -> value, sensor_record -> timestamp);

    // mqtt_send_encrypted_data(client, buf, "ph");
    esp_mqtt_client_publish(client, topic, buf, 0, 1, 0);
    
    ESP_LOGI(TAG, "Message: %s published on topic /ph", buf);
}

void mqtt_send_sensor_record2(esp_mqtt_client_handle_t client, struct sensor_record2 *sensor_record, char* deviceID, char* topic)
{
    char buf[100];
    sprintf(buf, "{deviceId: %s, value: %d, timestamp: %d}", deviceID, sensor_record -> value, sensor_record -> timestamp);

    // mqtt_send_encrypted_data(client, buf, "ph");
    esp_mqtt_client_publish(client, topic, buf, 0, 1, 0);
    
    ESP_LOGI(TAG, "Message: %s published on topic /ph", buf);
}

void mqtt_send_water_alert(esp_mqtt_client_handle_t client, int timestamp, char* deviceID)
{
    // convert ph_record -> value to string
    char buf[100];
    sprintf(buf, "{deviceId: %s, timestamp: %d}", deviceID, timestamp);

    esp_mqtt_client_publish(client, "flood", buf, 0, 1, 0);
    
    ESP_LOGI(TAG, "Message: %s published on topic /flood", buf);
}

void mqtt_send_sensor_not_working_alert(esp_mqtt_client_handle_t client, int timestamp, char* deviceID, char** sensors)
{
    // convert ph_record -> value to string
    char buf[100];

    char sensors_list_buff[100] = "";

    for (int i = 0; i < 3; i++)
    {
        if (sensors[i] != NULL)
        {
            strcat(sensors_list_buff, sensors[i]);
            strcat(sensors_list_buff, ", ");
        }
    }
    sprintf(buf, "deviceId: %s, timestamp: %d, sensors: %s", deviceID, timestamp, sensors_list_buff);

    esp_mqtt_client_publish(client, "sensor_error", buf, 0, 1, 0);

    ESP_LOGI(TAG, "Message: %s published on topic /sensor_error", buf);
}

void mqtt_send_unknown_woke_up_reason_alert(esp_mqtt_client_handle_t client, int timestamp, char* deviceID, char* reason)
{
    char buf[100];
    sprintf(buf, "{deviceId: %s, timestamp: %d, error: %s}", deviceID, timestamp, reason);

    esp_mqtt_client_publish(client, "device_error", buf, 0, 1, 0);

    ESP_LOGI(TAG, "Message: %s published on topic /device_error", buf);
}