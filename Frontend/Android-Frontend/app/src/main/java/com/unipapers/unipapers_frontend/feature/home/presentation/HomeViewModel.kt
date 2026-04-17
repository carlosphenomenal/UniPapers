package com.unipapers.unipapers_frontend.feature.home.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
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
    val academicYear: String,
    val courseName: String,
    val courseCode: String,
    val type: String,
    val pageCount: Int
)

data class HomeDataJson(
    @SerializedName("recentPapers") val recentPapers: List<PaperJson>
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
                val recentPapers = homeData.recentPapers.map { it.toPaper() }

                // Step 4: Update the state
                _state.update { currentState ->
                    currentState.copy(
                        recentPapers = recentPapers,
                        papers = recentPapers // Just for demonstration
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
                    it.courseName.contains(query, ignoreCase = true) ||
                            it.courseName.contains(query, ignoreCase = true)
                }
            }
            currentState.copy(
                searchQuery = query,
                papers = filtered
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
                papers = filtered
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
        courseName = this.courseName,
        courseCode = this.courseCode,
        type = when (this.type) {
            "EXAM" -> PaperType.EXAM
            "TEST" -> PaperType.TEST
            "ASSIGNMENT" -> PaperType.ASSIGNMENT
            else -> PaperType.NOTES
        },
        academicYear = this.academicYear
    )
}