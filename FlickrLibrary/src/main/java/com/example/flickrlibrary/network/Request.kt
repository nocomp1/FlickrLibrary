package com.example.flickrlibrary.network

import android.graphics.Bitmap
import com.example.flickrlibrary.Constants.Companion.HTTP_METHOD_GET
import com.example.flickrlibrary.Constants.Companion.HTTP_METHOD_POST
import com.example.flickrlibrary.model.AuthorizationResource
import com.example.flickrlibrary.model.CallResource
import com.example.flickrlibrary.utils.FlickrUtils
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class Request {

    companion object {

        fun makeGetRequest(finalUrl: URL): CallResource {

            val inputStream: InputStream
            val result: String?
            val conn: HttpURLConnection = finalUrl.openConnection() as HttpURLConnection

            // make GET request to the given URL
            conn.requestMethod = HTTP_METHOD_GET
            conn.connect()

            conn.responseCode
            conn.responseMessage

            try {
                // receive response as inputStream
                inputStream = conn.inputStream

                // convert inputStream to string
                result = inputStream?.let { FlickrUtils.convertInputStreamToString(it) }

            } catch (e: Exception) {
                return CallResource.Failure(
                    e.message
                )
            }

            return CallResource.Success(
                result,
                AuthorizationResource()
            )
        }

        fun makePostImageRequest(finalUrl: URL, bitmap: Bitmap): CallResource {
            val outputStream: OutputStream
            val result: Scanner
            val response: String
            val conn: HttpURLConnection = finalUrl.openConnection() as HttpURLConnection

            // make GET request to the given URL
            conn.requestMethod = HTTP_METHOD_POST
            conn.doInput = true
            conn.doOutput = true
            conn.connect()

            // conn.responseCode
            // conn.responseMessage


            try {

                outputStream = conn.outputStream
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                outputStream.close()

                result = Scanner(conn.inputStream)
                response = result.nextLine()
                println("Uploading imageXXX = $response")
                result.close()

            } catch (e: IOException) {

                return CallResource.Failure(
                    e.message
                )
            }

            return CallResource.Success(
                response,
                AuthorizationResource()
            )


        }
    }
}