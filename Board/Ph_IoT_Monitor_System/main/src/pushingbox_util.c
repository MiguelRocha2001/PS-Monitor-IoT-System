#include "pushingbox_util.h"

#include <string.h>
#include <stdlib.h>
#include "esp_log.h"
#include "esp_event.h"
#include "esp_system.h"

#include "esp_http_client.h"

static const char *TAG = "PUSHINGBOX_UTIL";



void send_notification(char* devid) {
    /**
     * NOTE: All the configuration parameters for http_client must be spefied either in URL or as host and path parameters.
     * If host and path parameters are not set, query parameter will be ignored. In such cases,
     * query parameter should be specified in URL.
     *
     * If URL as well as host and path parameters are specified, values of host and path will be considered.
     */
    esp_http_client_config_t config = {
            .host = "api.pushingbox.com",
            .path = "pushingbox",
    };
    esp_http_client_handle_t client = esp_http_client_init(&config);

    // GET

    char* base_url = "http://api.pushingbox.com/pushingbox?devid=";

    char* url = malloc(sizeof(char) * (strlen(base_url) + strlen(devid)));


    sprintf(url, "%s%s", base_url, devid);
    ESP_LOGE(TAG, "send request to: %s", url);

    esp_http_client_set_url(client, url);

    esp_err_t err = esp_http_client_perform(client);
    if (err == ESP_OK) {
        ESP_LOGI(TAG, "HTTP GET Status = %d, content_length = %d",
                 esp_http_client_get_status_code(client),
                 esp_http_client_get_content_length(client));
    } else {
        ESP_LOGE(TAG, "HTTP GET request failed: %s", esp_err_to_name(err));
    }

    esp_http_client_cleanup(client);
}