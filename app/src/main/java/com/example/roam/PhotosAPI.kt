package com.example.roam

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotosAPI {

    @GET("place/photo")
    fun getPhoto(
        @Query("maxwidth") maxWidth: String,
        @Query("minheight") minHeight: String,
        @Query("photo_reference") photoReference: String,
        @Query("key") apiKey: String
    ): Call<ResponseBody>

    companion object {


        fun create(): PhotosAPI {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Set the desired logging level
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            // Create Retrofit instance
            return Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PhotosAPI::class.java)
        }
    }
}
