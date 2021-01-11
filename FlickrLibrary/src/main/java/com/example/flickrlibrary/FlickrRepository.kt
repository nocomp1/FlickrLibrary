package com.example.flickrlibrary

import android.graphics.Bitmap
import com.example.flickrlibrary.model.CallResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FlickrRepository {

    private val flickApi = FlickrApi()

    suspend fun getRequestToken(url: String): CallResource {
        return withContext(Dispatchers.Default) {
            flickApi.getRequestToken(url)
        }

    }

    suspend fun getGalleries(url: String): CallResource {
        return withContext(Dispatchers.Default) {
            flickApi.getGalleries(url)
        }

    }

    suspend fun getGalleryPhotos(url: String, galleryId: String?): CallResource {
        return withContext(Dispatchers.Default) {
            flickApi.getGalleryPhotos(url, galleryId)
        }

    }

    suspend fun getAccessToken(
        oauthVerifierToken: String,
        oauthToken: String,
        url: String
    ): CallResource {
        return withContext(Dispatchers.Default) {
            flickApi.getAccessToken(oauthVerifierToken, oauthToken, url)
        }

    }

   suspend fun uploadPhoto(url: String,image :Bitmap) :CallResource{
       return withContext(Dispatchers.Default) {
           flickApi.upLoadImage(url,image)
    }

}
}

