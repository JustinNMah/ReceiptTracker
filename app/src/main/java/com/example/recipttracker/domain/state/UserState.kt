package com.example.recipttracker.domain.state

import com.example.recipttracker.domain.model.User

data class UserState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)