package com.example.beautifulmind

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.beautifulmind.dbRecording.RecordingDao
import com.example.beautifulmind.dbUser.UserDao

class RecordingViewModelFactory (private val dao: RecordingDao):

    ViewModelProvider.Factory{

    override fun<T: ViewModel> create(modelClass: Class<T>):T{
        if(modelClass.isAssignableFrom(RecordingViewModel::class.java)){
            return RecordingViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
    }

