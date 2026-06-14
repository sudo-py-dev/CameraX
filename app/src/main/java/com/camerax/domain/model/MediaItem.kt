package com.camerax.domain.model

enum class MediaType {
    PHOTO,
    VIDEO,
}

data class MediaItem(
    val id: Long = 0L,
    val uri: String,
    val type: MediaType,
    val timestampMs: Long,
    val sizeBytes: Long = 0L,
    val durationMs: Long? = null,
    val width: Int = 0,
    val height: Int = 0,
    val name: String = "",
)
