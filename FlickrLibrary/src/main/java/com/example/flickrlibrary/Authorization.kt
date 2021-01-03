package com.example.flickrlibrary

import com.google.gson.annotations.SerializedName

data class RequestToken(
    @SerializedName("oauth_token")
    val oAuthToken : String,
    @SerializedName("oauth_token_secret")
    val oAuthTokenSecret : String,
)

data class Test(

    val userId : Int,
    val id : Int,
    val title : String,
    val complete:String
)