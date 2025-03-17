package com.likhit.userlist.data.remote.model

data class UserResponse(
    val limit: Int,
    val skip: Int,
    val total: Int,
    val users: List<User>
)
