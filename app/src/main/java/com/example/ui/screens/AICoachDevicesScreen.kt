package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.ChatMessageEntity
import com.example.data.UserProfileEntity
import com.example.data.WearableDeviceEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AICoachDevicesScreen(
    userProfile: UserProfileEntity,
    chatMessages: List<ChatMessageEntity>,
    wearables: List<WearableDeviceEntity>,
    isThinking: Boolean,
    onSendMessage: (String) -> Unit,
    onToggleDarkMode: () -> Unit,
    onUpdateProfile: (String, Int, Float, Float, Float, String) -> Unit
) {
    var selectedSection by remember { mutableIntStateOf(0) } // 0 = 24/7 AI Coach, 1 = Wearables & Health, 2 = Profile & Achievements

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = selectedSection,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.testTag("coach_tab_row")
        ) {
            Tab(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                text = { Text("24/7 AI Coach", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Psychology, contentDescription = "Coach") },
                modifier = Modifier.testTag("tab_ai_coach")
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                text = { Text("Devices & Sync", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Watch, contentDescription = "Devices") },
                modifier = Modifier.testTag("tab_devices")
            )
            Tab(
                selected = selectedSection == 2,
                onClick = { selectedSection = 2 },
                text = { Text("Profile", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                modifier = Modifier.testTag("tab_profile")
            )
        }

        when (selectedSection) {
            0 -> AICoachChatSection(chatMessages, isThinking, onSendMessage)
            1 -> WearablesSection(wearables)
            2 -> ProfileSection(userProfile, onToggleDarkMode, onUpdateProfile)
        }
    }
}

@Composable
fun AICoachChatSection(
    messages: List<ChatMessageEntity>,
    isThinking: Boolean,
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Suggested Quick Question Prompts
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            val suggestions = listOf(
                "How much protein do I need?",
                "Give me a 30-min fat burn routine",
                "How to improve my bench press form?",
                "What should I eat before morning cardio?"
            )
            items(suggestions) { q ->
                SuggestionChip(
                    onClick = { onSendMessage(q) },
                    label = { Text(q, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.testTag("suggestion_${q.take(8)}")
                )
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "USER"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        ),
                        color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .testTag("chat_bubble_${msg.id}")
                    ) {
                        Text(
                            text = msg.text,
                            modifier = Modifier.padding(12.dp),
                            color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            if (isThinking) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Text("FitPulse Coach is analyzing response...", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // Input Box Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask your 24/7 AI Coach...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("coach_input_field"),
                shape = RoundedCornerShape(20.dp)
            )

            FloatingActionButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier.testTag("send_coach_message_button"),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun WearablesSection(wearables: List<WearableDeviceEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Connected Wearables & Health Sensors", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(wearables) { dev ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wearable_card_${dev.id}"),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (dev.deviceType.contains("Ring")) Icons.Default.CircleNotifications else Icons.Default.Watch,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(dev.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("${dev.deviceType} • Battery ${dev.batteryPercent}%", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AccentGreen.copy(alpha = 0.2f)
                        ) {
                            Text("Synced LIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WearableMetricItem("Heart Rate", "${dev.heartRateBpm} bpm", Icons.Default.Favorite, AccentPurple)
                        WearableMetricItem("SpO2", "${dev.spO2Percent}%", Icons.Default.Air, AccentCyan)
                        WearableMetricItem("Sleep", "${dev.sleepHours} hrs", Icons.Default.Bedtime, MaterialTheme.colorScheme.primary)
                        WearableMetricItem("Recovery", "${dev.recoveryScore}%", Icons.Default.Shield, AccentGreen)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSection(
    userProfile: UserProfileEntity,
    onToggleDarkMode: () -> Unit,
    onUpdateProfile: (String, Int, Float, Float, Float, String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var nameText by remember { mutableStateOf(userProfile.name) }
    var ageText by remember { mutableStateOf(userProfile.age.toString()) }
    var weightText by remember { mutableStateOf(userProfile.weightKg.toString()) }
    var heightText by remember { mutableStateOf(userProfile.heightCm.toString()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Avatar & Stats Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Text(userProfile.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Fitness Score: ${userProfile.fitnessScore}/100", fontWeight = FontWeight.Bold, color = AccentGreen)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ProfileMetricBox("BMI", String.format("%.1f", userProfile.bmi))
                        ProfileMetricBox("Weight", "${userProfile.weightKg} kg")
                        ProfileMetricBox("Height", "${userProfile.heightCm.toInt()} cm")
                        ProfileMetricBox("Streak", "${userProfile.streakCount} days")
                    }
                }
            }
        }

        // Settings Controls (Dark Theme & Edit Profile)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dark Mode Theme", fontWeight = FontWeight.Bold)
                        Switch(
                            checked = userProfile.isDarkMode,
                            onCheckedChange = { onToggleDarkMode() },
                            modifier = Modifier.testTag("dark_mode_switch")
                        )
                    }

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Edit Assessment Details", fontWeight = FontWeight.Bold)
                        IconButton(onClick = { isEditing = !isEditing }, modifier = Modifier.testTag("edit_profile_toggle")) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }

                    if (isEditing) {
                        OutlinedTextField(
                            value = nameText,
                            onValueChange = { nameText = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth().testTag("edit_name_input")
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = weightText,
                                onValueChange = { weightText = it },
                                label = { Text("Weight (kg)") },
                                modifier = Modifier.weight(1f).testTag("edit_weight_input")
                            )
                            OutlinedTextField(
                                value = heightText,
                                onValueChange = { heightText = it },
                                label = { Text("Height (cm)") },
                                modifier = Modifier.weight(1f).testTag("edit_height_input")
                            )
                        }
                        Button(
                            onClick = {
                                val w = weightText.toFloatOrNull() ?: userProfile.weightKg
                                val h = heightText.toFloatOrNull() ?: userProfile.heightCm
                                val a = ageText.toIntOrNull() ?: userProfile.age
                                onUpdateProfile(nameText, a, h, w, userProfile.targetWeightKg, userProfile.fitnessGoal)
                                isEditing = false
                            },
                            modifier = Modifier.fillMaxWidth().testTag("save_profile_button")
                        ) {
                            Text("Save Profile")
                        }
                    }
                }
            }
        }

        // Achievements & Badges
        item {
            Text("Achievements & Badges", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BadgeCard("10k Steps", Icons.Default.DirectionsWalk, AccentGreen)
                BadgeCard("AI Food Master", Icons.Default.QrCodeScanner, AccentCyan)
                BadgeCard("Rep Legend", Icons.Default.FitnessCenter, AccentPurple)
            }
        }
    }
}

@Composable
fun BadgeCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun WearableMetricItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
fun ProfileMetricBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}
