package com.example.recipttracker.data.repository

import com.example.recipttracker.data.local.UserDao
import com.example.recipttracker.domain.model.User
import com.example.recipttracker.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import at.favre.lib.crypto.bcrypt.BCrypt

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override fun getAllUsers(): Flow<List<User>> {
        return userDao.getUsers()
    }

    override suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)
    }

    override suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    override suspend fun registerUser(username: String, plainPassword: String): Boolean {
        val existingUser = userDao.getUserByUsername(username)
        if (existingUser != null) return false

        val hashedPassword = BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray())
        val newUser = User(username = username, hashedPassword = hashedPassword)
        userDao.insertUser(newUser)
        return true
    }

    override suspend fun authenticateUser(username: String, plainPassword: String): User? {
        val user = userDao.getUserByUsername(username) ?: return null
        val result = BCrypt.verifyer().verify(plainPassword.toCharArray(), user.hashedPassword)
        return if (result.verified) user else null
    }
}
