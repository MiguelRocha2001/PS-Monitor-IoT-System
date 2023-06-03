#include <string.h>
#include <stdio.h>
#include <stdbool.h>
#include "esp_log.h"
#include "aes/esp_aes.h"
#include "PKCS7.h"
#include "aes_util.h"
#include "base64_util.h"
#include "esp_system.h"
#include "utils.h"

static const char *TAG = "AES_UTIL";

uint8_t* get_random_iv() {
//    uint8_t* iv = (uint8_t*) malloc(sizeof(uint8_t) * IV_SIZE);
//    // esp_fill_random(&iv, sizeof(uint8_t) * IV_SIZE);
//    iv = memset(iv, 'A',sizeof(uint8_t) * IV_SIZE );
    uint8_t* iv = get_random_array(IV_SIZE);
    return iv;
}


void print_array(uint8_t* a, int n) {
    for (int i = 0; i < n; i++) {
        ESP_LOGI("TEST", "%x, ", a[i]);
    }
}


esp_aes_context init_AES(const uint8_t key_256[KEY_SIZE_BYTES]) {
    esp_aes_context ctx;
    esp_aes_init(&ctx);
    esp_aes_setkey(&ctx, key_256, KEY_SIZE_BITS);
    return ctx;
}

void free_AES(esp_aes_context ctx) {
    esp_aes_free(&ctx);
}

void free_AES_Encrypted(AES_Encrypted* aes_encrypted) {
    free(aes_encrypted->data);
    free(aes_encrypted);
}

void free_AES_Decrypted(AES_Decrypted* aes_decrypted) {
    free(aes_decrypted->data);
    free(aes_decrypted);
}

AES_Encrypted* encrypt_AES(esp_aes_context ctx, uint8_t* plaintext, int size) {

    PKCS7_Padding* paddingResult;
    AES_Encrypted* aes_encrypted = (AES_Encrypted*) malloc(sizeof(AES_Encrypted));

    paddingResult = addPadding(plaintext, size, PADDING_BLOCK_SIZE);

    uint8_t* chipertext = (uint8_t *) malloc(sizeof(uint8_t) * paddingResult->dataLengthWithPadding);

    aes_encrypted->data = chipertext;
    aes_encrypted->dataLength = paddingResult->dataLengthWithPadding;

    uint8_t* nonce[IV_SIZE];
    uint8_t* iv = get_random_iv();
    memcpy(nonce, iv, sizeof(uint8_t) * IV_SIZE);

    aes_encrypted->iv = iv;
    esp_aes_crypt_cbc(&ctx, ESP_AES_ENCRYPT, paddingResult->dataLengthWithPadding, nonce, (uint8_t *) paddingResult->dataWithPadding, chipertext);

    return aes_encrypted;
}


AES_Decrypted* decrypt_AES(esp_aes_context ctx, uint8_t* chipertext, int size, uint8_t* iv) {
    
    uint8_t *decryptedtext = (uint8_t *) malloc(sizeof(uint8_t) * size);
    AES_Decrypted* aes_decrypted = (AES_Decrypted*) malloc(sizeof(AES_Decrypted));
    uint8_t nonce[IV_SIZE];
    memcpy(nonce, iv, IV_SIZE);
    
    esp_aes_crypt_cbc(&ctx, ESP_AES_DECRYPT, size, nonce, chipertext, decryptedtext);

    PKCS7_unPadding* unpaddingResult;
    unpaddingResult = removePadding(decryptedtext, size);
    aes_decrypted->data = (uint8_t *) unpaddingResult->dataWithoutPadding;
    aes_decrypted-> dataLength = unpaddingResult->dataLengthWithoutPadding;

    return aes_decrypted;
}


char* encrypt_str_AES(esp_aes_context ctx, char* plaintext_str) {

    int size = strlen(plaintext_str);
    AES_Encrypted* aes_encrypted = encrypt_AES(ctx, (uint8_t *) plaintext_str, size);

    size_t base64_size;
    char* encrypted_base64 = base64_encode(aes_encrypted->data, aes_encrypted->dataLength, &base64_size);

    size_t iv_base64_size;
    char* iv_base64 = base64_encode(aes_encrypted->iv, sizeof(uint8_t) * IV_SIZE, &iv_base64_size);

    char* encrypted_and_iv_base64 = (char*) malloc(base64_size + iv_base64_size + sizeof(char));
    
    sprintf(encrypted_and_iv_base64, "%s %s", encrypted_base64, iv_base64);

    // encrypted_and_iv_base64[(int)(base64_size/sizeof(char))] = ' ';
    // strcat(encrypted_and_iv_base64, iv_base64);

     free(encrypted_base64);
     free(iv_base64);
     free_AES_Encrypted(aes_encrypted);


    return encrypted_and_iv_base64;
}


char* decrypt_base64_AES(esp_aes_context ctx, char* chipertext_and_iv_base64) {


    char* sep = " ";

    char *pt;
    int iv_base64_size;

    pt = strtok(chipertext_and_iv_base64, sep);

    char* chipertext_base64 = malloc(sizeof(char) * (strlen(pt) + 1));
    strcpy(chipertext_base64, pt);

    size_t size_iv;
    pt = strtok (NULL, sep);
    iv_base64_size = (int)(strlen(pt)/sizeof(char));
    uint8_t* iv = base64_decode(pt, iv_base64_size, &size_iv);

    int base64_size = (int)(strlen(chipertext_base64)/sizeof(char));

    size_t size;
    uint8_t* chipertext = base64_decode(chipertext_base64, base64_size, &size);


    AES_Decrypted* aes_decrypted = decrypt_AES(ctx, chipertext, size, iv);

    // Convert to char*
    char* decrypted_str = (char *) malloc(sizeof(char) * (aes_decrypted->dataLength + 1));
    memcpy(decrypted_str, aes_decrypted->data, aes_decrypted->dataLength);
    decrypted_str[aes_decrypted->dataLength] = '\0';

    free(chipertext);

    free_AES_Decrypted(aes_decrypted);

    return decrypted_str;
}
