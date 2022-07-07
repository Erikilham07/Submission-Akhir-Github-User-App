package com.dicoding.searchgithubuser.di

import android.content.Context
import com.dicoding.searchgithubuser.data.local.room.UsersDatabase
import com.dicoding.searchgithubuser.data.remote.NewRepository
import com.dicoding.searchgithubuser.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): NewRepository {
        val apiService = ApiConfig.getApiService()
        val database = UsersDatabase.getInstance(context)
        val dao = database.newsDao()
        return NewRepository.getInstance(apiService, dao)
    }
}