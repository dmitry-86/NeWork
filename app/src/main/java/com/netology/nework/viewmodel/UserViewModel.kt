package com.netology.nework.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.netology.nework.auth.AppAuth
import com.netology.nework.db.AppDb
import com.netology.nework.dto.Job
import com.netology.nework.dto.User
import com.netology.nework.model.FeedModel
import com.netology.nework.model.FeedModelState
import com.netology.nework.model.JobFeedModel
import com.netology.nework.repository.JobRepository
import com.netology.nework.repository.JobRepositoryImpl
import com.netology.nework.repository.UserRepository
import com.netology.nework.repository.UserRepositoryImpl
import com.netology.nework.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val empty = User(
    id = 0,
    name = " ",
    login = " ",
    avatar = " "
)

@ExperimentalCoroutinesApi
class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository =
        UserRepositoryImpl(AppDb.getInstance(context = application).userDao())

    val data: LiveData<List<User>> = repository.data
        .asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _usersIds = MutableLiveData<Set<Long>>()
    val userIds: LiveData<Set<Long>>
        get() = _usersIds

    init {
        loadUsers()
    }

    fun loadUsers() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshUsers() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }
}