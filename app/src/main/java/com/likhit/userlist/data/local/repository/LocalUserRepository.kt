package com.likhit.userlist.data.local.repository

import com.likhit.userlist.data.local.UserDao
import com.likhit.userlist.data.local.model.UserEntity
import kotlinx.coroutines.flow.Flow

class LocalUserRepository(
    private val userDao: UserDao
) {
    fun getAllUsers(): Flow<List<UserEntity>>{
        return userDao.getAllUsers()
    }

    suspend fun upsertUsers(users: List<UserEntity>){
        userDao.upsertUsers(users)
    }

    suspend fun clearAllUsers(){
        userDao.clearAllUsers()
    }
}