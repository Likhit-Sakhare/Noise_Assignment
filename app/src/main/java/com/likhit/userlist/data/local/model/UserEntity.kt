package com.likhit.userlist.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    val firstName: String,
    val lastName: String,
    val gender: String,
    val birthDate: String,
    val phone: String,
    val addressJson: String,
    val imageUrl: String?,
    val lastUpdated: Long = System.currentTimeMillis(),
    @PrimaryKey
    val id: Int,
)