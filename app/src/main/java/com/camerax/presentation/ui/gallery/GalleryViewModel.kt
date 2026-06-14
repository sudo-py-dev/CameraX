package com.camerax.presentation.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camerax.domain.model.MediaItem
import com.camerax.domain.model.MediaType
import com.camerax.domain.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class GalleryFilter {
    ALL,
    PHOTOS,
    VIDEOS,
}

class GalleryViewModel(
    private val mediaRepository: MediaRepository,
) : ViewModel() {
    private val _filter = MutableStateFlow(GalleryFilter.ALL)
    val filter: StateFlow<GalleryFilter> = _filter.asStateFlow()

    private val _limit = MutableStateFlow(30)

    val mediaItems: StateFlow<List<MediaItem>> =
        _limit.flatMapLatest { limit ->
            mediaRepository.getMediaItems(limit = limit, offset = 0)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadMore() {
        _limit.value += 30
    }

    val filteredMedia: StateFlow<List<MediaItem>> =
        combine(mediaItems, _filter) { items, filter ->
            when (filter) {
                GalleryFilter.ALL -> items
                GalleryFilter.PHOTOS -> items.filter { it.type == MediaType.PHOTO }
                GalleryFilter.VIDEOS -> items.filter { it.type == MediaType.VIDEO }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mediaCount: StateFlow<Int> =
        mediaRepository.getMediaCount()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()

    val isSelectionMode: Boolean get() = _selectedIds.value.isNotEmpty()

    fun setFilter(filter: GalleryFilter) {
        _filter.value = filter
    }

    fun deleteMedia(id: Long) {
        viewModelScope.launch {
            mediaRepository.deleteMedia(id)
        }
    }

    fun deleteAllMedia() {
        viewModelScope.launch {
            mediaRepository.deleteAllMedia()
        }
    }

    fun toggleSelection(id: Long) {
        _selectedIds.value =
            _selectedIds.value.let { current ->
                if (current.contains(id)) current - id else current + id
            }
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    fun deleteSelected() {
        viewModelScope.launch {
            mediaRepository.deleteMediaIds(_selectedIds.value.toList())
            _selectedIds.value = emptySet()
        }
    }

    suspend fun getMediaById(id: Long): MediaItem? {
        return mediaRepository.getMediaById(id)
    }
}
