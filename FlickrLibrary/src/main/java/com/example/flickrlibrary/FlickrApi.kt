package com.example.flickrlibrary

import android.graphics.Bitmap
import com.example.flickrlibrary.Constants.Companion.GET_GALLERY_LIST
import com.example.flickrlibrary.Constants.Companion.GET_GALLERY_PHOTOS
import com.example.flickrlibrary.Constants.Companion.HTTP_METHOD_GET
import com.example.flickrlibrary.Constants.Companion.HTTP_METHOD_POST
import com.example.flickrlibrary.Constants.Companion.OAUTH_TOKEN_KEY
import com.example.flickrlibrary.Constants.Companion.OAUTH_TOKEN_SECRET_KEY
import com.example.flickrlibrary.Constants.Companion.OAUTH_USERNAME_KEY
import com.example.flickrlibrary.Constants.Companion.USER_NSID_KEY
import com.example.flickrlibrary.model.AuthorizationResource
import com.example.flickrlibrary.model.CallResource
import com.example.flickrlibrary.model.CallResource.Failure
import com.example.flickrlibrary.model.CallResource.Success
import com.example.flickrlibrary.network.Request
import com.example.flickrlibrary.utils.FlickrUtils
import java.net.URL


class FlickrApi {

    //API Endpoints
    /**
     * The first step to obtaining authorization for a user
     * is to get a Request Token using your Consumer Key.
     * This is a temporary token that will be used to authenticate
     * the user to your application. This token, along with a
     * token secret, will later be exchanged for an Access Token.
     */
    fun getRequestToken(url: String): CallResource {

        val parameterString = FlickrUtils.getParameterStringForOAuth()

        val oAuthSignature = FlickrUtils.createOauthSignature(parameterString, url, HTTP_METHOD_GET)
        println(oAuthSignature)

        // create URL
        val finalUrl = URL("$url?$parameterString&oauth_signature=$oAuthSignature")

        when (val resource = Request.makeGetRequest(finalUrl)) {

            is Success -> {
                resource.response?.let { result ->
                    //save the token secret in memory for next step requesting access token
                    //this token secret will now be apart of the signature
                    FlickrToken.oAuthTokenSecret =
                        FlickrUtils.extractResponseStringValue(result, OAUTH_TOKEN_SECRET_KEY)

                }
                return resource
            }
            is Failure -> {
                return resource

            }

        }

    }

    /**
     * Second step after the user authorizes the application, you can exchange
     * the approved Request Token for an Access Token. This Access Token
     * should be stored by your application, and used to make authorized
     * requests to Flickr.
     */
    fun getAccessToken(
        oAuthVerifier: String,
        oAuthToken: String,
        url: String
    ): CallResource {

        val parameterString = FlickrUtils.getParameterStringForOAuth(oAuthToken, oAuthVerifier)

        val oAuthSignature = FlickrUtils.createOauthSignature(parameterString, url, HTTP_METHOD_GET)
        println(oAuthSignature)

        // create URL
        val finalUrl = URL("$url?$parameterString&oauth_signature=$oAuthSignature")

        when (val resource = Request.makeGetRequest(finalUrl)) {

            is Success -> {
                resource.response?.let { result ->
                    //save credentials in memory and also just in case client side wants to
                    // save credentials and oauth token in sharedPrefference we pass a
                    // AuthorizationResource object

                    //This information can be saved and used later as many times as needed,
                    // without re-asking user's authorisation. The user can at any time revoke
                    // the authorisation in her Flickr user account.
                    FlickrToken.accessToken =
                        FlickrUtils.extractResponseStringValue(result, OAUTH_TOKEN_KEY)
                    FlickrToken.oAuthTokenSecret =
                        FlickrUtils.extractResponseStringValue(result, OAUTH_TOKEN_SECRET_KEY)
                    FlickrToken.nsId =
                        FlickrUtils.extractResponseStringValue(result, USER_NSID_KEY)
                    FlickrToken.username =
                        FlickrUtils.extractResponseStringValue(result, OAUTH_USERNAME_KEY)

                }
                return Success(
                    resource.response,
                    AuthorizationResource(
                        FlickrUtils.extractResponseStringValue(
                            resource.response,
                            OAUTH_TOKEN_KEY
                        ),
                        FlickrUtils.extractResponseStringValue(
                            resource.response,
                            OAUTH_TOKEN_SECRET_KEY
                        ),
                        FlickrUtils.extractResponseStringValue(resource.response, USER_NSID_KEY),
                        FlickrUtils.extractResponseStringValue(
                            resource.response,
                            OAUTH_USERNAME_KEY
                        ),
                        true
                    )
                )
            }
            is Failure -> {
                return resource

            }

        }

    }

    fun getGalleries(url: String): CallResource {
        val parameterString = FlickrUtils.getParameterStringForRest(
            FlickrToken.accessToken,
            GET_GALLERY_LIST,
            FlickrToken.nsId
        )

        val oAuthSignature = FlickrUtils.createOauthSignature(parameterString, url, HTTP_METHOD_GET)
        println(oAuthSignature)

        // create URL
        val finalUrl = URL("$url?$parameterString&oauth_signature=$oAuthSignature")

        return when (val resource = Request.makeGetRequest(finalUrl)) {

            is Success -> {
                resource
            }
            is Failure -> {
                resource

            }

        }

    }


    fun getGalleryPhotos(url: String, galleryId: String?): CallResource {
        val parameterString = FlickrUtils.getParameterStringForRest(
            FlickrToken.accessToken,
            GET_GALLERY_PHOTOS,
            FlickrToken.nsId,
            galleryId
        )

        val oAuthSignature = FlickrUtils.createOauthSignature(parameterString, url, HTTP_METHOD_GET)
        println(oAuthSignature)

        // create URL
        val finalUrl = URL("$url?$parameterString&oauth_signature=$oAuthSignature")
        return when (val resource = Request.makeGetRequest(finalUrl)) {

            is Success -> {
                resource
            }
            is Failure -> {
                resource

            }

        }

    }

    fun upLoadImage(url: String, image: Bitmap): CallResource {
        val parameterString = FlickrUtils.getParameterStringForRest(
            FlickrToken.accessToken,
            GET_GALLERY_LIST,
            FlickrToken.nsId
        )

        val oAuthSignature =
            FlickrUtils.createOauthSignature(parameterString, url, HTTP_METHOD_POST)
        println(oAuthSignature)


        // create URL
        val finalUrl = URL("$url?$parameterString&photo=MyPhoto&oauth_signature=$oAuthSignature")

        return when (val resource = Request.makePostImageRequest(finalUrl, image)) {

            is Success -> {
                resource
            }
            is Failure -> {
                resource

            }

        }


    }

}
