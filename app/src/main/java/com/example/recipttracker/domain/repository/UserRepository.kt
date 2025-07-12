package com.example.recipttracker.domain.repository

import com.example.recipttracker.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getAllUsers(): Flow<List<User>>

    suspend fun getUserById(id: Int): User?

    suspend fun getUserByUsername(username: String): User?

    suspend fun registerUser(username: String, plainPassword: String): Boolean

    suspend fun authenticateUser(username: String, plainPassword: String): User?

}
