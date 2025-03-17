package com.likhit.userlist.presentation.user_list

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likhit.userlist.data.remote.model.User
import com.likhit.userlist.data.remote.repository.UserRepository
import com.likhit.userlist.presentation.utils.UIState
import com.likhit.userlist.presentation.utils.showToast
import com.likhit.userlist.utils.Result
import com.likhit.userlist.utils.isNetworkAvailable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserListViewModel(
    private val appContext: Context,
    private val userRepository: UserRepository
): ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private val _uiState = MutableStateFlow(UIState.NOTHING)
    val uiState = _uiState.asStateFlow()

    private val _isSearchBarVisible = MutableStateFlow(false)
    val isSearchBarVisible = _isSearchBarVisible.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var allUsers: List<User> = emptyList()

    init {
        getUsers()
    }

    fun refreshUsers(){
        _users.update { emptyList() }
        getUsers(isRefreshing = true)
    }

    private fun getUsers(isRefreshing: Boolean = false){
        if(!isNetworkAvailable(appContext)){
            showToast(appContext, "No Internet connection")
        }
        viewModelScope.launch {
            userRepository.getUsers().collect { result ->
                when(result){
                    is Result.Success -> {
                        result.data?.let { users ->
                            allUsers = users
                            _users.update { users }
                            _uiState.value = UIState.SUCCESS
                        }?: Log.e("GetUserError", "Response body is null")
                    }
                    is Result.Error -> {
                        result.message?.let { message ->
                            Log.e("GetUserError", message)
                            showToast(
                                context = appContext,
                                message = message
                            )
                            _uiState.value = UIState.ERROR
                        }
                    }
                    is Result.Loading -> {
                        _uiState.value = if(isRefreshing) UIState.REFRESHING else UIState.LOADING
                    }
                }
            }
        }
    }

    fun toggleSearchBar(){
        _isSearchBarVisible.value = !_isSearchBarVisible.value
    }

    private fun searchUsers(query: String){
        viewModelScope.launch {
            userRepository.searchUser(
                allUsers,
                query
            ).collect { result ->
                when(result){
                    is Result.Success -> {
                        result.data?.also { filteredUsers ->
                            _users.update {
                                filteredUsers
                            }
                            _uiState.value = UIState.SUCCESS
                        }
                    }
                    is Result.Error -> {
                        result.message?.let { message ->
                            Log.e("SearchUserError", message)
                            showToast(
                                context = appContext,
                                message = message
                            )
                            _uiState.value = UIState.ERROR
                        }
                    }
                    is Result.Loading -> {
                        _uiState.value = UIState.LOADING
                    }
                }
            }
        }
    }

    fun onSearchQueryChange(query: String){
        _searchQuery.value = query
        if(query.isEmpty()){
            _users.update {
                allUsers
            }
        }else{
            searchUsers(query.trim())
        }
    }
}