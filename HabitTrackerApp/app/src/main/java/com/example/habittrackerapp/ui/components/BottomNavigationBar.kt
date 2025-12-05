package com.example.habittrackerapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.habittrackerapp.ui.theme.HabitTrackerAppTheme

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
) {
    object Habits : BottomNavItem(
        route = "habits",
        label = "Привычки",
        icon = { Icon(Icons.Default.Home, contentDescription = "Привычки") }
    )

    object Statistics : BottomNavItem(
        route = "statistics",
        label = "Статистика",
        icon = { Icon(Icons.Default.ShowChart, contentDescription = "Статистика") }
    )

    object Settings : BottomNavItem(
        route = "settings",
        label = "Настройки",
        icon = { Icon(Icons.Default.Settings, contentDescription = "Настройки") }
    )
}

@Composable
fun HabitBottomNavigation(
    currentRoute: String,
    onItemClick: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Habits,
        BottomNavItem.Statistics,
        BottomNavItem.Settings
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onItemClick(item.route) },
                icon = item.icon,
                label = { Text(item.label) }
            )
        }
    }
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    HabitTrackerAppTheme {
        HabitBottomNavigation(
            currentRoute = "habits",
            onItemClick = {}
        )
    }
}