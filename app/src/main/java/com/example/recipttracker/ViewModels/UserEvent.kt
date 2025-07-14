package com.example.recipttracker.domain.event

sealed class UserEvent {
    data class Login(val username: String, val password: String) : UserEvent()
    data class SignUp(val username: String, val password: String, val confirmPassword: String) : UserEvent()
    object Logout : UserEvent()
}
