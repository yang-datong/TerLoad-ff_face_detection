package com.rl.ff_face_detection_terload.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Api {
    private const val baseUrl = "https://a1.easemob.com/1135230423163966/demo/"

    val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

}