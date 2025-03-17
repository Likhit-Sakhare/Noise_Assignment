package com.likhit.userlist.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.likhit.userlist.data.local.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Upsert
    suspend fun upsertUsers(users: List<UserEntity>)

    @Query("DELETE FROM users")
    suspend fun clearAllUsers()
}