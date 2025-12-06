package com.example.habittrackerapp

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
import com.example.habittrackerapp.ui.screens.auth.LoginScreen
import com.example.habittrackerapp.ui.screens.auth.RegisterScreen
import com.example.habittrackerapp.ui.theme.HabitTrackerAppTheme

@Composable
fun MainApp() {
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

@Composable
fun AuthScreen() {
    val navController = rememberNavController()
    val authViewModel: com.example.habittrackerapp.ui.viewmodel.AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val authState = authViewModel.uiState.collectAsState().value

    // Если аутентифицирован, выходим из экрана аутентификации
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            // Автоматически переключится через AppContent
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    // Автоматически переключится через authState
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    // Автоматически переключится через authState
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
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
            MainApp()
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
            MainApp()
        }
    }
}