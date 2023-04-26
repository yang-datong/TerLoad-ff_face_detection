package com.rl.ff_face_detection_terload.network

import com.rl.ff_face_detection_terload.extensions.EM_app_name
import com.rl.ff_face_detection_terload.extensions.EM_host
import com.rl.ff_face_detection_terload.extensions.EM_org_name
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Api {
    private const val baseUrl = "https://${EM_host}/${EM_org_name}/${EM_app_name}/"

    val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

}