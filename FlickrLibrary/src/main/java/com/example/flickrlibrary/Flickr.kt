package com.example.flickrlibrary

import android.content.Intent
import android.net.Uri
import com.example.flickrlibrary.Constants.Companion.BASE_URL
import com.example.flickrlibrary.Constants.Companion.OAUTH_CALLBACK
import com.example.flickrlibrary.Constants.Companion.OAUTH_TOKEN_KEY
import com.example.flickrlibrary.Constants.Companion.OAUTH_TOKEN_SECRET_KEY
import com.example.flickrlibrary.Constants.Companion.OAUTH_VERIFIER_KEY
import com.example.flickrlibrary.Constants.Companion.REQUEST_AUTHORIZE_PATH
import com.example.flickrlibrary.Constants.Companion.USER_NSID_KEY
import com.example.flickrlibrary.model.Galleries
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.net.URI


class Flickr(private val listener: FlickrOauthListener) {

    interface FlickrOauthListener {
        fun requestToken(token: String)
        fun authenticationSuccess(results: Boolean)
        fun photoGalleries(galleries: Galleries)
        fun photoUrlList(urls: List<String>)
        fun onError(throwable: Throwable)
    }

    private val disposables = CompositeDisposable()

    fun getFlickAuthPageIntent(oauthToken: String): Intent {

        return Intent(
            Intent.ACTION_VIEW, Uri.parse(
                "$BASE_URL$REQUEST_AUTHORIZE_PATH?oauth_token=$oauthToken&oauth_callback=$OAUTH_CALLBACK"
            )
        )
    }

    /**
     * Return the list of galleries created by a user. Sorted from newest to oldest.
     */
    fun getPhotoGalleries() {

        FlickrRepository().getGalleryList(
            RetrofitInstance.nsId
        )
            .subscribeOn(Schedulers.io())
            .subscribe({ galleryBase ->

                galleryBase.body()
                    ?.let { base -> listener.photoGalleries(base.galleries) }

            }, {
                listener.onError(it)

            }).also { disposables.add(it) }
    }

    /**
     *
     */
    fun getOauthVerifier(intent: Intent?) {
        intent?.data?.let { data ->
            val uri = data.toString()

            RetrofitInstance.oAuthToken = extractStringValue(uri, OAUTH_TOKEN_KEY)

            val oauthVerifierToken = extractStringValue(uri, OAUTH_VERIFIER_KEY)

            FlickrRepository().getAccessToken(oauthVerifierToken)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    it.body()
                    it.errorBody()?.string()

                    //update final access token and secret with the new response for next calls
                    RetrofitInstance.oAuthToken =
                        extractStringValue(it.body().toString(), OAUTH_TOKEN_KEY)
                    RetrofitInstance.oAuthTokenSecret =
                        extractStringValue(it.body().toString(), OAUTH_TOKEN_SECRET_KEY)
                    RetrofitInstance.nsId = extractStringValue(it.body().toString(), USER_NSID_KEY)

                    listener.authenticationSuccess(true)

                }, {
                    listener.authenticationSuccess(false)
                    listener.onError(it)
                })

        }

    }

    fun uploadPhoto(path: String?) {

        val file = File(URI(path))
        val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        FlickrRepository().uploadPhoto(body)
            .subscribeOn(Schedulers.io())
            .subscribe({ response ->

                response.body()


            }, {
                println(it.message)

            }).also { disposables.add(it) }

    }

    fun authorizeUser() {
        FlickrRepository().getRequestToken()
            .subscribeOn(Schedulers.io())
            .subscribe({

                it.body()?.let { response ->
                    val oauthToken = extractStringValue(response, OAUTH_TOKEN_KEY)
                    RetrofitInstance.oAuthTokenSecret =
                        extractStringValue(response, OAUTH_TOKEN_SECRET_KEY)
                    listener.requestToken(oauthToken)

                }

            }, {
                listener.onError(it)

            })
            .also { disposables.add(it) }


    }

    private fun extractStringValue(stringResponse: String, key: String): String {
        var counter = 0
        val finalString = StringBuilder()
        val finalValue = StringBuilder()
        stringResponse.forEach { char ->
            if (char == key[counter] && counter <= key.length) {
                finalString.append(char)

                if (finalString.length < key.length) {
                    counter++
                }

            } else if (finalString.toString() != key) {
                counter = 0
                finalString.clear()
            }

            if (finalString.length == key.length && char.toString() != "=" && char.toString() != "&") {
                finalValue.append(char)
                counter = 0
            }
        }

        return finalValue.toString()
    }

    /**
     * Call this after you retrieve galleryId from gallery list
     * Returns a list of photo urls for display to callback
     * [photoUrlList(urls: List<String>)]
     */
    fun getGalleryPhotos(galleryId: String?): List<String> {
        val photoUrls = mutableListOf<String>()
        galleryId?.let { id ->
            FlickrRepository().getGalleryPhotos(id)
                .subscribeOn(Schedulers.io())
                .subscribe({

                    it.body()?.let { response ->

                        response.photos.photo.forEach { photo ->
                            val farmId = photo.farm
                            val serverId = photo.server
                            val photoId = photo.id
                            val secret = photo.secret
                            val url =
                                "https://farm$farmId.staticflickr.com/$serverId/${photoId}_$secret.jpg"

                            photoUrls.add(url)
                        }

                        listener.photoUrlList(photoUrls)
                    }

                }, {
                    listener.onError(it)

                })
                .also { disposables.add(it) }


        }
        return photoUrls
    }

}
