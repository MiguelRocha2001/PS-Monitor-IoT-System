#include "esp_sleep.h"
#include "driver/touch_sensor.h"

#define TOUCH_PIN 2
#define TOUCH_THRESHOLD 50

void touchpad_init(void) {
    // Initialize touch pad driver
    touch_pad_init();

    // Configure touch pad for wake-up
    touch_pad_config(TOUCH_PIN);
    touch_pad_set_thresh(TOUCH_PIN, TOUCH_THRESHOLD);

    // Set up touch pad wake-up
    esp_sleep_enable_touchpad_wakeup();
}

void app_main(void) {
    // Initialize touch pad for wake-up
    touchpad_init();

    // Put ESP32-S2 into deep sleep mode
    esp_deep_sleep_start();
}
