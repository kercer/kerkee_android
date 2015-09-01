LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := iniparser
LOCAL_SRC_FILES := iniparser.c \
	dictionary.c

#LOCAL_LDLIBS += -llog

include $(BUILD_SHARED_LIBRARY)
