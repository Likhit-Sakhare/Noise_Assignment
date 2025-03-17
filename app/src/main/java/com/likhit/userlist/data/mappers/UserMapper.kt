package com.likhit.userlist.data.mappers

import com.google.gson.Gson
import com.likhit.userlist.data.local.model.UserEntity
import com.likhit.userlist.data.remote.model.Address
import com.likhit.userlist.data.remote.model.User

object UserMapper {
    fun toUserEntity(user: User): UserEntity {
        return UserEntity(
            firstName = user.firstName,
            lastName = user.lastName,
            gender = user.gender,
            birthDate = user.birthDate,
            phone = user.phone,
            addressJson = Gson().toJson(user.address),
            imageUrl = user.image,
            id = user.id
        )
    }

    fun toUser(userEntity: UserEntity): User {
        return User(
            firstName = userEntity.firstName,
            lastName = userEntity.lastName,
            gender = userEntity.gender,
            birthDate = userEntity.birthDate,
            phone = userEntity.phone,
            address = Gson().fromJson(userEntity.addressJson, Address::class.java),
            image = userEntity.imageUrl!!,
            id = userEntity.id
        )
    }
}