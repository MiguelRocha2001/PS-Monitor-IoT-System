#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include "ph_reader_fake.h"

float generate_random_float()
{
    srand((unsigned int)time(NULL));

    float a = 5.0;
    return ((float)rand()/(float)(RAND_MAX)) * a;
}

int generate_random_int()
{
    srand((unsigned int)time(NULL));

    float a = 5.0;
    return (rand()/(RAND_MAX)) * a;
}

void read_ph(struct ph_record *ph_record)
{
    float ph_value = generate_random_float();
    int timestamp = generate_random_int();
    ph_record -> value = ph_value;
    ph_record -> timestamp = timestamp;
}