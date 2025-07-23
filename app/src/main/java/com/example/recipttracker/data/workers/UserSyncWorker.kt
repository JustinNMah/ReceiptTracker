package com.example.recipttracker.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.recipttracker.data.local.UserDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val dao = UserDatabase.getInstance(applicationContext).userDao
        val firestore = FirebaseFirestore.getInstance()

        val unsynced = dao.getUnsyncedUsers()

        try {
            unsynced.forEach { user ->
                firestore
                    .collection("users")
                    .document(user.id.toString())
                    .set(user)
                    .await()

                dao.updateUser(user.copy(syncedWithCloud = true))
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Sync failed", e)
            Result.retry()
        }
    }
}