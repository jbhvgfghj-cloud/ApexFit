package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.FoodAnalysisResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FitPulseViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FitPulseDatabase.getDatabase(application)
    private val repository = FitPulseRepository(db.fitPulseDao())

    val userProfile: StateFlow<UserProfileEntity> = repository.userProfile
        .map { it ?: UserProfileEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfileEntity())

    val foodLogs: StateFlow<List<FoodLogEntity>> = repository.allFoodLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workoutLogs: StateFlow<List<WorkoutLogEntity>> = repository.allWorkoutLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stepLogs: StateFlow<List<StepLogEntity>> = repository.allStepLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wearables: StateFlow<List<WearableDeviceEntity>> = repository.allWearables
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.chatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customRecipes: StateFlow<List<CustomRecipeEntity>> = repository.customRecipes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Food Scanner UI State
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanResult = MutableStateFlow<FoodAnalysisResult?>(null)
    val scanResult: StateFlow<FoodAnalysisResult?> = _scanResult.asStateFlow()

    // AI Rep Counter State
    private val _selectedExercise = MutableStateFlow("Push-ups")
    val selectedExercise: StateFlow<String> = _selectedExercise.asStateFlow()

    private val _currentReps = MutableStateFlow(0)
    val currentReps: StateFlow<Int> = _currentReps.asStateFlow()

    private val _currentSets = MutableStateFlow(1)
    val currentSets: StateFlow<Int> = _currentSets.asStateFlow()

    private val _workoutDurationSeconds = MutableStateFlow(0)
    val workoutDurationSeconds: StateFlow<Int> = _workoutDurationSeconds.asStateFlow()

    private val _formAccuracyPercent = MutableStateFlow(94)
    val formAccuracyPercent: StateFlow<Int> = _formAccuracyPercent.asStateFlow()

    private val _formFeedbackMessage = MutableStateFlow("Perfect Form! Keep chest low and core engaged.")
    val formFeedbackMessage: StateFlow<String> = _formFeedbackMessage.asStateFlow()

    private val _isRepCounterActive = MutableStateFlow(false)
    val isRepCounterActive: StateFlow<Boolean> = _isRepCounterActive.asStateFlow()

    // AI Coach UI State
    private val _isCoachThinking = MutableStateFlow(false)
    val isCoachThinking: StateFlow<Boolean> = _isCoachThinking.asStateFlow()

    // AI Generated Workout Routine Output
    private val _generatedWorkout = MutableStateFlow<String?>(null)
    val generatedWorkout: StateFlow<String?> = _generatedWorkout.asStateFlow()

    private val _isGeneratingWorkout = MutableStateFlow(false)
    val isGeneratingWorkout: StateFlow<Boolean> = _isGeneratingWorkout.asStateFlow()

    init {
        seedInitialDataIfNeeded()
    }

    private fun seedInitialDataIfNeeded() {
        viewModelScope.launch {
            repository.userProfile.first() ?: run {
                repository.saveUserProfile(UserProfileEntity())
            }

            if (repository.allFoodLogs.first().isEmpty()) {
                repository.addFoodLog(
                    FoodLogEntity(
                        name = "Oatmeal with Blueberries & Almonds",
                        category = "Complex Carbs & Fiber",
                        weightGrams = 250f,
                        portionSize = "1 bowl",
                        mealType = "Breakfast",
                        calories = 380,
                        proteinG = 14f,
                        carbsG = 58f,
                        fatG = 9f,
                        fiberG = 8f,
                        sugarG = 12f,
                        vitamins = "Vitamin B1, E, C",
                        minerals = "Manganese, Iron, Zinc",
                        sodiumMg = 120f,
                        healthScore = 96,
                        recommendations = "Great slow-release energy for morning workouts!"
                    )
                )
                repository.addFoodLog(
                    FoodLogEntity(
                        name = "Grilled Chicken Breast & Quinoa Salad",
                        category = "Lean Protein Meal",
                        weightGrams = 320f,
                        portionSize = "1 plate",
                        mealType = "Lunch",
                        calories = 510,
                        proteinG = 42f,
                        carbsG = 44f,
                        fatG = 11f,
                        fiberG = 6f,
                        sugarG = 3f,
                        vitamins = "Vitamin A, B6, B12",
                        minerals = "Potassium, Magnesium, Iron",
                        sodiumMg = 310f,
                        healthScore = 94,
                        recommendations = "High protein to rebuild muscle tissue!"
                    )
                )
            }

            if (repository.allWorkoutLogs.first().isEmpty()) {
                repository.addWorkoutLog(
                    WorkoutLogEntity(
                        exerciseName = "Barbell Bench Press",
                        exerciseCategory = "Chest",
                        repCount = 36,
                        setCount = 3,
                        durationSeconds = 1200,
                        caloriesBurned = 165,
                        formScore = 95,
                        notes = "Increased weight by 2.5kg!"
                    )
                )
                repository.addWorkoutLog(
                    WorkoutLogEntity(
                        exerciseName = "Bodyweight Squats",
                        exerciseCategory = "Legs",
                        repCount = 50,
                        setCount = 4,
                        durationSeconds = 900,
                        caloriesBurned = 140,
                        formScore = 92,
                        notes = "Clean depth on all reps."
                    )
                )
            }

            if (repository.allStepLogs.first().isEmpty()) {
                val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                repository.saveStepLog(
                    StepLogEntity(
                        dateString = todayStr,
                        stepCount = 7842,
                        distanceKm = 5.6f,
                        caloriesBurned = 380,
                        activeMinutes = 58,
                        avgSpeedKmh = 4.9f
                    )
                )
            }

            if (repository.allWearables.first().isEmpty()) {
                repository.saveWearable(
                    WearableDeviceEntity(
                        id = "watch_pro",
                        name = "FitPulse Watch Ultra",
                        deviceType = "Smartwatch",
                        isConnected = true,
                        batteryPercent = 88,
                        heartRateBpm = 68,
                        spO2Percent = 99,
                        sleepHours = 7.8f,
                        stressScore = 18,
                        recoveryScore = 92
                    )
                )
                repository.saveWearable(
                    WearableDeviceEntity(
                        id = "ring_x",
                        name = "PulseRing Gen 3",
                        deviceType = "Smart Ring",
                        isConnected = true,
                        batteryPercent = 74,
                        heartRateBpm = 66,
                        spO2Percent = 98,
                        sleepHours = 8.0f,
                        stressScore = 15,
                        recoveryScore = 95
                    )
                )
            }

            if (repository.chatMessages.first().isEmpty()) {
                repository.addChatMessage(
                    sender = "AI",
                    text = "Welcome to FitPulse AI! I'm your 24/7 personal coach. Ask me anything about your diet, AI rep counter form, or custom workout plans!"
                )
            }
        }
    }

    // Food Scanner Logic
    fun scanFood(bitmap: Bitmap?, foodQueryText: String?) {
        viewModelScope.launch {
            _isScanning.value = true
            val result = repository.scanAndAnalyzeFood(bitmap, foodQueryText)
            _scanResult.value = result
            _isScanning.value = false

            // Automatically add to food log
            repository.addFoodLog(
                FoodLogEntity(
                    name = result.name,
                    category = result.category,
                    weightGrams = result.weightGrams,
                    portionSize = result.portionSize,
                    mealType = getCurrentMealType(),
                    calories = result.calories,
                    proteinG = result.proteinG,
                    carbsG = result.carbsG,
                    fatG = result.fatG,
                    fiberG = result.fiberG,
                    sugarG = result.sugarG,
                    vitamins = result.vitamins,
                    minerals = result.minerals,
                    sodiumMg = result.sodiumMg,
                    healthScore = result.healthScore,
                    recommendations = result.recommendation
                )
            )
        }
    }

    fun addManualFoodLog(
        name: String,
        category: String,
        weightGrams: Float,
        portionSize: String,
        mealType: String,
        calories: Int,
        proteinG: Float,
        carbsG: Float,
        fatG: Float
    ) {
        viewModelScope.launch {
            repository.addFoodLog(
                FoodLogEntity(
                    name = name.ifBlank { "Custom Food Item" },
                    category = category.ifBlank { "Custom" },
                    weightGrams = weightGrams,
                    portionSize = portionSize.ifBlank { "${weightGrams.toInt()}g" },
                    mealType = mealType,
                    calories = calories,
                    proteinG = proteinG,
                    carbsG = carbsG,
                    fatG = fatG,
                    healthScore = 88,
                    recommendations = "Manually logged item added to daily nutrition journal."
                )
            )
        }
    }

    fun deleteFoodLog(id: Long) {
        viewModelScope.launch {
            repository.deleteFoodLog(id)
        }
    }

    // Water Tracker Logic
    fun addWaterMl(amountMl: Int) {
        viewModelScope.launch {
            val current = userProfile.value
            val updated = current.copy(currentWaterMl = current.currentWaterMl + amountMl)
            repository.saveUserProfile(updated)
        }
    }

    // AI Rep Counter Logic
    fun selectRepExercise(exercise: String) {
        _selectedExercise.value = exercise
        _currentReps.value = 0
        _currentSets.value = 1
        _workoutDurationSeconds.value = 0
    }

    fun toggleRepCounterActive(active: Boolean) {
        _isRepCounterActive.value = active
    }

    fun triggerRepDetected() {
        _currentReps.value += 1
        val reps = _currentReps.value
        if (reps > 0 && reps % 12 == 0) {
            _currentSets.value += 1
        }
        _workoutDurationSeconds.value += 3

        // Dynamic AI feedback based on rep state
        val feedbacks = listOf(
            "Excellent depth! Keep chest upright.",
            "Great motion smoothness! 95% form match.",
            "Squeeze target muscles at peak contraction!",
            "Perfect spinal alignment maintained.",
            "Pacing is on point! Maintain controlled tempo."
        )
        _formFeedbackMessage.value = feedbacks.random()
        _formAccuracyPercent.value = (91..98).random()
    }

    fun finishWorkoutSession() {
        val reps = _currentReps.value
        val sets = _currentSets.value
        val duration = _workoutDurationSeconds.value.coerceAtLeast(30)
        val calories = (reps * 2.5 + duration * 0.15).toInt()
        val exercise = _selectedExercise.value

        viewModelScope.launch {
            repository.addWorkoutLog(
                WorkoutLogEntity(
                    exerciseName = exercise,
                    exerciseCategory = getExerciseCategory(exercise),
                    repCount = reps,
                    setCount = sets,
                    durationSeconds = duration,
                    caloriesBurned = calories,
                    formScore = _formAccuracyPercent.value,
                    notes = "AI Rep Counter Session - $sets sets of $reps reps."
                )
            )
            // Reset counter
            _currentReps.value = 0
            _currentSets.value = 1
            _workoutDurationSeconds.value = 0
            _isRepCounterActive.value = false
        }
    }

    // AI Coach Chat Logic
    fun sendCoachMessage(userText: String) {
        if (userText.isBlank()) return
        viewModelScope.launch {
            repository.addChatMessage("USER", userText)
            _isCoachThinking.value = true

            val profile = userProfile.value
            val profileInfo = "Name: ${profile.name}, Goal: ${profile.fitnessGoal}, Weight: ${profile.weightKg}kg, Target Cal: ${profile.dailyCalorieGoal}kcal"

            val history = chatMessages.value.takeLast(6).map { it.sender to it.text }

            val response = repository.askAICoach(userText, profileInfo, history)
            repository.addChatMessage("AI", response)
            _isCoachThinking.value = false
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    // AI Workout Routine Generator
    fun generateWorkoutRoutine(
        goal: String,
        level: String,
        equipment: String,
        muscles: List<String>
    ) {
        viewModelScope.launch {
            _isGeneratingWorkout.value = true
            val routineText = repository.generateWorkout(goal, level, equipment, muscles)
            _generatedWorkout.value = routineText
            _isGeneratingWorkout.value = false
        }
    }

    // Profile & Theme Logic
    fun toggleDarkMode() {
        viewModelScope.launch {
            val current = userProfile.value
            repository.saveUserProfile(current.copy(isDarkMode = !current.isDarkMode))
        }
    }

    fun updateProfile(
        name: String,
        age: Int,
        heightCm: Float,
        weightKg: Float,
        targetWeightKg: Float,
        goal: String
    ) {
        viewModelScope.launch {
            val current = userProfile.value
            val bmi = weightKg / ((heightCm / 100) * (heightCm / 100))
            val bmr = (10 * weightKg + 6.25 * heightCm - 5 * age + 5).toInt()
            val tdee = (bmr * 1.45).toInt()

            val updated = current.copy(
                name = name,
                age = age,
                heightCm = heightCm,
                weightKg = weightKg,
                targetWeightKg = targetWeightKg,
                fitnessGoal = goal,
                bmi = bmi,
                bmr = bmr,
                tdee = tdee,
                dailyCalorieGoal = if (goal.contains("Loss")) tdee - 400 else if (goal.contains("Muscle") || goal.contains("Gain")) tdee + 300 else tdee
            )
            repository.saveUserProfile(updated)
        }
    }

    private fun getCurrentMealType(): String {
        val hour = SimpleDateFormat("HH", Locale.getDefault()).format(Date()).toIntOrNull() ?: 12
        return when (hour) {
            in 5..10 -> "Breakfast"
            in 11..15 -> "Lunch"
            in 16..21 -> "Dinner"
            else -> "Snack"
        }
    }

    private fun getExerciseCategory(exercise: String): String {
        val lower = exercise.lowercase()
        return when {
            lower.contains("push") || lower.contains("chest") || lower.contains("press") -> "Chest & Triceps"
            lower.contains("pull") || lower.contains("row") || lower.contains("curl") -> "Back & Biceps"
            lower.contains("squat") || lower.contains("lunge") || lower.contains("leg") -> "Legs & Glutes"
            lower.contains("plank") || lower.contains("sit") || lower.contains("crunch") || lower.contains("ab") -> "Core"
            else -> "Full Body"
        }
    }
}
