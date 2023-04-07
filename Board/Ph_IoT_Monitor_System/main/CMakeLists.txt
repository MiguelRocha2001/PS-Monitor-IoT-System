idf_component_register(
        SRCS
            "src/main_aes_test.c"
            "src/utils/nvs_util.c"
            "src/utils/pushingbox_util.c"
            "src/utils/esp_touch_util.c"
            "src/utils/wifi_connect_util.c"
            "src/utils/ph_reader_fake.c"
            "src/utils/broker_util.c"
            "src/utils/deep_sleep.c"
            "src/utils/dht11.c"
            "src/utils/time_util.c"
            "src/utils/aes_util.c"
            "src/utils/base64_util.c"
            "src/utils/PKCS7.c"
            "src/utils/rsa_util.c"
            "src/utils/sha256_util.c"
            "src/utils/utils.c"
            "src/utils/entropy_poll.c"
        INCLUDE_DIRS "include"
)
target_compile_options(${COMPONENT_LIB} PRIVATE  -Wno-format)