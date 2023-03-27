#include "ph_reader_fake.h"

#ifndef PH_RECORDS_H
#define PH_RECORDS_H

#define MAX_PH_VALUES 100

typedef struct ph_records_struct {
    struct ph_record ph_values[MAX_PH_VALUES];
    int index;
} ph_records_struct;

#endif