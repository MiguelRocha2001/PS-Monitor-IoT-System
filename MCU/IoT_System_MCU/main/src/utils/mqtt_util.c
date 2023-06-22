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

#define N_BROKER_CONNECT_TRIES 5

static const char *TAG = "MQTT_MODULE";

static const char *CONFIG_BROKER_URL = "mqtt://0.tcp.eu.ngrok.io:18610/";

int isConnected = 0;

int try_to_connect_to_broker_if_necessary(esp_mqtt_client_handle_t mqtt_client)
{
    if (isConnected) 
    {
        return 1;
    }
    else
    {
        for(int i = 0; i < N_BROKER_CONNECT_TRIES; i++)
        {
            ESP_LOGW(TAG, "Trying to connect to MQTT broker...");
            esp_mqtt_client_reconnect(mqtt_client);
            vTaskDelay(1000 / portTICK_PERIOD_MS);
            if (isConnected) 
            {
                return 1;
            }
            else
            {
                ESP_LOGE(TAG, "Failed to connect to MQTT broker");
            }
        }
        return 0;
    }
}

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
            isConnected = 1;
            ESP_LOGI(TAG, "MQTT_EVENT_CONNECTED");
            break;
        case MQTT_EVENT_DISCONNECTED:
            isConnected = 0;
            ESP_LOGI(TAG, "MQTT_EVENT_DISCONNECTED");
            break;
        case MQTT_EVENT_SUBSCRIBED:
            ESP_LOGI(TAG, "MQTT_EVENT_SUBSCRIBED, msg_id=%d", event->msg_id);
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

            if (event->error_handle->error_type == MQTT_ERROR_TYPE_CONNECTION_REFUSED) {
                log_error_if_nonzero("Connection refused error", event->error_handle->connect_return_code);
            }
            break;
        default:
            ESP_LOGI(TAG, "Other event id:%d", event->event_id);
            break;
    }
}

void mqtt_app_terminate(esp_mqtt_client_handle_t client)
{
    ESP_LOGI(TAG, "Terminating MQTT");
    ESP_ERROR_CHECK(esp_mqtt_client_disconnect(client));
    ESP_ERROR_CHECK(esp_mqtt_client_stop(client));
    ESP_ERROR_CHECK(esp_mqtt_client_destroy(client));
    esp_event_handler_unregister(ESP_EVENT_ANY_ID, ESP_EVENT_ANY_ID, mqtt_event_handler);
    isConnected = 0;
    ESP_LOGI(TAG, "MQTT terminated");
}

esp_mqtt_client_handle_t mqtt_app_init_and_start(void)
{
    esp_mqtt_client_config_t mqtt_cfg = {
        .broker.address.uri = CONFIG_BROKER_URL,
    };

    isConnected = 0;
    
    esp_mqtt_client_handle_t client = esp_mqtt_client_init(&mqtt_cfg);
    /* The last argument may be used to pass data to the event handler, in this example mqtt_event_handler */
    esp_mqtt_client_register_event(client, ESP_EVENT_ANY_ID, mqtt_event_handler, NULL);
    ESP_ERROR_CHECK(esp_mqtt_client_start(client));
    return client;
}

esp_mqtt_client_handle_t setup_mqtt()
{
    ESP_LOGI(TAG, "Setting up MQTT...");

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

    esp_mqtt_client_handle_t client = mqtt_app_init_and_start();
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

void mqtt_send_sensor_record(esp_mqtt_client_handle_t client, struct sensor_record *sensor_record, char* deviceID, char* sensor_type)
{
    char buf[200];
    sprintf(buf, "{\"device_id\": \"%s\", \"value\": %f, \"timestamp\": %d, \"sensor_type\": \"%s\"}", deviceID, sensor_record->value, sensor_record->timestamp, sensor_type);

    char topic[100] = "sensor_record";
    esp_mqtt_client_publish(client, topic, buf, 0, 0, 0);
    
    ESP_LOGI(TAG, "Message: %s published on topic /sensor_record", buf);
}

void mqtt_send_device_wake_up_reason_alert(esp_mqtt_client_handle_t client, int timestamp, char* deviceID, char* wake_up_reason)
{
    char buf[200];
    ESP_LOGI(TAG, "Wake up reason: %s", wake_up_reason);
    sprintf(buf, "{\"device_id\": \"%s\", \"timestamp\": %d, \"reason\": \"%s\"}", deviceID, timestamp, wake_up_reason);

    esp_mqtt_client_publish(client, "device_wake_up_log", buf, 0, 0, 0);

    ESP_LOGI(TAG, "Message: %s published on topic /device_wake_up_log", buf);
}

void mqtt_send_error_reading_sensor(esp_mqtt_client_handle_t client, int timestamp, char* deviceID, char* sensor_type)
{
    char buf[200];
    sprintf(buf, "{\"device_id\": \"%s\", \"timestamp\": %d, \"sensor_type\": \"%s\"}", deviceID, timestamp, sensor_type);

    esp_mqtt_client_publish(client, "error_reading_sensor", buf, 0, 1, 0);

    ESP_LOGI(TAG, "Message: %s published on topic /error_reading_sensor", buf);
}