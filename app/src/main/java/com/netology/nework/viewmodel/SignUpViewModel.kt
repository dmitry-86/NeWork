package com.netology.nework.viewmodel

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
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
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
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
                when(_photo.value) {
                    noPhoto -> repository.registerUser(login, pass, name)
                    else -> _photo.value?.file?.let {
                        repository.registerUserWithAvatar(login, pass, name, PhotoUpload(it))
                    }
                }
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(errorLogin = true)
            }
        }
        _photo.value = noPhoto
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

}