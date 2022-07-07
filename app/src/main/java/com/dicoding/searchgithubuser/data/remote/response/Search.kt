package com.dicoding.searchgithubuser

import com.google.gson.annotations.SerializedName

data class Search(

	@field:SerializedName("total_count")
	val totalCount: Int,

	@field:SerializedName("items")
	val items: List<ItemsItem?>? = null
)

data class ItemsItem(

	@field:SerializedName("login")
	val username: String? = null,

	@field:SerializedName("avatar_url")
	val avatarUrl: String? = null,

)
