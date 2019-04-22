package com.chibatching.flowreactiveuisample

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combineLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MainViewModel : ViewModel() {
    val org = MutableLiveData<String>()
    val repository = MutableLiveData<String>()

    val repos = object : MutableLiveData<List<Repo>>() {
        override fun onActive() {
            value?.let { return }

            viewModelScope.launch {
                var job: Deferred<Unit>? = null
                org.asFlow()
                    .combineLatest(repository.asFlow()) { org, repo ->
                        Pair(org, repo)
                    }
                    .debounce(500)
                    .distinctUntilChanged()
                    .collect {
                        job?.cancel()
                        job = async(Dispatchers.Main) {
                            value = searchRepository(it.first, it.second)
                        }
                    }
            }
        }
    }

    private val service: GitHubService = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(GitHubService::class.java)

    private suspend fun searchRepository(org: String?, repo: String?): List<Repo> =
        suspendCancellableCoroutine { continuation ->
            val query =
                listOfNotNull(
                    if (org.isNullOrBlank()) null else "org:$org",
                    if (repo.isNullOrBlank()) null else "in:name $repo"
                ).joinToString(" ")
            if (query.isBlank()) {
                continuation.resume(emptyList())
                return@suspendCancellableCoroutine
            }
            val call = service.search(query)
            call.enqueue(object : Callback<SearchResult> {
                override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                    if (continuation.isActive && !call.isCanceled) {
                        continuation.resumeWithException(t)
                    }
                }

                override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
                    continuation.resume(response.body()?.items ?: emptyList())
                }
            })
            continuation.invokeOnCancellation {
                call.cancel()
            }
        }
}
