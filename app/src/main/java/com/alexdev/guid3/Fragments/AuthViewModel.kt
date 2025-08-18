package com.alexdev.guid3.Fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    // Estado: true = login, false = registro
    private val _isLoginMode = MutableLiveData(true)
    val isLoginMode: LiveData<Boolean> get() = _isLoginMode

    fun toggleMode() {
        _isLoginMode.value = !(_isLoginMode.value ?: true)
    }
}

