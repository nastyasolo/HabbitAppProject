package com.example.habittrackerapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habittrackerapp.domain.model.Priority
import com.example.habittrackerapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityChip(
    priority: Priority,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (containerColor, selectedContainerColor, textColor) = when (priority) {
        Priority.HIGH -> Triple(
            PriorityHighContainer.copy(alpha = if (isSelected) 1f else 0.2f),
            PriorityHighContainer,
            PriorityHigh
        )
        Priority.MEDIUM -> Triple(
            PriorityMediumContainer.copy(alpha = if (isSelected) 1f else 0.2f),
            PriorityMediumContainer,
            PriorityMedium
        )
        Priority.LOW -> Triple(
            PriorityLowContainer.copy(alpha = if (isSelected) 1f else 0.2f),
            PriorityLowContainer,
            PriorityLow
        )
    }

    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = priority.displayName,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = if (isSelected) Color.Black else textColor
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = containerColor,
            selectedContainerColor = selectedContainerColor,
            labelColor = if (isSelected) Color.Black else textColor,
            selectedLabelColor = Color.Black,
            iconColor = if (isSelected) Color.Black else textColor,
            selectedLeadingIconColor = Color.Black
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = textColor.copy(alpha = 0.3f),
            selectedBorderColor = textColor,
            borderWidth = 1.dp,
            selectedBorderWidth = 2.dp
        ),
        modifier = modifier.padding(vertical = 4.dp)
    )
}