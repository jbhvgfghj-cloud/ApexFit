package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Alex Vance",
    val age: Int = 26,
    val gender: String = "Male",
    val heightCm: Float = 178f,
    val weightKg: Float = 74.5f,
    val targetWeightKg: Float = 70.0f,
    val activityLevel: String = "Moderate (3-5 days/wk)",
    val fitnessGoal: String = "Muscle Building & Fat Loss",
    val bmi: Float = 23.5f,
    val bmr: Int = 1750,
    val tdee: Int = 2400,
    val dailyCalorieGoal: Int = 2200,
    val dailyProteinGoalG: Int = 160,
    val dailyCarbsGoalG: Int = 220,
    val dailyFatGoalG: Int = 70,
    val dailyWaterGoalMl: Int = 3000,
    val currentWaterMl: Int = 1750,
    val dailyStepsGoal: Int = 10000,
    val currentSteps: Int = 7842,
    val streakCount: Int = 12,
    val fitnessScore: Int = 88,
    val isDarkMode: Boolean = true
)

@Entity(tableName = "food_logs")
data class FoodLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val weightGrams: Float,
    val portionSize: String,
    val mealType: String, // Breakfast, Lunch, Dinner, Snack
    val calories: Int,
    val proteinG: Float,
    val carbsG: Float,
    val fatG: Float,
    val fiberG: Float = 0f,
    val sugarG: Float = 0f,
    val vitamins: String = "Vitamin C, A, B12",
    val minerals: String = "Calcium, Potassium, Iron",
    val sodiumMg: Float = 250f,
    val healthScore: Int = 85,
    val recommendations: String = "Great source of lean protein!",
    val imageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseName: String,
    val exerciseCategory: String, // Chest, Back, Legs, Core, Cardio
    val repCount: Int,
    val setCount: Int,
    val durationSeconds: Int,
    val caloriesBurned: Int,
    val formScore: Int = 92, // Form accuracy percentage from AI
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "step_logs")
data class StepLogEntity(
    @PrimaryKey val dateString: String, // e.g. "2026-08-07"
    val stepCount: Int,
    val distanceKm: Float,
    val caloriesBurned: Int,
    val activeMinutes: Int,
    val avgSpeedKmh: Float = 4.8f
)

@Entity(tableName = "wearable_devices")
data class WearableDeviceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val deviceType: String, // Smartwatch, Ring, Band, Scale
    val isConnected: Boolean,
    val batteryPercent: Int = 85,
    val heartRateBpm: Int = 72,
    val spO2Percent: Int = 98,
    val sleepHours: Float = 7.5f,
    val stressScore: Int = 24,
    val recoveryScore: Int = 88,
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "USER" or "AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_recipes")
data class CustomRecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val totalCalories: Int,
    val proteinG: Float,
    val carbsG: Float,
    val fatG: Float,
    val servingCount: Int = 1,
    val ingredientsList: String,
    val instructions: String
)
