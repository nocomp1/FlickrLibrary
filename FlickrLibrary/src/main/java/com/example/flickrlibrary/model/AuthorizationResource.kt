package com.example.flickrlibrary.model


sealed class CallResource {

    data class Success(

        val response: String? = null,
        val authResource: AuthorizationResource

    ) : CallResource()

    data class Failure(


        val errorMessage: String?
    ) : CallResource()

}

data class AuthorizationResource(

    val authorizationToken: String = "",
    val authorizationSecret: String = "",
    val nsId: String = "",
    val userName: String = "",
    val isAuthorized: Boolean = false

)