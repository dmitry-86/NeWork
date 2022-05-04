package com.netology.nework.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.maps.model.Marker
import com.netology.nework.auth.AppAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.netology.nework.db.AppDb
import com.netology.nework.dto.*
import com.netology.nework.enumeration.EventType
import com.netology.nework.model.*
import com.netology.nework.repository.*
import com.netology.nework.util.SingleLiveEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Calendar


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
    link = "",
    likes = 0,
)

private val noPhoto = PhotoModel()

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

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

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
        edited.value?.let {
            _eventCreated.value = Unit
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

    fun edit(event: Event) {
        edited.value = event
    }

    fun changeContent(content: String, datetime: String, link: String, eventType: EventType) {
        val content = content.trim()
        val datetime = datetime.trim()
        val link = link.trim()
        val eventType = eventType

        Log.i("event!!", content + datetime + link + eventType)

        if (edited.value?.content == content
            && edited.value?.datetime == datetime
            && edited.value?.link == link
            && edited.value?.type == eventType) {
            return
        }
        edited.value = edited.value?.copy(content = content, datetime = datetime, link = link, type = eventType)
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