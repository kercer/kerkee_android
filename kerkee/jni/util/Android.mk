LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := kerkee_util
LOCAL_SRC_FILES := util.cpp \
	NativeUtil.c \
	FileSys.c \
	md5.c

LOCAL_LDLIBS += -llog

include $(BUILD_SHARED_LIBRARY)
