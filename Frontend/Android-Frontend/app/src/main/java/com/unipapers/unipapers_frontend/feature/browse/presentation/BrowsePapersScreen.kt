package com.unipapers.unipapers_frontend.feature.browse.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unipapers.unipapers_frontend.feature.browse.presentation.components.PaperListItem
import com.unipapers.unipapers_frontend.feature.browse.presentation.components.PaperTypeTabRow
import com.unipapers.unipapers_frontend.core.ui.theme.PrimaryBlue
import com.unipapers.unipapers_frontend.core.domain.model.Paper
import com.unipapers.unipapers_frontend.core.domain.model.PaperType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowsePapersScreen(
    viewModel: BrowsePapersViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onPaperClick: (String) -> Unit,
    onNavigateToPdf: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    BrowsePapersContent(
        state = state,
        onBackClick = onBackClick,
        onPaperClick = onPaperClick,
        onTypeSelected = viewModel::onTypeSelected,
        onNavigateToPdf = onNavigateToPdf
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowsePapersContent(
    state: BrowsePapersState,
    onBackClick: () -> Unit,
    onPaperClick: (String) -> Unit,
    onTypeSelected: (PaperType) -> Unit,
    onNavigateToPdf: (String) -> Unit
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue)
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = state.courseCode,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = state.courseName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        Surface(
                            modifier = Modifier.padding(end = 16.dp),
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = state.paperCount.toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "papers",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PrimaryBlue,
                        titleContentColor = Color.White
                    )
                )
                
                PaperTypeTabRow(
                    selectedType = state.selectedType,
                    onTypeSelected = onTypeSelected
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FB))
        ) {
            val filteredPapers = state.papers.filter { it.type == state.selectedType }
            
            if (filteredPapers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No ${state.selectedType.displayName.lowercase()}s found",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredPapers) { paper ->
                        PaperListItem(
                            paper = paper,
                            onClick = { 
                                onPaperClick(paper.id)
                                if (paper.url.isNotEmpty()) {
                                    onNavigateToPdf(paper.url)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun BrowsePapersScreenPreview() {
    val mockState = BrowsePapersState(
        courseCode = "CSC 1100",
        courseName = "Introduction to Computer Science",
        paperCount = 2,
        papers = listOf(
            Paper(
                id = "1",
                academicYear = "2023/2024",
                courseCode = "CSC 1100",
                courseName = "Introduction to Computer Science",
                type = PaperType.EXAM,
                yearOfStudy = 1
            ),
            Paper(
                id = "2",
                academicYear = "2023/2024",
                courseCode = "CSC 1100",
                courseName = "Introduction to Computer Science",
                type = PaperType.TEST,
                yearOfStudy = 1
            )
        )
    )
    
    BrowsePapersContent(
        state = mockState,
        onBackClick = { },
        onPaperClick = { },
        onTypeSelected = { },
        onNavigateToPdf = { }
    )
}
