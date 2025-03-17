package com.likhit.userlist.data.remote.model

data class User(
    val address: Address,
    val birthDate: String,
    val firstName: String,
    val gender: String,
    val id: Int,
    val image: String,
    val lastName: String,
    val phone: String,
)
