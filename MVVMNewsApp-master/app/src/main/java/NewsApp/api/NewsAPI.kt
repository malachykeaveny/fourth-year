package NewsApp.api

import models.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import util.Constants.Companion.API_KEY

interface NewsAPI {

    @GET("v2/top-headlines")

    suspend fun getBreakingNews(
        @Query("q")
        newsTopic: String = "Covid",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("v2/top-headlines")

    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>
}