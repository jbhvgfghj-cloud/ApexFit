package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WorkoutLogEntity
import com.example.ui.theme.*

data class ExerciseDetail(
    val name: String,
    val targetMuscle: String,
    val secondaryMuscles: String,
    val difficulty: String,
    val instructions: List<String>,
    val commonMistakes: String,
    val safetyTip: String,
    val activationScore: Int // % muscle activation
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsMuscleScreen(
    generatedWorkout: String?,
    isGenerating: Boolean,
    workoutLogs: List<WorkoutLogEntity>,
    onGenerateWorkout: (String, String, String, List<String>) -> Unit
) {
    var selectedMuscleGroup by remember { mutableStateOf("Chest") }
    var selectedGoal by remember { mutableStateOf("Muscle Gain") }
    var selectedLevel by remember { mutableStateOf("Intermediate") }
    var selectedEquipment by remember { mutableStateOf("Dumbbells & Gym") }

    val muscleGroups = listOf(
        "Chest", "Back", "Shoulders", "Biceps", "Triceps",
        "Abs & Core", "Glutes", "Quadriceps", "Hamstrings", "Full Body"
    )

    val exercisesMap = remember {
        mapOf(
            "Chest" to listOf(
                ExerciseDetail(
                    name = "Incline Dumbbell Bench Press",
                    targetMuscle = "Upper Pectoralis Major",
                    secondaryMuscles = "Anterior Deltoid, Triceps Brachii",
                    difficulty = "Intermediate",
                    instructions = listOf("Set incline bench to 30 degrees.", "Drive dumbbells up while squeezing chest at peak.", "Lower with 3-second controlled tempo."),
                    commonMistakes = "Flaring elbows out past 90 degrees.",
                    safetyTip = "Keep shoulder blades retracted and depressed into bench.",
                    activationScore = 92
                ),
                ExerciseDetail(
                    name = "Cable Chest Flyes",
                    targetMuscle = "Sternal Pectoralis",
                    secondaryMuscles = "Anterior Deltoids",
                    difficulty = "Beginner",
                    instructions = listOf("Set pulley to shoulder height.", "Bring hands together in wide hugging arc.", "Squeeze chest for 1 second at peak."),
                    commonMistakes = "Bending elbows too much turning it into a press.",
                    safetyTip = "Maintain constant slight bend in elbows throughout movement.",
                    activationScore = 88
                )
            ),
            "Back" to listOf(
                ExerciseDetail(
                    name = "Lat Pulldown / Wide Pull-Ups",
                    targetMuscle = "Latissimus Dorsi",
                    secondaryMuscles = "Rhomboids, Biceps, Rear Delts",
                    difficulty = "Intermediate",
                    instructions = listOf("Grasp bar slightly wider than shoulder width.", "Drive elbows down towards hips.", "Squeeze lats at bottom position."),
                    commonMistakes = "Leaning back excessively to pull weight.",
                    safetyTip = "Avoid pulling bar behind neck.",
                    activationScore = 94
                )
            ),
            "Abs & Core" to listOf(
                ExerciseDetail(
                    name = "Hanging Knee / Leg Raises",
                    targetMuscle = "Rectus Abdominis & Lower Abs",
                    secondaryMuscles = "Obliques, Hip Flexors",
                    difficulty = "Advanced",
                    instructions = listOf("Hang from pull-up bar with overhand grip.", "Curv hip upwards bringing knees towards chest.", "Slowly lower without swinging."),
                    commonMistakes = "Using momentum and swinging hips.",
                    safetyTip = "Engage lats to stabilize upper body.",
                    activationScore = 96
                )
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Workout Generator Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_workout_generator_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Custom Workout Generator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedGoal == "Muscle Gain",
                            onClick = { selectedGoal = "Muscle Gain" },
                            label = { Text("Muscle Gain") }
                        )
                        FilterChip(
                            selected = selectedGoal == "Fat Loss",
                            onClick = { selectedGoal = "Fat Loss" },
                            label = { Text("Fat Loss") }
                        )
                        FilterChip(
                            selected = selectedGoal == "Strength",
                            onClick = { selectedGoal = "Strength" },
                            label = { Text("Strength") }
                        )
                    }

                    Button(
                        onClick = {
                            onGenerateWorkout(selectedGoal, selectedLevel, selectedEquipment, listOf(selectedMuscleGroup))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("generate_routine_button"),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isGenerating,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating Personalized Routine...")
                        } else {
                            Icon(Icons.Default.Bolt, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate $selectedMuscleGroup Workout Routine", fontWeight = FontWeight.Bold)
                        }
                    }

                    generatedWorkout?.let { routine ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = routine,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(14.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Muscle Directory Header & Chips
        item {
            Text("Target Muscle Group Directory", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(muscleGroups) { muscle ->
                    FilterChip(
                        selected = selectedMuscleGroup == muscle,
                        onClick = { selectedMuscleGroup = muscle },
                        label = { Text(muscle) },
                        leadingIcon = { Icon(Icons.Default.Adjust, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.testTag("muscle_group_${muscle.take(6)}")
                    )
                }
            }
        }

        // Selected Muscle Exercises List
        val currentExercises = exercisesMap[selectedMuscleGroup] ?: exercisesMap["Chest"] ?: emptyList()
        items(currentExercises) { exercise ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("exercise_card_${exercise.name.take(10)}"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(exercise.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Primary: ${exercise.targetMuscle}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        // Activation Badge
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AccentOrange.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Activation: ${exercise.activationScore}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentOrange,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text("Instructions:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    exercise.instructions.forEachIndexed { idx, step ->
                        Text("${idx + 1}. $step", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                    }

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Safety Tip: ${exercise.safetyTip}", fontSize = 11.sp, color = AccentGreen)
                        Text("Level: ${exercise.difficulty}", fontSize = 11.sp, color = AccentPurple, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Recent Workout Session Log
        item {
            Text("Recent Workout History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        if (workoutLogs.isEmpty()) {
            item {
                Text("No workouts recorded yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        } else {
            items(workoutLogs.take(3)) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(log.exerciseName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${log.setCount} sets • ${log.repCount} total reps", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${log.caloriesBurned} kcal", fontWeight = FontWeight.Bold, color = AccentCyan)
                            Text("Form Score: ${log.formScore}%", fontSize = 11.sp, color = AccentGreen)
                        }
                    }
                }
            }
        }
    }
}
