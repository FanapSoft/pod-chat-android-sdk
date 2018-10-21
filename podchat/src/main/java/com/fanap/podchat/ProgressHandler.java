package com.fanap.podchat;

import com.fanap.podchat.mainmodel.FileUpload;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.FileImageUpload;

//A callback for while file is being uploaded.
public abstract class ProgressHandler {


    public interface onProgress {
        void onProgressUpdate(int bytesSent);

        void onFinish(String imageJson, FileImageUpload fileImageUpload);

        void onError(String jsonError, ErrorOutPut error);
    }
    public interface onProgressFile {
        void onProgressUpdate(int bytesSent);

        void onFinish(String imageJson, FileUpload fileImageUpload);

        void onError(String jsonError, ErrorOutPut error);
    }

    /**
     * @param bytesSent        - Bytes sent since the last time this callback was called.
     * @param totalBytesSent   - Total number of bytes sent so far.
     * @param totalBytesToSend - Total bytes to send.
     */
    public void onProgress(int bytesSent, int totalBytesSent, int totalBytesToSend) {
    }
}
