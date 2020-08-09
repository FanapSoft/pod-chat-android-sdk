LOCAL_PATH := $(call my-dir)

$(info "##########################################################")
$(info "###################### LOCAL PATH #########################")
$(info "##########################################################")
$(info $(LOCAL_PATH))
$(info "##########################################################")
$(info "##########################################################")
$(info "##########################################################")


$(info "##########################################################")
$(info "######################## Opus ############################")
$(info "##########################################################")

include $(CLEAR_VARS)

LOCAL_LDLIBS := -llog

LOCAL_MODULE := podopus

LOCAL_CFLAGS := -DUSE_ALLOCA

LOCAL_CFLAGS += -DHAVE_LRINTF

OPUS_VERSION := "1.3.1"
PACKAGE_VERSION := $(OPUS_VERSION)
LOCAL_CFLAGS += -DOPUS_VERSION='$(OPUS_VERSION)'
WARNINGS := -Wall -W -Wstrict-prototypes -Wextra -Wcast-align -Wnested-externs -Wshadow
LOCAL_CFLAGS += -O2 -g $(WARNINGS) -DOPUS_BUILD


$(info "##########################################################")
$(info "################### Opus Compile Options #################")
$(info "##########################################################")


ifeq ($(TARGET_ARCH_ABI), armeabi-v7a)
LOCAL_ARM_MODE := arm
LOCAL_ARM_NEON := true
LOCAL_CFLAGS += -O3 -march=armv7-a -mfpu=neon -mfloat-abi=softfp
endif

ifeq ($(TARGET_ARCH_ABI),x86)
LOCAL_CFLAGS += -msse4.2 -mavx
LOCAL_CFLAGS += -DOPUS_X86_MAY_HAVE_SSE -DOPUS_X86_MAY_HAVE_SSE2 -DOPUS_X86_MAY_HAVE_SSE4_1 \
				-DOPUS_X86_MAY_HAVE_AVX -DOPUS_X86_PRESUME_SSE -DOPUS_X86_PRESUME_SSE2 \
				-DOPUS_HAVE_RTCD -DCPU_INFO_BY_C
HAVE_SSE=1
HAVE_SSE2=1
HAVE_SSE4_1=1
endif


ifeq ($(TARGET_ARCH_ABI),x86_64)
LOCAL_CFLAGS += -mavx
LOCAL_CFLAGS += -DOPUS_X86_MAY_HAVE_SSE -DOPUS_X86_MAY_HAVE_SSE2 -DOPUS_X86_MAY_HAVE_SSE4_1 \
				-DOPUS_X86_MAY_HAVE_AVX -DOPUS_X86_PRESUME_SSE -DOPUS_X86_PRESUME_SSE2 \
				-DOPUS_X86_PRESUME_SSE4_1 -DOPUS_HAVE_RTCD -DCPU_INFO_BY_C
HAVE_SSE=1
HAVE_SSE2=1
HAVE_SSE4_1=1
endif


include $(LOCAL_PATH)/opus/silk_sources.mk
include $(LOCAL_PATH)/opus/celt_sources.mk
include $(LOCAL_PATH)/opus/opus_sources.mk


ifdef FIXED_POINT
SILK_SOURCES += $(SILK_SOURCES_FIXED)
ifdef HAVE_SSE4_1
SILK_SOURCES += $(SILK_SOURCES_SSE4_1) $(SILK_SOURCES_FIXED_SSE4_1)
endif
ifdef HAVE_ARM_NEON_INTR
SILK_SOURCES += $(SILK_SOURCES_FIXED_ARM_NEON_INTR)
endif
else
SILK_SOURCES += $(SILK_SOURCES_FLOAT)
ifdef HAVE_SSE4_1
SILK_SOURCES += $(SILK_SOURCES_SSE4_1)
endif
endif

ifdef FIXED_POINT
else
OPUS_SOURCES += $(OPUS_SOURCES_FLOAT)
endif

ifdef HAVE_SSE
CELT_SOURCES += $(CELT_SOURCES_SSE)
endif
ifdef HAVE_SSE2
CELT_SOURCES += $(CELT_SOURCES_SSE2)
endif
ifdef HAVE_SSE4_1
CELT_SOURCES += $(CELT_SOURCES_SSE4_1)
endif

ifdef CPU_ARM
CELT_SOURCES += $(CELT_SOURCES_ARM)
SILK_SOURCES += $(SILK_SOURCES_ARM)

ifdef HAVE_ARM_NEON_INTR
CELT_SOURCES += $(CELT_SOURCES_ARM_NEON_INTR)
SILK_SOURCES += $(SILK_SOURCES_ARM_NEON_INTR)
endif

ifdef HAVE_ARM_NE10
CELT_SOURCES += $(CELT_SOURCES_ARM_NE10)
endif
endif

LOCAL_SRC_FILES := \
	$(patsubst %,$(LOCAL_PATH)/opus/%,$(SILK_SOURCES) $(CELT_SOURCES) $(OPUS_SOURCES)) \
	Java_com_fanap_podchat_Encoder.c \
	Java_com_fanap_podchat_Decoder.c \


all:
	echo $(LOCAL_SRC_FILES)

LOCAL_C_INCLUDES += $(LOCAL_PATH)/opus/include/ \
	$(LOCAL_PATH)/opus/silk/ \
	$(LOCAL_PATH)/opus/silk/fixed \
	$(LOCAL_PATH)/opus/silk/float \
	$(LOCAL_PATH)/opus/celt/ \
	$(LOCAL_PATH)/opus/src/ \
	$(LOCAL_PATH)/opus/

ifdef FIXED_POINT
CFLAGS += -DFIXED_POINT=1 -DDISABLE_FLOAT_API
#LOCAL_C_INCLUDES += $(LOCAL_PATH)/opus/silk/fixed
else
#LOCAL_C_INCLUDES += $(LOCAL_PATH)/opus/silk/float
endif

LOCAL_STATIC_LIBRARIES := cpufeatures

# TARGET_ARCH_ABI := armeabi-v7a arm64-v8a

include $(BUILD_SHARED_LIBRARY)



$(info "##########################################################")
$(info "##################### End Opus Options ###################")
$(info "##########################################################")


$(info "##########################################################")
$(info "######################### Speexdsp #######################")
$(info "##########################################################")

include $(CLEAR_VARS)

LOCAL_MODULE := podspeexdsp

LOCAL_CFLAGS = -DFIXED_POINT -DUSE_KISS_FFT -DEXPORT="" -DHAVE_STDINT_H -UHAVE_CONFIG_H
LOCAL_C_INCLUDES := $(LOCAL_PATH)/speexdsp/include
LOCAL_LDLIBS := -llog

LOCAL_SRC_FILES :=  \
	$(LOCAL_PATH)/speexdsp/libspeexdsp/buffer.c \
	$(LOCAL_PATH)/speexdsp/libspeexdsp/fftwrap.c \
	$(LOCAL_PATH)/speexdsp/libspeexdsp/filterbank.c \
	$(LOCAL_PATH)/speexdsp/libspeexdsp/jitter.c \
	$(LOCAL_PATH)/speexdsp/libspeexdsp/kiss_fft.c \
	$(LOCAL_PATH)/speexdsp/libspeexdsp/kiss_fftr.c \
	$(LOCAL_PATH)/speexdsp/libspeexdsp/mdf.c \
	$(LOCAL_PATH)/speexdsp/libspeexdsp/preprocess.c \
	$(LOCAL_PATH)/speexdsp/libspeexdsp/resample.c \
	$(LOCAL_PATH)/speexdsp/libspeexdsp/scal.c \
	$(LOCAL_PATH)/speexdsp/libspeexdsp/smallft.c \
	Java_com_fanap_podchat_EchoCanceller.cpp

include $(BUILD_SHARED_LIBRARY)

$(info "##########################################################")
$(info "####################### End Speexdsp #####################")
$(info "##########################################################")

$(call import-module,android/cpufeatures)


