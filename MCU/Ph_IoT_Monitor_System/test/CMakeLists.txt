cmake_minimum_required(VERSION 3.5)

# Define the project and the language
project(my_tests C)

# Add the source files
add_executable(my_tests test_main.c test_nav_utils.c)

# Include the main directory headers
target_include_directories(my_tests PRIVATE ${CMAKE_SOURCE_DIR}/main)

# Link the Unity library
target_link_libraries(my_tests PRIVATE unity)
