package com.example.recipttracker.data.local

import androidx.room.*
import com.example.recipttracker.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings WHERE userId = :userId LIMIT 1")
    suspend fun getSettingsByUserId(userId: UUID): Settings?

    @Query("SELECT * FROM settings WHERE userId = :userId LIMIT 1")
    fun getSettingsByUserIdFlow(userId: UUID): Flow<Settings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: Settings)

    @Update
    suspend fun updateSettings(settings: Settings)

    @Query("DELETE FROM settings WHERE userId = :userId")
    suspend fun deleteSettingsByUserId(userId: UUID)

    @Query("UPDATE settings SET enabledSortFields = :enabledSortFields, updatedAt = :updatedAt WHERE userId = :userId")
    suspend fun updateEnabledSortFields(userId: UUID, enabledSortFields: String, updatedAt: Long)
}