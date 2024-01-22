package com.example.roam

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ItineraryAPI {
    @POST("generate-itinerary")
    fun postData(@Body requestBody: ItineraryRequestData): Call<ItineraryResponse>

    companion object {
        private const val BASE_URL = "https://gptai-production.up.railway.app/"

        fun create(): ItineraryAPI {
            val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Log.d("OkHttpRequestBody", message) // Log to the console
                }
            }).apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val httpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()

            return retrofit.create(ItineraryAPI::class.java)
        }
    }

}