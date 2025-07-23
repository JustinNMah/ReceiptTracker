package com.example.recipttracker.ViewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipttracker.domain.event.UserEvent
import com.example.recipttracker.domain.state.UserState
import com.example.recipttracker.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.recipttracker.util.SessionManager

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = mutableStateOf(UserState())
    val state: State<UserState> = _state

    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.Logout -> logout()
            is UserEvent.Login -> login(event.username, event.password)
            is UserEvent.SignUp -> signUp(event.username, event.password, event.confirmPassword)
            is UserEvent.ClearError -> clearError()
        }
    }

    private fun signUp(username: String, password: String, confirmPassword: String) {
        if (username.isBlank() || password.isBlank()) {
            _state.value = UserState(error = "Fields cannot be empty")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            _state.value = UserState(error = "Please enter a valid email")
            return
        }

        if (password.length < 6) {
            _state.value = UserState(error = "Password must be at least 6 characters long")
            return
        }

        if (password != confirmPassword) {
            _state.value = UserState(error = "Passwords do not match")
            return
        }

        viewModelScope.launch {
            _state.value = UserState(isLoading = true)
            val success = userRepository.registerUser(username, password)
            if (success){
                login(username, password)
            } else{
                _state.value = UserState(error = "User already exists")
            }
        }
    }

    private fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _state.value = UserState(error = "Fields cannot be empty")
            return
        }

        viewModelScope.launch {
            _state.value = UserState(isLoading = true)
            val user = userRepository.authenticateUser(username, password)
            _state.value = if (user != null) {
                SessionManager.setLoggedIn(true)
                UserState(success = true, user = user)
            } else {
                UserState(error = "Invalid credentials")
            }
        }
    }

    private fun logout() {
        SessionManager.logout()
        viewModelScope.launch {
            _state.value = UserState()
        }
    }

    private fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
