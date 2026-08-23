package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf("splash") }
                    var showExpandedPlayer by remember { mutableStateOf(false) }
                    
                    val mediaViewModel: MediaViewModel = viewModel()

                    Box(modifier = Modifier.fillMaxSize()) {
                        when (currentScreen) {
                            "splash" -> {
                                HansMediaSplash(
                                    onSplashFinished = {
                                        currentScreen = "dashboard"
                                    }
                                )
                            }
                            "dashboard" -> {
                                HansMediaDashboard(
                                    viewModel = mediaViewModel,
                                    onMediaSelected = { mediaItem ->
                                        // Play media and open expanded player
                                        mediaViewModel.playMedia(mediaItem)
                                        showExpandedPlayer = true
                                    },
                                    onAdminClicked = {
                                        currentScreen = "admin"
                                    }
                                )
                            }
                            "admin" -> {
                                HansMediaAdmin(
                                    viewModel = mediaViewModel,
                                    onBack = {
                                        currentScreen = "dashboard"
                                    }
                                )
                            }
                        }

                        // Seamless animated overlay transition for the expanded high-fidelity player
                        AnimatedVisibility(
                            visible = showExpandedPlayer && currentScreen == "dashboard",
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            ExpandedPlayerView(
                                viewModel = mediaViewModel,
                                onDismiss = { showExpandedPlayer = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
