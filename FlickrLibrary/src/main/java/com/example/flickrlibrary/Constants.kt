package com.example.flickrlibrary

class Constants {
    companion object {
        const val BASE_URL = "https://www.flickr.com/services/"
        const val REQUEST_TOKEN_PATH = "oauth/request_token"
        const val UPLOAD_PHOTO_PATH = "upload/"
        const val ACCESS_TOKEN_PATH = "oauth/access_token"
        const val REQUEST_AUTHORIZE_PATH = "oauth/authorize"
        const val GET_GALLERY_REST_PATH = "rest"

        const val OAUTH_CALLBACK = "flickr%3A%2F%2Fcallback"
        const val CONSUMER_KEY = "a59847aa317c742eac8d585fca341cd2"
        const val CONSUMER_SECRET = "2b1e43d1941bf4dd"

        const val HTTP_METHOD_GET = "GET"
        const val HTTP_METHOD_POST = "POST"

        //Keys used to extract string values inside custom method
        const val OAUTH_VERIFIER_KEY = "oauth_verifier="
        const val OAUTH_TOKEN_KEY = "oauth_token="
        const val OAUTH_TOKEN_SECRET_KEY = "oauth_token_secret="
        const val OAUTH_USERNAME_KEY = "username="
        const val USER_NSID_KEY = "user_nsid="

        //api methods
        const val GET_GALLERY_LIST = "flickr.galleries.getList"
        const val GET_GALLERY_PHOTOS = "flickr.galleries.getPhotos"



    }
}