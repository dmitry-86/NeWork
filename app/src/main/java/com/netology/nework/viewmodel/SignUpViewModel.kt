package com.netology.nework.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netology.nework.dto.PhotoUpload
import com.netology.nework.dto.Token
import com.netology.nework.model.FeedModelState
import com.netology.nework.model.PhotoModel
import com.netology.nework.repository.AuthRepository
import kotlinx.coroutines.launch
import java.io.File

class SignUpViewModel : ViewModel() {

    private val repository = AuthRepository()

    val data = MutableLiveData<Token>()

    private val noPhoto = PhotoModel()
    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    fun registerUser(login: String, pass: String, name: String) {
        viewModelScope.launch {
            try {
                when (_photo.value) {
                    noPhoto -> data.value = repository.registerUser(login, pass, name)
                    else -> data.value = repository.registerUserWithAvatar(login, pass, name,
                        _photo.value?.file?.let {
                            PhotoUpload(it)
                        })
                }
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(errorLogin = true)
            }
            _photo.value = noPhoto
        }
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

}