package com.dicoding.searchgithubuser.data.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.dicoding.searchgithubuser.data.local.entity.NewsEntity
import com.dicoding.searchgithubuser.data.local.room.NewsDao
import com.dicoding.searchgithubuser.data.remote.retrofit.ApiService

class NewRepository (
    private val apiService: ApiService,
    private val newsDao: NewsDao
    ){

    fun getFavouriteUsers() : LiveData<List<NewsEntity>> {
        return newsDao.getFavouriteUsers()
    }

    suspend fun deleteAll() {
        newsDao.deleteAll()
    }

    suspend fun setUsersFavourite(user: NewsEntity, favouriteState: Boolean){
        user.isFavourite = favouriteState
        newsDao.updateUser(user)
    }

    fun getUser(username: String): LiveData<Result<NewsEntity>> = liveData{
        emit(Result.Loading)
        try {
            val user = apiService.getUserDetail(username)
            val isFavourite = newsDao.isUsersFavourite(user.username)
            newsDao.delete(user.username)
            newsDao.insertUser(NewsEntity(
                user.username,
                user.company,
                user.publicRepos,
                user.followers,
                user.avatar,
                user.following,
                user.name,
                user.location,
                isFavourite
            ))
        }catch (e : Exception){
            Log.d("UsersRepository", "getUser: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val isFavourite = newsDao.isUsers(username)
        if(isFavourite){
            val localData : LiveData<Result<NewsEntity>> = newsDao.getUser(username).map { Result.Success(it) }
            emitSource(localData)
        }else{
            emit(Result.Error("Not Found!"))
        }
    }
    companion object {
        @Volatile
        private var instance: NewRepository? = null
        fun getInstance(
            apiService: ApiService,
            newsDao: NewsDao
        ): NewRepository =
            instance ?: synchronized(this) {
                instance ?: NewRepository(apiService, newsDao)
            }.also { instance = it }
    }
}

