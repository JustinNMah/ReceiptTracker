package com.example.recipttracker.domain.repository

import com.example.recipttracker.domain.model.Settings
import com.example.recipttracker.domain.util.SortField
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface SettingsRepository {
    suspend fun getSettingsByUserId(userId: UUID): Settings?
    fun getSettingsByUserIdFlow(userId: UUID): Flow<Settings?>
    suspend fun insertSettings(settings: Settings)
    suspend fun updateSettings(settings: Settings)
    suspend fun deleteSettingsByUserId(userId: UUID)
    suspend fun updateEnabledSortFields(userId: UUID, enabledSortFields: Set<SortField>)
}