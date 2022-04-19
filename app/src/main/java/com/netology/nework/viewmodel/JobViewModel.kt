package com.netology.nework.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.netology.nework.auth.AppAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.netology.nework.db.AppDb
import com.netology.nework.dto.Job
import com.netology.nework.dto.Post
import com.netology.nework.model.FeedModel
import com.netology.nework.model.FeedModelState
import com.netology.nework.model.JobFeedModel
import com.netology.nework.repository.JobRepository
import com.netology.nework.repository.JobRepositoryImpl
import com.netology.nework.repository.PostRepository
import com.netology.nework.repository.PostRepositoryImpl
import com.netology.nework.util.SingleLiveEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Calendar


private val empty = Job(
    id = 0,
    authorId = 0,
    name = " ",
    position = " ",
    start = 0,
    finish = null,
    link = null
)

@ExperimentalCoroutinesApi
class JobViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JobRepository =
        JobRepositoryImpl(AppDb.getInstance(context = application).jobDao())

    val data: LiveData<JobFeedModel> = AppAuth.getInstance()
        .authStateFlow
        .flatMapLatest {
            repository.data
                .map (::JobFeedModel)
        }.asLiveData(Dispatchers.Default)


    val userData: LiveData<FeedModel> = AppAuth.getInstance()
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.userData
                .map { jobs ->
                    FeedModel(
                    )
                }
        }.asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _jobCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _jobCreated

    init {
        loadJobs()
    }

    fun loadJobs() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshJobs() = viewModelScope.launch {
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
            _jobCreated.value = Unit
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
    fun edit(job: Job) {
        edited.value = job
    }

    fun changeContent(name: String, position: String, started: String, finished: String, link: String) {
        val name = name.trim()
        val position = position.trim()
        val started = started.trim()
        val finished = finished.trim()
        val link = link.trim()
        if (edited.value?.name == name
            && edited.value?.position == position
            && edited.value?.start == started.toLong()
            && edited.value?.finish == finished.toLong()
            && edited.value?.link == link) {
            return
        }
        edited.value = edited.value?.copy(name = name, position = position, start = started.toLong(), finish = finished.toLong(), link = link)
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