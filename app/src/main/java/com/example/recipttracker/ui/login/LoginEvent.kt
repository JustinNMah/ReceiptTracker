package com.example.recipttracker.ui.login

sealed class LoginEvent {
    data class Login(val username: String, val password: String) : LoginEvent()
}
