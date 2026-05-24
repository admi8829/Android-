package com.example.ui

import androidx.compose.material.icons.filled.Close
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.scale
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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.filled.School
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
                
                grades.chunked(2).forEach { rowGrades ->
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF8FAFC), Color(0xFFEFF6FF))
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
                    color = Color(0xFF0F172A),
                    letterSpacing = (-0.7).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == "AMH") "ለክፍል ${selectedGrade} በጥንቃቄ የተዘጋጁ የትምህርት ኮርሶችና ፈተናዎች" else "Curriculum modules for Grade $selectedGrade academically curated",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Live Supabase Sync Status Indicator
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSupabaseLoading) Color(0xFFEFF6FF) else Color(0xFFECFDF5)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSupabaseLoading) Color(0xFFBFDBFE) else Color(0xFFA7F3D0)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSupabaseLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFF1D4ED8)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Querying live Supabase schema (cqrgqkczemoxgcpdlpin)...",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E40AF)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF059669), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (supabaseSubjects.isNotEmpty()) {
                                "Direct Live Connection Verified • cqrgqkczemoxgcpdlpin"
                            } else {
                                "Live Supabase Sync Complete • Grade $selectedGrade Loaded"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF065F46)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "REFRESH",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF047857),
                            modifier = Modifier
                                .clickable {
                                    viewModel.loadSupabaseSubjects(selectedGrade ?: 9)
                                }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

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
            itemsIndexed(loadedSubjects) { index, subject ->
                // Luxurious staggered cascade entrance animation
                var isVisible by remember { mutableStateOf(false) }
                LaunchedEffect(key1 = subject) {
                    kotlinx.coroutines.delay(index * 70L) // Beautiful rapid cascade!
                    isVisible = true
                }

                // Look for any matching unit list from Supabase
                val matchSupabaseSubject = supabaseSubjects.find { it.name.lowercase() == subject.lowercase() }
                val unitCount = matchSupabaseSubject?.units?.size ?: 3

                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { 45 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) + fadeIn(animationSpec = tween(400)),
                    exit = fadeOut()
                ) {
                    // Determine beautiful gamified topic brand matching colors and premium icons
                    val theme = remember(subject) {
                        when (subject.trim().lowercase()) {
                            "biology" -> SubjectTheme(Color(0xFF10B981), Color(0xFFECFDF5), Color(0xFF065F46), Icons.Default.Book)
                            "chemistry" -> SubjectTheme(Color(0xFFF59E0B), Color(0xFFFFFBEB), Color(0xFF92400E), Icons.Default.Class)
                            "mathematics", "maths", "math" -> SubjectTheme(Color(0xFF3B82F6), Color(0xFFEFF6FF), Color(0xFF1E40AF), Icons.Default.School)
                            "physics" -> SubjectTheme(Color(0xFF8B5CF6), Color(0xFFF5F3FF), Color(0xFF5B21B6), Icons.Default.Refresh)
                            else -> SubjectTheme(Color(0xFF6366F1), Color(0xFFEEF2FF), Color(0xFF3730A3), Icons.Default.Book)
                        }
                    }

                    // Tactile interactive scale feedback
                    var isPressed by remember { mutableStateOf(false) }
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1.0f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                        label = "subject_card_tap"
                    )

                    // Infinite pulse indicator for study call-to-action details
                    val infiniteTransition = rememberInfiniteTransition(label = "subject_pulse")
                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 1.0f,
                        targetValue = 1.08f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulse"
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(scale)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        isPressed = true
                                        tryAwaitRelease()
                                        isPressed = false
                                    },
                                    onTap = { onSubjectClicked(subject) }
                                )
                            }
                            .testTag("subject_card_${subject.lowercase()}"),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.5.dp, theme.primaryColor.copy(alpha = 0.18f)),
                        shape = RoundedCornerShape(22.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isPressed) 1.5.dp else 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                             Column(modifier = Modifier.weight(1f)) {
                                 // Localized Subject name translation
                                 val localizedName = remember(subject, language) {
                                     if (language == "AMH") {
                                         when (subject.trim().lowercase()) {
                                             "biology" -> "ባዮሎጂ (Biology)"
                                             "chemistry" -> "ኬሚስትሪ (Chemistry)"
                                             "mathematics", "maths", "math" -> "ሒሳብ (Mathematics)"
                                             "physics" -> "ፊዚክስ (Physics)"
                                             else -> subject
                                         }
                                     } else {
                                         subject
                                     }
                                 }
                                 
                                 Text(
                                     text = localizedName,
                                     fontSize = 18.sp,
                                     fontWeight = FontWeight.ExtraBold,
                                     color = Color(0xFF0F172A),
                                     letterSpacing = (-0.3).sp
                                 )
                                 
                                 Spacer(modifier = Modifier.height(4.dp))
                                 
                                 Text(
                                     text = if (language == "AMH") {
                                         "ክፍል $selectedGrade ስርአተ-ትምህርት • $unitCount ምዕራፎች"
                                     } else {
                                         "Grade $selectedGrade Curriculum • $unitCount Chapters"
                                     },
                                     fontSize = 13.sp,
                                     color = Color(0xFF64748B),
                                     fontWeight = FontWeight.SemiBold
                                 )
                                 
                                 Spacer(modifier = Modifier.height(4.dp))
                                 
                                 // Friendly modern tags to build professional interactive tone
                                 Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                     Box(
                                         modifier = Modifier
                                             .background(theme.primaryColor.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                             .padding(horizontal = 6.dp, vertical = 2.dp)
                                     ) {
                                         Text(
                                             text = if (language == "AMH") "ፈጣን ጥያቄዎች" else "Interactive Quizzes",
                                             fontSize = 9.sp,
                                             fontWeight = FontWeight.Bold,
                                             color = theme.primaryColor
                                         )
                                     }
                                     Box(
                                         modifier = Modifier
                                             .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                                             .padding(horizontal = 6.dp, vertical = 2.dp)
                                     ) {
                                         Text(
                                             text = if (language == "AMH") "አገር አቀፍ ፈተና" else "Exam prep",
                                             fontSize = 9.sp,
                                             fontWeight = FontWeight.Bold,
                                             color = Color(0xFF475569)
                                         )
                                     }
                                 }
                             }
                             
                             Spacer(modifier = Modifier.width(10.dp))
                             
                             // Vibrant action button guiding user clearly
                             Box(
                                 modifier = Modifier
                                     .scale(pulseScale)
                                     .size(40.dp)
                                     .background(
                                         Brush.radialGradient(
                                             colors = listOf(theme.primaryColor, theme.primaryColor.copy(alpha = 0.85f))
                                         ),
                                         CircleShape
                                     ),
                                 contentAlignment = Alignment.Center
                             ) {
                                 Icon(
                                     imageVector = Icons.Default.PlayArrow,
                                     contentDescription = "Begin Subject",
                                     tint = Color.White,
                                     modifier = Modifier.size(22.dp)
                                 )
                             }
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
    onUnitClicked: (String) -> Unit
) {
    val selectedGrade by viewModel.selectedGrade.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val supabaseSubjects by viewModel.supabaseSubjects.collectAsState()

    // Determine beautiful subject brand theme matching the selected subject
    val theme = remember(selectedSubject) {
        when (selectedSubject?.trim()?.lowercase()) {
            "biology" -> SubjectTheme(Color(0xFF10B981), Color(0xFFECFDF5), Color(0xFF065F46), Icons.Default.Book)
            "chemistry" -> SubjectTheme(Color(0xFFF59E0B), Color(0xFFFFFBEB), Color(0xFF92400E), Icons.Default.Class)
            "mathematics", "maths", "math" -> SubjectTheme(Color(0xFF3B82F6), Color(0xFFEFF6FF), Color(0xFF1E40AF), Icons.Default.School)
            "physics" -> SubjectTheme(Color(0xFF8B5CF6), Color(0xFFF5F3FF), Color(0xFF5B21B6), Icons.Default.Refresh)
            else -> SubjectTheme(Color(0xFF6366F1), Color(0xFFEEF2FF), Color(0xFF3730A3), Icons.Default.Book)
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

    // Trigger sequential loading animations
    val listState = rememberLazyListState()
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = selectedSubject) {
        visible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF8FAFC), Color(0xFFEFF6FF))
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
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                            .size(150.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 50.dp, y = (-50).dp)
                            .background(Color.White.copy(alpha = 0.08f), CircleShape)
                    )
                    
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.4f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = theme.cardIcon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = selectedSubject ?: "Subject Modules",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Grade $selectedGrade Curriculum • ${unitsList.size} Chapters Loaded",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp,
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
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A),
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )

        // Lazy ListView with beautiful offset slide-up animations for each card
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            itemsIndexed(unitsList) { index, unitTitle ->
                var itemPreloadState by remember { mutableStateOf(false) }
                LaunchedEffect(key1 = true) {
                    kotlinx.coroutines.delay(index * 120L) // cascading stagger animation effect!
                    itemPreloadState = true
                }

                AnimatedVisibility(
                    visible = itemPreloadState,
                    enter = fadeIn(animationSpec = tween(durationMillis = 400)) + 
                            slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(durationMillis = 400))
                ) {
                    UnitCardItem(
                        index = index + 1,
                        unitTitle = unitTitle,
                        theme = theme,
                        onClick = { onUnitClicked(unitTitle) }
                    )
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
    onClick: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 0.98f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "unit_item_pressed"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isHovered = true
                        tryAwaitRelease()
                        isHovered = false
                    },
                    onTap = { isExpanded = !isExpanded }
                )
            }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isExpanded) theme.primaryColor else Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 6.dp else 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Elegant modern badge indicator for unit numbering
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(theme.primaryColor, theme.primaryColor.copy(alpha = 0.8f))
                            ),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$index",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Parse Unit Prefix if exists, or show beautiful titles
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
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    Text(
                        text = cleanedTitle,
                        color = Color(0xFF0F172A),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(theme.lightBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val rotation by animateFloatAsState(
                        targetValue = if (isExpanded) 90f else 0f,
                        animationSpec = tween(300),
                        label = "expand_rotation"
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Expand Options",
                        tint = theme.primaryColor,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer { rotationZ = rotation }
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC))
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Start Quiz Button
                        Button(
                            onClick = onClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = theme.primaryColor,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Start Quiz", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        // Download Quiz Button
                        OutlinedButton(
                            onClick = { /* TODO: Implement Download */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, theme.primaryColor.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = theme.primaryColor
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Download", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayQuizScreen(viewModel: QuizViewModel) {
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
                        color = Color(0xFF475569),
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
                        Text("Back to Home", color = Color(0xFF475569), fontWeight = FontWeight.Bold)
                    }
                } else {
                    CircularProgressIndicator(color = Color(0xFF3B82F6))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Preparing quiz questions... Please wait.", fontWeight = FontWeight.Medium)
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
                colors = listOf(Color(0xFFE0E7FF), Color(0xFFF8FAFC))
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
                        color = Color(0xFF64748B)
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
                            trackColor = Color(0xFFE2E8F0)
                        )
                        Text(
                            text = "$timerSeconds",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (timerSeconds < 10) Color(0xFFEF4444) else Color(0xFF0F172A)
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
                    trackColor = Color(0xFFE2E8F0)
                )
            }
        }

        // Question Statement Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = currentQuestion.questionText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.padding(24.dp),
                    lineHeight = 26.sp
                )
            }
        }

        // Choice Options Options Choice Cards
        items(currentQuestion.options.size) { index ->
            val optionText = currentQuestion.options[index]
            val isSelected = selectedAnswerIndex == index
            
            // UI state styling
            val targetCardColor = when {
                isAnswered && index == currentQuestion.correctAnswerIndex -> Color(0xFFECFDF5) // Green (correct option)
                isAnswered && isSelected && selectedAnswerIndex != currentQuestion.correctAnswerIndex -> Color(0xFFFEF2F2) // Red (incorrect selection)
                isSelected -> Color(0xFFEFF6FF) // blue selection before submission
                else -> Color.White
            }
            
            val animatedCardColor by animateColorAsState(
                targetValue = targetCardColor, 
                animationSpec = tween(300),
                label = "cardColorAnimation"
            )

            val targetBorderColor = when {
                isAnswered && index == currentQuestion.correctAnswerIndex -> Color(0xFF10B981) // positive border
                isAnswered && isSelected && selectedAnswerIndex != currentQuestion.correctAnswerIndex -> Color(0xFFEF4444) // negative border
                isSelected -> Color(0xFF3B82F6) // active focus border
                else -> Color(0xFFE2E8F0) // clean passive border
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
                                color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF1F5F9),
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
                        color = Color(0xFF1E293B),
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Submit or Next/Navigate Button controller
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
                        shape = RoundedCornerShape(12.dp)
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
                        Text(if (currentQuestionIndex == activeQuestions.size - 1) "Finish" else "Next")
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
                            disabledContainerColor = Color(0xFFE2E8F0)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Submit")
                    }
                }
            }
        }

        // Sliding detailed Explanation Block
        item {
            AnimatedVisibility(
                visible = isAnswered,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)), // success soft background
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (selectedAnswerIndex == currentQuestion.correctAnswerIndex) Icons.Default.CheckCircle else Icons.Default.Info,
                                contentDescription = "Evaluation indicator",
                                tint = if (selectedAnswerIndex == currentQuestion.correctAnswerIndex) Color(0xFF10B981) else Color(0xFFF59E0B),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedAnswerIndex == currentQuestion.correctAnswerIndex) "Well Done! Correct" else "Incorrect Choice",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedAnswerIndex == currentQuestion.correctAnswerIndex) Color(0xFF14532D) else Color(0xFF78350F)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Core Solution Breakdown:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF14532D)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentQuestion.explanation,
                            fontSize = 14.sp,
                            color = Color(0xFF1E3A1E),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreScreen(viewModel: QuizViewModel) {
    val score by viewModel.score.collectAsState()
    val activeQuestions by viewModel.activeQuestions.collectAsState()
    val totalQuestions = activeQuestions.size
    val selectedGrade by viewModel.selectedGrade.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()

    val percent = if (totalQuestions > 0) ((score.toFloat() / totalQuestions) * 100).toInt() else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                    color = if (percent >= 70) Color(0xFFECFDF5) else Color(0xFFFEF2F2),
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
            color = Color(0xFF0F172A)
        )
        
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Grade $selectedGrade Practice Quiz for $selectedSubject",
            fontSize = 14.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Performance Stat Card
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                    color = if (percent >= 70) Color(0xFF10B981) else Color(0xFF1B5ECF)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Score: $score correct out of $totalQuestions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF475569)
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
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry Icon")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry Quiz", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.resetToHome() }, // Back to homescreen
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("home_quiz_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64748B)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Finish", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BookmarksScreen(
    viewModel: QuizViewModel,
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
                    .size(72.dp)
                    .background(Color(0xFFFEF3C7), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = "No bookmarks saved",
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Bookmarks Yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Save difficult questions during active quiz play cycles to review them here at any time.",
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
                Text("Study Now", fontWeight = FontWeight.Bold)
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
        items(bookmarksList) { bookmarked ->
            val question = bookmarked.toQuestion()
            var isExpanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                                .background(Color(0xFFEFF6FF), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Grade ${question.grade} • ${question.subject}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5ECF)
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
                        color = Color(0xFF0F172A),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isExpanded) "Hide Solution" else "Show Solution",
                        fontSize = 13.sp,
                        color = Color(0xFF3B82F6),
                        fontWeight = FontWeight.Bold
                    )

                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth()
                        ) {
                            HorizontalDivider(color = Color(0xFFE2E8F0))
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
                                                color = if (isCorrectOption) Color(0xFFD1FAE5) else Color(0xFFF1F5F9),
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
                                            color = if (isCorrectOption) Color(0xFF10B981) else Color(0xFF64748B)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = choice,
                                        fontSize = 14.sp,
                                        color = if (isCorrectOption) Color(0xFF10B981) else Color(0xFF475569),
                                        fontWeight = if (isCorrectOption) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Explanation block
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF0FDF4), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Solution Breakdown:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF14532D)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = question.explanation,
                                        fontSize = 13.sp,
                                        color = Color(0xFF1E3A1E),
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
