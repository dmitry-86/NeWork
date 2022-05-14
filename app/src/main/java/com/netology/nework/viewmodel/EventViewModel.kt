package com.netology.nework.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.netology.nework.auth.AppAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.netology.nework.db.AppDb
import com.netology.nework.dto.*
import com.netology.nework.enumeration.AttachmentType
import com.netology.nework.enumeration.EventType
import com.netology.nework.model.*
import com.netology.nework.repository.*
import com.netology.nework.util.SingleLiveEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import java.io.InputStream


private val empty = Event(
    id = 0,
    authorId = 0,
    author = "Dima",
    authorAvatar = "",
    content = "Выпускники Kotlin Developer",
    datetime = "2021-09-17T16:46:58.887547Z",
    published = "2021-08-17T16:46:58.887547Z",
    coords = null,
    type = EventType.OFFLINE,
    likedByMe = false,
    attachment = null,
    link = ""
)

private val noMedia = MediaModel()

@ExperimentalCoroutinesApi
class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EventRepository =
        EventRepositoryImpl(AppDb.getInstance(context = application).eventDao())

    val data: LiveData<EventFeedModel> = AppAuth.getInstance()
        .authStateFlow
        .flatMapLatest {
            repository.data
                .map (::EventFeedModel)
        }.asLiveData(Dispatchers.Default)


    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

    private val _media = MutableLiveData(noMedia)
    val media: LiveData<MediaModel>
        get() = _media

    init {
        loadEvents()
    }

    fun loadEvents() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshEvents() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        }
        catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let { event ->
            _eventCreated.value = Unit
            viewModelScope.launch {
                try {
                    when (_media.value) {
                        noMedia -> repository.save(event)
                        else -> _media.value?.inputStream?.let { MediaUpload(it) }
                            ?.let { repository.saveWithAttachment(event, it, _media.value?.type!!) }
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

    fun edit(event: Event) {
        edited.value = event
    }

    fun changeContent(content: String, datetime: String, coords: Coordinates?, link: String, type: String) {
        val content = content.trim()
        val datetime = datetime.trim()
        val link = link.trim()
        val eventType = when(type){
            "online"-> EventType.ONLINE
            else-> EventType.OFFLINE
        }

        if (edited.value?.content == content
            && edited.value?.datetime == datetime
            && edited.value?.link == link
            && edited.value?.type == eventType) {
            return
        }
        edited.value = edited.value?.copy(content = content, datetime = datetime, coords=coords, link = link, type = eventType)
    }


    fun changeMedia(uri: Uri?, inputStream: InputStream?, type: AttachmentType?) {
        _media.value = MediaModel(uri, inputStream, type)
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

    fun likeEvent(event: Event) {
        if (!event.likedByMe) likeById(event.id) else dislikeById(event.id)
    }

    fun removeById(id: Long)  = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.removeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

}