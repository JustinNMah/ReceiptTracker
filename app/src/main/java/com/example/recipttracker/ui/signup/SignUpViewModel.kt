package com.example.recipttracker.ui.signup

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipttracker.domain.state.UserState
import com.example.recipttracker.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = mutableStateOf(UserState())
    val state: State<UserState> = _state

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.Register -> signup(event.username, event.password, event.confirmPassword)
        }
    }

    private fun signup(username: String, password: String, confirmPassword: String) {
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
            _state.value = if (success) {
                UserState(success = true)
            } else {
                UserState(error = "User already exists")
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
                UserState(success = true)
            } else {
                UserState(error = "Invalid credentials")
            }
        }
    }
}
