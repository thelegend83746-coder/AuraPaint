package com.aurapaint.studio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.aurapaint.studio.core.navigation.Screen
import com.aurapaint.studio.core.theme.AuraPaintTheme
import com.aurapaint.studio.core.theme.DarkBackground
import com.aurapaint.studio.project.ProjectRepository
import com.aurapaint.studio.ui.editor.EditorScreen
import com.aurapaint.studio.ui.home.HomeScreen
import com.aurapaint.studio.ui.settings.SettingsScreen

class MainActivity : ComponentActivity() {

    private val projectRepository by lazy { ProjectRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AuraPaintTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

                    Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
                        when (screen) {
                            is Screen.Home -> {
                                HomeScreen(
                                    repository = projectRepository,
                                    onOpenProject = { projectId ->
                                        currentScreen = Screen.Editor(projectId)
                                    },
                                    onOpenSettings = {
                                        currentScreen = Screen.Settings
                                    }
                                )
                            }

                            is Screen.Editor -> {
                                EditorScreen(
                                    projectId = screen.projectId,
                                    repository = projectRepository,
                                    onBackToGallery = {
                                        currentScreen = Screen.Home
                                    }
                                )
                            }

                            is Screen.Settings -> {
                                SettingsScreen(
                                    onBack = {
                                        currentScreen = Screen.Home
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
