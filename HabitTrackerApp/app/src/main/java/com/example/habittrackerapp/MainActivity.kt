package com.example.habittrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

    NavHost(
        navController = navController,
        startDestination = "habitList"
    ) {
        composable("habitList") {
            HabitListScreen(
                onAddHabitClick = {
                    navController.navigate("addEditHabit")
                },
                onHabitClick = { habitId ->
                    navController.navigate("addEditHabit?habitId=$habitId")
                }
            )
        }

        composable(
            route = "addEditHabit?habitId={habitId}",
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
                habitId = habitId,
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

@Preview(showBackground = true)
@Composable
fun HabitTrackerAppPreview() {
    HabitTrackerAppTheme {
        HabitTrackerApp()
    }
}