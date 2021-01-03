package com.example.flickrlibrary

import com.example.flickrlibrary.model.GalleryBase
import com.example.flickrlibrary.model.PhotoBase
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*


interface FlickrApi {

    //API Endpoints
    /**
     * The first step to obtaining authorization for a user
     * is to get a Request Token using your Consumer Key.
     * This is a temporary token that will be used to authenticate
     * the user to your application. This token, along with a
     * token secret, will later be exchanged for an Access Token.
     */

    @GET("oauth/request_token")
    fun getRequestToken(
        @Query("oauth_callback") oauthCallback: String,
    ): Single<Response<String>>

    /**
     * After the user authorizes your application, you can exchange
     * the approved Request Token for an Access Token. This Access Token
     * should be stored by your application, and used to make authorized
     * requests to Flickr.
     */

    @GET("oauth/access_token/")
    fun getAccessToken(
        @Query("oauth_verifier") oauthVerifier: String,
    ): Single<Response<String>>

    @GET("rest?nojsoncallback=1&format=json&method=flickr.galleries.getList")
    fun getGalleries(
        @Query("user_id", encoded = true) userNsId: String,
    ): Single<Response<GalleryBase>>

    /**
     * Return the list of photos for a gallery
     */
    @GET("rest?nojsoncallback=1&format=json&method=flickr.galleries.getPhotos")
    fun getGalleryPhotos(
        @Query("gallery_id", encoded = true) galleryId: String
    ): Single<Response<PhotoBase>>

    /* New File upload and order creation methods */
    @Multipart
    @POST("upload/")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Single<Response<String>>

}
