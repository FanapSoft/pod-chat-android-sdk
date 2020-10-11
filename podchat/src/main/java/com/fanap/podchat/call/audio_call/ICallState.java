package com.fanap.podchat.call.audio_call;


public interface ICallState{

    void onInfoEvent(String info);

    void onErrorEvent(String cause);

    void onEndCallRequested();

}
