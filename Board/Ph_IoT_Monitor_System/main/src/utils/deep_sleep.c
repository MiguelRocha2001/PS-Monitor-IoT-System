void start_deep_sleep(long sleep_time) {
    esp_sleep_enable_timer_wakeup(sleep_time);
    esp_deep_sleep_start();
}

void printDeepSleepWokeCause() {
    esp_sleep_wakeup_cause_t cause = esp_sleep_get_wakeup_cause();
    if (cause == ESP_SLEEP_WAKEUP_TIMER) {
        ESP_LOGI(TAG, "Woke up from timer");
    } else {
        ESP_LOGI(TAG, "Woke up duo to unknown reason");
    }
}