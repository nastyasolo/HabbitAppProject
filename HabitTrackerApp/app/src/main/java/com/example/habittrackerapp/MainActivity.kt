package com.example.habittrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.habittrackerapp.ui.components.HabitBottomNavigation
import com.example.habittrackerapp.ui.screens.AddEditHabitScreen
import com.example.habittrackerapp.ui.screens.HabitListScreen
import com.example.habittrackerapp.ui.screens.StatisticsScreen
import com.example.habittrackerapp.ui.screens.SettingsScreen
import com.example.habittrackerapp.ui.theme.HabitTrackerAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitTrackerAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HabitTrackerApp()
                }
            }
        }
    }
}

@Composable
fun HabitTrackerApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Определяем, показывать ли нижнюю навигацию
    val showBottomNavigation = when (currentRoute) {
        "habitList", "statistics", "settings" -> true
        else -> false
    }

    Scaffold(
        bottomBar = {
            if (showBottomNavigation) {
                HabitBottomNavigation(
                    selectedRoute = currentRoute ?: "habitList",
                    onItemSelected = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                // Очищаем стек до корня при навигации по нижней панели
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "habitList",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("habitList") {
                HabitListScreen(
                    onAddHabitClick = {
                        navController.navigate("addEditHabit/null")
                    },
                    onHabitClick = { habitId ->
                        navController.navigate("addEditHabit/$habitId")
                    }
                )
            }

            composable(
                route = "addEditHabit/{habitId}",
                arguments = listOf(
                    navArgument("habitId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val habitId = backStackEntry.arguments?.getString("habitId")
                AddEditHabitScreen(
                    habitId = if (habitId == "null") null else habitId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("statistics") {
                StatisticsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("settings") {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun HabitTrackerAppPreview_Light() {
    HabitTrackerAppTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HabitTrackerApp()
        }
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun HabitTrackerAppPreview_Dark() {
    HabitTrackerAppTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HabitTrackerApp()
        }
    }
}