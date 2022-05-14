package com.netology.nework.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.netology.nework.auth.AppAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.netology.nework.db.AppDb
import com.netology.nework.dto.Coordinates
import com.netology.nework.dto.MediaUpload
import com.netology.nework.dto.Post
import com.netology.nework.enumeration.AttachmentType
import com.netology.nework.model.FeedModel
import com.netology.nework.model.FeedModelState
import com.netology.nework.model.MediaModel
import com.netology.nework.repository.PostRepository
import com.netology.nework.repository.PostRepositoryImpl
import com.netology.nework.util.SingleLiveEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import java.io.InputStream


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

private val noMedia = MediaModel()

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
                        posts.map { it.copy(ownedByMe = it.authorId == myId,
                        likedByMe = it.likeOwnerIds.contains(myId))
                        },
                        posts.isEmpty()
                    )
                }
        }.asLiveData(Dispatchers.Default)

    val userData: LiveData<FeedModel> = AppAuth.getInstance()
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.userData
                .map { posts ->
                    FeedModel(
                        posts.map { it.copy(ownedByMe = it.authorId == myId,
                            likedByMe = it.likeOwnerIds.contains(myId))
                        },
                        posts.isEmpty()
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

    private val _media = MutableLiveData(noMedia)
    val media: LiveData<MediaModel>
        get() = _media

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
        edited.value?.let { post ->
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    when (_media.value) {
                        noMedia -> repository.save(post)
                        else -> _media.value?.inputStream?.let { MediaUpload(it) }
                            ?.let { repository.saveWithAttachment(post, it, _media.value?.type!!) }
                    }
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
        _media.value = noMedia
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String, coords: Coordinates?, link: String?) {
        val text = content.trim()
        val link = link?.trim()
        if (edited.value?.content == text) {
            return
        }
        Log.i("coords", coords?.lat.toString())
        edited.value = edited.value?.copy(content = text, coords = coords, link = link)
    }

    fun changeMedia(uri: Uri?, inputStream: InputStream?, type: AttachmentType?) {
        _media.value = MediaModel(uri, inputStream, type)
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.likeById(id)
            Log.i("like2", repository.likeById(id).toString())
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
            Log.i("like3", e.toString())
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