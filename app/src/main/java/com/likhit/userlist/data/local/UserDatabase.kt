package com.likhit.userlist.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.likhit.userlist.data.local.converters.Converters
import com.likhit.userlist.data.local.model.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class UserDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}