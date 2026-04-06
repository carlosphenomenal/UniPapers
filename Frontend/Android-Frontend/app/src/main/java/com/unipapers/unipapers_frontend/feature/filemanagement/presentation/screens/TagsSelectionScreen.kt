package com.unipapers.unipapers_frontend.feature.filemanagement.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unipapers.unipapers_frontend.core.ui.components.TagChip
import com.unipapers.unipapers_frontend.core.ui.theme.GrayText
import com.unipapers.unipapers_frontend.core.ui.theme.LightGray
import com.unipapers.unipapers_frontend.core.ui.theme.PrimaryBlue


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagsSelectionScreen(
    allTags: List<String>,
    selectedTags: List<String>,
    onTagToggle: (String) -> Unit,
    onAddTag: (String) -> Unit
) {
    var customTag by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Topic Tags",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Confirm AI-suggested tags to help students find this paper",
            style = MaterialTheme.typography.bodyMedium.copy(color = GrayText)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "${selectedTags.size} tags confirmed",
            style = MaterialTheme.typography.bodySmall.copy(color = GrayText)
        )

        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            allTags.forEach { tag ->
                val isSelected = selectedTags.contains(tag)
                TagChip(
                    text = tag,
                    isSelected = isSelected,
                    onToggle = { onTagToggle(tag) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = customTag,
                onValueChange = { customTag = it },
                placeholder = { Text("Add custom tag...", color = GrayText) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = LightGray,
                    focusedBorderColor = PrimaryBlue
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryBlue)
                    .clickable {
                        if (customTag.isNotBlank()) {
                            onAddTag(customTag.trim())
                            customTag = ""
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Tag",
                    tint = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}




@Composable
@Preview(showBackground = true)
fun TagSelectionScreenPreview(){
    TagsSelectionScreen(allTags = listOf("Tag1", "Tag2", "Tag3"), selectedTags = listOf("Tag1", "Tag2"), onTagToggle = {}, onAddTag = {})
}
