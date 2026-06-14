package com.camerax.presentation.ui.scanner

import app.cash.turbine.test
import com.camerax.MainDispatcherRule
import com.camerax.domain.model.BarcodeContentType
import com.camerax.domain.model.BarcodeFormatType
import com.camerax.domain.model.ScanResult
import com.camerax.domain.repository.ScanHistoryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ScanHistoryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: ScanHistoryRepository
    private lateinit var viewModel: ScanHistoryViewModel

    private val scansFlow = MutableStateFlow<List<ScanResult>>(emptyList())
    private val countFlow = MutableStateFlow(0)

    @Before
    fun setup() {
        repository =
            mockk(relaxed = true) {
                coEvery { getAllScans() } returns scansFlow
                coEvery { getScanCount() } returns countFlow
            }
        viewModel = ScanHistoryViewModel(repository)
    }

    @Test
    fun `scans flow emits correctly`() =
        runTest {
            viewModel.scans.test {
                assertEquals(emptyList<ScanResult>(), awaitItem())

                val mockScan =
                    ScanResult(
                        id = 1L,
                        rawValue = "test",
                        format = BarcodeFormatType.QR_CODE,
                        contentType = BarcodeContentType.TEXT,
                        displayValue = "test",
                        timestampMs = 12345L,
                    )
                scansFlow.value = listOf(mockScan)
                assertEquals(listOf(mockScan), awaitItem())
            }
        }

    @Test
    fun `toggleSelection adds and removes items`() =
        runTest {
            viewModel.selectedIds.test {
                assertEquals(emptySet<Long>(), awaitItem())

                viewModel.toggleSelection(1L)
                assertEquals(setOf(1L), awaitItem())

                viewModel.toggleSelection(2L)
                assertEquals(setOf(1L, 2L), awaitItem())

                viewModel.toggleSelection(1L)
                assertEquals(setOf(2L), awaitItem())
            }
        }

    @Test
    fun `clearSelection empties the set`() =
        runTest {
            viewModel.toggleSelection(1L)
            viewModel.toggleSelection(2L)

            viewModel.clearSelection()
            assertEquals(emptySet<Long>(), viewModel.selectedIds.value)
        }

    @Test
    fun `deleteSelected deletes all selected and clears selection`() =
        runTest {
            viewModel.toggleSelection(1L)
            viewModel.toggleSelection(2L)

            viewModel.deleteSelected()

            coVerify(exactly = 1) { repository.deleteScan(1L) }
            coVerify(exactly = 1) { repository.deleteScan(2L) }
            assertEquals(emptySet<Long>(), viewModel.selectedIds.value)
        }

    @Test
    fun `clearHistory calls repository`() =
        runTest {
            viewModel.clearHistory()
            coVerify(exactly = 1) { repository.clearHistory() }
        }
}
