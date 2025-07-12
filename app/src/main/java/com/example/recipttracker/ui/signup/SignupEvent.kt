package com.example.recipttracker.ui.signup

sealed class SignUpEvent {
    data class Register(val username: String, val password: String, val confirmPassword: String) : SignUpEvent()
}
