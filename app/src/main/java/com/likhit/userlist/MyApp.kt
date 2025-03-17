package com.likhit.userlist

import android.app.Application
import com.likhit.userlist.di.databaseModule
import com.likhit.userlist.di.networkModule
import com.likhit.userlist.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(
                networkModule,
                viewModelModule,
                databaseModule
            )
        }
    }
}