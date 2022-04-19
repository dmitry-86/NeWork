package com.netology.nework.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.netology.nework.auth.AppAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.netology.nework.db.AppDb
import com.netology.nework.dto.Coordinates
import com.netology.nework.dto.Event
import com.netology.nework.dto.Job
import com.netology.nework.dto.Post
import com.netology.nework.enumeration.EventType
import com.netology.nework.model.EventFeedModel
import com.netology.nework.model.FeedModel
import com.netology.nework.model.FeedModelState
import com.netology.nework.model.JobFeedModel
import com.netology.nework.repository.*
import com.netology.nework.util.SingleLiveEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
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
    type = EventType.OFFLINE
)

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


//    val userData: LiveData<FeedModel> = AppAuth.getInstance()
//        .authStateFlow
//        .flatMapLatest { (myId, _) ->
//            repository.userData
//                .map { events ->
//                    FeedModel(
//                    )
//                }
//        }.asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

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
                    repository.save(it)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
    }
    fun edit(event: Event) {
        edited.value = event
    }

    fun changeContent(content: String, eventType: EventType) {
        val content = content.trim()
//        val date = date.trim()
        val eventType = eventType

        Log.i("tag", content + eventType)

        if (edited.value?.content == content
//            && edited.value?.datetime == date
            && edited.value?.type == eventType) {
            return
        }
        edited.value = edited.value?.copy(content = content, type = eventType)
    }

//    fun likeById(id: Long) {
//        TODO()
//    }

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