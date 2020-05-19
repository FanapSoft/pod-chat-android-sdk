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
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

public interface FileApi {


    @NonNull
    @Multipart
    @POST("nzh/uploadFile")
    Observable<Response<FileUpload>> sendFile(
            @Part MultipartBody.Part file
            , @Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Part("fileName") RequestBody fileName);


    @NonNull
    @Multipart
    @POST("userGroup/uploadFile")
    Observable<Response<UploadToPodSpaceResponse>> uploadToPodSpace(
            @Part MultipartBody.Part file
            , @Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Part("filename") RequestBody fileName,
            @Part("userGroupHash") RequestBody userGroupHash);

    @NonNull
    @Multipart
    @POST("nzh/drive/uploadFile")
    Observable<Response<UploadToPodSpaceResponse>> uploadPublicFileToPodSpace(
            @Part MultipartBody.Part file
            , @Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Part("filename") RequestBody fileName,
            @Part("isPublic") Boolean isPublic
    );


    @NonNull
    @Multipart
    @POST("userGroup/uploadImage")
    Observable<Response<UploadToPodSpaceResponse>> uploadImageToPodSpace(
            @Part MultipartBody.Part file
            , @Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Part("filename") RequestBody fileName,
            @Part("userGroupHash") RequestBody userGroupHash,
            @Part("xC") RequestBody xC,
            @Part("yC") RequestBody yC,
            @Part("wC") RequestBody wC,
            @Part("hC") RequestBody hC);

    @NonNull
    @Multipart
    @POST("nzh/drive/uploadImage")
    Observable<Response<UploadToPodSpaceResponse>> uploadPublicImageToPodSpace(
            @Part MultipartBody.Part file
            , @Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Part("filename") RequestBody fileName,
            @Part("xC") RequestBody xC,
            @Part("yC") RequestBody yC,
            @Part("wC") RequestBody wC,
            @Part("hC") RequestBody hC,
            @Part("isPublic") Boolean isPublic);


    @NonNull
    @Multipart
    @POST("nzh/uploadFile")
    Call<Response<FileUpload>> sendFileRet(
            @Part MultipartBody.Part file
            , @Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Part("fileName") RequestBody fileName);


    @NonNull
    @Multipart
    @POST("nzh/uploadImage")
    Observable<Response<FileImageUpload>> sendImageFile(
            @Part MultipartBody.Part image
            , @Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Part("fileName") RequestBody fileName);

    @NonNull
    @POST("nzh/drive/uploadFileFromUrl")
    Observable<Response<FileUpload>> uploadFileFromUrl(
            @Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Part("fileName") String fileName
            , @Part("folderHash") String folderHash
            , @Part("metadata") String metadata
            , @Part("description") String description
            , @Part("isPublic") boolean isPublic
            , @Part("tags") ArrayList<String> tags
    );
//
//    @NonNull
//    @GET("nzh/file/")
//    @Streaming
//    Observable<Response<ResponseBody>> getFile
//            (@Query("fileId") int fileId
//                    , @Query("downloadable") boolean downloadable
//                    , @Query("hashCode") String hashCode);
//
//
//    @NonNull
//    @GET("nzh/image/")
//    @Streaming
//    Observable<Response<ResponseBody>> getImage
//            (@Query("fileId") int fileId
//                    , @Query("downloadable") boolean downloadable
//                    , @Query("hashCode") String hashCode);


    @NonNull
    @GET("nzh/file/")
    @Streaming
    Call<ResponseBody> getFile
            (@Query("fileId") long fileId
                    , @Query("downloadable") boolean downloadable
                    , @Query("hashCode") String hashCode);


    @NonNull
    @GET("nzh/image/")
    @Streaming
    Call<ResponseBody> getImage
            (@Query("fileId") long fileId
                    , @Query("downloadable") boolean downloadable
                    , @Query("hashCode") String hashCode);


    @GET
    @Streaming
    Call<ResponseBody> download(@Url String url);


    @GET("nzh/drive/downloadFile")
    @Streaming
    Call<ResponseBody> downloadPodSpaceFile(
            @Query("hash") String hash,
            @Header("_token_") String token,
            @Header("_token_issuer_") int tokenIssuer);

    @GET("nzh/drive/downloadImage")
    @Streaming
    Call<ResponseBody> downloadPodSpaceImage(
            @Query("hash") String hash,
            @Query("size") String size,
            @Query("quality") Float quality,
            @Query("crop") Boolean crop,
            @Header("_token_") String token,
            @Header("_token_issuer_") int tokenIssuer);


}
