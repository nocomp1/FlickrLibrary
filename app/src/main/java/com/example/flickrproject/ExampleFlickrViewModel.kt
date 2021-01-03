package com.example.flickrproject

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.flickrlibrary.Flickr
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

    private fun combineLatest() {
        ViewState(
            flickrGalleries = flickrGalleries.value ?: null,
            flickrGalleryPhotoUrls = flickrPhotoUrls.value ?: emptyList(),
            isFlickrUserAuth = isFlickrUserAuth.value ?: false

        ).apply { _viewState.value = copy() }
    }

    data class ViewState(
        val flickrGalleryPhotoUrls: List<String>,
        val flickrGalleries: Galleries?,
        val isFlickrUserAuth: Boolean

    )

    sealed class Action {
        object AuthenticateFlickrUser : Action()
        object GetFlickrGalleries : Action()
        object GetFlickrGalleryPhotos : Action()
        data class GetOauthVerifier(val data: Intent) : Action()
    }

    sealed class Events {
        data class RequestIntentForFlickrAuthPage(val flickerAuthPageIntent: Intent) : Events()
        data class IsUserAuthenticated(val results: Boolean) : Events()
        data class onError(val throwable: Throwable) : Events()
    }


    fun onAction(action: Action) {
        when (action) {
            Action.AuthenticateFlickrUser -> flickr.authorizeUser()
            Action.GetFlickrGalleries -> flickr.getPhotoGalleries()
            Action.GetFlickrGalleryPhotos -> {

                // "JUST FOR EXAMPLE ONLY CALL THIS AFTER USER SELECTS A GALLERY" +
                //         "AND PASS IN THE ID THROUGH THE ACTION"

                if (flickrGalId != null) {
                    flickr.getGalleryPhotos(flickrGalId)
                }
            }
            is Action.GetOauthVerifier -> flickr.getOauthVerifier(action.data)
        }
    }


    override fun requestToken(token: String) {
        _events.postValue(Events.RequestIntentForFlickrAuthPage(flickr.getFlickAuthPageIntent(token)))
    }

    override fun authenticationSuccess(results: Boolean) {
        _events.postValue(Events.IsUserAuthenticated(results))
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

    override fun onError(throwable: Throwable) {
        _events.postValue(Events.onError(throwable))
    }
}