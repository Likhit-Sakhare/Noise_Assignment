package com.likhit.userlist.di

import androidx.room.Room
import com.likhit.userlist.data.local.UserDatabase
import com.likhit.userlist.data.local.repository.LocalUserRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = UserDatabase::class.java,
            name = "user_database"
        ).build()
    }

    single {
        get<UserDatabase>().userDao()
    }

    single {
        LocalUserRepository(get())
    }
}