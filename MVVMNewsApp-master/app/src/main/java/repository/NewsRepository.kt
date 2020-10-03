package repository

import NewsApp.api.RetrofitInstance
import db.ArticleDatabase
//import retrofit2.Retrofit

class NewsRepository(
    val db: ArticleDatabase
) {

    suspend fun getBreakingNews(newsTopic: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(newsTopic, pageNumber)

}