#include <string.h>
#include <stdio.h>
#include <stdbool.h>
#include <esp_system.h>
#include <esp_heap_caps.h>
#include "esp_log.h"
#include "mbedtls/rsa.h"
#include "mbedtls/pk.h"
#include "base64_util.h"
#include "rsa_util.h"
#include "mbedtls/entropy_poll.h"
#include "sha256_util.h"

#define MBEDTLS_RSA_PRIVATE 1

const char TAG[] = "RSA_UTIL";

RTC_DATA_ATTR static char privkey_2048_buf[2048];


void set_rsa_private_key(const char* key) {
    strcpy(privkey_2048_buf, key);
}

static int myrand(void *rng_state, unsigned char *output, size_t len) {
    size_t olen;
    return mbedtls_hardware_poll(rng_state, output, len, &olen);
}


void print_rsa_key() {
    ESP_LOGI(TAG, "%s", privkey_2048_buf);
}

char* sign_RSA(char* to_sign) {
    mbedtls_pk_context clientkey;
    mbedtls_rsa_context rsa;


    unsigned char * signature_buf = (unsigned char *) malloc(sizeof(unsigned char) * KEY_SIZE_BYTES_RSA);

    int res = 0;

    mbedtls_pk_init(&clientkey);
    res = mbedtls_pk_parse_key(&clientkey, (const uint8_t *)privkey_2048_buf, sizeof(privkey_2048_buf), NULL, 0, NULL, NULL);

    if (res != 0) {
        ESP_LOGE(TAG, "Failed while parsing key. Code: %X", -res);
        return NULL;
    }

    rsa = *mbedtls_pk_rsa(clientkey);
    mbedtls_rsa_set_padding(&rsa, MBEDTLS_RSA_PKCS_V21, MBEDTLS_MD_SHA256);

    uint8_t* hash = (uint8_t*) malloc(sizeof(uint8_t) * 32);

    hash_sha256(to_sign, hash);


    res = mbedtls_rsa_rsassa_pss_sign(
        &rsa,
        myrand,
        MBEDTLS_RSA_PRIVATE,
        MBEDTLS_MD_SHA256,
        (unsigned int) mbedtls_md_get_size(MBEDTLS_MD_SHA256),
        (unsigned char*) hash,
        signature_buf);

    if (res != 0) {
        ESP_LOGE(TAG, "Failed while sign. Code: %X", -res);
        return NULL;
    }

    size_t base64_size;
    char* signature_base64 = base64_encode(signature_buf, KEY_SIZE_BYTES_RSA, &base64_size);

    mbedtls_rsa_free(&rsa);

    return signature_base64;
}


void free_RSA_Decrypted(RSA_Decrypted* rsa_decrypted) {
    free(rsa_decrypted->data);
    free(rsa_decrypted);
}

RSA_Decrypted* decrypt_RSA(uint8_t* chipertext) {
    mbedtls_pk_context clientkey;
    mbedtls_rsa_context rsa;

    unsigned char * decrypted_buf = (unsigned char *) malloc(sizeof(unsigned char) * KEY_SIZE_BYTES_RSA);
    RSA_Decrypted* rsa_decrypted = (RSA_Decrypted*) malloc(sizeof(RSA_Decrypted));
    rsa_decrypted->data = decrypted_buf;

    int res = 0;

    mbedtls_pk_init(&clientkey);
    res = mbedtls_pk_parse_key(&clientkey, (const uint8_t *)privkey_2048_buf, sizeof(privkey_2048_buf), NULL, 0, NULL, NULL);

    if (res != 0) {
        ESP_LOGE(TAG, "Failed while parsing key. Code: %X", -res);
        return NULL;
    }

    rsa = *mbedtls_pk_rsa(clientkey);
    mbedtls_rsa_set_padding(&rsa, MBEDTLS_RSA_PKCS_V21, MBEDTLS_MD_SHA256);

    size_t len_decrypted;
    res = mbedtls_rsa_rsaes_oaep_decrypt(&rsa, myrand, MBEDTLS_RSA_PRIVATE, NULL, 0, &len_decrypted, chipertext, decrypted_buf, sizeof(unsigned char) * KEY_SIZE_BYTES_RSA);
    if (res != 0) {
        ESP_LOGE(TAG, "Failed while decrypting. Code: %X", -res);
        return NULL;
    }

    rsa_decrypted->dataLength = (int)(len_decrypted / sizeof(uint8_t));

    mbedtls_rsa_free(&rsa);
//    mbedtls_pk_free(&clientkey);


    return rsa_decrypted;
}

char* decrypt_base64_RSA(char* chipertext_base64) {

    size_t base64_size = strlen(chipertext_base64);

    size_t size;

    uint8_t* chipertext = base64_decode(chipertext_base64, base64_size, &size);

    RSA_Decrypted* rsa_decrypted = decrypt_RSA(chipertext);

    // Convert to char*
    char* decrypted_str = (char *) malloc((sizeof(char) * rsa_decrypted->dataLength)+1);
    memcpy(decrypted_str, rsa_decrypted->data, rsa_decrypted->dataLength);
    decrypted_str[rsa_decrypted->dataLength] = '\0';

    free(chipertext);
    free_RSA_Decrypted(rsa_decrypted);

    return decrypted_str;
}