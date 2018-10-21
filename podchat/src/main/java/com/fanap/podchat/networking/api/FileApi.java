package com.fanap.podchat.networking.api;

import com.fanap.podchat.model.FileImageUpload;
import com.fanap.podchat.mainmodel.FileUpload;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

public interface FileApi {
    @Multipart
    @POST("nzh/uploadFile")
    Observable<Response<FileUpload>> sendFile(
            @Part MultipartBody.Part file
            , @Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Part("fileName") RequestBody fileName);

    @Multipart
    @POST("nzh/uploadImage")
    Observable<Response<FileImageUpload>> sendImageFile(
            @Part MultipartBody.Part image
            , @Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Part("fileName") RequestBody fileName);

    @GET("nzh/file/")
    Observable<Response<ResponseBody>> getFile(@Query("fileId") int fileId
            , @Query("downloadable") boolean downloadable
            , @Query("hashCode") String hashCode);
}
