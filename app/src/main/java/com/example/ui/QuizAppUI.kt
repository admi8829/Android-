package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
                val grades = listOf(9, 10, 11, 12)
                grades.forEach { gradeNum ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectGrade(gradeNum) }
                            .testTag("grade_card_${gradeNum}"),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFEFF6FF), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Class,
                                        contentDescription = "Grade Icon",
                                        tint = Color(0xFF1B5ECF),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Grade $gradeNum",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "National curriculum assessment quiz",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Start Grade $gradeNum",
                                tint = Color(0xFF1B5ECF)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectSelectionScreen(
    viewModel: QuizViewModel,
    onSubjectClicked: (String) -> Unit
) {
    val selectedGrade by viewModel.selectedGrade.collectAsState()
    val subjects = remember(selectedGrade) { viewModel.getSubjectsForSelectedGrade() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("subjects_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Choose a Subject to Begin",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        if (subjects.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "No subjects available for Grade $selectedGrade yet.",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(subjects) { subject ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSubjectClicked(subject) }
                        .testTag("subject_card_${subject.lowercase()}"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFECFDF5), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = "Subject Icon",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = subject,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Grade $selectedGrade Curriculum Practice",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
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

    val currentQuestion = activeQuestions.getOrNull(currentQuestionIndex)

    if (currentQuestion == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Preparing quiz questions...")
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("quiz_screen_player"),
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
                LinearProgressIndicator(
                    progress = progress,
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
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = currentQuestion.questionText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.padding(20.dp),
                    lineHeight = 26.sp
                )
            }
        }

        // Choice Options Options Choice Cards
        items(currentQuestion.options.size) { index ->
            val optionText = currentQuestion.options[index]
            val isSelected = selectedAnswerIndex == index
            
            // UI state styling
            val cardColor = when {
                isAnswered && index == currentQuestion.correctAnswerIndex -> Color(0xFFECFDF5) // Green (correct option)
                isAnswered && isSelected && selectedAnswerIndex != currentQuestion.correctAnswerIndex -> Color(0xFFFEF2F2) // Red (incorrect selection)
                isSelected -> Color(0xFFEFF6FF) // blue selection before submission
                else -> Color.White
            }

            val borderColor = when {
                isAnswered && index == currentQuestion.correctAnswerIndex -> Color(0xFF10B981) // positive border
                isAnswered && isSelected && selectedAnswerIndex != currentQuestion.correctAnswerIndex -> Color(0xFFEF4444) // negative border
                isSelected -> Color(0xFF3B82F6) // active focus border
                else -> Color(0xFFE2E8F0) // clean passive border
            }

            val iconColor = when {
                isAnswered && index == currentQuestion.correctAnswerIndex -> Color(0xFF10B981)
                isAnswered && isSelected && selectedAnswerIndex != currentQuestion.correctAnswerIndex -> Color(0xFFEF4444)
                isSelected -> Color(0xFF3B82F6)
                else -> Color(0xFF94A3B8)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isAnswered) { viewModel.selectAnswer(index) }
                    .testTag("quiz_option_$index"),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                border = BorderStroke(1.dp, borderColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
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
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = iconColor
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = optionText,
                        fontSize = 15.sp,
                        color = Color(0xFF1E293B),
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Submit or Next Button controller
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!isAnswered) {
                    Button(
                        onClick = { viewModel.submitAnswer() },
                        enabled = selectedAnswerIndex != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1B5ECF),
                            disabledContainerColor = Color(0xFFCBD5E1)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Submit Answer",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    Button(
                        onClick = { viewModel.nextQuestion() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("next_question_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1B5ECF)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (currentQuestionIndex + 1 < activeQuestions.size) "Next Question" else "See My Results",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
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
