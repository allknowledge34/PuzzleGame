package com.example.puzzlegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.puzzlegame.data.SettingsRepository
import com.example.puzzlegame.model.ColorPalette
import com.example.puzzlegame.ui.screens.GameScreen
import com.example.puzzlegame.ui.screens.SettingsScreen
import com.example.puzzlegame.ui.screens.SplashOverlay
import com.example.puzzlegame.ui.theme.PuzzleGameTheme
import com.example.puzzlegame.ui.theme.activePalette
import com.example.puzzlegame.viewModel.GameViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsRepo = remember { SettingsRepository(applicationContext) }
            val hapticEnabled by settingsRepo.hapticEnabledFlow.collectAsState(initial = false)
            val easyShapes by settingsRepo.easyShapesFlow.collectAsState(initial = false)
            val palette by settingsRepo.paletteFlow.collectAsState(initial = ColorPalette.JEWEL)
            val scope = rememberCoroutineScope()

            activePalette = palette

            PuzzleGameTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    val viewModel: GameViewModel = viewModel()

                    LaunchedEffect(hapticEnabled) {
                        viewModel.hapticEnabled = hapticEnabled
                    }
                    LaunchedEffect(easyShapes) {
                        viewModel.easyShapes = easyShapes
                    }

                    var showSettings by rememberSaveable { mutableStateOf(false) }

                    Box(modifier = Modifier.fillMaxSize()) {
                        GameScreen(
                            viewModel = viewModel,
                            onSettingsClick = { showSettings = true },
                            modifier = Modifier.padding(innerPadding)
                        )

                        if (showSettings) {
                            SettingsScreen(
                                hapticEnabled = hapticEnabled,
                                easyShapes = easyShapes,
                                selectedPalette = palette,
                                onHapticToggle = { enabled ->
                                    scope.launch { settingsRepo.saveHapticEnabled(enabled) }
                                },
                                onEasyShapesToggle = { enabled ->
                                    scope.launch { settingsRepo.saveEasyShapes(enabled) }
                                },
                                onPaletteSelect = { selected ->
                                    activePalette = selected
                                    scope.launch { settingsRepo.savePalette(selected) }
                                },
                                onBack = { showSettings = false }
                            )
                        }

                        var showSplash by rememberSaveable { mutableStateOf(true) }
                        if (showSplash) {
                            SplashOverlay(onFinished = { showSplash = false })
                        }
                    }
                }
            }
        }
    }
}