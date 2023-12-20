package com.example.chat_appication.shared

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface AppService {
    @POST("send")
    fun sendMessage(@HeaderMap headers: Map<String, String>, @Body messageBody: String): Call<String>
}