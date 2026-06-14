package com.camerax.data.repository

import com.camerax.data.local.MediaDao
import com.camerax.data.local.MediaEntity
import com.camerax.domain.model.MediaItem
import com.camerax.domain.model.MediaType
import com.camerax.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MediaRepositoryImpl(
    private val dao: MediaDao,
) : MediaRepository {
    override fun getMediaItems(
        limit: Int,
        offset: Int,
    ): Flow<List<MediaItem>> = dao.getMediaItems(limit, offset).map { entities -> entities.map { it.toDomain() } }

    override fun getAllMediaItems(): Flow<List<MediaItem>> = dao.getAllMediaItems().map { entities -> entities.map { it.toDomain() } }

    override suspend fun insertMedia(item: MediaItem): Long = dao.insert(item.toEntity())

    override suspend fun deleteMedia(id: Long) = dao.deleteById(id)

    override suspend fun deleteMediaIds(ids: List<Long>) = dao.deleteByIds(ids)

    override suspend fun deleteAllMedia() = dao.deleteAll()

    override suspend fun getMediaById(id: Long): MediaItem? = dao.getById(id)?.toDomain()

    override fun getMediaCount(): Flow<Int> = dao.getCount()

    private fun MediaEntity.toDomain(): MediaItem =
        MediaItem(
            id = id,
            uri = uri,
            type = runCatching { MediaType.valueOf(type) }.getOrDefault(MediaType.PHOTO),
            timestampMs = timestampMs,
            sizeBytes = sizeBytes,
            durationMs = durationMs,
            width = width,
            height = height,
            name = name,
        )

    private fun MediaItem.toEntity(): MediaEntity =
        MediaEntity(
            id = id,
            uri = uri,
            type = type.name,
            timestampMs = timestampMs,
            sizeBytes = sizeBytes,
            durationMs = durationMs,
            width = width,
            height = height,
            name = name,
        )
}
