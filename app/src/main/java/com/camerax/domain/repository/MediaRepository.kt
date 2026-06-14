package com.camerax.domain.repository

import com.camerax.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getMediaItems(
        limit: Int,
        offset: Int,
    ): Flow<List<MediaItem>>

    fun getAllMediaItems(): Flow<List<MediaItem>>

    suspend fun insertMedia(item: MediaItem): Long

    suspend fun deleteMedia(id: Long)

    suspend fun deleteMediaIds(ids: List<Long>)

    suspend fun deleteAllMedia()

    suspend fun getMediaById(id: Long): MediaItem?

    fun getMediaCount(): Flow<Int>
}
