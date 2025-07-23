package com.example.recipttracker.data.repository

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.recipttracker.data.local.UserDao
import com.example.recipttracker.domain.model.User
import com.example.recipttracker.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.recipttracker.data.workers.UserSyncWorker
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID
import java.util.concurrent.TimeUnit

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val context: Context
) : UserRepository {

    override fun getAllUsers(): Flow<List<User>> {
        return userDao.getUsers()
    }

    override suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    override suspend fun registerUser(username: String, plainPassword: String): Boolean {
        val existingUser = userDao.getUserByUsername(username)
        if (existingUser != null) return false

        // Check cloud database
        try {
            val firestore = FirebaseFirestore.getInstance()
            val snapshot = firestore
                .collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                // Username already taken in cloud database
                return false
            }
        } catch (_: Exception) {}

        val hashedPassword = BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray())
        val newUser = User(username = username, hashedPassword = hashedPassword)
        userDao.insertUser(newUser)
        scheduleSync()
        return true
    }

    override suspend fun authenticateUser(username: String, plainPassword: String): User? {
        // Check local database
        val localUser = userDao.getUserByUsername(username)
        if (localUser != null) {
            val result = BCrypt.verifyer().verify(plainPassword.toCharArray(), localUser.hashedPassword)
            if (result.verified) {
                return localUser
            }
        }

        // User not in local database so check global database
        return try {
            val firestore = FirebaseFirestore.getInstance()
            val cloudInstance = firestore
                .collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()
            if (!cloudInstance.isEmpty) {
                val doc = cloudInstance.documents.first()
                val data = doc.data
                val firestoreUser = if (data != null) {
                    val idMap = data["id"] as Map<*, *>
                    val uuid = UUID(
                        (idMap["mostSignificantBits"] as Number).toLong(),
                        (idMap["leastSignificantBits"] as Number).toLong()
                    )

                    User(
                        id = uuid,
                        username = data["username"] as String,
                        hashedPassword = data["hashedPassword"] as String,
                        createdAt = (data["createdAt"] as Number).toLong(),
                        syncedWithCloud = data["syncedWithCloud"] as Boolean
                    )
                } else {
                    null
                }
                if (firestoreUser != null) {
                    val cloudResult = BCrypt.verifyer().verify(plainPassword.toCharArray(), firestoreUser.hashedPassword)
                    if (cloudResult.verified) {
                        // Found and verified in the cloud database, so add it to local database
                        userDao.insertUser(firestoreUser)
                        return firestoreUser
                    }
                }
            }
            null
        } catch (e: Exception) {
            // Error with firestore cloud database. Could be that wifi is not connected
            null
        }
    }

    private fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<UserSyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    override suspend fun getUserById(id: UUID): User? {
        return userDao.getUserById(id)
    }

}
