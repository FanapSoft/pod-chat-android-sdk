//
// Created by Farhad on 8/5/2020.
//

#ifndef POD_CHAT_ANDROID_SDK_JAVA_COM_FANAP_PODCHAT_ECHOCANCELLER_H
#define POD_CHAT_ANDROID_SDK_JAVA_COM_FANAP_PODCHAT_ECHOCANCELLER_H

#endif //POD_CHAT_ANDROID_SDK_JAVA_COM_FANAP_PODCHAT_ECHOCANCELLER_H

#ifndef _Included_speexdsp_EchoCanceller
#define _Included_speexdsp_EchoCanceller


extern "C" {
JNIEXPORT void JNICALL Java_com_fanap_podchat_call_codec_speexdsp_EchoCanceller_open (JNIEnv *env, jobject jObj, jint jSampleRate, jint jBufSize, jint jTotalSize);
JNIEXPORT jshortArray JNICALL Java_com_fanap_podchat_call_codec_speexdsp_EchoCanceller_process  (JNIEnv * env, jobject jObj, jshortArray input_frame, jshortArray echo_frame);
JNIEXPORT void JNICALL  Java_com_fanap_podchat_call_codec_speexdsp_EchoCanceller_close(JNIEnv *env, jobject jObj);
JNIEXPORT jshortArray JNICALL  Java_com_fanap_podchat_call_codec_speexdsp_EchoCanceller_capture(JNIEnv *env, jobject jObj, jshortArray input_frame);
JNIEXPORT void JNICALL  Java_com_fanap_podchat_call_codec_speexdsp_EchoCanceller_playback(JNIEnv *env, jobject jObj, jshortArray echo_frame);
JNIEXPORT void JNICALL  Java_com_fanap_podchat_call_codec_speexdsp_EchoCanceller_reset(JNIEnv *env, jobject jObj);
JNIEXPORT bool JNICALL  Java_com_fanap_podchat_call_codec_speexdsp_EchoCanceller_isOpen(JNIEnv *env, jobject jObj);
}
#endif