idf_component_register(
        SRCS
            "src/main.c"
            "src/nvs_util.c"
            "src/pushingbox_util.c"
            "src/esp_touch_util.c"
            "src/wifi_connect_util.c"
            "src/ph_reader_fake.c"
        INCLUDE_DIRS "include"
)
target_compile_options(${COMPONENT_LIB} PRIVATE  -Wno-format)