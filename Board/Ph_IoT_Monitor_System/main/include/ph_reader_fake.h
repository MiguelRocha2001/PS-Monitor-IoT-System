#include <stdlib.h>
#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <stddef.h>

struct ph_record {
    float value;
    int timestamp;
};

void read_ph(struct ph_record *ph_record);

