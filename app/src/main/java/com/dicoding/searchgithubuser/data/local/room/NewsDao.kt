package com.dicoding.searchgithubuser.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.*
import com.dicoding.searchgithubuser.data.local.entity.NewsEntity

@Dao
interface NewsDao {
    @Query("SELECT * FROM news where favourite = 1 order by username")
    fun getFavouriteUsers(): LiveData<List<NewsEntity>>

    @Query("SELECT EXISTS(SELECT * FROM news WHERE username = :username AND favourite = 1)")
    suspend fun isUsersFavourite(username: String?): Boolean

    @Query("SELECT EXISTS(SELECT * FROM news WHERE username = :username)")
    suspend fun isUsers(username: String?): Boolean

    @Update
    suspend fun updateUser(users: NewsEntity)

    @Query("DELETE FROM news WHERE username = :username")
    suspend fun delete(username: String?)

    @Query("DELETE FROM news WHERE favourite = 0")
    suspend fun deleteAll()

    @Query("SELECT * FROM news where username = :username")
    fun getUser(username: String): LiveData<NewsEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: NewsEntity)
}