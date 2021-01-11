package com.example.flickrlibrary

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import com.example.flickrlibrary.Constants.Companion.ACCESS_TOKEN_PATH
import com.example.flickrlibrary.Constants.Companion.BASE_URL
import com.example.flickrlibrary.Constants.Companion.GET_GALLERY_REST_PATH
import com.example.flickrlibrary.Constants.Companion.OAUTH_CALLBACK
import com.example.flickrlibrary.Constants.Companion.OAUTH_TOKEN_KEY
import com.example.flickrlibrary.Constants.Companion.OAUTH_VERIFIER_KEY
import com.example.flickrlibrary.Constants.Companion.REQUEST_AUTHORIZE_PATH
import com.example.flickrlibrary.Constants.Companion.REQUEST_TOKEN_PATH
import com.example.flickrlibrary.Constants.Companion.UPLOAD_PHOTO_PATH
import com.example.flickrlibrary.model.*
import com.example.flickrlibrary.utils.FlickrUtils
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*


class Flickr(private val listener: FlickrOauthListener) : CoroutineScope {

    private val flickRepository = FlickrRepository()

    //Create coroutines job for this class
    private val job = Job()

    override val coroutineContext = job + Dispatchers.Main

    interface FlickrOauthListener {
        fun requestToken(token: String?)
        fun authenticationSuccess(results: AuthorizationResource)
        fun photoGalleries(galleries: Galleries)
        fun photoUrlList(urls: List<String>)
        fun onError(message: String?)
        fun loadingState(isLoading: Boolean)
    }

    fun getFlickAuthPageIntent(oauthToken: String): Intent {

        return Intent(
            Intent.ACTION_VIEW, Uri.parse(
                "$BASE_URL$REQUEST_AUTHORIZE_PATH?oauth_token=$oauthToken&oauth_callback=$OAUTH_CALLBACK"
            )
        )
    }


    /**
     *
     */
    fun getOauthVerifier(intent: Intent?) {

        intent?.data?.let { data ->

            val uri = data.toString()
            println(uri)
            val oauthToken = FlickrUtils.extractResponseStringValue(uri, OAUTH_TOKEN_KEY)
            println(oauthToken)
            val oauthVerifierToken = FlickrUtils.extractResponseStringValue(uri, OAUTH_VERIFIER_KEY)
            println(oauthVerifierToken)

            launch {
                val getAccessToken = async(Dispatchers.IO) {

                    listener.loadingState(true)

                    flickRepository.getAccessToken(
                        oauthVerifierToken,
                        oauthToken,
                        BASE_URL + ACCESS_TOKEN_PATH
                    )
                }

                when (val result = getAccessToken.await()) {

                    is CallResource.Success -> {
                        listener.loadingState(false)
                        listener.authenticationSuccess(result.authResource)
                    }
                    is CallResource.Failure -> {
                        listener.loadingState(false)
                        listener.onError(result.errorMessage)
                    }
                }

            }
        }


    }

    fun authorizeUser() {
        launch {
            val getRequestToken =
                async(Dispatchers.IO) {
                    listener.loadingState(true)
                    flickRepository.getRequestToken(BASE_URL + REQUEST_TOKEN_PATH)
                }

            when (val result = getRequestToken.await()) {

                is CallResource.Success -> {
                    listener.loadingState(false)
                    FlickrUtils.extractResponseStringValue(result.response, OAUTH_TOKEN_KEY)
                    listener.requestToken(result.response)
                }
                is CallResource.Failure -> {
                    listener.loadingState(false)
                    listener.onError(result.errorMessage)
                }
            }

        }

    }

    /**
     * Return the list of galleries created by a user. Sorted from newest to oldest.
     */
    fun getPhotoGalleries() {
        launch {
            val getGalleries =
                async(Dispatchers.IO) {
                    listener.loadingState(true)
                    flickRepository.getGalleries(BASE_URL + GET_GALLERY_REST_PATH)
                }

            when (val result = getGalleries.await()) {
                is CallResource.Success -> {
                    listener.loadingState(false)
                    val gson = GsonBuilder().create()
                    val galleryBase: GalleryBase = gson.fromJson(
                        result.response,
                        GalleryBase::class.java
                    )
                    if (galleryBase.galleries.gallery.isNotEmpty()) {

                        listener.photoGalleries(galleryBase.galleries)
                    } else {

                        listener.onError("Please first add a gallery")
                    }
                }
                is CallResource.Failure -> {
                    listener.loadingState(false)
                    listener.onError(result.errorMessage)
                }
            }

        }
    }

    /**
     * Call this after you retrieve galleryId from gallery list
     * Returns a list of photo urls for display to callback
     * [photoUrlList(urls: List<String>)]
     */
    fun getGalleryPhotos(galleryId: String?) {
        launch {
            val getGalleryPhotos =
                async(Dispatchers.IO) {
                    listener.loadingState(true)
                    flickRepository.getGalleryPhotos(BASE_URL + GET_GALLERY_REST_PATH, galleryId)

                }

            when (val result = getGalleryPhotos.await()) {
                is CallResource.Success -> {
                    listener.loadingState(false)

                    val gson = GsonBuilder().create()
                    val photoBase: PhotoBase = gson.fromJson(
                        result.response,
                        PhotoBase::class.java
                    )
                    val photoUrls = mutableListOf<String>()
                    if (photoBase.photos.photo.isNotEmpty()) {

                        photoBase.photos.photo.forEach { photo ->
                            val farmId = photo.farm
                            val serverId = photo.server
                            val photoId = photo.id
                            val secret = photo.secret
                            val photoUrl =
                                "https://farm$farmId.staticflickr.com/$serverId/${photoId}_$secret.jpg"

                            photoUrls.add(photoUrl)
                        }

                        listener.photoUrlList(photoUrls)
                    } else {
                        listener.loadingState(false)
                        listener.onError("Please first add a gallery with photos to your account")
                    }
                }
                is CallResource.Failure -> {
                    listener.loadingState(false)
                    listener.onError(result.errorMessage)
                }
            }

        }

    }

    fun uploadPhoto(image: Bitmap) {
        launch {
            val postImage =
                async(Dispatchers.IO) {
                    listener.loadingState(true)
                    flickRepository.uploadPhoto(BASE_URL + UPLOAD_PHOTO_PATH, image)
                }

            when (val result = postImage.await()) {

                is CallResource.Success -> {
                    listener.loadingState(false)
                    //FlickrUtils.extractResponseStringValue(result.response, OAUTH_TOKEN_KEY)

                }
                is CallResource.Failure -> {
                    listener.loadingState(false)
                    //listener.onError(result.errorMessage)
                }
            }

        }


//
//        val file = File(URI(path))
//        val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
//
//        FlickrRepository().uploadPhoto(body)
//            .subscribeOn(Schedulers.io())
//            .subscribe({ response ->
//
//                response.body()
//
//
//            }, {
//                println(it.message)
//
//            }).also { disposables.add(it) }

    }

}
