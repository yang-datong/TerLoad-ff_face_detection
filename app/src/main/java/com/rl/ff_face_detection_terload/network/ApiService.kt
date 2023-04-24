package com.rl.ff_face_detection_terload.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header

import retrofit2.http.PUT
import retrofit2.http.Path


interface ApiService {
    @PUT("users/{userId}/password")
    fun updatePassword(
            @Header("Authorization") token: String?,
            @Path("userId") userId: String,
            @Body requestBody: Map<String?, String?>?
    ): Call<ResponseBody?>?
}
