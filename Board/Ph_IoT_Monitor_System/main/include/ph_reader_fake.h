typedef struct {
    float value;
    int timestamp;
} ph_record_t;

void read_ph(ph_record_t* ph_record);