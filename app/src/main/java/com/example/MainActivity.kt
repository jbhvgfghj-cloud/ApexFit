package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.theme.FitPulseTheme
import com.example.viewmodel.FitPulseViewModel

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val viewModel: FitPulseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
            val foodLogs by viewModel.foodLogs.collectAsStateWithLifecycle()
            val workoutLogs by viewModel.workoutLogs.collectAsStateWithLifecycle()
            val wearables by viewModel.wearables.collectAsStateWithLifecycle()
            val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

            val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
            val scanResult by viewModel.scanResult.collectAsStateWithLifecycle()

            val selectedExercise by viewModel.selectedExercise.collectAsStateWithLifecycle()
            val currentReps by viewModel.currentReps.collectAsStateWithLifecycle()
            val currentSets by viewModel.currentSets.collectAsStateWithLifecycle()
            val workoutDuration by viewModel.workoutDurationSeconds.collectAsStateWithLifecycle()
            val formAccuracy by viewModel.formAccuracyPercent.collectAsStateWithLifecycle()
            val formFeedback by viewModel.formFeedbackMessage.collectAsStateWithLifecycle()
            val isRepActive by viewModel.isRepCounterActive.collectAsStateWithLifecycle()

            val isCoachThinking by viewModel.isCoachThinking.collectAsStateWithLifecycle()
            val generatedWorkout by viewModel.generatedWorkout.collectAsStateWithLifecycle()
            val isGeneratingWorkout by viewModel.isGeneratingWorkout.collectAsStateWithLifecycle()

            var selectedTab by remember { mutableIntStateOf(0) }

            FitPulseTheme(darkTheme = userProfile.isDarkMode) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("main_scaffold"),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "FitPulse AI",
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = { viewModel.toggleDarkMode() },
                                    modifier = Modifier.testTag("theme_toggle_button")
                                ) {
                                    Icon(
                                        imageVector = if (userProfile.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = "Theme Toggle"
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .testTag("main_bottom_nav_bar"),
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                icon = { Icon(if (selectedTab == 0) Icons.Filled.Dashboard else Icons.Outlined.Dashboard, contentDescription = "Dashboard") },
                                label = { Text("Dashboard") },
                                modifier = Modifier.testTag("nav_tab_dashboard")
                            )
                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                icon = { Icon(if (selectedTab == 1) Icons.Filled.QrCodeScanner else Icons.Outlined.QrCodeScanner, contentDescription = "AI Scanner") },
                                label = { Text("AI Scan") },
                                modifier = Modifier.testTag("nav_tab_scan")
                            )
                            NavigationBarItem(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                icon = { Icon(if (selectedTab == 2) Icons.Filled.FitnessCenter else Icons.Outlined.FitnessCenter, contentDescription = "Rep Counter") },
                                label = { Text("AI Reps") },
                                modifier = Modifier.testTag("nav_tab_rep_counter")
                            )
                            NavigationBarItem(
                                selected = selectedTab == 3,
                                onClick = { selectedTab = 3 },
                                icon = { Icon(if (selectedTab == 3) Icons.Filled.SportsGymnastics else Icons.Outlined.SportsGymnastics, contentDescription = "Workouts") },
                                label = { Text("Workouts") },
                                modifier = Modifier.testTag("nav_tab_workouts")
                            )
                            NavigationBarItem(
                                selected = selectedTab == 4,
                                onClick = { selectedTab = 4 },
                                icon = { Icon(if (selectedTab == 4) Icons.Filled.Psychology else Icons.Outlined.Psychology, contentDescription = "AI Coach") },
                                label = { Text("Coach") },
                                modifier = Modifier.testTag("nav_tab_coach")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTab) {
                            0 -> DashboardScreen(
                                userProfile = userProfile,
                                foodLogs = foodLogs,
                                onNavigateToScan = { selectedTab = 1 },
                                onNavigateToRepCounter = { selectedTab = 2 },
                                onNavigateToCoach = { selectedTab = 4 },
                                onAddWater = { amount -> viewModel.addWaterMl(amount) }
                            )
                            1 -> FoodScannerScreen(
                                isScanning = isScanning,
                                scanResult = scanResult,
                                foodLogs = foodLogs,
                                onScanFood = { bitmap, query -> viewModel.scanFood(bitmap, query) },
                                onAddManualFood = { name, cat, weight, portion, meal, cal, p, c, f ->
                                    viewModel.addManualFoodLog(name, cat, weight, portion, meal, cal, p, c, f)
                                },
                                onDeleteFood = { id -> viewModel.deleteFoodLog(id) }
                            )
                            2 -> RepCounterScreen(
                                selectedExercise = selectedExercise,
                                currentReps = currentReps,
                                currentSets = currentSets,
                                durationSeconds = workoutDuration,
                                formAccuracyPercent = formAccuracy,
                                formFeedbackMessage = formFeedback,
                                isActive = isRepActive,
                                onSelectExercise = { ex -> viewModel.selectRepExercise(ex) },
                                onToggleActive = { active -> viewModel.toggleRepCounterActive(active) },
                                onTriggerRep = { viewModel.triggerRepDetected() },
                                onFinishWorkout = { viewModel.finishWorkoutSession() }
                            )
                            3 -> WorkoutsMuscleScreen(
                                generatedWorkout = generatedWorkout,
                                isGenerating = isGeneratingWorkout,
                                workoutLogs = workoutLogs,
                                onGenerateWorkout = { goal, level, equip, muscles ->
                                    viewModel.generateWorkoutRoutine(goal, level, equip, muscles)
                                }
                            )
                            4 -> AICoachDevicesScreen(
                                userProfile = userProfile,
                                chatMessages = chatMessages,
                                wearables = wearables,
                                isThinking = isCoachThinking,
                                onSendMessage = { text -> viewModel.sendCoachMessage(text) },
                                onToggleDarkMode = { viewModel.toggleDarkMode() },
                                onUpdateProfile = { name, age, h, w, tw, goal ->
                                    viewModel.updateProfile(name, age, h, w, tw, goal)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
