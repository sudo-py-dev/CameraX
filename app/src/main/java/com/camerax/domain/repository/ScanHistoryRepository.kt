package com.camerax.domain.repository

import com.camerax.domain.model.ScanResult
import kotlinx.coroutines.flow.Flow

interface ScanHistoryRepository {
    fun getScanHistory(
        limit: Int,
        offset: Int,
    ): Flow<List<ScanResult>>

    fun getAllScans(): Flow<List<ScanResult>>

    suspend fun insertScan(result: ScanResult): Long

    suspend fun deleteScan(id: Long)

    suspend fun deleteScans(ids: List<Long>)

    suspend fun clearHistory()

    fun getScanCount(): Flow<Int>
}
