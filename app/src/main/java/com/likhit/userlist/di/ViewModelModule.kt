package com.likhit.userlist.di

import com.likhit.userlist.presentation.user_list.UserListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        UserListViewModel(androidContext(), get())
    }
}