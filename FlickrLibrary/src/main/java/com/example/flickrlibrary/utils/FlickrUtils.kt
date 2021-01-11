package com.example.flickrlibrary.utils

import android.util.Base64
import com.example.flickrlibrary.Constants
import com.example.flickrlibrary.FlickrToken
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.security.GeneralSecurityException
import java.util.*
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class FlickrUtils {

    companion object {

        //These are used for oauth signature
        private val timestamp = (System.currentTimeMillis() / 1000).toString()

        private const val oauthSignatureMethod = "HMAC-SHA1"

        var uuidString = UUID.randomUUID().toString().also {
            it.replace("-".toRegex(), "")
        }
        private val oauthNonce = uuidString

        private val oauthTimestamp: String = timestamp

        //Helper methods for manually encoding and creating a signature for request
        private fun encode(value: String?): String {
            var encoded: String? = null
            try {
                encoded = URLEncoder.encode(value, "UTF-8")
            } catch (ignore: UnsupportedEncodingException) {
            }
            val buf = StringBuilder(encoded!!.length)
            var focus: Char
            var i = 0
            while (i < encoded.length) {
                focus = encoded[i]
                if (focus == '*') {
                    buf.append("%2A")
                } else if (focus == '+') {
                    buf.append("%20")
                } else if (focus == '%' && i + 1 < encoded.length && encoded[i + 1] == '7' && encoded[i + 2] == 'E') {
                    buf.append('~')
                    i += 2
                } else {
                    buf.append(focus)
                }
                i++
            }
            return buf.toString()
        }

        @Throws(GeneralSecurityException::class, UnsupportedEncodingException::class)
        fun computeSignature(baseString: String, keyString: String): String {
            var secretKey: SecretKey?
            val keyBytes = keyString.toByteArray()
            secretKey = SecretKeySpec(keyBytes, "HmacSHA1")
            val mac = Mac.getInstance("HmacSHA1")
            mac.init(secretKey)
            val text = baseString.toByteArray()
            return Base64.encodeToString(mac.doFinal(text), Base64.DEFAULT).trim { it <= ' ' }
        }

        fun convertInputStreamToString(inputStream: InputStream): String {
            val bufferedReader: BufferedReader? = BufferedReader(InputStreamReader(inputStream))
            var line: String? = bufferedReader?.readLine()
            var result = ""

            while (line != null) {
                result += line
                line = bufferedReader?.readLine()
            }

            inputStream.close()
            return result
        }

        //This is a helper method for getting the value out of a string response
        //from the server example: oauth_token={the value we want}
        fun extractResponseStringValue(stringResponse: String?, key: String): String {
            var counter = 0
            val finalString = StringBuilder()
            val finalValue = StringBuilder()
            stringResponse?.forEach { char ->
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

        fun getParameterStringForOAuth(
            oauthToken: String = "",
            oauthVerifier: String = ""
        ): String {

            //parameter string in alphabetical order

            return "oauth_callback=${Constants.OAUTH_CALLBACK}&oauth_consumer_key=${Constants.CONSUMER_KEY}" +
                    "&oauth_nonce=$oauthNonce&oauth_signature_method=$oauthSignatureMethod" +
                    "&oauth_timestamp=$oauthTimestamp&oauth_token=$oauthToken&oauth_verifier=$oauthVerifier&oauth_version=1.0"
        }


        fun getParameterStringForRest(
            accessToken: String = "",
            method: String,
            nsId: String,
            galleryId: String? = ""
        ): String {

            return "gallery_id=$galleryId&nojsoncallback=1&format=json&method=${method}&oauth_consumer_key=${Constants.CONSUMER_KEY}" +
                    "&oauth_nonce=$oauthNonce&oauth_signature_method=$oauthSignatureMethod" +
                    "&oauth_timestamp=$oauthTimestamp&oauth_token=$accessToken&user_id=$nsId"
        }

        fun createOauthSignature(parameterString: String, url: String, httpMethod: String): String {
            val signatureBaseString: String =
                httpMethod + "&" + encode(url) + "&" + encode(
                    parameterString
                )

            // hash the HmacSHA1 string against the consumer secret
            var oauthSignature = ""
            try {
                oauthSignature = computeSignature(
                    signatureBaseString,
                    "${Constants.CONSUMER_SECRET}&${FlickrToken.oAuthTokenSecret}"
                )
            } catch (e: GeneralSecurityException) {
                e.printStackTrace()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            return oauthSignature
        }


    }
}