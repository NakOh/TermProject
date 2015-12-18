LOCAL_PATH := $(call my-dir)
 
include $(CLEAR_VARS)
 
LOCAL_MODULE	:= termproject
LOCAL_SRC_FILES	:= termproject.c
 
include $(BUILD_SHARED_LIBRARY)
