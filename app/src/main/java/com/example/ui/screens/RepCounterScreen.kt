package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun RepCounterScreen(
    selectedExercise: String,
    currentReps: Int,
    currentSets: Int,
    durationSeconds: Int,
    formAccuracyPercent: Int,
    formFeedbackMessage: String,
    isActive: Boolean,
    onSelectExercise: (String) -> Unit,
    onToggleActive: (Boolean) -> Unit,
    onTriggerRep: () -> Unit,
    onFinishWorkout: () -> Unit
) {
    val exercises = listOf(
        "Push-ups", "Bodyweight Squats", "Walking Lunges", "Dumbbell Bicep Curls",
        "Plank Hold", "Jumping Jacks", "Burpees", "Sit-ups", "Overhead Press"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Exercise Selector Chips
        Text("Select Exercise for Pose Analysis:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(exercises) { ex ->
                FilterChip(
                    selected = selectedExercise == ex,
                    onClick = { onSelectExercise(ex) },
                    label = { Text(ex) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.FitnessCenter,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (selectedExercise == ex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    modifier = Modifier.testTag("exercise_chip_${ex.take(8)}")
                )
            }
        }

        // Camera Pose Tracking Simulation Frame with Canvas Skeleton Overlay
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("pose_tracker_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Animated Pulsing Skeleton Canvas Overlay
                val primary = MaterialTheme.colorScheme.primary
                val accentCyan = AccentCyan
                val accentOrange = AccentOrange

                Canvas(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    val w = size.width
                    val h = size.height

                    // Draw head, spine, arms, legs joints simulating pose keypoints
                    val head = Offset(w * 0.5f, h * 0.22f)
                    val neck = Offset(w * 0.5f, h * 0.32f)
                    val leftShoulder = Offset(w * 0.35f, h * 0.35f)
                    val rightShoulder = Offset(w * 0.65f, h * 0.35f)
                    val leftElbow = Offset(w * 0.28f, h * 0.48f)
                    val rightElbow = Offset(w * 0.72f, h * 0.48f)
                    val leftWrist = Offset(w * 0.25f, h * 0.60f)
                    val rightWrist = Offset(w * 0.75f, h * 0.60f)
                    val hip = Offset(w * 0.5f, h * 0.58f)
                    val leftKnee = Offset(w * 0.42f, h * 0.75f)
                    val rightKnee = Offset(w * 0.58f, h * 0.75f)
                    val leftAnkle = Offset(w * 0.40f, h * 0.90f)
                    val rightAnkle = Offset(w * 0.60f, h * 0.90f)

                    val joints = listOf(
                        head, neck, leftShoulder, rightShoulder, leftElbow, rightElbow,
                        leftWrist, rightWrist, hip, leftKnee, rightKnee, leftAnkle, rightAnkle
                    )

                    // Draw bones connecting keypoints
                    val bones = listOf(
                        head to neck, neck to leftShoulder, neck to rightShoulder,
                        leftShoulder to leftElbow, leftElbow to leftWrist,
                        rightShoulder to rightElbow, rightElbow to rightWrist,
                        neck to hip, hip to leftKnee, leftKnee to leftAnkle,
                        hip to rightKnee, rightKnee to rightAnkle
                    )

                    bones.forEach { (start, end) ->
                        drawLine(
                            color = accentCyan,
                            start = start,
                            end = end,
                            strokeWidth = 6f,
                            cap = StrokeCap.Round
                        )
                    }

                    joints.forEach { joint ->
                        drawCircle(color = accentOrange, radius = 10f, center = joint)
                        drawCircle(color = Color.White, radius = 5f, center = joint)
                    }
                }

                // AI Status & Real-Time Joint Angle Badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("MediaPipe Pose Engine: ACTIVE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("Elbow Angle: 92° | Hip Alignment: OK", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                    }
                }

                // Accuracy Badge Top Right
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = AccentGreen.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentGreen)
                ) {
                    Text(
                        text = "$formAccuracyPercent% Form Match",
                        color = AccentGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                // Form Feedback Banner Bottom Center
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(formFeedbackMessage, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Live Rep Counter Metrics Row
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("rep_metrics_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetricCounterBox("REPS", "$currentReps", MaterialTheme.colorScheme.primary)
                Divider(modifier = Modifier.height(40.dp).width(1.dp))
                MetricCounterBox("SETS", "$currentSets", AccentPurple)
                Divider(modifier = Modifier.height(40.dp).width(1.dp))
                val mins = durationSeconds / 60
                val secs = durationSeconds % 60
                MetricCounterBox("TIME", String.format("%02d:%02d", mins, secs), AccentOrange)
                Divider(modifier = Modifier.height(40.dp).width(1.dp))
                val calBurned = (currentReps * 2.5 + durationSeconds * 0.15).toInt()
                MetricCounterBox("CALORIES", "$calBurned", AccentCyan)
            }
        }

        // Action Buttons Row (Simulate Rep & Finish Workout)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onTriggerRep,
                modifier = Modifier
                    .weight(1.2f)
                    .height(54.dp)
                    .testTag("simulate_rep_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
            ) {
                Icon(Icons.Default.AddTask, contentDescription = "Simulate Rep")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Detect Rep (+1)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Button(
                onClick = onFinishWorkout,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("finish_workout_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Flag, contentDescription = "Finish")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Finish", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MetricCounterBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
