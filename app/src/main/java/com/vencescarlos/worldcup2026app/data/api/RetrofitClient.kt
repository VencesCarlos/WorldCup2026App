package com.vencescarlos.worldcup2026app.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val TEST_BASE_URL = "https://www.computomovil.com/2026-2/"
    private const val OFFICIAL_BASE_URL = "https://v3.football.api-sports.io/"

    private val testRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(TEST_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val officialRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(OFFICIAL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val testApiService: ApiService by lazy {
        testRetrofit.create(ApiService::class.java)
    }

    val officialApiService: ApiService by lazy {
        officialRetrofit.create(ApiService::class.java)
    }
}