package com.camerax.presentation.ui.gallery

import app.cash.turbine.test
import com.camerax.MainDispatcherRule
import com.camerax.domain.model.MediaItem
import com.camerax.domain.model.MediaType
import com.camerax.domain.repository.MediaRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GalleryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: MediaRepository
    private lateinit var viewModel: GalleryViewModel

    private val allMediaFlow = MutableStateFlow<List<MediaItem>>(emptyList())
    private val countFlow = MutableStateFlow(0)

    private val photoItem = MediaItem(1L, "uri1", MediaType.PHOTO, 1000L, 100L, null, 0, 0, "photo.jpg")
    private val videoItem = MediaItem(2L, "uri2", MediaType.VIDEO, 2000L, 200L, 5000L, 0, 0, "video.mp4")

    @Before
    fun setup() {
        repository =
            mockk(relaxed = true) {
                coEvery { getMediaItems(any(), any()) } returns allMediaFlow
                coEvery { getMediaCount() } returns countFlow
            }
        viewModel = GalleryViewModel(repository)
    }

    @Test
    fun `filteredMedia emits correctly based on filter`() =
        runTest {
            viewModel.filteredMedia.test {
                assertEquals(emptyList<MediaItem>(), awaitItem())

                allMediaFlow.value = listOf(photoItem, videoItem)
                assertEquals(listOf(photoItem, videoItem), awaitItem()) // Default is ALL

                viewModel.setFilter(GalleryFilter.PHOTOS)
                assertEquals(listOf(photoItem), awaitItem())

                viewModel.setFilter(GalleryFilter.VIDEOS)
                assertEquals(listOf(videoItem), awaitItem())

                viewModel.setFilter(GalleryFilter.ALL)
                assertEquals(listOf(photoItem, videoItem), awaitItem())
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
    fun `deleteSelected deletes all selected and clears selection`() =
        runTest {
            viewModel.toggleSelection(1L)
            viewModel.toggleSelection(2L)

            viewModel.deleteSelected()

            coVerify(exactly = 1) { repository.deleteMediaIds(listOf(1L, 2L)) }
            assertEquals(emptySet<Long>(), viewModel.selectedIds.value)
        }

    @Test
    fun `deleteAllMedia calls repository`() =
        runTest {
            viewModel.deleteAllMedia()
            coVerify(exactly = 1) { repository.deleteAllMedia() }
        }
}
