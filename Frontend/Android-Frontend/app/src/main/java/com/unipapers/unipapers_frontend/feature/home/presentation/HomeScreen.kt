package com.unipapers.unipapers_frontend.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unipapers.unipapers_frontend.feature.home.presentation.components.CourseUnitGrid
import com.unipapers.unipapers_frontend.feature.home.presentation.components.ContinueStudyingSection
import com.unipapers.unipapers_frontend.feature.home.presentation.components.FilterChipsRow
import com.unipapers.unipapers_frontend.feature.home.presentation.components.RecentPapersRow
import com.unipapers.unipapers_frontend.core.ui.theme.NavyBlue
import com.unipapers.unipapers_frontend.core.ui.theme.NearWhite
import com.unipapers.unipapers_frontend.core.ui.theme.MediumGray
import com.unipapers.unipapers_frontend.core.ui.theme.Amber

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearWhite)
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        // Top header
        HomeHeader(userName = state.userName)

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            HomeSearchBar(
                query = state.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChanged(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filter chips
            FilterChipsRow(
                selectedFilter = state.selectedFilter,
                onFilterSelected = { viewModel.onFilterSelected(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            HomeSectionHeading(title = "Recently Uploaded")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal scrolling paper cards
        RecentPapersRow(papers = state.filteredPapers)

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

            Spacer(modifier = Modifier.height(24.dp))

            HomeSectionHeading(title = "Browse by Course Unit")

            Spacer(modifier = Modifier.height(8.dp))

            // Course unit grid
            CourseUnitGrid(courseUnits = state.courseUnits)

            Spacer(modifier = Modifier.height(24.dp))

            HomeSectionHeading(title = "Continue Studying")

            Spacer(modifier = Modifier.height(8.dp))

            // Continue studying cards
            ContinueStudyingSection(items = state.continueStudying)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────
// HOME HEADER
// ─────────────────────────────────────────────

@Composable
fun HomeHeader(userName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyBlue)
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Good morning,",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userName,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Icons top right
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }
                BadgedBox(
                    badge = {
                        Badge(containerColor = Amber) {
                            Text("2", fontSize = 10.sp, color = NavyBlue)
                        }
                    }
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// SEARCH BAR
// ─────────────────────────────────────────────

@Composable
fun HomeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Search topic, course unit, or paper type...",
                color = MediumGray,
                fontSize = 13.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MediumGray
            )
        },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NavyBlue,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true
    )
}

// ─────────────────────────────────────────────
// SECTION HEADING
// ─────────────────────────────────────────────

@Composable
fun HomeSectionHeading(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = NavyBlue
    )
}

// ─────────────────────────────────────────────
// PREVIEW
// ─────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}