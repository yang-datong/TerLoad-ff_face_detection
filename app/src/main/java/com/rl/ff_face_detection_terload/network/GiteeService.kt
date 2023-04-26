package com.rl.ff_face_detection_terload.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header

import retrofit2.http.POST
import retrofit2.http.Path


interface GiteeService {
    @POST("repos/{owner}/{repo}/contents/{path}")
    fun uploadFile(
            @Path("owner") owner: String?,
            @Path("repo") repo: String?,
            @Path("path") path: String?,
            @Header("Content-Type") contentType: String?,
            @Body requestBody: Map<String, String>
    ): Call<ResponseBody?>?
}