package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.viewmodel.QuizViewModel

@Composable
fun SmartXAppUI(viewModel: QuizViewModel) {
    val selectedGrade by viewModel.selectedGrade.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val isQuizFinished by viewModel.isQuizFinished.collectAsState()
    
    var currentSubScreen by remember { mutableStateOf("home") } 
    var isDarkMode by remember { mutableStateOf(false) }
    var language by remember { mutableStateOf("EN") }

    BackHandler {
        when {
            selectedSubject != null -> viewModel.selectSubject(null)
            selectedGrade != null -> viewModel.selectGrade(null)
            currentSubScreen != "home" -> currentSubScreen = "home"
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            if (selectedGrade == null && selectedSubject == null && currentSubScreen == "home") {
                SmartXTopAppBar(
                    isDarkMode = isDarkMode,
                    onToggleDark = { isDarkMode = !isDarkMode },
                    language = language,
                    onToggleLanguage = { language = if (language == "EN") "አማ" else "EN" },
                    onMenuClick = { /* Open drawer */ }
                )
            } else {
                QuizTopAppBarRefactored(
                    title = when {
                        selectedSubject != null -> "Grade $selectedGrade - $selectedSubject"
                        selectedGrade != null -> "Grade $selectedGrade Subjects"
                        currentSubScreen == "courses" -> "All Courses"
                        currentSubScreen == "profile" -> "Profile"
                        currentSubScreen == "settings" -> "Settings"
                        else -> "Smart X Academy"
                    },
                    onBack = {
                        when {
                            selectedSubject != null -> viewModel.selectSubject(null)
                            selectedGrade != null -> viewModel.selectGrade(null)
                            currentSubScreen != "home" -> currentSubScreen = "home"
                        }
                    },
                    showBackButton = selectedGrade != null || currentSubScreen != "home"
                )
            }
        },
        bottomBar = {
            if (selectedGrade == null && selectedSubject == null) {
                Column {
                    AdMobBanner(modifier = Modifier.fillMaxWidth())
                    SmartXBottomNav(
                        currentScreen = currentSubScreen,
                        onScreenSelected = { currentSubScreen = it }
                    )
                }
            } else {
                AdMobBanner(modifier = Modifier.fillMaxWidth().navigationBarsPadding())
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkMode) Color(0xFF121212) else Color(0xFFF5F7FA))
                .padding(innerPadding)
        ) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when {
                    selectedSubject != null -> {
                        if (isQuizFinished) {
                            ScoreScreen(viewModel = viewModel)
                        } else {
                            PlayQuizScreen(viewModel = viewModel)
                        }
                    }
                    selectedGrade != null -> {
                        SubjectSelectionScreen(
                            viewModel = viewModel,
                            onSubjectClicked = { subject -> viewModel.selectSubject(subject) }
                        )
                    }
                    else -> {
                        when (currentSubScreen) {
                            "home" -> SmartXHomeScreen(viewModel = viewModel, isDarkMode = isDarkMode)
                            "courses" -> PlaceholderScreen("Courses Catalogue", Icons.Default.MenuBook)
                            "profile" -> BookmarksScreen(viewModel = viewModel, onBackToHome = { currentSubScreen = "home" }) // Reusing Bookmarks/history for Profile demo
                            "settings" -> PlaceholderScreen("Settings & Options", Icons.Default.Settings)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmartXTopAppBar(
    onMenuClick: () -> Unit,
    isDarkMode: Boolean,
    onToggleDark: () -> Unit,
    language: String,
    onToggleLanguage: () -> Unit
) {
    val bgColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val contentColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onMenuClick) {
            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = contentColor)
        }
        Text(
            text = "Smart X Academy",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = contentColor
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onToggleDark) {
                Icon(
                    if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode, 
                    contentDescription = "Toggle Theme",
                    tint = contentColor
                )
            }
            IconButton(onClick = onToggleLanguage) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Language, contentDescription = "Language", tint = contentColor, modifier = Modifier.size(20.dp))
                    Text(
                        text = if(language == "EN") "EN/አማ" else "አማ/EN", 
                        fontSize = 9.sp, 
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
            }
        }
    }
}

@Composable
fun QuizTopAppBarRefactored(
    title: String,
    onBack: () -> Unit,
    showBackButton: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBackButton) {
            IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate Back")
            }
        } else {
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
    }
}

@Composable
fun SmartXBottomNav(
    currentScreen: String,
    onScreenSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == "home",
            onClick = { onScreenSelected("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF1B5ECF), selectedTextColor = Color(0xFF1B5ECF))
        )
        NavigationBarItem(
            selected = currentScreen == "courses",
            onClick = { onScreenSelected("courses") },
            icon = { Icon(Icons.Default.MenuBook, contentDescription = "Courses") },
            label = { Text("Courses") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF1B5ECF), selectedTextColor = Color(0xFF1B5ECF))
        )
        NavigationBarItem(
            selected = currentScreen == "profile",
            onClick = { onScreenSelected("profile") },
            icon = { Icon(Icons.Default.PersonOutline, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF1B5ECF), selectedTextColor = Color(0xFF1B5ECF))
        )
        NavigationBarItem(
            selected = currentScreen == "settings",
            onClick = { onScreenSelected("settings") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF1B5ECF), selectedTextColor = Color(0xFF1B5ECF))
        )
    }
}

@Composable
fun PlaceholderScreen(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
    }
}

@Composable
fun SmartXHomeScreen(viewModel: QuizViewModel, isDarkMode: Boolean) {
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Feature Video Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE2E8F0))
                            .clickable {
                                try {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
                                    context.startActivity(intent)
                                } catch(e: Exception) {
                                    // Ignore exception if no browser or intent handler exists on the emulator
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Welcome to Smart X Academy!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        Icon(
                            Icons.Default.PlayCircleFilled,
                            contentDescription = "Play",
                            tint = Color.Red,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Watch tutorial: Getting started with the Smart X Academy App.",
                        fontSize = 14.sp,
                        color = if (isDarkMode) Color.LightGray else Color.DarkGray
                    )
                }
            }
        }
        
        // Explore Your Grade Section
        item {
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Text("Explore Your Grade", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Select your grade to view courses.", fontSize = 15.sp, color = Color.Gray)
            }
        }

        // Grades Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    GradeCard(
                        grade = 9, color = Color(0xFF3B82F6), icon = Icons.Default.MenuBook, 
                        subtitle = "Begin your journey!", modifier = Modifier.weight(1f), cardColor = cardColor, textColor = textColor
                    ) { viewModel.selectGrade(9) }
                    GradeCard(
                        grade = 10, color = Color(0xFF10B981), icon = Icons.Default.Science, 
                        subtitle = "Expand your knowledge!", modifier = Modifier.weight(1f), cardColor = cardColor, textColor = textColor
                    ) { viewModel.selectGrade(10) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                    GradeCard(
                        grade = 11, color = Color(0xFFF59E0B), icon = Icons.Default.Calculate, 
                        subtitle = "Prepare for excellence!", modifier = Modifier.weight(1f), cardColor = cardColor, textColor = textColor
                    ) { viewModel.selectGrade(11) }
                    GradeCard(
                        grade = 12, color = Color(0xFF8B5CF6), icon = Icons.Default.School, 
                        subtitle = "Achieve your goals!", modifier = Modifier.weight(1f), cardColor = cardColor, textColor = textColor
                    ) { viewModel.selectGrade(12) }
                }
            }
        }
    }
}

@Composable
fun GradeCard(
    grade: Int, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, subtitle: String, 
    modifier: Modifier = Modifier, cardColor: Color, textColor: Color, onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                 modifier = Modifier.size(56.dp).background(color, RoundedCornerShape(14.dp)),
                 contentAlignment = Alignment.Center
            ) {
                 Icon(icon, contentDescription=null, tint=Color.White, modifier=Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Grade $grade", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp)
        }
    }
}
