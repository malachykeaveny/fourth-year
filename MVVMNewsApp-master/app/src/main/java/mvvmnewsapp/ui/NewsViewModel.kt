package mvvmnewsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import models.NewsResponse
import repository.NewsRepository
import retrofit2.Response
import util.Resource

class NewsViewModel(
    val newsRepository: NewsRepository
) : ViewModel() {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val breakingNewsPage = 1

    init {
        getBreakingNews("Covid")
    }

    fun getBreakingNews(newsTopic: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(newsTopic, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}