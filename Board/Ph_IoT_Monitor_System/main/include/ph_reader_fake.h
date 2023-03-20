#ifndef PH_RECORD_H
#define PH_RECORD_H

typedef struct ph_record {
    float value;
    int timestamp;
} ph_record;

void read_ph(ph_record *ph_record);

#endif /* PH_RECORD_H */
