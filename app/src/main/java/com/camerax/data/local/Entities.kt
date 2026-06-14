package com.camerax.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_items")
data class MediaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val uri: String,
    val type: String,
    val timestampMs: Long,
    val sizeBytes: Long,
    val durationMs: Long?,
    val width: Int,
    val height: Int,
    val name: String,
)

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val rawValue: String,
    val format: String,
    val contentType: String,
    val displayValue: String,
    val timestampMs: Long,
)
