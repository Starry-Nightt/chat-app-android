package com.example.chat_appication.shared

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class AppClient {

    companion object {
        private const val url = "https://fcm.googleapis.com/fcm/"
        private  var retrofit: Retrofit? = null

        fun getClient(): Retrofit{
            if (retrofit == null){
                retrofit = Retrofit.Builder().baseUrl(url).addConverterFactory(ScalarsConverterFactory.create()).build()
            }
            return retrofit as Retrofit
        }
    }
}