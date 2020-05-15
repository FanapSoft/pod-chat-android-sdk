package com.fanap.podchat.chat.file_manager.upload_file;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.fanap.podchat.mainmodel.FileUpload;
import com.fanap.podchat.model.FileImageUpload;
import com.fanap.podchat.model.ResultFile;
import com.fanap.podchat.model.ResultImageFile;
import com.fanap.podchat.networking.ProgressRequestBody;
import com.fanap.podchat.networking.api.FileApi;
import com.fanap.podchat.networking.retrofithelper.RetrofitHelperFileServer;
import com.fanap.podchat.util.FilePick;
import com.fanap.podchat.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PodUploader {


    public interface IPodUploader {

        void onFailure(String cause);

        void onUploadStarted(String mimeType, File file, long length);

        void onProgressUpdate(int progress, int totalBytesSent, int totalBytesToSend);
    }


    public interface IPodUploadFileToPodSpace extends IPodUploader {
        void onSuccess(UploadToPodSpaceResult response, File file, String mimeType, long length);
    }

    public interface IPodUploadFile extends IPodUploader {
        void onSuccess(FileUpload response, File file, String mimeType, long length);
    }

    public interface IPodUploadImage extends IPodUploader {
        void onSuccess(FileImageUpload response, File file, String mimeType, long length);
    }


    public static Subscription uploadToPodSpace(
            String uniqueId,
            @NonNull Uri fileUri,
            String threadHashCode,
            Context context,
            String fileServer,
            String token,
            int tokenIssuer,
            IPodUploadFileToPodSpace listener) throws Exception {


        if (fileUri.getPath() == null) throw new NullPointerException("Invalid file uri!");
        String mimeType = FileUtils.getMimeType(fileUri,context);

        String path = FilePick.getSmartFilePath(context, fileUri);
        if (path == null) throw new NullPointerException("Invalid path!");

        File file = new File(path);

        if (!file.exists() || !file.isFile()) throw new FileNotFoundException("Invalid file!");

        listener.onUploadStarted(mimeType, file, file.length());

        RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(fileServer);
        FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);

        RequestBody namePart = RequestBody.create(MediaType.parse("multipart/form-data"), file.getName());
        RequestBody hashGroupPart = RequestBody.create(MediaType.parse("multipart/form-data"), threadHashCode);

        ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId, new ProgressRequestBody.UploadCallbacks() {

            @Override
            public void onProgress(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {

                listener.onProgressUpdate(progress, totalBytesSent, totalBytesToSend);
            }

        });

        MultipartBody.Part filePart = MultipartBody
                .Part.createFormData("file",
                        file.getName(),
                        requestFile);


        Observable<Response<UploadToPodSpaceResponse>> uploadObservable =
                fileApi.uploadToPodSpace(
                        filePart,
                        token,
                        tokenIssuer,
                        namePart,
                        hashGroupPart);


        return uploadObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> listener.onFailure(throwable.getMessage()))
                .subscribe(response -> {
                    if (response.isSuccessful()
                            && response.body() != null) {


                        if (response.body().isHasError()) {
                            listener.onFailure(response.body().getMessage());
                            return;
                        }

                        listener.onSuccess(response.body().getUploadToPodSpaceResult(), file, mimeType, file.length());

                    } else {

                        listener.onFailure(response.message());

                    }

                }, throwable -> listener.onFailure(throwable.getMessage()));


    }


    public static Subscription uploadFileToChatServer(
            String uniqueId,
            @NonNull Uri fileUri,
            Context context,
            String fileServer,
            String token,
            int tokenIssuer,
            IPodUploadFile listener) throws Exception {


        if (fileUri.getPath() == null) throw new NullPointerException("Invalid file uri!");
        String mimeType = FileUtils.getMimeType(fileUri,context);

        String path = FilePick.getSmartFilePath(context, fileUri);
        if (path == null) throw new NullPointerException("Invalid path!");

        File file = new File(path);

        if (!file.exists() || !file.isFile()) throw new FileNotFoundException("Invalid file!");

        listener.onUploadStarted(mimeType, file, file.length());

        RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(fileServer);
        FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);

        RequestBody namePart = RequestBody.create(MediaType.parse("multipart/form-data"), file.getName());

        ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId, new ProgressRequestBody.UploadCallbacks() {

            @Override
            public void onProgress(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {

                listener.onProgressUpdate(progress, totalBytesSent, totalBytesToSend);
            }

        });

        MultipartBody.Part filePart = MultipartBody
                .Part.createFormData("file",
                        file.getName(),
                        requestFile);


        Observable<Response<FileUpload>> uploadObservable =
                fileApi.sendFile(
                        filePart,
                        token,
                        tokenIssuer,
                        namePart);


        return uploadObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> listener.onFailure(uniqueId + " - " + error.getMessage()))
                .subscribe(response -> {
                    if (response.isSuccessful()
                            && response.body() != null) {

                        if (response.body().isHasError()) {
                            listener.onFailure(uniqueId + " - " + response.body().getMessage() + " - " + response.body().getReferenceNumber());
                        } else {
                            listener.onSuccess(response.body(), file, mimeType, file.length());
                        }

                    } else {

                        if (response.body() != null) {
                            listener.onFailure(uniqueId + " - " + response.body().getMessage() + " - " + response.body().getReferenceNumber());
                        } else {
                            listener.onFailure(uniqueId + " - " + response.message());

                        }


                    }

                });


    }

    public static Subscription uploadImageToChatServer(
            String uniqueId,
            @NonNull Uri fileUri,
            Context context,
            String fileServer,
            String token,
            int tokenIssuer,
            IPodUploadImage listener) throws Exception {



        if (fileUri.getPath() == null) throw new NullPointerException("Invalid file uri!");
        String mimeType = FileUtils.getMimeType(fileUri,context);

        String path = FilePick.getSmartFilePath(context, fileUri);
        if (path == null) throw new NullPointerException("Invalid path!");

        File file = new File(path);

        if (!file.exists() || !file.isFile()) throw new FileNotFoundException("Invalid file!");

        listener.onUploadStarted(mimeType, file, file.length());

        RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(fileServer);
        FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);

        RequestBody namePart = RequestBody.create(MediaType.parse("multipart/form-data"), file.getName());

        ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId, new ProgressRequestBody.UploadCallbacks() {

            @Override
            public void onProgress(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {

                listener.onProgressUpdate(progress, totalBytesSent, totalBytesToSend);
            }

        });

        MultipartBody.Part filePart = MultipartBody
                .Part.createFormData("image",
                        file.getName(),
                        requestFile);


        Observable<Response<FileImageUpload>> uploadObservable =
                fileApi.sendImageFile(
                        filePart,
                        token,
                        tokenIssuer,
                        namePart);


        return uploadObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> listener.onFailure(uniqueId + " - " + error.getMessage()))
                .subscribe(response -> {
                    if (response.isSuccessful()
                            && response.body() != null) {

                        if (response.body().isHasError()) {
                            listener.onFailure(uniqueId + " - " + response.body().getMessage() + " - " + response.body().getReferenceNumber());
                        } else {
                            listener.onSuccess(response.body(), file, mimeType, file.length());
                        }


                    } else {

                        if (response.body() != null) {
                            listener.onFailure(uniqueId + " - " + response.body().getMessage() + " - " + response.body().getReferenceNumber());
                        } else {
                            listener.onFailure(uniqueId + " - " + response.message());

                        }

                    }

                });


    }


    public static ResultFile generateFileUploadResult(UploadToPodSpaceResult response) {

        ResultFile result = new ResultFile();
        result.setId(0);
        result.setName(response.getName());
        result.setHashCode(response.getHashCode());
//        result.setDescription(response.);
        // TODO: 5/6/2020 ask for description

        result.setSize(response.getSize());
        result.setUrl(response.getParentHash());

        return result;

    }

    public static ResultImageFile generateImageUploadResult(UploadToPodSpaceResult response) {


        ResultImageFile result = new ResultImageFile();
        result.setId(0);
        result.setName(response.getName());
        result.setHashCode(response.getHashCode());
//        result.setDescription(response.getDescription());
        result.setUrl(response.getParentHash());

        return result;
    }


}
