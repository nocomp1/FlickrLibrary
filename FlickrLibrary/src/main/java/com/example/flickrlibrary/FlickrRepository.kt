package com.example.flickrlibrary

import com.example.flickrlibrary.Constants.Companion.OAUTH_CALLBACK
import com.example.flickrlibrary.model.GalleryBase
import com.example.flickrlibrary.model.PhotoBase
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Response

class FlickrRepository {

    fun getRequestToken(): Single<Response<String>> {
        return RetrofitInstance().api.getRequestToken(
            OAUTH_CALLBACK
        )
    }

    fun getAccessToken(
        oauthVerifier: String,
    ): Single<Response<String>> {
        return RetrofitInstance().api.getAccessToken(
            oauthVerifier,
        )
    }

    fun getGalleryList(
        userNsId: String,
    ): Single<Response<GalleryBase>> {
        return RetrofitInstance().api.getGalleries(
            userNsId
        )
    }

    fun getGalleryPhotos(galleryId: String): Single<Response<PhotoBase>> {
       return RetrofitInstance().api.getGalleryPhotos(galleryId)
    }

    fun uploadPhoto(body: MultipartBody.Part) :Single<Response<String>>{
        return RetrofitInstance().api.uploadImage(body)
    }
}

