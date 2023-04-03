#ifndef RSA_UTIL_H
#define RSA_UTIL_H
#include "mbedtls/rsa.h"


#define KEY_SIZE_BITS_RSA 2048
#define KEY_SIZE_BYTES_RSA KEY_SIZE_BITS_RSA/8


typedef struct {
    unsigned char* data;
    int dataLength;
} RSA_Decrypted;

typedef struct {
    unsigned char* data;
    int dataLength;
} RSA_Encrypted;

void set_rsa_private_key(const char* key);

char* sign_RSA(char* plaintext);

void free_RSA_Decrypted(RSA_Decrypted* rsa_decrypted);

RSA_Decrypted* decrypt_RSA(uint8_t* chipertext);

char* decrypt_base64_RSA(char* chipertext_base64);

void print_rsa_key();

#endif // RSA_UTIL_H