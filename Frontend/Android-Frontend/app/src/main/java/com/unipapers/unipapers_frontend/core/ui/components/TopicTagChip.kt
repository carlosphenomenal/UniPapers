package com.unipapers.unipapers_frontend.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unipapers.unipapers_frontend.core.ui.theme.GrayText
import com.unipapers.unipapers_frontend.core.ui.theme.LightGray
import com.unipapers.unipapers_frontend.core.ui.theme.PrimaryBlue
import com.unipapers.unipapers_frontend.core.ui.theme.PrimaryOrange

@Composable
fun TagChip(
    text: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val backgroundColor = if (isSelected) PrimaryOrange.copy(alpha = 0.1f) else LightGray
    val contentColor = if (isSelected) PrimaryBlue else GrayText
    val borderColor = if (isSelected) PrimaryOrange else Color.Transparent

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .then(
                if (isSelected) Modifier.border(1.dp, borderColor, RoundedCornerShape(20.dp))
                else Modifier
            )
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun TagChipPreview(){
    TagChip(text = "Tag", isSelected = true, onToggle = {})
}