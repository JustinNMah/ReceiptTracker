package com.example.recipttracker.data.local

import androidx.room.*
import com.example.recipttracker.domain.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user ORDER BY createdAt DESC")
    fun getUsers(): Flow<List<User>>

    @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: UUID): User?

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM user WHERE syncedWithCloud = 0")
    suspend fun getUnsyncedUsers(): List<User>
}
