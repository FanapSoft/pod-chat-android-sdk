package com.fanap.podchat.chat.file_manager.upload_file;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fanap.podchat.mainmodel.FileUpload;
import com.fanap.podchat.model.FileImageUpload;
import com.fanap.podchat.model.ResultFile;
import com.fanap.podchat.model.ResultImageFile;
import com.fanap.podchat.networking.ProgressRequestBody;
import com.fanap.podchat.networking.api.FileApi;
import com.fanap.podchat.networking.retrofithelper.RetrofitHelperFileServer;
import com.fanap.podchat.util.FilePick;
import com.fanap.podchat.util.FileUtils;
import com.fanap.podchat.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.fanap.podchat.chat.Chat.TAG;

public class PodUploader {


    public interface IPodUploader {

        void onFailure(String cause);

        void onUploadStarted(String mimeType, File file, long length);

        void onProgressUpdate(int progress, int totalBytesSent, int totalBytesToSend);
    }

    public interface IPodUploadFileToPodSpace extends IPodUploader {
        default void onSuccess(UploadToPodSpaceResult response, File file, String mimeType, long length) {
        }

        default void onSuccess(UploadToPodSpaceResult response, File file, String mimeType, long length, int actualWidth, int ActualHeight, int width, int height) {
        }
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
        String mimeType = FileUtils.getMimeType(fileUri, context);

        String path = FilePick.getSmartFilePath(context, fileUri);

        if (path == null) throw new NullPointerException("Invalid path!");

        File file = new File(path);

        if (!file.exists() || !file.isFile()) throw new FileNotFoundException("Invalid file!");


        long fileSize = 0;

        try {
            fileSize = file.length();
        } catch (Exception x) {
            Log.e(TAG, "File length exception: " + x.getMessage());
        }

        if (mimeType != null && FileUtils.isImage(mimeType) && !FileUtils.isGif(mimeType)) {

            return uploadImageToPodSpace(uniqueId, threadHashCode, fileServer, token, tokenIssuer, "", "", "", "", listener, mimeType, file, fileSize);

        }

        return uploadFileToPodSpace(uniqueId, threadHashCode, fileServer, token, tokenIssuer, listener, mimeType, file, fileSize);

    }


    public static Subscription uploadToPodSpace(
            String uniqueId,
            @NonNull Uri fileUri,
            String threadHashCode,
            Context context,
            String fileServer,
            String token,
            int tokenIssuer,
            String xC,
            String yC,
            String hC,
            String wC,
            IPodUploadFileToPodSpace listener) throws Exception {


        if (fileUri.getPath() == null) throw new NullPointerException("Invalid file uri!");
        String mimeType = FileUtils.getMimeType(fileUri, context);

        String path = FilePick.getSmartFilePath(context, fileUri);

        if (path == null) throw new NullPointerException("Invalid path!");

        File file = new File(path);

        if (!file.exists() || !file.isFile()) throw new FileNotFoundException("Invalid file!");


        long fileSize = 0;

        try {
            fileSize = file.length();
        } catch (Exception x) {
            Log.e(TAG, "File length exception: " + x.getMessage());
        }


        if (mimeType != null && FileUtils.isImage(mimeType) && !FileUtils.isGif(mimeType)) {

            return uploadImageToPodSpace(uniqueId, threadHashCode, fileServer, token, tokenIssuer, xC, yC, hC, wC, listener, mimeType, file, fileSize);

        }

        return uploadFileToPodSpace(uniqueId, threadHashCode, fileServer, token, tokenIssuer, listener, mimeType, file, fileSize);

    }




    public static Subscription uploadPublicToPodSpace(
            String uniqueId,
            @NonNull Uri fileUri,
            Context context,
            String fileServer,
            String token,
            int tokenIssuer,
            String xC,
            String yC,
            String hC,
            String wC,
            IPodUploadFileToPodSpace listener) throws Exception {


        if (fileUri.getPath() == null) throw new NullPointerException("Invalid file uri!");
        String mimeType = FileUtils.getMimeType(fileUri, context);

        String path = FilePick.getSmartFilePath(context, fileUri);

        if (path == null) throw new NullPointerException("Invalid path!");

        File file = new File(path);

        if (!file.exists() || !file.isFile()) throw new FileNotFoundException("Invalid file!");


        long fileSize = 0;

        try {
            fileSize = file.length();
        } catch (Exception x) {
            Log.e(TAG, "File length exception: " + x.getMessage());
        }


        if (mimeType != null && FileUtils.isImage(mimeType) && !FileUtils.isGif(mimeType)) {

            return uploadPublicImageToPodSpace(uniqueId, fileServer, token, tokenIssuer, xC, yC, hC, wC, listener, mimeType, file, fileSize);

        }

        return uploadPublicFileToPodSpace(uniqueId, fileServer, token, tokenIssuer, listener, mimeType, file, fileSize);

    }

public static Subscription uploadPublicToPodSpace(
            String uniqueId,
            @NonNull Uri fileUri,
            Context context,
            String fileServer,
            String token,
            int tokenIssuer,
            IPodUploadFileToPodSpace listener) throws Exception {


        if (fileUri.getPath() == null) throw new NullPointerException("Invalid file uri!");
        String mimeType = FileUtils.getMimeType(fileUri, context);

        String path = FilePick.getSmartFilePath(context, fileUri);

        if (path == null) throw new NullPointerException("Invalid path!");

        File file = new File(path);

        if (!file.exists() || !file.isFile()) throw new FileNotFoundException("Invalid file!");


        long fileSize = 0;

        try {
            fileSize = file.length();
        } catch (Exception x) {
            Log.e(TAG, "File length exception: " + x.getMessage());
        }


        if (mimeType != null && FileUtils.isImage(mimeType) && !FileUtils.isGif(mimeType)) {

            return uploadPublicImageToPodSpace(uniqueId, fileServer, token, tokenIssuer, "", "", "", "", listener, mimeType, file, fileSize);

        }

        return uploadPublicFileToPodSpace(uniqueId, fileServer, token, tokenIssuer, listener, mimeType, file, fileSize);

    }




    private static Subscription uploadFileToPodSpace(String uniqueId, String threadHashCode, String fileServer, String token, int tokenIssuer, IPodUploadFileToPodSpace listener, String mimeType, File file, long fileSize) {

        listener.onUploadStarted(mimeType, file, fileSize);

        RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(fileServer);
        FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);

        RequestBody namePart = RequestBody.create(MediaType.parse("multipart/form-data"), file.getName());

        RequestBody hashGroupPart = RequestBody.create(MediaType.parse("multipart/form-data"), threadHashCode);

        ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId, new ProgressRequestBody.UploadCallbacks() {

            @Override
            public void onProgress(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {

                if (progress < 95)
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


        long finalFileSize = fileSize;

        return uploadObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> listener.onFailure(error.getMessage()))
                .subscribe(response -> {
                    if (response.isSuccessful()
                            && response.body() != null) {

                        if (response.body().isHasError()) {
                            listener.onFailure(response.body().getMessage());
                            return;
                        }

                        listener.onProgressUpdate(100, (int) finalFileSize, 0);

                        listener.onSuccess(response.body().getUploadToPodSpaceResult(), file, mimeType, finalFileSize);
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                listener.onFailure(response.errorBody().string());
                            } else {
                                listener.onFailure(response.message());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }, error -> listener.onFailure(error.getMessage()));
    }

    private static Subscription uploadImageToPodSpace(String uniqueId, String threadHashCode, String fileServer, String token, int tokenIssuer, String xC, String yC, String hC, String wC, IPodUploadFileToPodSpace listener, String mimeType, File file, long fileSize) throws FileNotFoundException {
        int width = 0;
        int height = 0;
        String nWidth = "0";
        String nHeight = "0";
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            width = options.outWidth;
            height = options.outHeight;

            nWidth = !Util.isNullOrEmpty(wC) ? wC : String.valueOf(width);
            nHeight = !Util.isNullOrEmpty(hC) ? hC : String.valueOf(height);

        } catch (Exception e) {
            throw new FileNotFoundException("Invalid image!");
        }


        listener.onUploadStarted(mimeType, file, fileSize);

        RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(fileServer);
        FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);

        RequestBody namePart = RequestBody.create(MediaType.parse("multipart/form-data"), file.getName());

        RequestBody hashGroupPart = RequestBody.create(MediaType.parse("multipart/form-data"), threadHashCode);


        RequestBody xCPart = RequestBody.create(MediaType.parse("multipart/form-data"), !Util.isNullOrEmpty(xC) ? xC : "0");
        RequestBody yCPart = RequestBody.create(MediaType.parse("multipart/form-data"), !Util.isNullOrEmpty(yC) ? yC : "0");
        RequestBody hCPart = RequestBody.create(MediaType.parse("multipart/form-data"), nWidth);
        RequestBody wCPart = RequestBody.create(MediaType.parse("multipart/form-data"), nHeight);


        ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId, new ProgressRequestBody.UploadCallbacks() {

            @Override
            public void onProgress(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {

                if (progress < 95)
                    listener.onProgressUpdate(progress, totalBytesSent, totalBytesToSend);
            }

        });

        MultipartBody.Part filePart = MultipartBody
                .Part.createFormData("file",
                        file.getName(),
                        requestFile);

        Observable<Response<UploadToPodSpaceResponse>> uploadObservable =
                fileApi.uploadImageToPodSpace(
                        filePart,
                        token,
                        tokenIssuer,
                        namePart,
                        hashGroupPart,
                        xCPart,
                        yCPart,
                        wCPart,
                        hCPart);


        int finalWidth = width;
        int finalHeight = height;
        String finalNWidth = nWidth;
        String finalNHeight = nHeight;
        return uploadObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> listener.onFailure(error.getMessage()))
                .subscribe(response -> {
                    if (response.isSuccessful()
                            && response.body() != null) {

                        if (response.body().isHasError()) {
                            listener.onFailure(response.body().getMessage());
                            return;
                        }

                        listener.onProgressUpdate(100, (int) fileSize, 0);

                        listener.onSuccess(response.body().getUploadToPodSpaceResult(), file, mimeType, fileSize
                                , finalWidth, finalHeight, Integer.parseInt(finalNWidth), Integer.parseInt(finalNHeight));
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                listener.onFailure(response.errorBody().string());
                            } else {
                                listener.onFailure(response.message());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }, error -> listener.onFailure(error.getMessage()));
    }





    public static Subscription uploadPublicFileToPodSpace(String uniqueId, String fileServer, String token, int tokenIssuer, IPodUploadFileToPodSpace listener, String mimeType, File file, long fileSize) {

        listener.onUploadStarted(mimeType, file, fileSize);

        RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(fileServer);
        FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);

        RequestBody namePart = RequestBody.create(MediaType.parse("multipart/form-data"), file.getName());

        ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId, new ProgressRequestBody.UploadCallbacks() {

            @Override
            public void onProgress(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {

                if (progress < 95)
                    listener.onProgressUpdate(progress, totalBytesSent, totalBytesToSend);
            }

        });

        MultipartBody.Part filePart = MultipartBody
                .Part.createFormData("file",
                        file.getName(),
                        requestFile);

        Observable<Response<UploadToPodSpaceResponse>> uploadObservable =
                fileApi.uploadPublicFileToPodSpace(
                        filePart,
                        token,
                        tokenIssuer,
                        namePart,
                        true);


        return uploadObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> listener.onFailure(error.getMessage()))
                .subscribe(response -> {
                    if (response.isSuccessful()
                            && response.body() != null) {

                        if (response.body().isHasError()) {
                            listener.onFailure(response.body().getMessage());
                            return;
                        }

                        listener.onProgressUpdate(100, (int) fileSize, 0);

                        listener.onSuccess(response.body().getUploadToPodSpaceResult(), file, mimeType, fileSize);
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                listener.onFailure(response.errorBody().string());
                            } else {
                                listener.onFailure(response.message());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }, error -> listener.onFailure(error.getMessage()));
    }

    public static Subscription uploadPublicImageToPodSpace(String uniqueId, String fileServer, String token, int tokenIssuer, String xC, String yC, String hC, String wC, IPodUploadFileToPodSpace listener, String mimeType, File file, long fileSize) throws FileNotFoundException {
        int width = 0;
        int height = 0;
        String nWidth = "";
        String nHeight = "";
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            width = options.outWidth;
            height = options.outHeight;

            nWidth = !Util.isNullOrEmpty(wC) && Long.parseLong(wC) > 0 ? wC : String.valueOf(width);
            nHeight = !Util.isNullOrEmpty(hC) && Long.parseLong(hC) > 0 ? hC : String.valueOf(height);

        } catch (Exception e) {
            throw new FileNotFoundException("Invalid image!");
        }


        listener.onUploadStarted(mimeType, file, fileSize);

        RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(fileServer);
        FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);

        RequestBody namePart = RequestBody.create(MediaType.parse("multipart/form-data"), file.getName());

        RequestBody xCPart = RequestBody.create(MediaType.parse("multipart/form-data"), !Util.isNullOrEmpty(xC) ? xC : "0");
        RequestBody yCPart = RequestBody.create(MediaType.parse("multipart/form-data"), !Util.isNullOrEmpty(yC) ? yC : "0");
        RequestBody hCPart = RequestBody.create(MediaType.parse("multipart/form-data"), nWidth);
        RequestBody wCPart = RequestBody.create(MediaType.parse("multipart/form-data"), nHeight);


        ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId, new ProgressRequestBody.UploadCallbacks() {

            @Override
            public void onProgress(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {

                if (progress < 95)
                    listener.onProgressUpdate(progress, totalBytesSent, totalBytesToSend);
            }

        });

        MultipartBody.Part filePart = MultipartBody
                .Part.createFormData("file",
                        file.getName(),
                        requestFile);

        Observable<Response<UploadToPodSpaceResponse>> uploadObservable =
                fileApi.uploadPublicImageToPodSpace(
                        filePart,
                        token,
                        tokenIssuer,
                        namePart,
                        xCPart,
                        yCPart,
                        wCPart,
                        hCPart,
                        true);


        int finalWidth = width;
        int finalHeight = height;
        String finalNWidth = nWidth;
        String finalNHeight = nHeight;
        return uploadObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> listener.onFailure(error.getMessage()))
                .subscribe(response -> {
                    if (response.isSuccessful()
                            && response.body() != null) {

                        if (response.body().isHasError()) {
                            listener.onFailure(response.body().getMessage());
                            return;
                        }

                        listener.onProgressUpdate(100, (int) fileSize, 0);

                        listener.onSuccess(response.body().getUploadToPodSpaceResult(), file, mimeType, fileSize
                                , finalWidth, finalHeight, Integer.parseInt(finalNWidth), Integer.parseInt(finalNHeight));
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                listener.onFailure(response.errorBody().string());
                            } else {
                                listener.onFailure(response.message());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }, error -> listener.onFailure(error.getMessage()));
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
        String mimeType = FileUtils.getMimeType(fileUri, context);

        String path = FilePick.getSmartFilePath(context, fileUri);
        if (path == null) throw new NullPointerException("Invalid path!");

        File file = new File(path);

        if (!file.exists() || !file.isFile()) throw new FileNotFoundException("Invalid file!");

        long fileSize = 0;

        try {
            fileSize = file.length();
        } catch (Exception x) {
            Log.e(TAG, "File length exception: " + x.getMessage());
        }

        listener.onUploadStarted(mimeType, file, fileSize);

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


        long finalFileSize = fileSize;
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
                            listener.onSuccess(response.body(), file, mimeType, finalFileSize);
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
        String mimeType = FileUtils.getMimeType(fileUri, context);

        String path = FilePick.getSmartFilePath(context, fileUri);
        if (path == null) throw new NullPointerException("Invalid path!");

        File file = new File(path);

        if (!file.exists() || !file.isFile()) throw new FileNotFoundException("Invalid file!");


        long fileSize = 0;

        try {
            fileSize = file.length();
        } catch (Exception x) {
            Log.e(TAG, "File length exception: " + x.getMessage());
        }


        listener.onUploadStarted(mimeType, file, fileSize);

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


        long finalFileSize = fileSize;
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
                            listener.onSuccess(response.body(), file, mimeType, finalFileSize);
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
