package com.fanap.podchat.networking.api;

import android.support.annotation.NonNull;

import com.fanap.podchat.chat.file_manager.upload_file.UploadToPodSpaceResponse;
import com.fanap.podchat.mainmodel.FileUpload;
import com.fanap.podchat.model.FileImageUpload;
import com.fanap.podchat.networking.ProgressResponseBody;

import java.util.ArrayList;

import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

public interface FileApi {


    @NonNull
    @Multipart
    @POST("/api/usergroups/{userGroupHash}/files")
    Observable<Response<UploadToPodSpaceResponse>> uploadToPodSpace(
            @Part MultipartBody.Part file,
            @Header("Authorization") String token,
            @Path("userGroupHash") String userGroupHash,
            @Part("description") String description
    );

    @NonNull
    @Multipart
    @POST("/api/files")
    Observable<Response<UploadToPodSpaceResponse>> uploadPublicFileToPodSpace(
            @Part MultipartBody.Part file,
             @Header("Authorization") String token,
            @Part("description") String description,
            @Part("isPublic") Boolean isPublic
    );


    @NonNull
    @Multipart
    @POST("/api/usergroups/{userGroupHash}/images")
    Observable<Response<UploadToPodSpaceResponse>> uploadImageToPodSpace(
            @Part MultipartBody.Part file,
            @Header("Authorization") String token,
            @Path("userGroupHash") String userGroupHash,
            @Part("description") String description,
            @Part("x") Integer xC,
            @Part("y") Integer yC,
            @Part("with") Integer wC
            );

    @NonNull
    @Multipart
    @POST("/api/images")
    Observable<Response<UploadToPodSpaceResponse>> uploadPublicImageToPodSpace(
            @Part MultipartBody.Part file
            , @Header("Authorization") String token,
            @Part("x") Integer xC,
            @Part("y") Integer yC,
            @Part("with") Integer wC,
            @Part("isPublic") Boolean isPublic);

    @GET
    @Streaming
    Call<ResponseBody> download(@Url String url);


    @GET("/api/files/{hash}")
    @Streaming
    Call<ResponseBody> downloadPodSpaceFile(
            @Path("hash") String hash,
            @Header("Authorization") String token);

    @GET("/api/images/{hash}")
    @Streaming
    Call<ResponseBody> downloadPodSpaceImage(
            @Path("hash") String hash,
            @Query("size") String size,
            @Query("quality") Float quality,
            @Query("crop") Boolean crop,
            @Query("checkUserGroupAccess") Boolean checkUserGroupAccess,
            @Header("Authorization") String token);


}
