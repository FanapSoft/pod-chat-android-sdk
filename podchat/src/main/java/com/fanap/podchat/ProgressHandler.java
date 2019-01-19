package com.fanap.podchat;

import com.fanap.podchat.mainmodel.FileUpload;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.ResultFile;
import com.fanap.podchat.model.ResultImageFile;

//A callback for while file is being uploaded.
public abstract class ProgressHandler {

    public interface onProgress {
        default void onProgressUpdate(int bytesSent) {
        }

        default void onProgressUpdate(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
        }

        default void onFinish(String imageJson, ChatResponse<ResultImageFile> chatResponse) {
        }

        default void onError(String jsonError, ErrorOutPut error) {
        }
    }

    public interface onProgressFile {
        void onProgressUpdate(int bytesSent);

        default void onProgress(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
        }

        default void onFinish(String imageJson, FileUpload fileImageUpload) {
        }

        default void onError(String jsonError, ErrorOutPut error) {
        }
    }

    public interface sendFileMessage {

        default void onProgressUpdate(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
        }

        default void onFinishImage(String json, ChatResponse<ResultImageFile> chatResponse) {
        }

        default void onFinishFile(String json, ChatResponse<ResultFile> chatResponse) {
        }

        default void onError(String jsonError, ErrorOutPut error) {
        }
    }

    /**
     * @param bytesSent        - Bytes sent since the last time this callback was called.
     * @param totalBytesSent   - Total number of bytes sent so far.
     * @param totalBytesToSend - Total bytes to send.
     */
    public void onProgress(int bytesSent, int totalBytesSent, int totalBytesToSend) {
    }
}
