package com.example.flickrlibrary

import com.example.flickrlibrary.Constants.Companion.BASE_URL
import com.example.flickrlibrary.Constants.Companion.CONSUMER_KEY
import com.example.flickrlibrary.Constants.Companion.CONSUMER_SECRET
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor


class RetrofitInstance {

    companion object {
        var oAuthTokenSecret: String = ""
        var oAuthToken: String = ""
        var nsId: String = ""
    }

    private val retrofit by lazy {
        val consumer = OkHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET)
        consumer.setTokenWithSecret(oAuthToken, oAuthTokenSecret)

        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder.addNetworkInterceptor(SigningInterceptor(consumer))

        val gSon = GsonBuilder()
            .setLenient()
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gSon))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpBuilder.build())
            .build()
    }

    val api: FlickrApi by lazy {

        retrofit.create(FlickrApi::class.java)
    }

}