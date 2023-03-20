typedef struct ph_record {
    float value;
    int timestamp;
} Ph_record;

void read_ph(Ph_record* ph_record);