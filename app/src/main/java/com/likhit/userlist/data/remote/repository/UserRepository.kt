package com.likhit.userlist.data.remote.repository

import android.content.Context
import com.likhit.userlist.data.local.repository.LocalUserRepository
import com.likhit.userlist.data.mappers.UserMapper
import com.likhit.userlist.data.remote.UserApi
import com.likhit.userlist.data.remote.model.User
import com.likhit.userlist.utils.Result
import com.likhit.userlist.utils.isNetworkAvailable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class UserRepository(
    private val api: UserApi,
    private val localUserRepository: LocalUserRepository,
    private val context: Context
) {
    fun getUsers(): Flow<Result<List<User>>>{
        return flow {
            emit(Result.Loading())

            val cachedUsers = localUserRepository.getAllUsers().firstOrNull()
            if(!cachedUsers.isNullOrEmpty()){
                emit(Result.Success(
                    cachedUsers.map { UserMapper.toUser(it) }
                ))
            }

            val isConnected = isNetworkAvailable(context)

            if(isConnected){
                try {
                    val freshUsers = api.getUsers().users
                    localUserRepository.upsertUsers(freshUsers.map {
                        UserMapper.toUserEntity(it)
                    })
                    emit(Result.Success(freshUsers))
                }catch (e: HttpException){
                    if(cachedUsers.isNullOrEmpty()){
                        emit(Result.Error(message = "HttpException: ${e.message()}"))
                    }
                }catch (e: IOException){
                    if(cachedUsers.isNullOrEmpty()){
                        emit(Result.Error(message = "NetworkException: ${e.message?: "IOException"}"))
                    }
                }catch (e: Exception){
                    if(cachedUsers.isNullOrEmpty()){
                        emit(Result.Error(message = "Something went wrong ${e.message?: "Exception"}"))
                    }
                }
            }else{
                if(cachedUsers.isNullOrEmpty()){
                    emit(Result.Error(message = "You are offline and no cached data is available"))
                }
            }
        }
    }

    fun searchUser(
        allUsers: List<User>,
        query: String
    ): Flow<Result<List<User>>> {
        return flow {
            try {
                emit(Result.Loading())
                val filteredUsers = allUsers.filter { user ->
                    user.firstName.contains(query, true)
                }
                emit(Result.Success(filteredUsers))
            }catch (e: Exception){
                emit(Result.Error(message = e.message))
            }
        }
    }
}