LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
OPENCV_INSTALL_MODULES:=on
include ../OpenCV-2.4.9-android/sdk/native/jni/OpenCV.mk

LOCAL_MODULE := faces
LOCAL_LDLIBS += -llog -ldl
LOCAL_SRC_FILES := faces.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)

$(info $(LOCAL_LDLIBS))
 
include $(BUILD_SHARED_LIBRARY)