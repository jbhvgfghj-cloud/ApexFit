package com.example.data

import android.graphics.Bitmap
import com.example.network.FoodAnalysisResult
import com.example.network.GeminiService
import kotlinx.coroutines.flow.Flow

class FitPulseRepository(private val dao: FitPulseDao) {

    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val allFoodLogs: Flow<List<FoodLogEntity>> = dao.getAllFoodLogs()
    val allWorkoutLogs: Flow<List<WorkoutLogEntity>> = dao.getAllWorkoutLogs()
    val allStepLogs: Flow<List<StepLogEntity>> = dao.getAllStepLogs()
    val allWearables: Flow<List<WearableDeviceEntity>> = dao.getAllWearables()
    val chatMessages: Flow<List<ChatMessageEntity>> = dao.getAllChatMessages()
    val customRecipes: Flow<List<CustomRecipeEntity>> = dao.getAllRecipes()

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        dao.insertOrUpdateUserProfile(profile)
    }

    suspend fun addFoodLog(foodLog: FoodLogEntity) {
        dao.insertFoodLog(foodLog)
    }

    suspend fun deleteFoodLog(id: Long) {
        dao.deleteFoodLog(id)
    }

    suspend fun clearFoodLogs() {
        dao.clearAllFoodLogs()
    }

    suspend fun addWorkoutLog(workoutLog: WorkoutLogEntity) {
        dao.insertWorkoutLog(workoutLog)
    }

    suspend fun deleteWorkoutLog(id: Long) {
        dao.deleteWorkoutLog(id)
    }

    suspend fun saveStepLog(stepLog: StepLogEntity) {
        dao.insertStepLog(stepLog)
    }

    suspend fun saveWearable(device: WearableDeviceEntity) {
        dao.insertOrUpdateWearable(device)
    }

    suspend fun addChatMessage(sender: String, text: String) {
        dao.insertChatMessage(ChatMessageEntity(sender = sender, text = text))
    }

    suspend fun clearChat() {
        dao.clearChatHistory()
    }

    suspend fun saveRecipe(recipe: CustomRecipeEntity) {
        dao.insertRecipe(recipe)
    }

    suspend fun deleteRecipe(id: Long) {
        dao.deleteRecipe(id)
    }

    // AI Integrations
    suspend fun scanAndAnalyzeFood(bitmap: Bitmap?, queryText: String?): FoodAnalysisResult {
        return GeminiService.analyzeFoodImage(bitmap, queryText)
    }

    suspend fun askAICoach(
        userPrompt: String,
        userProfileInfo: String,
        history: List<Pair<String, String>>
    ): String {
        return GeminiService.getCoachResponse(userPrompt, userProfileInfo, history)
    }

    suspend fun generateWorkout(
        goal: String,
        level: String,
        equipment: String,
        targetMuscles: List<String>
    ): String {
        return GeminiService.generateWorkoutRoutine(goal, level, equipment, targetMuscles)
    }
}
