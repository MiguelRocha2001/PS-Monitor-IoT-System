idf_component_register(
        SRCS
            "src/main_final.c"
            "src/utils/nvs_util.c"
            "src/utils/pushingbox_util.c"
            "src/utils/esp_touch_util.c"
            "src/utils/wifi_connect_util.c"
            "src/utils/ph_reader_fake.c"
            "src/utils/broker_util.c"
        INCLUDE_DIRS "include"
)
target_compile_options(${COMPONENT_LIB} PRIVATE  -Wno-format)