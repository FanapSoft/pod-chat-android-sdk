package com.fanap.podchat.networking.api;

import androidx.annotation.NonNull;

import com.fanap.podchat.mainmodel.SearchContactVO;
import com.fanap.podchat.mainmodel.UpdateContact;
import com.fanap.podchat.model.ContactRemove;
import com.fanap.podchat.model.Contacts;

import java.util.ArrayList;

import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface ContactApi {

    @NonNull
    @POST("nzh/addContacts")
    @FormUrlEncoded
    Observable<Response<Contacts>> addContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("firstName") String firstName
            , @Field("lastName") String lastName
            , @Field("email") String email
            , @Field("uniqueId") String uniqueId
            , @Field("cellphoneNumber") String cellphoneNumber
    );

    /* addContact Without type code */
    @NonNull
    @POST("nzh/addContacts")
    @FormUrlEncoded
    Observable<Response<Contacts>> addContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("firstName") String firstName
            , @Field("lastName") String lastName
            , @Field("email") String email
            , @Field("uniqueId") String uniqueId
            , @Field("cellphoneNumber") String cellphoneNumber
            , @Field("typeCode") String typeCode);


    /* addContact With username */
    @NonNull
    @POST("nzh/addContacts")
    @FormUrlEncoded
    Observable<Response<Contacts>> addContactWithUserName(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("firstName") String firstName
            , @Field("lastName") String lastName
            , @Field("username") String username
            , @Field("uniqueId") String uniqueId
            , @Field("typeCode") String typeCode
            , @Field("cellphoneNumber") String cellphoneNumber
            , @Field("email") String email
    );

    /* addContacts With type code*/
    @NonNull
    @POST("nzh/addContacts")
    @FormUrlEncoded
    Observable<Response<Contacts>> addContacts(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("firstName") ArrayList<String> firstName
            , @Field("lastName") ArrayList<String> lastName
            , @Field("email") ArrayList<String> email
            , @Field("uniqueId") ArrayList<String> uniqueId
            , @Field("cellphoneNumber") ArrayList<String> cellphoneNumber
            , @Field("typeCode") ArrayList<String> typeCode
    );

    /* addContacts Without type code*/
    @NonNull
    @POST("nzh/addContacts")
    @FormUrlEncoded
    Observable<Response<Contacts>> addContacts(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("firstName") ArrayList<String> firstName
            , @Field("lastName") ArrayList<String> lastName
            , @Field("email") ArrayList<String> email
            , @Field("uniqueId") ArrayList<String> uniqueId
            , @Field("cellphoneNumber") ArrayList<String> cellphoneNumber
    );

    /* removeContacts With type code*/
    @NonNull
    @POST("nzh/removeContacts")
    @FormUrlEncoded
    Observable<Response<ContactRemove>> removeContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("id") long userId
            , @Field("typeCode") String typeCode
    );

    /* removeContacts Without type code*/
    @NonNull
    @POST("nzh/removeContacts")
    @FormUrlEncoded
    Observable<Response<ContactRemove>> removeContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("id") long userId);

    /* Update contact without type code*/
    @NonNull
    @POST("nzh/updateContacts")
    @FormUrlEncoded
    Observable<Response<UpdateContact>> updateContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("id") long id
            , @Field("firstName") String firstName
            , @Field("lastName") String lastName
            , @Field("email") String email
            , @Field("uniqueId") String uniqueId
            , @Field("cellphoneNumber") String cellphoneNumber);

    /* Update contact with type code*/
    @NonNull
    @POST("nzh/updateContacts")
    @FormUrlEncoded
    Observable<Response<UpdateContact>> updateContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("id") long id
            , @Field("firstName") String firstName
            , @Field("lastName") String lastName
            , @Field("email") String email
            , @Field("uniqueId") String uniqueId
            , @Field("cellphoneNumber") String cellphoneNumber
            , @Field("typeCode") String typeCode);

    @NonNull
    @GET("nzh/listContacts")
    Observable<Response<SearchContactVO>> searchContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Query("id") String id
            , @Query("firstName") String firstName
            , @Query("lastName") String lastName
            , @Query("email") String email
            , @Query("uniqueId") String uniqueId
            , @Query("offset") String offset
            , @Query("size") String size
            , @Query("typeCode") String typeCode
            , @Query("q") String query
            , @Query("cellphoneNumber") String cellphoneNumber);
}
