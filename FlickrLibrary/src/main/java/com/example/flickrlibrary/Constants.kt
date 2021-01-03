package com.example.flickrlibrary

class Constants {
    companion object {
        const val BASE_URL = "https://www.flickr.com/services/"
        const val REQUEST_TOKEN_PATH = "oauth/request_token"
        const val REQUEST_AUTHORIZE_PATH = "oauth/authorize"

        const val OAUTH_CALLBACK = "flickr://callback"
        const val CONSUMER_KEY = "a59847aa317c742eac8d585fca341cd2"
        const val CONSUMER_SECRET = "2b1e43d1941bf4dd"

        //Keys used to extract string values inside custom method
        const val OAUTH_VERIFIER_KEY = "oauth_verifier="
        const val OAUTH_TOKEN_KEY = "oauth_token="
        const val OAUTH_TOKEN_SECRET_KEY = "oauth_token_secret="
        const val USER_NSID_KEY = "user_nsid="


        //  "https://www.flickr.com/services/oauth/request_token?oauth_callback=flickr%3A%2F%2Fcallback&oauth_consumer_key=a59847aa317c742eac8d585fca341cd2&oauth_nonce=FW2hJiDnEfocyAzqWx8BmG5L4ckH1I1R&oauth_signature=qgDVbw8QYvW188ZeiN%2FAOlNOPQY%3D&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1608913596&oauth_version=1.0"

    }
}