package com.example.beautifulmind

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.example.beautifulmind.dbUser.User
import com.example.beautifulmind.dbUser.UserDao
import kotlinx.coroutines.launch

class UserViewModel (private val dao: UserDao): ViewModel() {

    val users = dao.getAllUser()

    fun insertUser(user: User)=viewModelScope.launch {
        dao.insertUser(user)
    }

    fun updateUser(user: User)=viewModelScope.launch {
        dao.updateUser(user)
    }

    fun deleteUser(user: User)=viewModelScope.launch {
        dao.deleteUser(user)
    }


}