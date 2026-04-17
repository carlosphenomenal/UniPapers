package com.unipapers.unipapers_frontend.feature.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unipapers.unipapers_frontend.core.ui.theme.MediumGray
import com.unipapers.unipapers_frontend.core.ui.theme.NavyBlue
import com.unipapers.unipapers_frontend.core.ui.theme.NearWhite
import com.unipapers.unipapers_frontend.core.ui.theme.SimpleBlue
import com.unipapers.unipapers_frontend.feature.home.domain.model.Paper
import com.unipapers.unipapers_frontend.feature.home.domain.model.PaperType
import com.unipapers.unipapers_frontend.feature.home.presentation.components.CourseUnitGrid
import com.unipapers.unipapers_frontend.feature.home.presentation.components.FilterChipsRow
import com.unipapers.unipapers_frontend.feature.home.presentation.components.RecentPapersRow
import com.unipapers.unipapers_frontend.feature.home.presentation.viewModel.HomeState
import com.unipapers.unipapers_frontend.feature.home.presentation.viewModel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    HomeScreenContent(
        state = state,
        onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
        onFilterSelected = { viewModel.onFilterSelected(it) }
    )
}

@Composable
fun HomeScreenContent(
    state: HomeState,
    onSearchQueryChanged: (String) -> Unit,
    onFilterSelected: (PaperType?) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearWhite)
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp)
    ) {
        // Top header
        HomeHeader()

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = (-8).dp)
        ) {
            // Search bar
            HomeSearchBar(
                query = state.searchQuery,
                onQueryChange = onSearchQueryChanged
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Filter chips
            FilterChipsRow(
                selectedFilter = state.selectedFilter,
                onFilterSelected = onFilterSelected
            )



            if (state.recentPapers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                HomeSectionHeading(title = "Recent Papers")
            }
        }

        if (state.recentPapers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            // Horizontal scrolling paper cards
            RecentPapersRow(papers = state.recentPapers)
        }
        
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(24.dp))

            HomeSectionHeading(title = "Recommended for you")

            if (state.isLoading) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = SimpleBlue
                )
            }

            state.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Course unit grid
            CourseUnitGrid(papers = state.papers)

            Spacer(modifier = Modifier.height(32.dp))

        }
    }
}

// ─────────────────────────────────────────────
// HOME HEADER
// ─────────────────────────────────────────────

@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "UniPapers",
            color = SimpleBlue,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        BadgedBox(
            badge = {
                Badge(containerColor = NavyBlue) {
                    Text("2", fontSize = 10.sp, color = Color.White)
                }
            }
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = NavyBlue
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
// SEARCH BAR
// ─────────────────────────────────────────────

@Composable
fun HomeSearchBar(
    query: String?,
    onQueryChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        if (query != null) {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Search by course, code, program...",
                        color = MediumGray,
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MediumGray,
                        modifier = Modifier.size(24.dp)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = NavyBlue
                ),
                singleLine = true
            )
        }
    }
}

// ─────────────────────────────────────────────
// SECTION HEADING
// ─────────────────────────────────────────────

@Composable
fun HomeSectionHeading(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        color = SimpleBlue
    )
}

// ─────────────────────────────────────────────
// PREVIEW
// ─────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    val samplePaper1 = Paper(
        id = "1",
        courseName = "Computer Architecture",
        courseCode = "CSC 1100",
        type = PaperType.EXAM,
        academicYear = "2023/2024"
    )
    val samplePaper2 = samplePaper1.copy(id = "2")
    val samplePaper3 = samplePaper1.copy(id = "3")

    HomeScreenContent(
        state = HomeState(
            recentPapers = listOf(samplePaper1, samplePaper2),

            papers = listOf(samplePaper1, samplePaper2, samplePaper3)
        ),
        onSearchQueryChanged = {},
        onFilterSelected = {}
    )
}
