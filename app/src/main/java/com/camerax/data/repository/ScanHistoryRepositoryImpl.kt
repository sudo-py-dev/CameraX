package com.camerax.data.repository

import com.camerax.data.local.ScanHistoryDao
import com.camerax.data.local.ScanHistoryEntity
import com.camerax.domain.model.BarcodeContentType
import com.camerax.domain.model.BarcodeFormatType
import com.camerax.domain.model.ScanResult
import com.camerax.domain.repository.ScanHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScanHistoryRepositoryImpl(
    private val dao: ScanHistoryDao,
) : ScanHistoryRepository {
    override fun getScanHistory(
        limit: Int,
        offset: Int,
    ): Flow<List<ScanResult>> = dao.getScans(limit, offset).map { entities -> entities.map { it.toDomain() } }

    override fun getAllScans(): Flow<List<ScanResult>> = dao.getAllScans().map { entities -> entities.map { it.toDomain() } }

    override suspend fun insertScan(result: ScanResult): Long = dao.insert(result.toEntity())

    override suspend fun deleteScan(id: Long) = dao.deleteById(id)

    override suspend fun deleteScans(ids: List<Long>) = dao.deleteByIds(ids)

    override suspend fun clearHistory() = dao.deleteAll()

    override fun getScanCount(): Flow<Int> = dao.getCount()

    private fun ScanHistoryEntity.toDomain(): ScanResult =
        ScanResult(
            id = id,
            rawValue = rawValue,
            format = runCatching { BarcodeFormatType.valueOf(format) }.getOrDefault(BarcodeFormatType.UNKNOWN),
            contentType = runCatching { BarcodeContentType.valueOf(contentType) }.getOrDefault(BarcodeContentType.UNKNOWN),
            displayValue = displayValue,
            timestampMs = timestampMs,
        )

    private fun ScanResult.toEntity(): ScanHistoryEntity =
        ScanHistoryEntity(
            id = id,
            rawValue = rawValue,
            format = format.name,
            contentType = contentType.name,
            displayValue = displayValue,
            timestampMs = timestampMs,
        )
}
