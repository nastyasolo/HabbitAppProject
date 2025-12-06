package com.example.habittrackerapp.ui.navigation

sealed class Screen(val route: String) {
    object HabitList : Screen("habitList")
    object AddEditHabit : Screen("addEditHabit?habitId={habitId}") {
        fun createRoute(habitId: String? = null) =
            if (habitId != null) "addEditHabit?habitId=$habitId" else "addEditHabit"
    }
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
}

// Модель для элемента нижней навигации
data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: Int, // Используем ресурсы для иконок
    val selectedIcon: Int
)