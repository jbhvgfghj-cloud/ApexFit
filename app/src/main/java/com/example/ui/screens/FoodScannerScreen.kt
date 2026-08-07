package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FoodLogEntity
import com.example.network.FoodAnalysisResult
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScannerScreen(
    isScanning: Boolean,
    scanResult: FoodAnalysisResult?,
    foodLogs: List<FoodLogEntity>,
    onScanFood: (Bitmap?, String?) -> Unit,
    onAddManualFood: (String, String, Float, String, String, Int, Float, Float, Float) -> Unit,
    onDeleteFood: (Long) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = AI Camera Scan, 1 = Manual Entry, 2 = Food Database & Journal

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Selector Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.testTag("food_tab_row")
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("AI Scan", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.CameraAlt, contentDescription = "Camera Scan") },
                modifier = Modifier.testTag("tab_ai_scan")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Manual Entry", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.EditNote, contentDescription = "Manual Entry") },
                modifier = Modifier.testTag("tab_manual_entry")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Journal (${foodLogs.size})", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Journal") },
                modifier = Modifier.testTag("tab_journal")
            )
        }

        when (selectedTab) {
            0 -> AIScanTab(isScanning, scanResult, onScanFood)
            1 -> ManualEntryTab(onAddManualFood) { selectedTab = 2 }
            2 -> JournalDatabaseTab(foodLogs, onDeleteFood)
        }
    }
}

@Composable
fun AIScanTab(
    isScanning: Boolean,
    scanResult: FoodAnalysisResult?,
    onScanFood: (Bitmap?, String?) -> Unit
) {
    var foodQueryInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Camera Viewfinder Simulation Canvas Frame
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .testTag("camera_viewfinder_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Scanning reticle animation frame
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .border(
                                width = 3.dp,
                                color = if (isScanning) AccentCyan else MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isScanning) {
                            CircularProgressIndicator(color = AccentCyan)
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CenterFocusWeak,
                                    contentDescription = "Focus",
                                    tint = AccentCyan,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Center Food in Viewfinder",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Scanner status badge
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gemini AI Food Vision Active", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Quick Preset Meal Buttons
        item {
            Text("Or select quick food sample:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val presets = listOf("Grilled Salmon & Avocado", "Mediterranean Protein Salad", "Chicken & Sweet Potato Bowl", "Oatmeal with Blueberries")
                items(presets) { preset ->
                    FilterChip(
                        selected = foodQueryInput == preset,
                        onClick = {
                            foodQueryInput = preset
                            val dummyBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888).apply {
                                Canvas(this).drawColor(AndroidColor.DKGRAY)
                            }
                            onScanFood(dummyBitmap, preset)
                        },
                        label = { Text(preset) },
                        leadingIcon = { Icon(Icons.Default.RestaurantMenu, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.testTag("preset_chip_${preset.take(10)}")
                    )
                }
            }
        }

        // Custom Query Field & Scan Trigger Button
        item {
            OutlinedTextField(
                value = foodQueryInput,
                onValueChange = { foodQueryInput = it },
                label = { Text("Describe food item (optional)") },
                placeholder = { Text("e.g., 2 eggs, 1 toast with butter") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("food_query_input"),
                shape = RoundedCornerShape(14.dp),
                trailingIcon = {
                    if (foodQueryInput.isNotEmpty()) {
                        IconButton(onClick = { foodQueryInput = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )
        }

        item {
            Button(
                onClick = {
                    val dummyBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888).apply {
                        Canvas(this).drawColor(AndroidColor.DKGRAY)
                    }
                    onScanFood(dummyBitmap, foodQueryInput)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("trigger_ai_scan_button"),
                shape = RoundedCornerShape(16.dp),
                enabled = !isScanning,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "Scan")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isScanning) "AI Analyzing Nutritional Content..." else "Scan & Analyze Food Now",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        // AI Nutritional Analysis Card Display
        scanResult?.let { result ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("scan_result_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = result.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${result.category} • ${result.portionSize}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }

                            // Health Score Badge
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = AccentGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Score: ${result.healthScore}/100",
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGreen,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Divider()

                        // Macro Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            NutrientBadge("Calories", "${result.calories} kcal", MaterialTheme.colorScheme.primary)
                            NutrientBadge("Protein", "${result.proteinG.toInt()}g", AccentCyan)
                            NutrientBadge("Carbs", "${result.carbsG.toInt()}g", AccentOrange)
                            NutrientBadge("Fats", "${result.fatG.toInt()}g", AccentPurple)
                        }

                        Divider()

                        // Micros & Advice
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Micro-nutrients:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Vitamins: ${result.vitamins}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                            Text("Minerals: ${result.minerals} | Sodium: ${result.sodiumMg.toInt()}mg", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.TipsAndUpdates, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(result.recommendation, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEntryTab(
    onAddManualFood: (String, String, Float, String, String, Int, Float, Float, Float) -> Unit,
    onSuccess: () -> Unit
) {
    var foodName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Protein") }
    var weightText by remember { mutableStateOf("200") }
    var weightUnit by remember { mutableStateOf("grams") } // grams, kg, oz, lbs
    var mealType by remember { mutableStateOf("Lunch") }
    var caloriesText by remember { mutableStateOf("320") }
    var proteinText by remember { mutableStateOf("28") }
    var carbsText by remember { mutableStateOf("30") }
    var fatText by remember { mutableStateOf("8") }

    // Auto-calculate macros when weight changes
    val weightGrams = remember(weightText, weightUnit) {
        val raw = weightText.toFloatOrNull() ?: 100f
        when (weightUnit) {
            "kg" -> raw * 1000f
            "oz" -> raw * 28.35f
            "lbs" -> raw * 453.59f
            else -> raw
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Manual Food & Nutrition Logger", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        item {
            OutlinedTextField(
                value = foodName,
                onValueChange = { foodName = it },
                label = { Text("Food Item Name *") },
                placeholder = { Text("e.g. Ribeye Steak, White Rice, Protein Shake") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("manual_food_name_input"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Weight") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("manual_food_weight_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                var expandedUnit by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedUnit,
                    onExpandedChange = { expandedUnit = !expandedUnit },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = weightUnit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit) },
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedUnit,
                        onDismissRequest = { expandedUnit = false }
                    ) {
                        listOf("grams", "kg", "oz", "lbs").forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit) },
                                onClick = {
                                    weightUnit = unit
                                    expandedUnit = false
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            Text("Meal Type:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Breakfast", "Lunch", "Dinner", "Snack").forEach { type ->
                    FilterChip(
                        selected = mealType == type,
                        onClick = { mealType = type },
                        label = { Text(type) },
                        modifier = Modifier.testTag("meal_type_${type}")
                    )
                }
            }
        }

        item {
            Text("Nutrients (Auto-calculated or Manual):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = { caloriesText = it },
                    label = { Text("Calories (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("manual_calories_input"),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = proteinText,
                    onValueChange = { proteinText = it },
                    label = { Text("Protein (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("manual_protein_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = carbsText,
                    onValueChange = { carbsText = it },
                    label = { Text("Carbs (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("manual_carbs_input"),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = fatText,
                    onValueChange = { fatText = it },
                    label = { Text("Fat (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("manual_fat_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            Button(
                onClick = {
                    val cal = caloriesText.toIntOrNull() ?: 250
                    val prot = proteinText.toFloatOrNull() ?: 20f
                    val carbs = carbsText.toFloatOrNull() ?: 25f
                    val fat = fatText.toFloatOrNull() ?: 8f

                    onAddManualFood(
                        foodName,
                        category,
                        weightGrams,
                        "${weightGrams.toInt()}g",
                        mealType,
                        cal,
                        prot,
                        carbs,
                        fat
                    )
                    onSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_manual_food_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save to Food Journal", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun JournalDatabaseTab(
    foodLogs: List<FoodLogEntity>,
    onDeleteFood: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Saved Food Journal Entries", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Total: ${foodLogs.sumOf { it.calories }} kcal", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }

        if (foodLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No journal items logged.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
        } else {
            items(foodLogs) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("journal_item_${item.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("${item.mealType} • ${item.weightGrams.toInt()}g (${item.category})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                            IconButton(onClick = { onDeleteFood(item.id) }, modifier = Modifier.testTag("delete_food_${item.id}")) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Calories: ${item.calories} kcal", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                            Text("P: ${item.proteinG.toInt()}g | C: ${item.carbsG.toInt()}g | F: ${item.fatG.toInt()}g", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NutrientBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
