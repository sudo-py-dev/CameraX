package com.camerax.presentation.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camerax.domain.model.ScanResult
import com.camerax.domain.repository.ScanHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScanHistoryViewModel(
    private val scanHistoryRepository: ScanHistoryRepository,
) : ViewModel() {
    val scans: StateFlow<List<ScanResult>> =
        scanHistoryRepository.getAllScans()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scanCount: StateFlow<Int> =
        scanHistoryRepository.getScanCount()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()

    fun deleteScan(id: Long) {
        viewModelScope.launch {
            scanHistoryRepository.deleteScan(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            scanHistoryRepository.clearHistory()
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
            scanHistoryRepository.deleteScans(_selectedIds.value.toList())
            _selectedIds.value = emptySet()
        }
    }
}
