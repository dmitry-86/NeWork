package com.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netology.nework.dto.User
import com.netology.nework.model.FeedModelState
import com.netology.nework.repository.AuthRepository
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    private val repository = AuthRepository()

    val data = MutableLiveData<User>()

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    fun authUser(login: String, password: String) {
        viewModelScope.launch {
            try {
                data.value = repository.authUser(login, password)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(errorLogin = true)
            }
        }
    }
}