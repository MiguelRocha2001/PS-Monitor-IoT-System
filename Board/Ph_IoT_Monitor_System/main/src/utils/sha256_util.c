#include <esp_err.h>
#include <string.h>
#include "mbedtls/sha256.h"
#include "sha256_util.h"


esp_err_t hash_sha256(char* message, uint8_t* hash) {
    mbedtls_sha256_context ctx;
    mbedtls_sha256_init(&ctx);

    int ret;

    ret = mbedtls_sha256_starts(&ctx, 0);
    if(ret != ESP_OK) {
        printf("mbedtls_sha256_starts_ret ret : %X\n", ret);
        mbedtls_sha256_free(&ctx);
        return ret;
    }

    size_t message_size = strlen(message);

    ret = mbedtls_sha256_update(&ctx, (const unsigned char *) message, message_size);
    if(ret != ESP_OK) {
        printf("mbedtls_sha256_update_ret ret : %X\n", ret);
        mbedtls_sha256_free(&ctx);
        return ret;
    }

    ret = mbedtls_sha256_finish(&ctx, hash);
    if(ret != ESP_OK) {
        printf("mbedtls_sha256_finish_ret ret : %X\n", ret);
    }

    mbedtls_sha256_free(&ctx);

    return ret;
}