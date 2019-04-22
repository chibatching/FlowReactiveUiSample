package com.chibatching.flowreactiveuisample

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResult(
    val total_count: Int,
    val incomplete_results: Boolean,
    val items: List<Repo>
)

data class Repo(
    val id: Long,
    val full_name: String
)
