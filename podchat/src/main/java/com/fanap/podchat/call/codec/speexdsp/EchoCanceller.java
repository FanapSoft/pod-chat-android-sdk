package com.fanap.podchat.call.codec.speexdsp;

public class EchoCanceller {

    static {
        System.loadLibrary("podspeexdsp");
    }

    public native void open(int sampleRate, int bufSize, int totalSize);

    public native short[] process(short[] input_frame, short[] echo_frame);

    public native short[] capture(short[] input_frame);

    public native void playback(short[] echo_frame);

    public native void close();




    public synchronized void openEcho(int sampleRate, int bufSize, int totalLength)
    {
        open(sampleRate,bufSize,totalLength);
    }

    public synchronized short[] processEcho(short[] inputframe, short[] echoframe)
    {
        return process(inputframe, echoframe);
    }

    public synchronized void closeEcho()
    {
        close();
    }


}
