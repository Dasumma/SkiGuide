package com.dasumma.mobileskinav

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @Headers("Accept: application/json")
    @GET("query")
    suspend fun getQuery(@Query("query") query: String): QueryReturn
}