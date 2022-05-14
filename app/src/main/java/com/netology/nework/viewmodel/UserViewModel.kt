package com.netology.nework.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.netology.nework.auth.AppAuth
import com.netology.nework.db.AppDb
import com.netology.nework.dto.*
import com.netology.nework.model.*
import com.netology.nework.repository.UserRepository
import com.netology.nework.repository.UserRepositoryImpl
import com.netology.nework.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

private val empty = User(
    id = 0,
    name = " ",
    login = " ",
    avatar = " "
)

private val noPhoto = PhotoModel()

@ExperimentalCoroutinesApi
class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository =
        UserRepositoryImpl(AppDb.getInstance(context = application).userDao())

    val data: LiveData<UserModel> = repository.data
        .map { user ->
            UserModel(
                user,
                user.isEmpty()
            )
        }.asLiveData(Dispatchers.Default)

    val id = AppAuth.getInstance().authStateFlow.value.id

    val user = MutableLiveData<User>()

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

//    private val _user = MutableLiveData<User>()
//    val user: LiveData<User>
//        get() = _user

//    private val _usersIds = MutableLiveData<Set<Long>>()
//    val userIds: LiveData<Set<Long>>
//        get() = _usersIds

    private val edited = MutableLiveData(empty)
    private val _userCreated = SingleLiveEvent<Unit>()
    val userCreated: LiveData<Unit>
        get() = _userCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    init {
        loadUsers()
    }


    fun save() {
        edited.value?.let {
            _userCreated.value = Unit
            viewModelScope.launch {
                try {
                    when (_photo.value) {
                        noPhoto -> repository.save(it)
                        else -> _photo.value?.file?.let { file ->
                            repository.saveWithAttachment(it, PhotoUpload(file))
                        }
                    }
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }


    fun edit(user: User) {
        edited.value = user
    }

    fun changeContent(name: String) {
        val text = name.trim()
        if (edited.value?.name == text) {
            return
        }
        edited.value = edited.value?.copy(name = name)
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
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

    fun getUserById(id: Long) = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            user.value = repository.getUserById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }
}