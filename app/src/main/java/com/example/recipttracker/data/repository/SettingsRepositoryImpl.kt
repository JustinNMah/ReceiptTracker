package com.example.recipttracker.data.repository

import com.example.recipttracker.data.local.SettingsDao
import com.example.recipttracker.domain.model.Settings
import com.example.recipttracker.domain.repository.SettingsRepository
import com.example.recipttracker.domain.util.SortField
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class SettingsRepositoryImpl(
    private val dao: SettingsDao
) : SettingsRepository {

    override suspend fun getSettingsByUserId(userId: UUID): Settings? {
        return dao.getSettingsByUserId(userId)
    }

    override fun getSettingsByUserIdFlow(userId: UUID): Flow<Settings?> {
        return dao.getSettingsByUserIdFlow(userId)
    }

    override suspend fun insertSettings(settings: Settings) {
        dao.insertSettings(settings)
    }

    override suspend fun updateSettings(settings: Settings) {
        dao.updateSettings(settings.copy(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun deleteSettingsByUserId(userId: UUID) {
        dao.deleteSettingsByUserId(userId)
    }

    override suspend fun updateEnabledSortFields(userId: UUID, enabledSortFields: Set<SortField>) {
        val sortFieldNames = enabledSortFields.map { it.name }
        val json = Json.encodeToString(sortFieldNames)
        dao.updateEnabledSortFields(userId, json, System.currentTimeMillis())
    }

    companion object {
        fun parseEnabledSortFields(json: String): Set<SortField> {
            return try {
                val sortFieldNames = Json.decodeFromString<List<String>>(json)
                sortFieldNames.mapNotNull { name ->
                    SortField.entries.find { it.name == name }
                }.toSet()
            } catch (e: Exception) {
                SortField.entries.toSet()
            }
        }
    }
}