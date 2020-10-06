//
// Created by Farhad on 8/5/2020.
//

#include <speex/speex_echo.h>
#include <speex/speex_preprocess.h>
#include <jni.h>
#include "Java_com_fanap_podchat_EchoCanceller.h"


SpeexEchoState *st;
SpeexPreprocessState *den;

extern "C" JNIEXPORT void JNICALL
Java_com_fanap_podchat_call_codec_speexdsp_EchoCanceller_open(JNIEnv *env, jobject thiz, jint sample_rate,
jint buf_size, jint total_size) {

int sampleRate=sample_rate;

st = speex_echo_state_init(buf_size, total_size);
den = speex_preprocess_state_init(buf_size, sampleRate);
speex_echo_ctl(st, SPEEX_ECHO_SET_SAMPLING_RATE, &sampleRate);
speex_preprocess_ctl(den, SPEEX_PREPROCESS_SET_ECHO_STATE, st);
//speex_preprocess_ctl(den, SPEEX_PREPROCESS_SET_DENOISE, st);
//speex_preprocess_ctl(den, SPEEX_PREPROCESS_SET_DEREVERB, st);
}

extern "C" JNIEXPORT jshortArray JNICALL
Java_com_fanap_podchat_call_codec_speexdsp_EchoCanceller_process(JNIEnv *env, jobject thiz,
jshortArray input_frame,
        jshortArray echo_frame) {
//create native shorts from java shorts
jshort *native_input_frame = env->GetShortArrayElements(input_frame, 0);
jshort *native_echo_frame = env->GetShortArrayElements(echo_frame, 0);

//allocate memory for output data
jint length = env->GetArrayLength(input_frame);
jshortArray temp = env->NewShortArray(length);
jshort *native_output_frame = env->GetShortArrayElements(temp, 0);

//call echo cancellation
speex_echo_cancellation(st, native_input_frame, native_echo_frame, native_output_frame);
//preprocess output frame
speex_preprocess_run(den, native_output_frame);

//convert native output to java layer output
jshortArray output_shorts = env->NewShortArray(length);
env->SetShortArrayRegion(output_shorts, 0, length, native_output_frame);

//cleanup and return
env->ReleaseShortArrayElements(input_frame, native_input_frame, 0);
env->ReleaseShortArrayElements(echo_frame, native_echo_frame, 0);
env->ReleaseShortArrayElements(temp, native_output_frame, 0);

return output_shorts;
}

extern "C" JNIEXPORT jshortArray JNICALL
Java_com_fanap_podchat_call_codec_speexdsp_EchoCanceller_capture(JNIEnv *env, jobject jObj,
jshortArray input_frame) {
env->MonitorEnter(jObj);
jshort *native_input_frame = env->GetShortArrayElements(input_frame, 0);

jint length = env->GetArrayLength(input_frame);
jshortArray temp = env->NewShortArray(length);
jshort *native_output_frame = env->GetShortArrayElements(temp, 0);

speex_echo_capture(st, native_input_frame, native_output_frame);
speex_preprocess_run(den, native_output_frame);

jshortArray output_shorts = env->NewShortArray(length);
env->SetShortArrayRegion(output_shorts, 0, length, native_output_frame);

env->ReleaseShortArrayElements(input_frame, native_input_frame, 0);
env->ReleaseShortArrayElements(temp, native_output_frame, 0);
env->MonitorExit(jObj);
return output_shorts;
}

extern "C" JNIEXPORT void JNICALL
Java_com_fanap_podchat_call_codec_speexdsp_EchoCanceller_playback(JNIEnv *env, jobject thiz,
        jshortArray echo_frame) {
jshort *native_echo_frame = env->GetShortArrayElements(echo_frame, 0);
speex_echo_playback(st, native_echo_frame);
env->ReleaseShortArrayElements(echo_frame, native_echo_frame, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_com_fanap_podchat_call_codec_speexdsp_EchoCanceller_close(JNIEnv *env, jobject thiz) {
speex_echo_state_destroy(st);
speex_preprocess_state_destroy(den);
st = 0;
den = 0;
}
