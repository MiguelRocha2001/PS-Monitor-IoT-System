
#ifndef AES_UTIL_H
#define AES_UTIL_H
#include "aes/esp_aes.h"

typedef struct {
    uint8_t* data;
    int dataLength;
    uint8_t* iv;
} AES_Encrypted;

typedef struct {
    uint8_t* data;
    int dataLength;
} AES_Decrypted;

#define KEY_SIZE_BITS 256
#define KEY_SIZE_BYTES KEY_SIZE_BITS/8
#define PADDING_BLOCK_SIZE 16 // padding block size
#define IV_SIZE 16

void print_array(uint8_t* a, int n);

esp_aes_context init_AES(const uint8_t key_256[KEY_SIZE_BYTES]);
void free_AES(esp_aes_context ctx);


void free_AES_Encrypted(AES_Encrypted* aes_encrypted);
void free_AES_Decrypted(AES_Decrypted* aes_decrypted);

AES_Encrypted* encrypt_AES(esp_aes_context ctx, uint8_t* plaintext, int size);
AES_Decrypted* decrypt_AES(esp_aes_context ctx, uint8_t* chipertext, int size, uint8_t* iv);

char* encrypt_str_AES(esp_aes_context ctx, char* plaintext_str);
char* decrypt_base64_AES(esp_aes_context ctx, char* chipertext_and_iv_base64);

#endif // AES_UTIL_H