package com.example.habittrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.habittrackerapp.ui.theme.HabitTrackerAppTheme
import com.example.habittrackerapp.ui.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Получаем ViewModel для темы
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            HabitTrackerAppTheme(darkTheme = isDarkTheme) {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent() {
    val authViewModel: com.example.habittrackerapp.ui.viewmodel.AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()

    if (!authState.isAuthenticated) {
        AuthScreen()
    } else {
        MainApp()
    }
}