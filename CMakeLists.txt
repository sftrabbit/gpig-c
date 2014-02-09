cmake_minimum_required(VERSION 2.6)

set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_SOURCE_DIR}/CMakeModules")
set(CMAKE_BUILD_TYPE "Debug" CACHE STRING "Build type")

project(GPIG-C NONE)
set(GPIG-C_PROJECT_NAME "GPIC-C")

add_subdirectory(docs)
