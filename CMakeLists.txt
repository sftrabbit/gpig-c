cmake_minimum_required(VERSION 2.6)

set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_SOURCE_DIR}/CMakeModules")

if (NOT CMAKE_BUILD_TYPE)
	message(STATUS "No build type given, default to Release")
	set(CMAKE_BUILD_TYPE "Release" CACHE STRING "Build type" FORCE)
endif()

project(GPIG-C NONE)

set(GPIG-C_PROJECT_NAME "GPIC-C")

add_subdirectory(docs)
