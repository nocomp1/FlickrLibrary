package com.example.flickrproject

import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.flickrlibrary.Flickr
import com.example.flickrlibrary.model.AuthorizationResource
import com.example.flickrlibrary.model.Galleries

class ExampleFlickrViewModel : ViewModel(), Flickr.FlickrOauthListener {

    private var flickr: Flickr = Flickr(this)

    //Just for example purpose you will access the gallery id when user
    //selects a gallery.
    private var flickrGalId: String? = null

    private val _viewState = MediatorLiveData<ViewState>()
    val viewState: LiveData<ViewState> get() = _viewState

    private val _events: MutableLiveData<Events> = MediatorLiveData()
    val event: LiveData<Events> get() = _events

    private val flickrPhotoUrls = MutableLiveData<List<String>>()
        .also { _viewState.addSource(it) { combineLatest() } }

    private val flickrGalleries = MutableLiveData<Galleries>()
        .also { _viewState.addSource(it) { combineLatest() } }

    private val isFlickrUserAuth = MutableLiveData<Boolean>()
        .also { _viewState.addSource(it) { combineLatest() } }

    private val flickrIsLoading = MutableLiveData<Boolean>()
        .also { _viewState.addSource(it) { combineLatest() } }

    private fun combineLatest() {
        ViewState(
            flickrGalleries = flickrGalleries.value,
            flickrGalleryPhotoUrls = flickrPhotoUrls.value ?: emptyList(),
            isFlickrUserAuth = isFlickrUserAuth.value ?: false,
            isLoading = flickrIsLoading.value ?: false

        ).apply { _viewState.value = copy() }
    }

    data class ViewState(
        val flickrGalleryPhotoUrls: List<String>,
        val flickrGalleries: Galleries?,
        val isFlickrUserAuth: Boolean,
        val isLoading: Boolean
    )

    sealed class Action {
        object AuthenticateFlickrUser : Action()
        object GetFlickrGalleries : Action()
        object GetFlickrGalleryPhotos : Action()
        data class GetOauthVerifier(val data: Intent) : Action()
        data class UploadImageToFlickr(val image: Bitmap) : Action()
    }

    sealed class Events {
        data class RequestIntentForFlickrAuthPage(val flickerAuthPageIntent: Intent) : Events()
        data class IsUserAuthenticated(val results: Boolean) : Events()
        data class OnError(val message: String?) : Events()
    }


    fun onAction(action: Action) {
        when (action) {
            Action.AuthenticateFlickrUser -> {
                flickr.authorizeUser()
            }
            Action.GetFlickrGalleries -> flickr.getPhotoGalleries()
            Action.GetFlickrGalleryPhotos -> {

                // "JUST FOR EXAMPLE ONLY CALL THIS AFTER USER SELECTS A GALLERY" +
                //         "AND PASS IN THE ID THROUGH THE ACTION"

                if (flickrGalId != null) {
                    flickr.getGalleryPhotos(flickrGalId)
                } else {
                    _events.postValue(Events.OnError("Please add a gallery to your account"))
                }
            }
            is Action.GetOauthVerifier -> flickr.getOauthVerifier(action.data)
            is Action.UploadImageToFlickr -> flickr.uploadPhoto(action.image)
        }
    }


    override fun requestToken(token: String?) {
        token?.let { tokenForIntent ->
            _events.postValue(
                Events.RequestIntentForFlickrAuthPage(
                    flickr.getFlickAuthPageIntent(
                        tokenForIntent
                    )
                )
            )
        }

    }

    override fun authenticationSuccess(results: AuthorizationResource) {

        //Library already persist oauth_tokens but only until app is destroyed
        //the AuthorizationResource can be use for storing in shared Prefs or other methods
        //This information can be saved and used later as many times as needed,
        // without re-asking user's authorisation. The user can at any time revoke
        // the authorisation in her Flickr user account.

        _events.postValue(Events.IsUserAuthenticated(results.isAuthorized))

    }

    override fun photoGalleries(galleries: Galleries) {
        flickrGalleries.postValue(galleries)

        //Just for example purpose - gallery id should be set
        //when user selects a gallery
        flickrGalId = galleries.gallery[0].gallery_id

    }

    override fun photoUrlList(urls: List<String>) {
        flickrPhotoUrls.postValue(urls)
    }

    override fun onError(message: String?) {
        _events.postValue(Events.OnError(message))
    }

    override fun loadingState(isLoading: Boolean) {
        flickrIsLoading.postValue(isLoading)

    }
}