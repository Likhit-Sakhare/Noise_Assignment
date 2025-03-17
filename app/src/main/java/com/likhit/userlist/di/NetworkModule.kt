package com.likhit.userlist.di

import com.likhit.userlist.data.remote.UserApi
import com.likhit.userlist.data.remote.repository.UserRepository
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(UserApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<UserApi> {
        get<Retrofit>().create(UserApi::class.java)
    }

    single {
        UserRepository(get(), get(), get())
    }
}