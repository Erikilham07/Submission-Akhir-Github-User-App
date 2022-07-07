package com.dicoding.searchgithubuser.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.searchgithubuser.data.remote.retrofit.ApiConfig
import com.dicoding.searchgithubuser.utils.Event
import com.dicoding.searchgithubuser.ItemsItem
import com.dicoding.searchgithubuser.Search
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel(){
    private val _userList = MutableLiveData<List<ItemsItem>>()
    val userList: LiveData<List<ItemsItem>> = _userList

    private val _userCount = MutableLiveData<Int>()
    val userCount: LiveData<Int> = _userCount

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    fun findUser(username : String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUsers(username)
        client.enqueue(object : Callback<Search> {
            override fun onResponse(
                call: Call<Search>,
                response: Response<Search>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _userList.value = response.body()?.items as List<ItemsItem>
                    _userCount.value = response.body()?.totalCount
                    _toastText.value = Event("Success")
                } else {
                    _toastText.value = Event("Tidak ada data yang ditampilkan!")
                }
            }
            override fun onFailure(call: Call<Search>, t: Throwable) {
                _isLoading.value = false
                _toastText.value = Event("onFailure: ${t.message.toString()}")
            }
        })
    }
    companion object{
        private const val TAG = "MainViewModel"
    }
}