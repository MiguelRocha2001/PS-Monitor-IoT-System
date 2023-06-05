#include <stdio.h>
#include <unity.h>

// Include your test files here
#include "test_nav_utils.c"

void app_main()
{
    UNITY_BEGIN();

    // Run your tests here
    RUN_TEST(test_nav_utils);

    UNITY_END();
}