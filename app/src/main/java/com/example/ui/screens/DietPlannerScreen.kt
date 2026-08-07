package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfileEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietPlannerScreen(
    userProfile: UserProfileEntity,
    onAddWater: (Int) -> Unit
) {
    var selectedDietType by remember { mutableStateOf("High Protein") }

    val mealPlansMap = mapOf(
        "High Protein" to listOf(
            MealPlanItem("Breakfast", "Egg White & Spinach Omelet with Avocado", "420 kcal", "P: 35g | C: 12g | F: 22g"),
            MealPlanItem("Lunch", "Grilled Chicken Breast with Quinoa & Steamed Broccoli", "550 kcal", "P: 48g | C: 45g | F: 12g"),
            MealPlanItem("Snack", "Greek Yogurt with Whey Protein & Walnuts", "260 kcal", "P: 28g | C: 14g | F: 8g"),
            MealPlanItem("Dinner", "Baked Salmon Fillet with Asparagus & Sweet Potato", "520 kcal", "P: 42g | C: 38g | F: 18g")
        ),
        "Keto / Low Carb" to listOf(
            MealPlanItem("Breakfast", "Scrambled Eggs with Bacon & Cheddar Cheese", "480 kcal", "P: 30g | C: 3g | F: 38g"),
            MealPlanItem("Lunch", "Beef Ribeye Steak with Zucchini Noodles & Butter", "620 kcal", "P: 44g | C: 5g | F: 46g"),
            MealPlanItem("Dinner", "Pan-Seared Salmon with Olive Oil Pesto Greens", "540 kcal", "P: 38g | C: 4g | F: 42g")
        ),
        "Vegan / Plant Power" to listOf(
            MealPlanItem("Breakfast", "Chia Seed Protein Smoothie with Almond Butter & Berries", "380 kcal", "P: 22g | C: 44g | F: 14g"),
            MealPlanItem("Lunch", "Tofu & Chickpea Buddha Bowl with Tahini Dressing", "490 kcal", "P: 26g | C: 58g | F: 16g"),
            MealPlanItem("Dinner", "Lentil & Sweet Potato Curry with Brown Rice", "510 kcal", "P: 24g | C: 72g | F: 12g")
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Calorie & BMR/TDEE Calculator Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bmr_tdee_calculator_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Calculated BMR & TDEE Metabolism", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MetabolismStatBox("BMR (Basal)", "${userProfile.bmr} kcal", AccentPurple)
                        MetabolismStatBox("TDEE (Active)", "${userProfile.tdee} kcal", AccentCyan)
                        MetabolismStatBox("Target Goal", "${userProfile.dailyCalorieGoal} kcal", MaterialTheme.colorScheme.primary)
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Calculate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Mifflin-St Jeor formula applied for ${userProfile.weightKg}kg, ${userProfile.heightCm.toInt()}cm, ${userProfile.age}yo ${userProfile.gender}.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Diet Selector Chips
        item {
            Text("Select AI Meal Plan Type:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("High Protein", "Keto / Low Carb", "Vegan / Plant Power")) { diet ->
                    FilterChip(
                        selected = selectedDietType == diet,
                        onClick = { selectedDietType = diet },
                        label = { Text(diet) },
                        leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.testTag("diet_chip_${diet.take(6)}")
                    )
                }
            }
        }

        // Daily AI Meal Plan Cards
        val currentMealPlan = mealPlansMap[selectedDietType] ?: emptyList()
        items(currentMealPlan) { meal ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("meal_plan_card_${meal.mealName}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(meal.mealType, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(meal.mealName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(meal.macros, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Text(meal.calories, fontWeight = FontWeight.Bold, color = AccentCyan)
                }
            }
        }

        // Grocery List Auto-Generator
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("grocery_list_card"),
                shape = RoundedCornerShape(20.dp),
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
                        Text("Weekly AI Grocery List", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = AccentGreen)
                    }

                    listOf(
                        "Chicken Breast / Salmon Fillets (1.5 kg)",
                        "Eggs / Egg Whites (2 dozen)",
                        "Quinoa & Brown Rice (1 kg)",
                        "Avocados, Spinach, Broccoli & Sweet Potatoes",
                        "Greek Yogurt & Whey Protein Isolate"
                    ).forEach { grocery ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(grocery, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }
}

data class MealPlanItem(
    val mealType: String,
    val mealName: String,
    val calories: String,
    val macros: String
)

@Composable
fun MetabolismStatBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
