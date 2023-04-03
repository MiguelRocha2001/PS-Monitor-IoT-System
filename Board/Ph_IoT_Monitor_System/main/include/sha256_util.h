#ifndef SHA256_UTIL_H
#define SHA256_UTIL_H
#include "mbedtls/sha256.h"

esp_err_t hash_sha256(char* message, uint8_t* hash);

#endif // SHA256_UTIL_H