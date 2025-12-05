package com.example.habittrackerapp.ui.navigation

sealed class NavRoutes(val route: String) {
    object HabitList : NavRoutes("habitList")
    object AddHabit : NavRoutes("addHabit")
    object HabitDetail : NavRoutes("habitDetail/{habitId}") {
        fun createRoute(habitId: String) = "habitDetail/$habitId"
    }
}