package com.unipapers.unipapers_frontend.feature.home.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.unipapers.unipapers_frontend.feature.home.domain.ContinueStudyingItem
import com.unipapers.unipapers_frontend.feature.home.domain.CourseUnit
import com.unipapers.unipapers_frontend.feature.home.domain.Paper
import com.unipapers.unipapers_frontend.feature.home.domain.PaperType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────
// JSON RESPONSE SHAPES
// ─────────────────────────────────────────────

data class PaperJson(
    val id: String,
    val title: String,
    val courseUnit: String,
    val type: String,
    val academicYear: String,
    val uploadedBy: String,
    val timeAgo: String,
    val pageCount: Int
)

data class CourseUnitJson(
    val code: String,
    val name: String,
    val paperCount: Int
)

data class HomeDataJson(
    @SerializedName("recentPapers") val recentPapers: List<PaperJson>,
    @SerializedName("courseUnits") val courseUnits: List<CourseUnitJson>,
    @SerializedName("continueStudying") val continueStudying: List<PaperJson>
)

// ─────────────────────────────────────────────
// VIEWMODEL
// ─────────────────────────────────────────────

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                // Step 1: Open and read the JSON file from assets
                val jsonString = getApplication<Application>()
                    .assets
                    .open("home_data.json")
                    .bufferedReader()
                    .use { it.readText() }

                // Step 2: Parse JSON into our data classes using Gson
                val homeData = Gson().fromJson(jsonString, HomeDataJson::class.java)

                // Step 3: Map JSON objects to domain models
                val papers = homeData.recentPapers.map { it.toPaper() }
                val courseUnits = homeData.courseUnits.map {
                    CourseUnit(it.code, it.name, it.paperCount)
                }
                val continueStudying = homeData.continueStudying.map {
                    ContinueStudyingItem(
                        paper = it.toPaper(),
                        progressPercent = 45
                    )
                }

                // Step 4: Update the state
                _state.update { currentState ->
                    currentState.copy(
                        papers = papers,
                        filteredPapers = papers,
                        courseUnits = courseUnits,
                        continueStudying = continueStudying,
                        userName = "Victory"
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { currentState ->
            val filtered = if (query.isEmpty()) {
                currentState.papers
            } else {
                currentState.papers.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.courseUnit.contains(query, ignoreCase = true)
                }
            }
            currentState.copy(
                searchQuery = query,
                filteredPapers = filtered
            )
        }
    }

    fun onFilterSelected(filter: PaperType?) {
        _state.update { currentState ->
            val filtered = if (filter == null) {
                currentState.papers
            } else {
                currentState.papers.filter { it.type == filter }
            }
            currentState.copy(
                selectedFilter = filter,
                filteredPapers = filtered
            )
        }
    }
}

// ─────────────────────────────────────────────
// EXTENSION FUNCTION
// ─────────────────────────────────────────────

fun PaperJson.toPaper(): Paper {
    return Paper(
        id = this.id,
        title = this.title,
        courseUnit = this.courseUnit,
        type = when (this.type.trim().uppercase()) {
            "EXAM" -> PaperType.EXAM
            "TEST" -> PaperType.TEST_CAT
            else -> PaperType.LECTURE_NOTES
        },
        academicYear = this.academicYear,
        uploadedBy = this.uploadedBy,
        timeAgo = this.timeAgo,
        pageCount = this.pageCount
    )
}