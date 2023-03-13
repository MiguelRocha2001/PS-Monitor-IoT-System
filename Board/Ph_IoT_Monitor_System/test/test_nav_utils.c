#include <unity.h>
#include "esp_err.h"

void test_nav_utils()
{
    esp_err_t error = init_nvs();
    TEST_ASSERT_EQUAL(ESP_OK, error);
}
