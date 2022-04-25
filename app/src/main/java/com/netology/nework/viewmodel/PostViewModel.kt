package com.netology.nework.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.google.android.gms.maps.model.Marker
import com.netology.nework.auth.AppAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.netology.nework.db.AppDb
import com.netology.nework.dto.Coordinates
import com.netology.nework.dto.MediaUpload
import com.netology.nework.dto.Post
import com.netology.nework.model.FeedModel
import com.netology.nework.model.FeedModelState
import com.netology.nework.model.PhotoModel
import com.netology.nework.repository.PostRepository
import com.netology.nework.repository.PostRepositoryImpl
import com.netology.nework.util.SingleLiveEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import java.io.File


private val empty = Post(
    id = 0,
    authorId = 0,
    author = "Dima",
    authorAvatar = "",
    content = "New Post",
    published = "2021-08-17T16:46:58.887547Z",
    coords = null,
    link = null,
    likedByMe = false,
    attachment = null
)

private val noPhoto = PhotoModel()

@ExperimentalCoroutinesApi
class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    val data: LiveData<FeedModel> = AppAuth.getInstance()
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.data
                .map { posts ->
                    FeedModel(
                        posts.map { it.copy(ownedByMe = it.authorId == myId) },
//                        posts.isEmpty()
                    )
                }
        }.asLiveData(Dispatchers.Default)

    val userData: LiveData<FeedModel> = AppAuth.getInstance()
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.userData
                .map { posts ->
                    FeedModel(
                        posts.map { it.copy(ownedByMe = it.authorId == myId) },
//                        posts.isEmpty()
                    )
                }
        }.asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    when (_photo.value) {
                        noPhoto -> repository.save(it)
                        else -> _photo.value?.file?.let { file ->
                            repository.saveWithAttachment(it, MediaUpload(file))
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

    fun saveMarker(marker: Marker){
        val lat = marker.position.latitude
        val long = marker.position.longitude

        val coords: Coordinates = Coordinates(lat, long)

        edited.value?.copy(coords = coords)
    }


    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String, link: String) {
        val text = content.trim()
        val link = link.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text, link = link)
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.likeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun dislikeById(id: Long) = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.dislikeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun likePost(post: Post) {
        if (!post.likedByMe) likeById(post.id) else dislikeById(post.id)
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.removeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }


}