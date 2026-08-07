package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FoodLogEntity
import com.example.data.UserProfileEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userProfile: UserProfileEntity,
    foodLogs: List<FoodLogEntity>,
    onNavigateToScan: () -> Unit,
    onNavigateToRepCounter: () -> Unit,
    onNavigateToCoach: () -> Unit,
    onAddWater: (Int) -> Unit
) {
    val totalCaloriesConsumed = foodLogs.sumOf { it.calories }
    val totalProteinConsumed = foodLogs.sumOf { it.proteinG.toDouble() }.toFloat()
    val totalCarbsConsumed = foodLogs.sumOf { it.carbsG.toDouble() }.toFloat()
    val totalFatConsumed = foodLogs.sumOf { it.fatG.toDouble() }.toFloat()

    val calorieProgress = (totalCaloriesConsumed.toFloat() / userProfile.dailyCalorieGoal.coerceAtLeast(1)).coerceIn(0f, 1f)
    val stepProgress = (userProfile.currentSteps.toFloat() / userProfile.dailyStepsGoal.coerceAtLeast(1)).coerceIn(0f, 1f)
    val waterProgress = (userProfile.currentWaterMl.toFloat() / userProfile.dailyWaterGoalMl.coerceAtLeast(1)).coerceIn(0f, 1f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header & Streak Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("welcome_streak_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Welcome back, ${userProfile.name} 👋",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Goal: ${userProfile.fitnessGoal}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Streak Badge Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AccentOrange.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentOrange.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = AccentOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "${userProfile.streakCount} Day Streak",
                                fontWeight = FontWeight.Bold,
                                color = AccentOrange,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Action Hero Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateToScan,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("quick_action_scan"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan Food", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Food Scan", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToRepCounter,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("quick_action_rep_counter"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                ) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = "Rep Counter", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Rep Counter", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Calorie Arc & Macronutrients Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calorie_macro_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Energy & Macros",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$totalCaloriesConsumed / ${userProfile.dailyCalorieGoal} kcal",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Circular Progress Canvas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(130.dp)
                        ) {
                            val primaryColor = MaterialTheme.colorScheme.primary
                            val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawArc(
                                    color = surfaceVariant,
                                    startAngle = 135f,
                                    sweepAngle = 270f,
                                    useCenter = false,
                                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawArc(
                                    color = primaryColor,
                                    startAngle = 135f,
                                    sweepAngle = 270f * calorieProgress,
                                    useCenter = false,
                                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${(userProfile.dailyCalorieGoal - totalCaloriesConsumed).coerceAtLeast(0)}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "kcal left",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        // Macro Breakdown Progress Rows
                        Column(
                            modifier = Modifier.weight(1f).padding(start = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MacroBarItem("Protein", totalProteinConsumed, userProfile.dailyProteinGoalG.toFloat(), AccentCyan, "g")
                            MacroBarItem("Carbs", totalCarbsConsumed, userProfile.dailyCarbsGoalG.toFloat(), AccentOrange, "g")
                            MacroBarItem("Fats", totalFatConsumed, userProfile.dailyFatGoalG.toFloat(), AccentPurple, "g")
                        }
                    }
                }
            }
        }

        // Step Counter & Hydration Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Steps Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("steps_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DirectionsWalk, contentDescription = "Steps", tint = AccentGreen)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Step Goal", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }

                        Text(
                            text = "${userProfile.currentSteps}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        LinearProgressIndicator(
                            progress = { stepProgress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = AccentGreen,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Text(
                            text = "${(userProfile.currentSteps * 0.00075f).let { String.format("%.1f", it) }} km | ${userProfile.dailyStepsGoal} goal",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Hydration Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("water_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WaterDrop, contentDescription = "Water", tint = AccentCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Hydration", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }

                        Text(
                            text = "${userProfile.currentWaterMl} ml",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        LinearProgressIndicator(
                            progress = { waterProgress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = AccentCyan,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Add water:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Surface(
                                    onClick = { onAddWater(250) },
                                    shape = RoundedCornerShape(6.dp),
                                    color = AccentCyan.copy(alpha = 0.2f),
                                    modifier = Modifier.testTag("add_water_250")
                                ) {
                                    Text("+250", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentCyan, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                Surface(
                                    onClick = { onAddWater(500) },
                                    shape = RoundedCornerShape(6.dp),
                                    color = AccentCyan.copy(alpha = 0.2f),
                                    modifier = Modifier.testTag("add_water_500")
                                ) {
                                    Text("+500", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentCyan, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Weekly Calorie Trend Canvas Chart
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weekly_analytics_chart"),
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
                        Text(
                            text = "Weekly Activity & Intake",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Target Met: 85%",
                            fontSize = 12.sp,
                            color = AccentGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Custom Canvas Bar Chart
                    val primaryColor = MaterialTheme.colorScheme.primary
                    val secondaryColor = MaterialTheme.colorScheme.secondary
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val intakeRatios = listOf(0.85f, 0.92f, 0.78f, 0.95f, 0.88f, 1.0f, 0.70f)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val barWidth = 24.dp.toPx()
                            val spaceBetween = (size.width - (barWidth * days.size)) / (days.size + 1)

                            intakeRatios.forEachIndexed { index, ratio ->
                                val x = spaceBetween + index * (barWidth + spaceBetween)
                                val barHeight = size.height * ratio * 0.8f
                                val y = size.height - barHeight

                                drawRoundRect(
                                    color = if (index == 5) secondaryColor else primaryColor,
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        days.forEach { day ->
                            Text(text = day, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }
                }
            }
        }

        // Today's Scanned Foods Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Nutrition Journal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToScan, modifier = Modifier.testTag("view_scan_journal")) {
                    Text("Scan Food +")
                }
            }
        }

        if (foodLogs.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.Fastfood, contentDescription = "Empty Food", modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No food logged yet today", fontWeight = FontWeight.SemiBold)
                        Text("Tap 'AI Food Scan' or log manually to track macros.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                }
            }
        } else {
            items(foodLogs.take(3)) { food ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("food_journal_item_${food.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Restaurant, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(food.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${food.mealType} • ${food.portionSize}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("${food.calories} kcal", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("P: ${food.proteinG.toInt()}g | C: ${food.carbsG.toInt()}g", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MacroBarItem(label: String, current: Float, target: Float, color: Color, unit: String) {
    val progress = (current / target.coerceAtLeast(1f)).coerceIn(0f, 1f)
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "${current.toInt()}/${target.toInt()}$unit", fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
