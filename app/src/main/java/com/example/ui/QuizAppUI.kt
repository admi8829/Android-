package com.example.ui

import androidx.compose.material.icons.filled.Close
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookmarkedQuestion
import com.example.data.Question
import com.example.viewmodel.QuizViewModel

@Composable
fun QuizAppUI(viewModel: QuizViewModel) {
    val selectedGrade by viewModel.selectedGrade.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val isQuizFinished by viewModel.isQuizFinished.collectAsState()
    
    var currentSubScreen by remember { mutableStateOf<String>("home") } // "home", "bookmarks", "history"

    // Intercept hardware Back Button
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
            QuizTopAppBar(
                title = when {
                    selectedSubject != null -> "Grade $selectedGrade - $selectedSubject"
                    selectedGrade != null -> "Grade $selectedGrade Subjects"
                    currentSubScreen == "bookmarks" -> "My Bookmarks"
                    currentSubScreen == "history" -> "Progress History"
                    else -> "EthioQuiz (Grades 9-12)"
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
        },
        bottomBar = {
            // Keep continuous AdMob Banner Ad anchored at the bottom
            AdMobBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC)) // High comfort slate background
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
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
                            onSubjectClicked = { subject ->
                                viewModel.selectSubject(subject)
                            }
                        )
                    }
                    else -> {
                        when (currentSubScreen) {
                            "bookmarks" -> BookmarksScreen(
                                viewModel = viewModel,
                                onBackToHome = { currentSubScreen = "home" }
                            )
                            "history" -> HistoryScreen(
                                viewModel = viewModel,
                                onBackToHome = { currentSubScreen = "home" }
                            )
                            else -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateToBookmarks = { currentSubScreen = "bookmarks" },
                                onNavigateToHistory = { currentSubScreen = "history" }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizTopAppBar(
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
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("appbar_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate Back",
                    tint = Color(0xFF0F172A)
                )
            }
        } else {
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.School,
            contentDescription = "EthioQuiz Academic Icon",
            tint = Color(0xFF1B5ECF),
            modifier = Modifier
                .padding(end = 12.dp)
                .size(28.dp)
        )
    }
}

@Composable
fun HomeScreen(
    viewModel: QuizViewModel,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val historyList by viewModel.history.collectAsState()
    val bookmarksList by viewModel.bookmarks.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_feed"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "እንኳን ደህና መጡ! 🇪🇹",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1B5ECF)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Welcome to EthioQuiz! Excel in your Grade 9 - 12 national exam preparations with interactive assessments.",
                        fontSize = 15.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Quick Stats row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bookmarks Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToBookmarks() },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Bookmarks count",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${bookmarksList.size}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Bookmarks",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // History / Stats Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToHistory() },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Attempts count",
                            tint = Color(0xFF1B5ECF),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${historyList.size}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Test Attempts",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        // Section Title: Grade Selections
        item {
            Text(
                text = "Select Your Grade",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Grade Selections Grid
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val dynamicGrades by viewModel.supabaseGrades.collectAsState()
                val grades = if (dynamicGrades.isNotEmpty()) dynamicGrades else listOf(9, 10, 11, 12)
                
                grades.chunked(3).forEach { rowGrades ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowGrades.forEach { gradeNum ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.selectGrade(gradeNum) }
                                    .testTag("grade_card_${gradeNum}"),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .background(Brush.linearGradient(
                                                colors = listOf(Color(0xFFEFF6FF), Color(0xFFDBEAFE))
                                            ), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Class,
                                            contentDescription = "Grade Icon",
                                            tint = Color(0xFF1B5ECF),
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Grade $gradeNum",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "National Quiz",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                        // Fill empty space if the row is incomplete
                        if (rowGrades.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

data class SubjectTheme(
    val primaryColor: Color,
    val lightBg: Color,
    val darkText: Color,
    val cardIcon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun SubjectSelectionScreen(
    viewModel: QuizViewModel,
    isDarkMode: Boolean = false,
    language: String = "EN",
    onSubjectClicked: (String) -> Unit
) {
    val selectedGrade by viewModel.selectedGrade.collectAsState()
    val localSubjects = remember(selectedGrade) { viewModel.getSubjectsForSelectedGrade() }
    
    val supabaseSubjects by viewModel.supabaseSubjects.collectAsState()
    val isSupabaseLoading by viewModel.isSupabaseLoading.collectAsState()
    val supabaseError by viewModel.supabaseError.collectAsState()
    val supabaseCacheHit by viewModel.supabaseCacheHit.collectAsState()

    var isBannerDismissed by remember(selectedGrade) { mutableStateOf(false) }

    // Dynamically merge/deduplicate or prioritize Supabase subjects
    val loadedSubjects = remember(supabaseSubjects, localSubjects) {
        if (supabaseSubjects.isNotEmpty()) {
            supabaseSubjects.map { it.name }.distinct().sorted()
        } else {
            localSubjects
        }
    }

    val bgGradientColors = if (isDarkMode) {
        listOf(Color(0xFF0F172A), Color(0xFF020617))
    } else {
        listOf(Color(0xFFF8FAFC), Color(0xFFEFF6FF))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = bgGradientColors
                )
            )
            .testTag("subjects_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dynamic Sync Status Bar (Spans full width)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                Text(
                    text = if (language == "AMH") "የሚማሩትን ርዕሰ-ጉዳይ ይምረጡ" else "Choose a Subject to Begin",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                    letterSpacing = (-0.7).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == "AMH") "ለክፍል ${selectedGrade} በጥንቃቄ የተዘጋጁ የትምህርት ኮርሶችና ፈተናዎች" else "Curriculum modules for Grade $selectedGrade academically curated",
                    fontSize = 14.sp,
                    color = if (isDarkMode) Color.LightGray else Color(0xFF64748B),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Live Supabase Sync block removed

        // Prominent Database Fetch Error Card if any
        supabaseError?.let { error ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = BorderStroke(1.5.dp, Color(0xFFFCA5A5))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error icon",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Supabase Fetch Error",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = error,
                            fontSize = 13.sp,
                            color = Color(0xFF7F1D1D),
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadSupabaseSubjects(selectedGrade ?: 9) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Try Again", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (loadedSubjects.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Text(
                        text = if (language == "AMH") "ለክፍል $selectedGrade የተዘጋጀ ርዕሰ-ጉዳይ በአሁኑ ሰዓት አልተገኘም።" else "No subjects available for Grade $selectedGrade yet.",
                        modifier = Modifier.padding(32.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                }
            }
        } else {
            itemsIndexed(loadedSubjects.chunked(3)) { rowIndex, rowSubjects ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowSubjects.forEachIndexed { itemIndex, subject ->
                        val index = rowIndex * 3 + itemIndex
                        
                        var isVisible by remember { mutableStateOf(false) }
                        LaunchedEffect(key1 = subject) {
                            kotlinx.coroutines.delay(index * 70L)
                            isVisible = true
                        }

                        val matchSupabaseSubject = supabaseSubjects.find { it.name.lowercase() == subject.lowercase() }
                        val unitCount = matchSupabaseSubject?.units?.size ?: 3

                        val theme = remember(subject) {
                            when (subject.trim().lowercase()) {
                                "biology" -> SubjectTheme(Color(0xFF10B981), Color(0xFFECFDF5), Color(0xFF065F46), Icons.Default.Spa)
                                "chemistry" -> SubjectTheme(Color(0xFFF59E0B), Color(0xFFFFFBEB), Color(0xFF92400E), Icons.Default.Science)
                                "mathematics", "maths", "math" -> SubjectTheme(Color(0xFF3B82F6), Color(0xFFEFF6FF), Color(0xFF1E40AF), Icons.Default.Calculate)
                                "physics" -> SubjectTheme(Color(0xFF8B5CF6), Color(0xFFF5F3FF), Color(0xFF5B21B6), Icons.Default.Bolt)
                                "english" -> SubjectTheme(Color(0xFFEC4899), Color(0xFFFDF2F8), Color(0xFF9D174D), Icons.Default.Translate)
                                "civics" -> SubjectTheme(Color(0xFF14B8A6), Color(0xFFF0FDFA), Color(0xFF0F766E), Icons.Default.Gavel)
                                "geography" -> SubjectTheme(Color(0xFF06B6D4), Color(0xFFECFEFF), Color(0xFF0891B2), Icons.Default.Public)
                                "history" -> SubjectTheme(Color(0xFFEF4444), Color(0xFFFEF2F2), Color(0xFF991B1B), Icons.Default.AutoStories)
                                else -> SubjectTheme(Color(0xFF6366F1), Color(0xFFEEF2FF), Color(0xFF3730A3), Icons.Default.Book)
                            }
                        }

                        val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val scale by animateFloatAsState(
                            targetValue = if (isPressed) 0.95f else 1.0f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                            label = "subject_card_tap"
                        )

                        val infiniteTransition = rememberInfiniteTransition(label = "subject_pulse_$index")
                        val pulseScale by infiniteTransition.animateFloat(
                            initialValue = 1.0f,
                            targetValue = 1.05f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulse_$index"
                        )

                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { 60 },
                                animationSpec = spring(
                                    dampingRatio = 0.7f,
                                    stiffness = Spring.StiffnessLow
                                )
                            ) + fadeIn(animationSpec = tween(400)) +
                            scaleIn(
                                initialScale = 0.9f,
                                animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow)
                            ),
                            exit = fadeOut(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .scale(scale)
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = androidx.compose.foundation.LocalIndication.current,
                                            onClick = { onSubjectClicked(subject) }
                                        )
                                        .testTag("subject_card_${subject.lowercase()}"),
                                    colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF1E293B) else Color.White),
                                    border = BorderStroke(1.2.dp, if (isDarkMode) theme.primaryColor.copy(alpha = 0.35f) else theme.primaryColor.copy(alpha = 0.18f)),
                                    shape = RoundedCornerShape(14.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = if (isPressed) 1.dp else 2.5.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .background(if (isDarkMode) theme.primaryColor.copy(alpha = 0.15f) else theme.lightBg, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(theme.cardIcon, contentDescription = null, tint = theme.primaryColor, modifier = Modifier.size(20.dp))
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))

                                            val localizedName = remember(subject, language) {
                                                if (language == "AMH") {
                                                    when (subject.trim().lowercase()) {
                                                        "biology" -> "ባዮሎጂ"
                                                        "chemistry" -> "ኬሚስትሪ"
                                                        "mathematics", "maths", "math" -> "ሒሳብ"
                                                        "physics" -> "ፊዚክስ"
                                                        "english" -> "እንግሊዝኛ"
                                                        "civics" -> "ስነ-ዜጋ"
                                                        "geography" -> "ጂኦግራፊ"
                                                        "history" -> "ታሪክ"
                                                        else -> subject
                                                    }
                                                } else {
                                                    subject
                                                }
                                            }
                                            
                                            Text(
                                                text = localizedName,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                                letterSpacing = (-0.3).sp,
                                                textAlign = TextAlign.Center,
                                                maxLines = 1
                                            )
                                            
                                            Spacer(modifier = Modifier.height(2.dp))
                                            
                                            Text(
                                                text = if (language == "AMH") "$unitCount ምዕራፎች" else "$unitCount Chapters",
                                                fontSize = 10.sp,
                                                color = if (isDarkMode) Color.LightGray else Color(0xFF64748B),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            
                                            Spacer(modifier = Modifier.height(10.dp))
                                        }

                                        // Full width bleed button
                                        Button(
                                            onClick = { onSubjectClicked(subject) },
                                            shape = RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp, topStart = 0.dp, topEnd = 0.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = theme.primaryColor),
                                            contentPadding = PaddingValues(vertical = 10.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .scale(pulseScale)
                                                .testTag("subject_get_start_${subject.lowercase()}")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(if (language == "AMH") "ጀምር" else "GET START", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }
                                    }
                                } // Closes Column
                                
                                if (index == 0) {
                                    Box(modifier = Modifier.matchParentSize(), contentAlignment = Alignment.Center) {
                                        PulsingGestureGuide()
                                    }
                                }
                            } // Closes Box
                        } // Closes AnimatedVisibility
                    } // Closes forEachIndexed
                    
                    if (rowSubjects.size < 3) {
                        repeat(3 - rowSubjects.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
         }
     }
 }

@Composable
fun UnitSelectionScreen(
    viewModel: QuizViewModel,
    isDarkMode: Boolean = false,
    language: String = "EN",
    onUnitClicked: (String) -> Unit
) {
    val selectedGrade by viewModel.selectedGrade.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val supabaseSubjects by viewModel.supabaseSubjects.collectAsState()

    var showUnlockProgressDialog by remember { mutableStateOf(false) }
    var showRegistrationDialog by remember { mutableStateOf(false) }
    var pendingUnitToOpen by remember { mutableStateOf<String?>(null) }
    var pendingAction by remember { mutableStateOf<String?>(null) } // "open" or "download"

    // Determine beautiful subject brand theme matching the selected subject
    val theme = remember(selectedSubject) {
        when (selectedSubject?.trim()?.lowercase()) {
            "biology" -> SubjectTheme(Color(0xFF10B981), Color(0xFFECFDF5), Color(0xFF065F46), Icons.Default.Spa)
            "chemistry" -> SubjectTheme(Color(0xFFF59E0B), Color(0xFFFFFBEB), Color(0xFF92400E), Icons.Default.Science)
            "mathematics", "maths", "math" -> SubjectTheme(Color(0xFF3B82F6), Color(0xFFEFF6FF), Color(0xFF1E40AF), Icons.Default.Calculate)
            "physics" -> SubjectTheme(Color(0xFF8B5CF6), Color(0xFFF5F3FF), Color(0xFF5B21B6), Icons.Default.Bolt)
            "english" -> SubjectTheme(Color(0xFFEC4899), Color(0xFFFDF2F8), Color(0xFF9D174D), Icons.Default.Translate)
            "civics" -> SubjectTheme(Color(0xFF14B8A6), Color(0xFFF0FDFA), Color(0xFF0F766E), Icons.Default.Gavel)
            "geography" -> SubjectTheme(Color(0xFF06B6D4), Color(0xFFECFEFF), Color(0xFF0891B2), Icons.Default.Public)
            "history" -> SubjectTheme(Color(0xFFEF4444), Color(0xFFFEF2F2), Color(0xFF991B1B), Icons.Default.AutoStories)
            else -> SubjectTheme(Color(0xFF6366F1), Color(0xFFEEF2FF), Color(0xFF3730A3), Icons.Default.Book)
        }
    }

    // Dialog markup for locked modules
    if (showUnlockProgressDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showUnlockProgressDialog = false },
            icon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = theme.primaryColor,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text = if (language == "AMH") "ምዕራፍ 2 የተቆለፈ ነው" else "Unit 2+ Locked!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = if (language == "AMH") {
                        "እባክዎ መጀመሪያ ለሁሉም የትምህርት አይነቶች ምዕራፍ 1 ን ያውርዱ ወይም ያጥኑ። ይህ የተሟላ እውቀት እንዲያገኙ ይረዳዎታል።"
                    } else {
                        "To unlock Unit 2 and above, you must first download or practice Unit 1 for ALL academic subjects in your selected grade.\n\nThis ensures a complete foundational understanding before progressing!"
                    },
                    fontSize = 14.sp,
                    color = if (isDarkMode) Color.LightGray else Color(0xFF475569),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = { showUnlockProgressDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primaryColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (language == "AMH") "እሺ" else "Got It", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = if (isDarkMode) Color(0xFF1E293B) else Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showRegistrationDialog) {
        var name by remember { mutableStateOf("") }
        var school by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var validationError by remember { mutableStateOf<String?>(null) }
        var isSubmitting by remember { mutableStateOf(false) }

        androidx.compose.ui.window.Dialog(onDismissRequest = { showRegistrationDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(28.dp),
                color = if (isDarkMode) Color(0xFF1E293B) else Color.White,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.HowToReg,
                        contentDescription = null,
                        tint = theme.primaryColor,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (language == "AMH") "አባልነት ይመዝገቡ" else "Student Registration",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == "AMH") "ቀጣዩን ምዕራፍ ለመክፈት አጭር መረጃ ይሙሉ" else "Complete this 1-time setup to unlock Unit 2+",
                        fontSize = 13.sp,
                        color = if (isDarkMode) Color.LightGray else Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    if (validationError != null) {
                        Surface(
                            color = Color(0xFFFEE2E2),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = validationError!!,
                                color = Color(0xFF991B1B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Name input
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; validationError = null },
                        label = { Text(if (language == "AMH") "ሙሉ ስም" else "Full Name") },
                        placeholder = { Text("e.g. Almaz Kebede") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // School name input
                    OutlinedTextField(
                        value = school,
                        onValueChange = { school = it; validationError = null },
                        label = { Text(if (language == "AMH") "የትምህርት ቤት ስም" else "School Name") },
                        placeholder = { Text("e.g. Bole Secondary School") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Phone input
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it; validationError = null },
                        label = { Text(if (language == "AMH") "ስልክ ቁጥር" else "Phone Number") },
                        placeholder = { Text("e.g. +251 912 345678") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Email input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; validationError = null },
                        label = { Text(if (language == "AMH") "ኢሜል አድራሻ" else "Email Address") },
                        placeholder = { Text("e.g. student@school.com") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    if (isSubmitting) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = theme.primaryColor,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showRegistrationDialog = false },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (language == "AMH") "ተመለስ" else "Cancel")
                            }

                            Button(
                                onClick = {
                                    if (name.trim().isEmpty() || school.trim().isEmpty() || phone.trim().isEmpty() || email.trim().isEmpty()) {
                                        validationError = if (language == "AMH") "እባክዎ ሁሉንም ክፍት ቦታዎች ይሙሉ" else "Please fill out all fields."
                                    } else if (!email.contains("@") || !email.contains(".")) {
                                        validationError = if (language == "AMH") "ትክክለኛ ኢሜል ያስገቡ" else "Please enter a valid email address."
                                    } else {
                                        isSubmitting = true
                                        viewModel.registerUser(name.trim(), school.trim(), phone.trim(), email.trim()) { success ->
                                            isSubmitting = false
                                            if (success) {
                                                showRegistrationDialog = false
                                                pendingUnitToOpen?.let { unitVal ->
                                                    if (pendingAction == "download") {
                                                        if (selectedGrade != null && selectedSubject != null) {
                                                            viewModel.downloadUnitQuestions(selectedGrade!!, selectedSubject!!, unitVal) {}
                                                        }
                                                    } else {
                                                        onUnitClicked(unitVal)
                                                    }
                                                    pendingUnitToOpen = null
                                                    pendingAction = null
                                                }
                                            } else {
                                                validationError = "Registration submission error. Please check your network and try again."
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = theme.primaryColor),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.5f)
                            ) {
                                Text(if (language == "AMH") "መዝግብ" else "Register & Play", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Find custom units list from Supabase or use fallback themed units if empty
    val currentSubjectData = supabaseSubjects.find { it.name.lowercase() == selectedSubject?.lowercase() }
    val unitsList = currentSubjectData?.units ?: listOf(
        "Unit 1: Foundation of $selectedSubject",
        "Unit 2: Essential Concepts & Applications",
        "Unit 3: Advanced Methods & Exercise",
        "Unit 4: Comprehensive Exam Prep Study"
    )

    LaunchedEffect(selectedGrade, selectedSubject, unitsList) {
        if (selectedGrade != null && selectedSubject != null) {
            viewModel.checkDownloadedStatus(selectedGrade!!, selectedSubject!!, unitsList)
        }
    }

    // Trigger sequential loading animations
    val listState = rememberLazyListState()
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = selectedSubject) {
        visible = true
    }

    val bgGradientColors = if (isDarkMode) {
        listOf(Color(0xFF0F172A), Color(0xFF020617))
    } else {
        listOf(Color(0xFFF8FAFC), Color(0xFFEFF6FF))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = bgGradientColors
                )
            )
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Large Premium Subject Header Card with fine-grained glassmorphism & gentle entering fade + slide
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(initialOffsetY = { -40 }, animationSpec = tween(600))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF1E293B) else Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(theme.darkText, theme.primaryColor)
                            )
                        )
                ) {
                    // Glow background decoration
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 30.dp, y = (-30).dp)
                            .background(Color.White.copy(alpha = 0.08f), CircleShape)
                    )
                    
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = theme.cardIcon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = selectedSubject ?: "Subject Modules",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Grade $selectedGrade Curriculum • ${unitsList.size} Chapters",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Subtitle "Select a Module to Start"
        Text(
            text = "Select a Unit to Learn & Practice",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isDarkMode) Color.White else Color(0xFF0F172A),
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        // Lazy ListView with beautiful offset slide-up animations for each card (3 Units chunked)
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            val chunkedUnits = unitsList.chunked(3)
            itemsIndexed(chunkedUnits) { rowIndex, rowUnits ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowUnits.forEachIndexed { colIndex, unitTitle ->
                        val itemIndex = rowIndex * 3 + colIndex + 1
                        
                        var itemPreloadState by remember { mutableStateOf(false) }
                        LaunchedEffect(key1 = unitTitle) {
                            kotlinx.coroutines.delay(rowIndex * 80L + colIndex * 60L) // cascading stagger animation effect per item!
                            itemPreloadState = true
                        }

                        AnimatedVisibility(
                            visible = itemPreloadState,
                            enter = fadeIn(animationSpec = tween(durationMillis = 400)) + 
                                    slideInVertically(
                                        initialOffsetY = { 80 }, 
                                        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow)
                                    ) +
                                    scaleIn(
                                        initialScale = 0.9f,
                                        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow)
                                    ),
                            modifier = Modifier.weight(1f)
                        ) {
                            val downloadedStatus by viewModel.downloadedUnits.collectAsState()
                            val isUnitDownloaded = downloadedStatus[unitTitle] ?: false
                            
                            val isFirstUnit = unitTitle.trim().lowercase().contains("unit 1")
                            val isLocked = !isFirstUnit && !viewModel.userProfile.collectAsState().value.isRegistered

                            UnitCardItem(
                                index = itemIndex,
                                unitTitle = unitTitle,
                                theme = theme,
                                isUnitDownloaded = isUnitDownloaded,
                                isDarkMode = isDarkMode,
                                isLocked = isLocked,
                                onDownload = { onComplete ->
                                    val profile = viewModel.userProfile.value
                                    if (!profile.isRegistered) {
                                        pendingAction = "download"
                                        pendingUnitToOpen = unitTitle
                                        showRegistrationDialog = true
                                    } else {
                                        if (selectedGrade != null && selectedSubject != null) {
                                            viewModel.downloadUnitQuestions(selectedGrade!!, selectedSubject!!, unitTitle, onComplete)
                                        } else {
                                            onComplete()
                                        }
                                    }
                                },
                                onClick = {
                                    val profile = viewModel.userProfile.value
                                    if (!profile.isRegistered && !isFirstUnit) {
                                        pendingAction = "open"
                                        pendingUnitToOpen = unitTitle
                                        showRegistrationDialog = true
                                    } else {
                                        onUnitClicked(unitTitle)
                                    }
                                }
                            )
                        }
                    }
                    
                    if (rowUnits.size < 3) {
                        repeat(3 - rowUnits.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnitCardItem(
    index: Int,
    unitTitle: String,
    theme: SubjectTheme,
    isUnitDownloaded: Boolean,
    isDarkMode: Boolean = false,
    isLocked: Boolean = false,
    onDownload: (onComplete: () -> Unit) -> Unit,
    onClick: () -> Unit
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isHovered by interactionSource.collectIsPressedAsState()
    var isDownloading by remember { mutableStateOf(false) }
    var isShowingAd by remember { mutableStateOf(false) }

    if (isShowingAd) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { isShowingAd = false }) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDarkMode) Color(0xFF1E293B) else Color.White, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                AdMobVideoPreRollAd(
                    onAdCompleted = {
                        isShowingAd = false
                        isDownloading = true
                        onDownload {
                            isDownloading = false
                        }
                    }
                )
            }
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isHovered) 0.94f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessLow
        ),
        label = "unit_item_pressed"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = { 
                    if (isLocked) {
                        onClick()
                    } else if (isUnitDownloaded) {
                        onClick()
                    } else if (!isDownloading) {
                        isShowingAd = true
                    }
                }
            )
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF1E293B) else Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isUnitDownloaded) Color(0xFF10B981) else if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnitDownloaded) 3.dp else 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        Brush.linearGradient(
                            colors = if (isLocked) {
                                listOf(Color(0xFF64748B), Color(0xFF475569))
                            } else {
                                listOf(theme.primaryColor, theme.primaryColor.copy(alpha = 0.8f))
                            }
                        ),
                        RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked Module",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                } else {
                    Text(
                        text = "$index",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            val cleanedTitle = if (unitTitle.contains("Unit ", ignoreCase = true) && unitTitle.contains(":")) {
                unitTitle.substringAfter(":").trim()
            } else {
                unitTitle
            }
            
            val chapterPrefix = if (unitTitle.contains("Unit ", ignoreCase = true) && unitTitle.contains(":")) {
                unitTitle.substringBefore(":").trim()
            } else {
                "Unit $index"
            }

            Text(
                text = chapterPrefix.uppercase(),
                color = theme.primaryColor,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = cleanedTitle,
                color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.heightIn(min = 28.dp)
            )

            if (isDownloading) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    color = theme.primaryColor,
                    strokeWidth = 2.dp
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isLocked) {
                                Color(0xFFFFEEEE)
                            } else if (isUnitDownloaded) {
                                Color(0xFFE8FDF5)
                            } else {
                                theme.primaryColor.copy(alpha = 0.08f)
                            }
                        )
                        .border(
                            1.dp,
                            if (isLocked) Color(0xFFFCA5A5) else if (isUnitDownloaded) Color(0xFFA7F3D0) else theme.primaryColor.copy(alpha = 0.2f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isLocked) {
                                Icons.Default.Lock
                            } else if (isUnitDownloaded) {
                                Icons.Default.CheckCircle
                            } else {
                                Icons.Default.CloudDownload
                            },
                            contentDescription = null,
                            tint = if (isLocked) Color(0xFFEF4444) else if (isUnitDownloaded) Color(0xFF10B981) else theme.primaryColor,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isLocked) "Locked" else if (isUnitDownloaded) "Ready" else "Download",
                            color = if (isLocked) Color(0xFFEF4444) else if (isUnitDownloaded) Color(0xFF10B981) else theme.primaryColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlayQuizScreen(viewModel: QuizViewModel, isDarkMode: Boolean = false) {
    val activeQuestions by viewModel.activeQuestions.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedAnswerIndex by viewModel.selectedAnswerIndex.collectAsState()
    val isAnswered by viewModel.isAnswered.collectAsState()
    val isCurrentQuestionBookmarked by viewModel.isCurrentQuestionBookmarked.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()

    val currentQuestion = activeQuestions.getOrNull(currentQuestionIndex)

    if (currentQuestion == null) {
        val isSupabaseLoading by viewModel.isSupabaseLoading.collectAsState()
        val supabaseError by viewModel.supabaseError.collectAsState()

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                if (isSupabaseLoading) {
                    CircularProgressIndicator(color = Color(0xFF3B82F6))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Downloading fresh quiz questions from Supabase...",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkMode) Color.White else Color(0xFF475569),
                        textAlign = TextAlign.Center
                    )
                } else if (supabaseError != null) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = supabaseError ?: "Supabase Fetch Error",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF991B1B),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.startActiveQuiz() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Try Again", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { viewModel.resetToHome() },
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Back to Home", color = if (isDarkMode) Color.White else Color(0xFF475569), fontWeight = FontWeight.Bold)
                    }
                } else {
                    CircularProgressIndicator(color = Color(0xFF3B82F6))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Preparing quiz questions... Please wait.", fontWeight = FontWeight.Medium, color = if (isDarkMode) Color.White else Color.Black)
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("quiz_screen_player")
            .background(Brush.verticalGradient(
                colors = if (isDarkMode) {
                    listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                } else {
                    listOf(Color(0xFFE0E7FF), Color(0xFFF8FAFC))
                }
            )),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tracker Indicator Row
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question ${currentQuestionIndex + 1} of ${activeQuestions.size}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkMode) Color.LightGray else Color(0xFF64748B)
                    )
                    
                    // Timer Display
                    val animatedTimerProgress by animateFloatAsState(
                        targetValue = timerSeconds / 30f,
                        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
                        label = "timerAnimation"
                    )
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp)) {
                        CircularProgressIndicator(
                            progress = { animatedTimerProgress },
                            modifier = Modifier.fillMaxSize(),
                            color = if (timerSeconds < 10) Color(0xFFEF4444) else Color(0xFF1B5ECF),
                            trackColor = if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
                        )
                        Text(
                            text = "$timerSeconds",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (timerSeconds < 10) Color(0xFFEF4444) else (if (isDarkMode) Color.White else Color(0xFF0F172A))
                        )
                    }
                    
                    IconButton(
                        onClick = { viewModel.toggleBookmark() },
                        modifier = Modifier.testTag("bookmark_active_button")
                    ) {
                        Icon(
                            imageVector = if (isCurrentQuestionBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Question Bookmark",
                            tint = if (isCurrentQuestionBookmarked) Color(0xFFF59E0B) else Color(0xFF94A3B8)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                val progress = (currentQuestionIndex.toFloat() / activeQuestions.size)
                val animatedProgress by animateFloatAsState(targetValue = progress, label = "quizProgress")
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF1B5ECF),
                    trackColor = if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
                )
            }
        }

        // Question Statement Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF1E293B) else Color.White),
                border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = currentQuestion.questionText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                    modifier = Modifier.padding(24.dp),
                    lineHeight = 26.sp
                )
            }
        }

        // Choice Options Cards
        items(currentQuestion.options.size) { index ->
            val optionText = currentQuestion.options[index]
            val isSelected = selectedAnswerIndex == index
            
            // Adaptive State-based styling
            val targetCardColor = when {
                isAnswered && index == currentQuestion.correctAnswerIndex -> if (isDarkMode) Color(0xFF064E3B) else Color(0xFFECFDF5)
                isAnswered && isSelected && selectedAnswerIndex != currentQuestion.correctAnswerIndex -> if (isDarkMode) Color(0xFF7F1D1D) else Color(0xFFFEF2F2)
                isSelected -> if (isDarkMode) Color(0xFF1E3A8A) else Color(0xFFEFF6FF)
                else -> if (isDarkMode) Color(0xFF1E293B) else Color.White
            }
            
            val animatedCardColor by animateColorAsState(
                targetValue = targetCardColor, 
                animationSpec = tween(300),
                label = "cardColorAnimation"
            )

            val targetBorderColor = when {
                isAnswered && index == currentQuestion.correctAnswerIndex -> Color(0xFF10B981)
                isAnswered && isSelected && selectedAnswerIndex != currentQuestion.correctAnswerIndex -> Color(0xFFEF4444)
                isSelected -> Color(0xFF3B82F6)
                else -> if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
            }
            
            val animatedBorderColor by animateColorAsState(
                targetValue = targetBorderColor, 
                animationSpec = tween(300),
                label = "borderColorAnimation"
            )

            val iconColor = when {
                isAnswered && index == currentQuestion.correctAnswerIndex -> Color(0xFF10B981)
                isAnswered && isSelected && selectedAnswerIndex != currentQuestion.correctAnswerIndex -> Color(0xFFEF4444)
                isSelected -> Color(0xFF3B82F6)
                else -> Color(0xFF94A3B8)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = if (isSelected) 1.02f else 1.0f
                        scaleY = if (isSelected) 1.02f else 1.0f
                    }
                    .clickable(enabled = !isAnswered) { viewModel.selectAnswer(index) }
                    .testTag("quiz_option_$index"),
                colors = CardDefaults.cardColors(containerColor = animatedCardColor),
                border = BorderStroke(if (isSelected) 2.dp else 1.5.dp, animatedBorderColor),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = if (isSelected) {
                                    if (isDarkMode) Color(0xFF172554) else Color(0xFFEFF6FF)
                                } else {
                                    if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF1F5F9)
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (index) {
                                0 -> "A"
                                1 -> "B"
                                2 -> "C"
                                else -> "D"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = iconColor
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = optionText,
                        fontSize = 16.sp,
                        color = if (isDarkMode) Color.White else Color(0xFF1E293B),
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Submit or Next Option Navigation Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (currentQuestionIndex > 0) {
                     OutlinedButton(
                        onClick = { viewModel.prevQuestion() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
                        )
                    ) {
                        Text("Back")
                    }
                }
                
                if (isAnswered) {
                     Button(
                        onClick = { viewModel.nextQuestion() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("nav_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (currentQuestionIndex == activeQuestions.size - 1) "Finish" else "Next", color = Color.White)
                    }
                } else {
                    Button(
                        onClick = { viewModel.submitAnswer() },
                        enabled = selectedAnswerIndex != null,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("submit_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1B5ECF),
                            disabledContainerColor = if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0),
                            disabledContentColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Submit", color = if (selectedAnswerIndex != null) Color.White else Color.Gray)
                    }
                }
            }
        }

        // Adaptive Solution Explanation Box
        item {
            AnimatedVisibility(
                visible = isAnswered,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut()
            ) {
                val isCorrect = selectedAnswerIndex == currentQuestion.correctAnswerIndex
                val localBg = if (isDarkMode) {
                    if (isCorrect) Color(0xFF064E3B).copy(alpha = 0.4f) else Color(0xFF7F1D1D).copy(alpha = 0.4f)
                } else {
                    if (isCorrect) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)
                }

                val localBorder = if (isDarkMode) {
                    if (isCorrect) Color(0xFF059669) else Color(0xFFDC2626)
                } else {
                    if (isCorrect) Color(0xFFBBF7D0) else Color(0xFFFCA5A5)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = localBg),
                    border = BorderStroke(1.dp, localBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Info,
                                contentDescription = "Evaluation indicator",
                                tint = if (isCorrect) Color(0xFF10B981) else Color(0xFFF59E0B),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isCorrect) "Well Done! Correct" else "Incorrect Choice",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) {
                                    if (isDarkMode) Color(0xFF34D399) else Color(0xFF14532D)
                                } else {
                                    if (isDarkMode) Color(0xFFFCA5A5) else Color(0xFF78350F)
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Core Solution Breakdown:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color.LightGray else Color(0xFF14532D)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentQuestion.explanation,
                            fontSize = 14.sp,
                            color = if (isDarkMode) Color.White else Color(0xFF1E293B),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreScreen(viewModel: QuizViewModel, isDarkMode: Boolean = false) {
    val score by viewModel.score.collectAsState()
    val activeQuestions by viewModel.activeQuestions.collectAsState()
    val totalQuestions = activeQuestions.size
    val selectedGrade by viewModel.selectedGrade.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()

    val percent = if (totalQuestions > 0) ((score.toFloat() / totalQuestions) * 100).toInt() else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkMode) Color(0xFF0F172A) else Color.White)
            .padding(24.dp)
            .testTag("score_screen_layout"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Achievement Circular Medal
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = if (isDarkMode) {
                        if (percent >= 70) Color(0xFF064E3B) else Color(0xFF7F1D1D)
                    } else {
                        if (percent >= 70) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (percent >= 70) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = "Success Score Medal",
                tint = if (percent >= 70) Color(0xFF10B981) else Color(0xFFEF4444),
                modifier = Modifier.size(60.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (percent >= 70) "Fantastic Achievement!" else "Keep Practicing!",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isDarkMode) Color.White else Color(0xFF0F172A)
        )
        
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Grade $selectedGrade Practice Quiz for $selectedSubject",
            fontSize = 14.sp,
            color = if (isDarkMode) Color.LightGray else Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Performance Stat Card
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF1E293B) else Color.White),
            border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$percent%",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    color = if (percent >= 70) Color(0xFF10B981) else Color(0xFF1D4ED8)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Score: $score correct out of $totalQuestions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkMode) Color.LightGray else Color(0xFF475569)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Reset and Action controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { viewModel.selectSubject(selectedSubject) }, // Reload/restart subject quiz
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("retry_quiz_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5ECF)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry Icon", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry Quiz", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Button(
                onClick = { viewModel.resetToHome() }, // Back to homescreen
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("home_quiz_button"),
                colors = ButtonDefaults.buttonColors(containerColor = if (isDarkMode) Color(0xFF475569) else Color(0xFF64748B)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Finish", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun BookmarksScreen(
    viewModel: QuizViewModel,
    isDarkMode: Boolean = false,
    onBackToHome: () -> Unit
) {
    val bookmarksList by viewModel.bookmarks.collectAsState()

    if (bookmarksList.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .testTag("bookmarks_screen_empty"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFECFDF5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = "No offline content",
                    tint = if (isDarkMode) Color(0xFF38BDF8) else Color(0xFF10B981),
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "No Saved Questions Yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDarkMode) Color.White else Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Save difficult questions during live interactive quizzes, and they will automatically appear here for offline review and quick revisions.",
                fontSize = 14.sp,
                color = if (isDarkMode) Color.LightGray else Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = onBackToHome,
                colors = ButtonDefaults.buttonColors(containerColor = if (isDarkMode) Color(0xFF3B82F6) else Color(0xFF10B981)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Browse Curriculum", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("bookmarks_view_feed"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Saved Offline Questions",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(bookmarksList) { bookmarked ->
            val question = bookmarked.toQuestion()
            var isExpanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF1E293B) else Color.White),
                border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(if (isDarkMode) Color(0xFF0F172A) else Color(0xFFEFF6FF), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Grade ${question.grade} • ${question.subject}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color(0xFF38BDF8) else Color(0xFF1B5ECF)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.removeBookmarkDirectly(question.id) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove Bookmark",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = question.questionText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isExpanded) "Hide Solution" else "Show Solution",
                        fontSize = 13.sp,
                        color = if (isDarkMode) Color(0xFF38BDF8) else Color(0xFF3B82F6),
                        fontWeight = FontWeight.Bold
                    )

                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth()
                        ) {
                            HorizontalDivider(color = if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Display the choices
                            question.options.forEachIndexed { optIndex, choice ->
                                val isCorrectOption = optIndex == question.correctAnswerIndex
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(
                                                color = if (isCorrectOption) {
                                                    if (isDarkMode) Color(0xFF064E3B) else Color(0xFFD1FAE5)
                                                } else {
                                                    if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF1F5F9)
                                                },
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = when (optIndex) {
                                                0 -> "A"
                                                1 -> "B"
                                                2 -> "C"
                                                else -> "D"
                                            },
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCorrectOption) Color(0xFF10B981) else if (isDarkMode) Color.LightGray else Color(0xFF64748B)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = choice,
                                        fontSize = 14.sp,
                                        color = if (isCorrectOption) Color(0xFF10B981) else if (isDarkMode) Color.White else Color(0xFF475569),
                                        fontWeight = if (isCorrectOption) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Explanation block
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isDarkMode) Color(0xFF064E3B).copy(alpha = 0.3f) else Color(0xFFF0FDF4), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Solution Breakdown:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkMode) Color(0xFF34D399) else Color(0xFF14532D)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = question.explanation,
                                        fontSize = 13.sp,
                                        color = if (isDarkMode) Color.LightGray else Color(0xFF1E3A1E),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OfflineHubScreen(
    viewModel: QuizViewModel,
    isDarkMode: Boolean,
    onBackToHome: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Downloaded, 1 = Bookmarks
    val context = androidx.compose.ui.platform.LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Tab Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val tabs = listOf("Downloaded Units", "Saved Questions")
            tabs.forEachIndexed { index, tabTitle ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) {
                                if (isDarkMode) Color(0xFF2563EB) else Color.White
                            } else {
                                Color.Transparent
                            }
                        )
                        .clickable { selectedTab = index }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tabTitle,
                        color = if (isSelected) {
                            if (isDarkMode) Color.White else Color(0xFF1E293B)
                        } else {
                            if (isDarkMode) Color.LightGray else Color(0xFF64748B)
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            // Downloaded Chapters Tab
            val downloadedUnitsList by viewModel.downloadedUnitsList.collectAsState()

            if (downloadedUnitsList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEFF6FF),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = "No Downloads",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Downloaded Chapters",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Curriculum units you download will appear here. You can practice them anywhere, anytime, completely offline, with no active internet connection!",
                        fontSize = 13.sp,
                        color = if (isDarkMode) Color.LightGray else Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBackToHome,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Explore Courses", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(downloadedUnitsList) { item ->
                        val theme = when (item.subject.lowercase().trim()) {
                            "biology" -> SubjectTheme(Color(0xFF10B981), Color(0xFFECFDF5), Color(0xFF065F46), Icons.Default.Spa)
                            "chemistry" -> SubjectTheme(Color(0xFFF59E0B), Color(0xFFFFFBEB), Color(0xFF92400E), Icons.Default.Science)
                            "mathematics", "maths", "math" -> SubjectTheme(Color(0xFF3B82F6), Color(0xFFEFF6FF), Color(0xFF1E40AF), Icons.Default.Calculate)
                            "physics" -> SubjectTheme(Color(0xFF8B5CF6), Color(0xFFF5F3FF), Color(0xFF5B21B6), Icons.Default.Bolt)
                            "english" -> SubjectTheme(Color(0xFFEC4899), Color(0xFFFDF2F8), Color(0xFF9D174D), Icons.Default.Translate)
                            "civics" -> SubjectTheme(Color(0xFF14B8A6), Color(0xFFF0FDFA), Color(0xFF0F766E), Icons.Default.Gavel)
                            "geography" -> SubjectTheme(Color(0xFF06B6D4), Color(0xFFECFEFF), Color(0xFF0891B2), Icons.Default.Public)
                            "history" -> SubjectTheme(Color(0xFFEF4444), Color(0xFFFEF2F2), Color(0xFF991B1B), Icons.Default.AutoStories)
                            else -> SubjectTheme(Color(0xFF6366F1), Color(0xFFEEF2FF), Color(0xFF3730A3), Icons.Default.Book)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectGrade(item.grade)
                                    viewModel.selectSubject(item.subject)
                                    viewModel.selectUnit(item.unit)
                                    viewModel.startActiveQuiz()
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDarkMode) Color(0xFF1E293B) else Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(theme.primaryColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = theme.cardIcon,
                                        contentDescription = null,
                                        tint = theme.primaryColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Grade ${item.grade} • ${item.subject}".uppercase(),
                                        color = theme.primaryColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.unit,
                                        color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(theme.primaryColor.copy(alpha = 0.12f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Start Offline Lesson",
                                        tint = theme.primaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Bookmarks Tab
            BookmarksScreen(
                viewModel = viewModel,
                isDarkMode = isDarkMode,
                onBackToHome = onBackToHome
            )
        }
    }
}

@Composable
fun HistoryScreen(
    viewModel: QuizViewModel,
    onBackToHome: () -> Unit
) {
    val historyList by viewModel.history.collectAsState()

    if (historyList.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .testTag("history_screen_empty"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Color(0xFFEFF6FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "No history recorded",
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Progress Record Yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Take practice quizzes on Grades 9 to 12 subjects to secure your history tracking here.",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onBackToHome,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5ECF)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Start Practice Now", fontWeight = FontWeight.Bold)
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("history_view_feed"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Performance Record",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Button(
                    onClick = { viewModel.clearAllHistory() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Clear All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(historyList) { historyItem ->
            val scorePercent = historyItem.percentage.toInt()
            val scoreColor = if (scorePercent >= 70) Color(0xFF10B981) else Color(0xFFF59E0B)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Grade ${historyItem.grade} - ${historyItem.subject}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Score: ${historyItem.score}/${historyItem.totalQuestions} questions correct",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(scoreColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$scorePercent%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = scoreColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PulsingGestureGuide(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_guide")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .scale(scale)
                .alpha(alpha)
                .background(Color(0xFF3B82F6), androidx.compose.foundation.shape.CircleShape)
        )
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.TouchApp,
            contentDescription = "Tap here",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ProfileScreen(
    viewModel: QuizViewModel,
    isDarkMode: Boolean,
    language: String = "EN"
) {
    val profile by viewModel.userProfile.collectAsState()
    val downloadedUnits by viewModel.downloadedUnitsList.collectAsState()
    val history by viewModel.history.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()

    val totalDownloaded = downloadedUnits.size
    val totalPracticed = history.size
    val totalBookmarks = bookmarks.size

    // Determine high contrast text and accent color schemes
    val textColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val cardBg = if (isDarkMode) Color(0xFF1E293B) else Color.White
    val borderColor = if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)

    val presetAvatars = remember {
        listOf(
            Triple("avatar_1", Icons.Default.School, Color(0xFF3B82F6)), // Scholar
            Triple("avatar_2", Icons.Default.MenuBook, Color(0xFF8B5CF6)), // Reader
            Triple("avatar_3", Icons.Default.Star, Color(0xFFEAB308)), // Star Student
            Triple("avatar_4", Icons.Default.Face, Color(0xFFEC4899)), // Enthusiast
            Triple("avatar_5", Icons.Default.Lightbulb, Color(0xFF14B8A6)), // Thinker
            Triple("avatar_6", Icons.Default.EmojiEvents, Color(0xFFF97316)), // Achiever
            Triple("avatar_7", Icons.Default.Science, Color(0xFF10B981)), // Scientist
            Triple("avatar_8", Icons.Default.AccountCircle, Color(0xFFEF4444)), // Leader
        )
    }

    var customAvatarUrlInput by remember { mutableStateOf("") }
    var isAvatarRowExpanded by remember { mutableStateOf(false) }

    fun getAvatarInfo(avatarId: String): Pair<androidx.compose.ui.graphics.vector.ImageVector, Color> {
        return presetAvatars.find { it.first == avatarId }?.let { it.second to it.third }
            ?: (Icons.Default.Person to Color(0xFF64748B))
    }

    val (avatarIcon, avatarTint) = getAvatarInfo(profile.avatarId)

    // Editing indicator
    var showAuthDialog by remember { mutableStateOf(false) }
    var authIsRegisterMode by remember { mutableStateOf(true) } // true: register, false: login
    var editIsUpdateMode by remember { mutableStateOf(false) } // true: update existing details

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Large Premium User Avatar Widget with Custom Selection Row
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBg, RoundedCornerShape(24.dp))
                .border(1.dp, borderColor, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(avatarTint.copy(alpha = 0.12f), CircleShape)
                    .border(2.dp, avatarTint, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = avatarIcon,
                    contentDescription = "User Avatar",
                    tint = avatarTint,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (language == "AMH") "የመገለጫ ምስል ይምረጡ" else "Personalize Your Profile Pic",
                fontSize = 13.sp,
                color = if (isDarkMode) Color.LightGray else Color(0xFF64748B),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Grid choice list of elegant icons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                presetAvatars.take(4).forEach { (id, icon, color) ->
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .weight(1f)
                            .clip(CircleShape)
                            .background(if (profile.avatarId == id) color.copy(alpha = 0.25f) else Color.Transparent)
                            .border(1.5.dp, if (profile.avatarId == id) color else borderColor.copy(alpha = 0.4f), CircleShape)
                            .clickable {
                                viewModel.updateUserAvatar(id)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                presetAvatars.drop(4).take(4).forEach { (id, icon, color) ->
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .weight(1f)
                            .clip(CircleShape)
                            .background(if (profile.avatarId == id) color.copy(alpha = 0.25f) else Color.Transparent)
                            .border(1.5.dp, if (profile.avatarId == id) color else borderColor.copy(alpha = 0.4f), CircleShape)
                            .clickable {
                                viewModel.updateUserAvatar(id)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Text Link / Option to paste any online picture link
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (language == "AMH") "ርዕስ በዩአርኤል ምስል ይጫኑ 🔗" else "Upload / Use Custom Photo Link 🔗",
                    fontSize = 11.sp,
                    color = Color(0xFF3B82F6),
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .clickable { isAvatarRowExpanded = !isAvatarRowExpanded }
                        .padding(vertical = 4.dp)
                )

                if (isAvatarRowExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customAvatarUrlInput,
                            onValueChange = { customAvatarUrlInput = it },
                            placeholder = { Text(if (language == "AMH") "የምስል ሊንክ እዚህ ይለጥፉ" else "Paste online profile photo link...") },
                            modifier = Modifier.weight(1f),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Button(
                            onClick = {
                                if (customAvatarUrlInput.trim().isNotEmpty()) {
                                    viewModel.updateUserAvatar(customAvatarUrlInput.trim())
                                    customAvatarUrlInput = ""
                                    isAvatarRowExpanded = false
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(if (language == "AMH") "አስቀምጥ" else "Save Link", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Student Metadata Block
        if (profile.isRegistered) {
            Text(
                text = profile.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${profile.school} • Grade ${profile.grade}",
                fontSize = 14.sp,
                color = if (isDarkMode) Color.LightGray else Color(0xFF64748B),
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(
                text = if (language == "AMH") "ያልተመዘገበ ተጠቃሚ" else "Guest Student",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = textColor
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = if (isDarkMode) Color(0xFFFEF2F2).copy(alpha = 0.1f) else Color(0xFFFEF2F2),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (language == "AMH") "ምዝገባ አልተጠናቀቀም (ምዕራፍ 2 የተቆለፈ ነው)" else "Registration Required to Unlock Unit 2+",
                    color = Color(0xFFEF4444),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Multi-metric statistics panel
        Text(
            text = if (language == "AMH") "የእርስዎ ስታቲስቲክስ" else "Academic Performance Overview",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = textColor,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Stat 1: Practice Sessions
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$totalPracticed", fontSize = 18.sp, fontWeight = FontWeight.Black, color = textColor)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(if (language == "AMH") "ልምምድ" else "Practiced", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }

            // Stat 2: Downloaded Modules
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$totalDownloaded", fontSize = 18.sp, fontWeight = FontWeight.Black, color = textColor)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(if (language == "AMH") "የወረዱ" else "Chapters", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }

            // Stat 3: Bookmarks
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$totalBookmarks", fontSize = 18.sp, fontWeight = FontWeight.Black, color = textColor)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(if (language == "AMH") "የተቀመጡ" else "Saved", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Registration details card or prompt banner
        if (profile.isRegistered) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "AMH") "የግል መረጃ" else "Registration Account Details",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = textColor
                )
                Text(
                    text = if (language == "AMH") "አድስ ⚙️" else "Edit Info ⚙️",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3B82F6),
                    modifier = Modifier
                        .clickable {
                            editIsUpdateMode = true
                            authIsRegisterMode = true
                            showAuthDialog = true
                        }
                        .padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (language == "AMH") "ስልክ ቁጥር" else "Phone", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = profile.phone, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = borderColor.copy(alpha = 0.5f))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (language == "AMH") "ኢሜል" else "Email", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = profile.email, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = borderColor.copy(alpha = 0.5f))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (language == "AMH") "ጾታ" else "Sex", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = profile.sex, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = borderColor.copy(alpha = 0.5f))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (language == "AMH") "የገመገሙት የይለፍ ቃል" else "Access Password", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = if (profile.password.isNotEmpty()) "••••••••" else "(Not Set)", color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = borderColor.copy(alpha = 0.5f))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (language == "AMH") "በጣም አስቸጋሪው ርዕሰ ጉዳይ" else "Difficult Subject", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = profile.difficultSubject, color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }
                    HorizontalDivider(color = borderColor.copy(alpha = 0.5f))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (language == "AMH") "በጣም ቀላሉ ርዕሰ ጉዳይ" else "Easiest Subject", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = profile.easySubject, color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }
                    HorizontalDivider(color = borderColor.copy(alpha = 0.5f))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (language == "AMH") "የምዝገባ ሁኔታ" else "Status", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (language == "AMH") "የተመዘገበ" else "Active (Verified)", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.AssignmentLate,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (language == "AMH") "በቀላሉ ይመዝገቡና ሁሉንም ምዕራፎች ይክፈቱ!" else "Unlock Complete Core Curriculum!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (language == "AMH") {
                            "ስምዎን፣ ትምህርት ቤት ቤትና ሌሎች መሰረታዊ መረጃዎችን በመሙላት ምዕራፍ 2 ን ጨምሮ ሁሉንም የትምህርት ምዕራፎች በነጻ ይክፈቱ።"
                        } else {
                            "Input your name, school, and academic indicators to unlock Unit 2, Unit 3, Unit 4, and Mock Exams for all subjects."
                        },
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            editIsUpdateMode = false
                            authIsRegisterMode = true
                            showAuthDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (language == "AMH") "አሁኑኑ ይመዝገቡ / ይግቡ" else "Register or Login Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Unified Dialog containing Login and Register functionality side-by-side!
        if (showAuthDialog) {
            var name by remember { mutableStateOf(if (editIsUpdateMode) profile.name else "") }
            var school by remember { mutableStateOf(if (editIsUpdateMode) profile.school else "") }
            var phone by remember { mutableStateOf(if (editIsUpdateMode) profile.phone else "") }
            var email by remember { mutableStateOf(if (editIsUpdateMode) profile.email else "") }
            var password by remember { mutableStateOf(if (editIsUpdateMode) profile.password else "") }
            var sex by remember { mutableStateOf(if (editIsUpdateMode) profile.sex else "Male") }
            var grade by remember { mutableStateOf(if (editIsUpdateMode) profile.grade else 9) }
            var difficultSubject by remember { mutableStateOf(if (editIsUpdateMode) profile.difficultSubject else "Physics") }
            var easySubject by remember { mutableStateOf(if (editIsUpdateMode) profile.easySubject else "English") }

            var passwordVisible by remember { mutableStateOf(false) }
            var errorMsg by remember { mutableStateOf<String?>(null) }
            var isSubmitting by remember { mutableStateOf(false) }

            val standardSubjects = listOf("Biology", "Chemistry", "Mathematics", "Physics", "English", "Civics", "Geography", "History")

            androidx.compose.ui.window.Dialog(onDismissRequest = { showAuthDialog = false }) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = if (isDarkMode) Color(0xFF1E293B) else Color.White,
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (authIsRegisterMode) Icons.Default.HowToReg else Icons.Default.Login,
                            contentDescription = null,
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Sliding mode selector (Show only if NOT updating existing account information)
                        if (!editIsUpdateMode) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Button(
                                    onClick = { authIsRegisterMode = true; errorMsg = null },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (authIsRegisterMode) Color(0xFF3B82F6) else Color.Transparent,
                                        contentColor = if (authIsRegisterMode) Color.White else (if (isDarkMode) Color.LightGray else Color.DarkGray)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1.5f),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Text(if (language == "AMH") "አዲስ ፍጠር" else "Register", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { authIsRegisterMode = false; errorMsg = null },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (!authIsRegisterMode) Color(0xFF3B82F6) else Color.Transparent,
                                        contentColor = if (!authIsRegisterMode) Color.White else (if (isDarkMode) Color.LightGray else Color.DarkGray)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1.3f),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Text(if (language == "AMH") "ግባ (Login)" else "Have account ? Login", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        } else {
                            Text(
                                text = if (language == "AMH") "የምዝገባ መረጃ ማሻሻያ" else "Modify Profile Information",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (errorMsg != null) {
                            Text(errorMsg!!, color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                        }

                        if (authIsRegisterMode) {
                            // REGISTER INPUT FIELDS
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text(if (language == "AMH") "ሙሉ ስም" else "Full Name") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = school,
                                onValueChange = { school = it },
                                label = { Text(if (language == "AMH") "የትምህርት ቤት ስም" else "School Name") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text(if (language == "AMH") "ስልክ ቁጥር" else "Phone Number") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text(if (language == "AMH") "ኢሜል አድራሻ" else "Email Address") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Password
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text(if (language == "AMH") "የይለፍ ቃል" else "Access Password (Min 4 chars)") },
                                visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                trailingIcon = {
                                    val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(icon, contentDescription = "Toggle password view")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Sex choice chips container
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                                Text(text = if (language == "AMH") "ጾታ" else "Choose Gender (Sex):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("Male", "Female", "Other").forEach { optionsSex ->
                                        val isSelected = sex == optionsSex
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(if (isSelected) Color(0xFF3B82F6) else (if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF1F5F9)), RoundedCornerShape(8.dp))
                                                .border(1.dp, if (isSelected) Color(0xFF3B82F6) else borderColor, RoundedCornerShape(8.dp))
                                                .clickable { sex = optionsSex }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = optionsSex,
                                                color = if (isSelected) Color.White else textColor,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            // Grade choice chips container
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                                Text(text = if (language == "AMH") "የትምህርት ደረጃ (ክፍል)" else "Select Your Grade Level:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(9, 10, 11, 12).forEach { optionGrade ->
                                        val isSelected = grade == optionGrade
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(if (isSelected) Color(0xFF10B981) else (if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF1F5F9)), RoundedCornerShape(8.dp))
                                                .border(1.dp, if (isSelected) Color(0xFF10B981) else borderColor, RoundedCornerShape(8.dp))
                                                .clickable { grade = optionGrade }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Grade $optionGrade",
                                                color = if (isSelected) Color.White else textColor,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            // Difficult Subject Dropdown emulation
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                                Text(text = "What is your MOST Difficult Subject?", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(standardSubjects) { subj ->
                                        val isSelected = difficultSubject == subj
                                        Box(
                                            modifier = Modifier
                                                .background(if (isSelected) Color(0xFFEF4444) else (if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF1F5F9)), RoundedCornerShape(16.dp))
                                                .border(1.dp, if (isSelected) Color(0xFFEF4444) else borderColor, RoundedCornerShape(16.dp))
                                                .clickable { difficultSubject = subj }
                                                .padding(horizontal = 12.dp, vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = subj,
                                                color = if (isSelected) Color.White else textColor,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            // Easy Subject Dropdown emulation
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                                Text(text = "What is your EASIEST Subject?", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(standardSubjects) { subj ->
                                        val isSelected = easySubject == subj
                                        Box(
                                            modifier = Modifier
                                                .background(if (isSelected) Color(0xFF10B981) else (if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF1F5F9)), RoundedCornerShape(16.dp))
                                                .border(1.dp, if (isSelected) Color(0xFF10B981) else borderColor, RoundedCornerShape(16.dp))
                                                .clickable { easySubject = subj }
                                                .padding(horizontal = 12.dp, vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = subj,
                                                color = if (isSelected) Color.White else textColor,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // LOGIN INPUT FIELDS
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text(if (language == "AMH") "የኢሜል አድራሻ" else "Registered Email") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text(if (language == "AMH") "የይለፍ ቃል" else "Your Account Password") },
                                visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                trailingIcon = {
                                    val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(icon, contentDescription = "Toggle password")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (isSubmitting) {
                            androidx.compose.material3.CircularProgressIndicator(color = Color(0xFF3B82F6), modifier = Modifier.size(28.dp))
                        } else {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedButton(onClick = { showAuthDialog = false }, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                                    Text(if (language == "AMH") "ተመለስ" else "Cancel")
                                }
                                Button(
                                    onClick = {
                                        if (authIsRegisterMode) {
                                            if (name.trim().isEmpty() || school.trim().isEmpty() || phone.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
                                                errorMsg = "Please fill out all fields"
                                            } else if (password.trim().length < 4) {
                                                errorMsg = "Password must be at least 4 characters"
                                            } else {
                                                isSubmitting = true
                                                viewModel.registerUser(
                                                    name = name.trim(),
                                                    school = school.trim(),
                                                    phone = phone.trim(),
                                                    email = email.trim(),
                                                    sex = sex,
                                                    password = password.trim(),
                                                    grade = grade,
                                                    difficultSubject = difficultSubject,
                                                    easySubject = easySubject
                                                ) { success ->
                                                    isSubmitting = false
                                                    if (success) {
                                                        showAuthDialog = false
                                                    } else {
                                                        errorMsg = "Error storing registration credentials"
                                                    }
                                                }
                                            }
                                        } else {
                                            // Handle login
                                            if (email.trim().isEmpty() || password.trim().isEmpty()) {
                                                errorMsg = "Please type your email and password"
                                            } else {
                                                isSubmitting = true
                                                viewModel.loginUser(email.trim(), password.trim()) { success ->
                                                    isSubmitting = false
                                                    if (success) {
                                                        showAuthDialog = false
                                                    } else {
                                                        errorMsg = "Invalid registered credentials! Try again or create a new student account."
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1.5f)
                                ) {
                                    Text(
                                        text = if (editIsUpdateMode) {
                                            (if (language == "AMH") "አሻሽል" else "Update Info")
                                        } else if (authIsRegisterMode) {
                                            (if (language == "AMH") "መዝግብ" else "Register")
                                        } else {
                                            (if (language == "AMH") "ግባ" else "Login")
                                        },
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
