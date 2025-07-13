package com.example.recipttracker.ui.login

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
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = mutableStateOf(UserState())
    val state: State<UserState> = _state

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Login -> login(event.username, event.password)
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
                UserState(success = true, user = user)
            } else {
                UserState(error = "Invalid credentials")
            }
        }
    }
}
