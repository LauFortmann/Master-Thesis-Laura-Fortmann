package com.example.beautifulmind

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beautifulmind.dbRecording.Recording
import com.example.beautifulmind.dbRecording.RecordingDao
import com.example.beautifulmind.dbUser.User
import com.example.beautifulmind.dbUser.UserDao
import kotlinx.coroutines.launch

class RecordingViewModel(private val dao: RecordingDao):ViewModel() {

    val recording = dao.getAllRecordings()
    val last = dao.getLastRecording()

    fun insertRecording(recording: Recording) = viewModelScope.launch {
        dao.insertRecording(recording)
    }

    fun updateRecording(recording: Recording) = viewModelScope.launch {
        dao.updateRecording(recording)
    }

    fun deleteRecording(recording: Recording) = viewModelScope.launch {
        dao.deleteRecording(recording)
    }

    fun getLastRecording()=viewModelScope.launch{
        dao.getLastRecording()
    }


}