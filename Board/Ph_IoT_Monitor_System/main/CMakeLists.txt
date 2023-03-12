idf_component_register(
        SRCS
            "main.c"
            "utils/nvs_util.c"
            "utils/pushingbox_util.c"
            "utils/esp_touch_util.c"
            "utils/wifi_connect_util.c"

        INCLUDE_DIRS "utils"
)
target_compile_options(${COMPONENT_LIB} PRIVATE  -Wno-format)