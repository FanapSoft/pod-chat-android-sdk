package com.fanap.podchat;

import com.fanap.podchat.chat.file_manager.download_file.model.ResultDownloadFile;
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

        default void onProgressUpdate(int bytesSent) {

        }

        default void onProgress(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
        }

        default void onFinish(String imageJson, FileUpload fileImageUpload) {
        }

        default void onImageFinish(String imageJson, ChatResponse<ResultImageFile> chatResponse) {
        }

        default void onError(String jsonError, ErrorOutPut error) { }
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


    public interface IDownloadFile {


        default void onProgressUpdate(String uniqueId, int bytesDownloaded, int totalBytesToDownload){}
        default void onProgressUpdate(String uniqueId, int progress){}
        void onError(String uniqueId, String error,String url);
        default void onLowFreeSpace(String uniqueId,String url){}
        default void onFileReady(ChatResponse<ResultDownloadFile> response){}
        default void onProgressUpdate(long bytesRead, long contentLength, boolean done){}


    }

    public interface cancelUpload {
        default void cancelUpload(String uniqueId) {

        }
    }

    public void onProgress(int bytesSent, int totalBytesSent, int totalBytesToSend) {
    }
}
