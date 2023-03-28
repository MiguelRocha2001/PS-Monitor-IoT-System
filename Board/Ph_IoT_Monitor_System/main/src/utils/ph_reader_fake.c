#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <esp_log.h>

#include "ph_reader_fake.h"

const static char* TAG = "PH_READER_FAKE";

float generate_random_float()
{
    srand((unsigned int)time(NULL));

    float a = 5.0;
    return ((float)rand()/(float)(RAND_MAX)) * a;
}

int generate_random_int()
{
    srand((unsigned int)time(NULL));

    return rand();
}

void read_ph(struct ph_record *ph_record)
{
    ESP_LOGE(TAG, "Reading pH...");
    float ph_value = generate_random_float();
    int timestamp = generate_random_int();
    ph_record -> value = ph_value;
    ph_record -> timestamp = timestamp;
}