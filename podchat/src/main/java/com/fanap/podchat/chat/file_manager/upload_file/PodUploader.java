package com.fanap.podchat.chat.file_manager.upload_file;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.fanap.podchat.ProgressHandler;
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

        void onSuccess(UploadToPodSpaceResponse response, File file, String mimeType, long length);

        void onFailure(String cause);

        void onUploadStarted(String mimeType, File file, long length);

    }


    public static Subscription uploadToPodSpace(
            String uniqueId,
            @NonNull Uri fileUri,
            String threadHashCode,
            ProgressHandler.sendFileMessage handler,
            Context context,
            String fileServer,
            String token,
            int tokenIssuer,

            IPodUploader listener) throws Exception {


        if (fileUri.getPath() == null) throw new NullPointerException("Invalid file uri!");
        String mimeType = FileUtils.getMimeType(new File(fileUri.getPath()));

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

                if (handler != null)
                    handler.onProgressUpdate(uniqueId, progress, totalBytesSent, totalBytesToSend);
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
                .subscribe(response -> {
                    if (response.isSuccessful()
                            && response.body() != null) {

                        listener.onSuccess(response.body(),file,mimeType,file.length());

                    } else {

                        listener.onFailure(response.message());

                    }

                });


    }
}
