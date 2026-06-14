package com.camerax.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_items ORDER BY timestampMs DESC LIMIT :limit OFFSET :offset")
    fun getMediaItems(
        limit: Int,
        offset: Int,
    ): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_items ORDER BY timestampMs DESC")
    fun getAllMediaItems(): Flow<List<MediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MediaEntity): Long

    @Query("DELETE FROM media_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM media_items WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("DELETE FROM media_items")
    suspend fun deleteAll()

    @Query("SELECT * FROM media_items WHERE id = :id")
    suspend fun getById(id: Long): MediaEntity?

    @Query("SELECT COUNT(*) FROM media_items")
    fun getCount(): Flow<Int>
}

@Dao
interface ScanHistoryDao {
    @Query("SELECT * FROM scan_history ORDER BY timestampMs DESC LIMIT :limit OFFSET :offset")
    fun getScans(
        limit: Int,
        offset: Int,
    ): Flow<List<ScanHistoryEntity>>

    @Query("SELECT * FROM scan_history ORDER BY timestampMs DESC")
    fun getAllScans(): Flow<List<ScanHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ScanHistoryEntity): Long

    @Query("DELETE FROM scan_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM scan_history WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("DELETE FROM scan_history")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM scan_history")
    fun getCount(): Flow<Int>
}
