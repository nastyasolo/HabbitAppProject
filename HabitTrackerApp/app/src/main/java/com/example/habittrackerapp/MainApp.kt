package com.example.habittrackerapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.habittrackerapp.ui.components.HabitBottomNavigation
import com.example.habittrackerapp.ui.screens.*
import com.example.habittrackerapp.ui.screens.auth.LoginScreen
import com.example.habittrackerapp.ui.screens.auth.RegisterScreen
import com.example.habittrackerapp.ui.theme.HabitTrackerAppTheme
import com.example.habittrackerapp.ui.viewmodel.AuthViewModel

@Composable
fun MainApp() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()

    if (authState.isAuthenticated) {
        AuthenticatedApp()
    } else {
        AuthScreen()
    }
}

@Composable
fun AuthScreen() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun AuthenticatedApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomNavigation = when (currentRoute) {
        "habitList", "tasks", "statistics", "settings", "firestore_only_habits" -> true
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

            // ТАСКИ
            composable("tasks") {
                TaskListScreen(
                    navController = navController
                )
            }

            composable(
                route = "addEditTask/{taskId}",
                arguments = listOf(
                    navArgument("taskId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId")
                AddEditTaskScreen(
                    taskId = if (taskId == "null") null else taskId,
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
            // Firestore-only демо экраны
            composable("firestore_only_habits") {
                FirestoreOnlyHabitListScreen(
                    onAddHabitClick = {
                        navController.navigate("firestore_only_add_edit_habit/null")
                    },
                    onHabitClick = { habitId ->
                        navController.navigate("firestore_only_add_edit_habit/$habitId")
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "firestore_only_add_edit_habit/{habitId}",
                arguments = listOf(
                    navArgument("habitId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val habitId = backStackEntry.arguments?.getString("habitId")
                FirestoreOnlyAddEditHabitScreen(
                    habitId = if (habitId == "null") null else habitId,
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