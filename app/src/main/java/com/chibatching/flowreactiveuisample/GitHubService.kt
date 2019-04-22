package com.chibatching.flowreactiveuisample

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubService {

    @GET("search/repositories")
    fun search(@Query("q") query: String): Call<SearchResult>
}
