package com.unipapers.unipapers_frontend.feature.filemanagement.presentation.viewmodels

import android.net.Uri
import app.cash.turbine.test
import com.unipapers.unipapers_frontend.core.domain.model.Course
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.AnalysisResponse
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.GeminiResult
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.PaperMetadata
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.usecase.SuggestTagsUseCase
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.usecase.UploadFileUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FileUploadViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: FileUploadViewModel
    private val uploadFileUseCase: UploadFileUseCase = mockk()
    private val suggestTagsUseCase: SuggestTagsUseCase = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = FileUploadViewModel(uploadFileUseCase, suggestTagsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onStep1Next triggers Gemini analysis and updates state with results`() = runTest {
        // Given
        val mockUri = mockk<Uri>()
        val mockTags = listOf("Kotlin", "Android", "Unit Testing")
        val mockMetadata = PaperMetadata(courseName = "Mobile Dev", academicYear = "2024")
        val mockResponse = AnalysisResponse(
            topicTags = mockTags,
            metadata = mockMetadata,
            confidence = "high",
            rawQuestionsSummary = "Test summary"
        )

        coEvery { suggestTagsUseCase(mockUri) } returns GeminiResult.Success(mockResponse)

        // When
        viewModel.onFileSelected(mockUri, "test.pdf", "100KB")
        viewModel.onStep1Next()

        // Then
        viewModel.uiState.test {
            awaitItem()

            // Advance until Gemini starts loading
            testDispatcher.scheduler.advanceUntilIdle()

            val finalState = expectMostRecentItem()
            assertEquals(false, finalState.isLoadingTags)
            assertEquals(mockTags, finalState.suggestedTags)
            assertEquals(mockMetadata, finalState.geminiMetadata)
            
            // Verify auto-fill worked because confidence was "high"
            assertEquals("Mobile Dev", finalState.selectedCourseUnitName)
            assertEquals("2024", finalState.selectedAcademicYear)
        }
    }

    @Test
    fun `buildUploadDto reads from shared upload state`() = runTest {
        viewModel.onCourseSelected(
            Course(
                publicId = "course-public-id-123",
                courseCode = "CSC 1100",
                courseName = "Introduction to Computing"
            )
        )
        viewModel.onPaperTypeSelected("Exam")
        viewModel.onAcademicYearSelected("2024/2025")
        viewModel.onSemesterSelected("Semester 2")
        viewModel.onYearOfStudySelected("Year 3")
        viewModel.onFileSelected(mockk<Uri>(), "paper.pdf", "100KB")
        viewModel.topicsNames.addAll(listOf("Kotlin", "Android"))

        val dto = viewModel.buildUploadDto("mocked-file-hash")
        assertEquals("course-public-id-123", dto.coursePublicId)
        assertEquals("Introduction to Computing", dto.courseName)
        assertEquals("paper.pdf", dto.fileName)
        assertEquals("Exam", dto.pastPaperType)
        assertEquals("2024/2025", dto.academicYear)
        assertEquals(3, dto.yearOfStudy)
        assertEquals(2, dto.semester)
        assertEquals(listOf("Kotlin", "Android"), dto.topicsNames)
    }

    @Test
    fun `onStep1Next handles Gemini error gracefully`() = runTest {
        // Given
        val mockUri = mockk<Uri>()
        coEvery { suggestTagsUseCase(mockUri) } returns GeminiResult.Error("API Limit Reached")

        // When
        viewModel.onFileSelected(mockUri, "test.pdf", "100KB")
        viewModel.onStep1Next()

        // Then
        viewModel.uiState.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()
            assertEquals(false, state.isLoadingTags)
            assertTrue(state.tagError!!.contains("Could not extract tags"))
        }
    }
}
