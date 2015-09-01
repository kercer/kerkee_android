LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

subdirs := $(LOCAL_PATH)/util/Android.mk
subdirs += $(LOCAL_PATH)/iniparser/src/Android.mk


include $(subdirs)
