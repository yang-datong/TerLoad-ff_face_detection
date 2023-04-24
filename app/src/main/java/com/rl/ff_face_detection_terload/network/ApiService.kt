package com.rl.ff_face_detection_terload.network

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @PUT("users/{userId}/password")
    fun updatePassword(
            @Header("Authorization") token: String?,
            @Path("userId") userId: String,
            @Body requestBody: Map<String?, String?>?
    ): Call<ResponseBody?>?


    //https://docs-im-beta.easemob.com/document/server-side/message.html#示例-3
    @Multipart
    @POST("chatfiles")
    fun uploadFile(
            @Header("Authorization") token: String?,
            @Part file: MultipartBody.Part
    ): Call<ResponseBody?>?


    //https://docs-im-beta.easemob.com/document/server-side/message.html#示例-3
    @GET("chatfiles/{file_uuid}")
    fun downloadFile(
            @Header("Authorization") token: String?,
//            @Header("share-secret") share_secret: String, //option 文件访问密钥。若上传文件时限制了访问，下载该文件时则需要该访问密钥。成功上传文件后，从 文件上传 的响应 body 中获取该密钥。
            @Path("file_uuid") file_uuid: String  //服务器为文件生成的 UUID
    ): Call<ResponseBody?>?


}
