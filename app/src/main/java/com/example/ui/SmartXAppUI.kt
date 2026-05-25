package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.viewmodel.QuizViewModel
import com.example.data.QuestionBank
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

// Dynamic Localization Vocabulary
object Loc {
    fun t(key: String, lang: String): String {
        val en = mapOf(
            "app_name" to "Smart X Academy",
            "menu_home" to "Home",
            "menu_profile" to "My Profile",
            "menu_saved_notes" to "Saved Notes",
            "menu_leaderboard" to "Leaderboard",
            "menu_telegram" to "Telegram Channel",
            "menu_rate" to "Rate App",
            "menu_about" to "About Smart X Academy",
            "menu_dev" to "Developer Company",
            "menu_privacy" to "Privacy & Terms of Policy",
            "menu_contact" to "Contact Us & Feedback",
            "menu_ad" to "AdMob System Check",
            "menu_title" to "Smart X Menu",
            "welcome" to "Welcome to Smart X Academy!",
            "explore_grade" to "Select Your Grade",
            "select_grade_desc" to "Select your grade to view courses.",
            "grade_9_subtitle" to "Begin your journey!",
            "grade_10_subtitle" to "Expand your knowledge!",
            "grade_11_subtitle" to "Prepare for excellence!",
            "grade_12_subtitle" to "Achieve your goals!",
            "courses" to "Courses",
            "home" to "Home",
            "profile" to "Offline",
            "settings" to "Online",
            "courses_title" to "Courses Catalogue",
            "settings_title" to "Settings & Options",
            "developer_title" to "Developer Company",
            "developer_desc" to "This app is crafted with love by the Expert Mobile Team at Smart X Academy Company to help Grades 9–12 students master school and national exams through interactive Q&A.",
            "privacy_title" to "Privacy, Terms & Conditions",
            "privacy_desc" to "At Smart X Academy, your privacy is our extreme priority. No personal data is stored or transmitted without your direct consent.",
            "about_app_desc" to "Smart X Academy is a cutting-edge mobile learning platform designed to enrich educational resources. Fast, intuitive, and works offline!",
            "grade_desc" to "Select a grade to see its curated subjects.",
            "back_to_main" to "Back to Home",
            "play_video" to "Interactive Video",
            "percent_completed" to "completed progress",
            "test_adunit_status" to "AdMob Test status: Verified OK"
        )
        val amh = mapOf(
            "app_name" to "ስማርት ኤክስ አካዳሚ",
            "menu_home" to "ዋና ገጽ",
            "menu_profile" to "የእኔ መገለጫ",
            "menu_saved_notes" to "የተቀመጡ ማስታወሻዎች",
            "menu_leaderboard" to "የደረጃ ሰንጠረዥ",
            "menu_telegram" to "ቴሌግራም ቻናል",
            "menu_rate" to "ደረጃ ይስጡ",
            "menu_about" to "ስለ ስማርት ኤክስ አካዳሚ",
            "menu_dev" to "አልሚው ድርጅት",
            "menu_privacy" to "የግላዊነት መመሪያና ደንቦች",
            "menu_contact" to "ያግኙን እና አስተያየት ይስጡ",
            "menu_ad" to "የአድሞብ ማረጋገጫ",
            "menu_title" to "የስማርት ኤክስ ዝርዝር",
            "welcome" to "እንኳን ወደ ስማርት ኤክስ የመማሪያ አካዳሚ በደህና መጡ!",
            "explore_grade" to "ክፍልዎን ይምረጡ",
            "select_grade_desc" to "ትምህርቶችን ለመመልከት የእርስዎን ክፍል ይምረጡ።",
            "grade_9_subtitle" to "ጉዞዎን ዛሬውኑ ይጀምሩ!",
            "grade_10_subtitle" to "እውቀትዎን በስፋት ያሳድጉ!",
            "grade_11_subtitle" to "ለላቀ ውጤት ይዘጋጁ!",
            "grade_12_subtitle" to "ግብዎን ይምቱ!",
            "courses" to "ትምህርቶች",
            "home" to "ዋና ገጽ",
            "profile" to "ከመስመር ውጪ (Offline)",
            "settings" to "በመስመር ላይ (Online)",
            "courses_title" to "የሁሉም ትምህርቶች ዝርዝር",
            "settings_title" to "የመተግበሪያ ቅንብሮች",
            "developer_title" to "አልሚው ድርጅት",
            "developer_desc" to "ይህ መተግበሪያ ከ9-12 ላሉ ተማሪዎች የአገር አቀፍና የትምህርት ቤት ፈተናዎችን በቀላሉ እንዲያልፉ በስማርት ኤክስ አካዳሚ ድርጅት የሞባይል መተግበሪያ ልማት ቡድን (Smart X Academy Company) በጥንቃቄ ተዘጋጅቷል::",
            "privacy_title" to "የግላዊነት እና የአጠቃቀም ደንቦች",
            "privacy_desc" to "በስማርት ኤክስ አካዳሚ የእርስዎ ግላዊነት በጥብቅ የተጠበቀ ነው:: ያለእርስዎ ፈቃድ ምንም ዳታ አይወሰድም::",
            "about_app_desc" to "ስማርት ኤክስ አካዳሚ ዘመናዊ እና ፈጣን የትምህርት መድረክ ሲሆን ያለ ኢንተርኔት (offline) ጭምር መስራት የሚችል ነው!",
            "grade_desc" to "ሞጁሎችን ለመመልከት የእርስዎን ክፍል ይምረጡ።",
            "back_to_main" to "ወደ ዋናው ይመለሱ",
            "play_video" to "ተንቀሳቃሽ ቪዲዮ",
            "percent_completed" to "የተጠናቀቀ ደረጃ",
            "test_adunit_status" to "የአድሞብ የሙከራ ሁኔታ: በጥሩ ሁኔታ ላይ ነው"
        )
        val selectedMap = if (lang == "AMH") amh else en
        return selectedMap[key] ?: (en[key] ?: key)
    }
}

@Composable
fun DrawerMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 3.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) Color.White.copy(alpha = 0.15f) else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    if (isSelected) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.10f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun DrawerSecondaryItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White.copy(alpha = 0.65f),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun SmartXAppUI(viewModel: QuizViewModel) {
    val selectedGrade by viewModel.selectedGrade.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val selectedUnit by viewModel.selectedUnit.collectAsState()
    val isQuizFinished by viewModel.isQuizFinished.collectAsState()
    
    var currentSubScreen by remember { mutableStateOf("home") } 
    var isDarkMode by remember { mutableStateOf(false) }
    var language by remember { mutableStateOf("EN") }
    var showSplashScreen by remember { mutableStateOf(true) }

    // Drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Dialog state for Drawer selections
    var activeDialog by remember { mutableStateOf<String?>(null) }

    // User Profile persistent states
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("smartx_user_prefs", android.content.Context.MODE_PRIVATE) }
    var userName by remember { mutableStateOf(sharedPref.getString("user_name", "User Name") ?: "User Name") }
    var userCustomBadge by remember { mutableStateOf(sharedPref.getString("user_badge", "Scholar") ?: "Scholar") }
    
    val historyList by viewModel.history.collectAsState()
    val totalPoints = historyList.sumOf { it.score } * 10
    
    // Automatically dynamic level badge based on user quiz success
    val userBadge = when {
        totalPoints < 100 -> "Novice Scholar"
        totalPoints < 300 -> "Acheiver Academic"
        totalPoints < 600 -> "Expert Scholar"
        else -> "Smart X Master"
    }

    // User selected Rating
    var userRatingScore by remember { mutableStateOf(5) }
    var userFeedbackText by remember { mutableStateOf("") }

    BackHandler {
        when {
            selectedUnit != null -> viewModel.selectUnit(null)
            selectedSubject != null -> viewModel.selectSubject(null)
            selectedGrade != null -> viewModel.selectGrade(null)
            currentSubScreen != "home" -> currentSubScreen = "home"
            drawerState.isOpen -> scope.launch { drawerState.close() }
        }
    }

    if (showSplashScreen) {
        var animateInState by remember { mutableStateOf(0) } // 0 = initial, 1 = entering/active, 2 = zoom-out/fade-away
        LaunchedEffect(Unit) {
            animateInState = 1
            kotlinx.coroutines.delay(2200)
            animateInState = 2
            kotlinx.coroutines.delay(450)
            showSplashScreen = false
        }
        
        val scale by animateFloatAsState(
            targetValue = when (animateInState) {
                0 -> 0.82f
                1 -> 1.00f
                else -> 1.18f // Premium zoom-out effect
            },
            animationSpec = if (animateInState == 2) {
                tween(durationMillis = 450, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            } else {
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            },
            label = "splash_scale"
        )
        val alpha by animateFloatAsState(
            targetValue = if (animateInState == 1) 1.0f else 0.0f,
            animationSpec = tween(durationMillis = if (animateInState == 2) 350 else 750),
            label = "splash_alpha"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (isDarkMode) {
                            listOf(Color(0xFF0F172A), Color(0xFF020617))
                        } else {
                            listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                        }
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .scale(scale)
                    .alpha(alpha)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Smart X Academy",
                        tint = Color(0xFF1D4ED8),
                        modifier = Modifier.size(54.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Smart X Academy",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (language == "AMH") "የክፍል 9–12 የአካዳሚክ መጠይቆች ማዕከል" else "Grades 9–12 Academic Q&A Hub",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(48.dp))
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 3.dp
                )
            }
        }
    } else {
        // Modal navigation drawer with a gorgeous, premium blue & teal backdrop matching the mockup
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(300.dp),
                    drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                    drawerContainerColor = Color.Transparent
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(300.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF1E3C72), // Elegant royal blue at top
                                        Color(0xFF1B5ECF), // Vibrant theme-matching blue
                                        Color(0xFF0F766E)  // Deep rich teal at bottom
                                    )
                                )
                            )
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(vertical = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Glassmorphic User Profile Header Card block as pictured
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Profile photo / Graduation cap shape circle
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                        .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = "Graduation Cap Profile Icon",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = userName,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                // Badge level lightbulb status pill
                                Box(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
                                        .border(0.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(50))
                                        .padding(horizontal = 12.dp, vertical = 3.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = "Badge Status",
                                            tint = Color(0xFFFDE047),
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = userBadge,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // High-contrast, clean Drawer Menu items in exact ordered design:
                        
                        // 1. Home (Home icon)
                        DrawerMenuItem(
                            icon = Icons.Default.Home,
                            label = Loc.t("menu_home", language),
                            isSelected = currentSubScreen == "home" && selectedGrade == null && selectedSubject == null,
                            onClick = {
                                scope.launch { drawerState.close() }
                                viewModel.selectGrade(null)
                                viewModel.selectSubject(null)
                                viewModel.selectUnit(null)
                                currentSubScreen = "home"
                            }
                        )

                        // 2. My Profile (Person icon)
                        DrawerMenuItem(
                            icon = Icons.Default.Person,
                            label = Loc.t("menu_profile", language),
                            isSelected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                activeDialog = "profile_editor"
                            }
                        )

                        // 3. Saved Notes (Bookmark icon)
                        DrawerMenuItem(
                            icon = Icons.Default.Bookmark,
                            label = Loc.t("menu_saved_notes", language),
                            isSelected = currentSubScreen == "profile",
                            onClick = {
                                scope.launch { drawerState.close() }
                                viewModel.selectGrade(null)
                                viewModel.selectSubject(null)
                                viewModel.selectUnit(null)
                                currentSubScreen = "profile"
                            }
                        )

                        // 4. Leaderboard (Trophy icon)
                        DrawerMenuItem(
                            icon = Icons.Default.EmojiEvents,
                            label = Loc.t("menu_leaderboard", language),
                            isSelected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                activeDialog = "leaderboard"
                            }
                        )

                        // 5. Telegram Channel (Send/Telegram icon rotated for paper plane look)
                        DrawerMenuItem(
                            icon = Icons.Default.Send,
                            label = Loc.t("menu_telegram", language),
                            isSelected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                try {
                                    val intent = android.content.Intent(
                                        android.content.Intent.ACTION_VIEW,
                                        android.net.Uri.parse("https://t.me/Smart_X_Academy")
                                    )
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    android.util.Log.e("SmartXAppUI", "Cannot open Telegram: ${e.message}")
                                }
                            }
                        )

                        // 6. Rate App (Star icon)
                        DrawerMenuItem(
                            icon = Icons.Default.Star,
                            label = Loc.t("menu_rate", language),
                            isSelected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                activeDialog = "rate_app"
                            }
                        )

                        // Subtle transparent line divide as pictured
                        androidx.compose.material3.HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            color = Color.White.copy(alpha = 0.15f),
                            thickness = 1.dp
                        )

                        // Bottom fixed secondary items:
                        DrawerSecondaryItem(
                            icon = Icons.Default.Business,
                            label = Loc.t("menu_dev", language),
                            onClick = {
                                scope.launch { drawerState.close() }
                                activeDialog = "dev"
                            }
                        )

                        DrawerSecondaryItem(
                            icon = Icons.Default.Security,
                            label = Loc.t("menu_privacy", language),
                            onClick = {
                                scope.launch { drawerState.close() }
                                activeDialog = "privacy"
                            }
                        )

                        DrawerSecondaryItem(
                            icon = Icons.Default.Info,
                            label = Loc.t("menu_about", language),
                            onClick = {
                                scope.launch { drawerState.close() }
                                activeDialog = "about"
                            }
                        )
                    }
                }
            }
        ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                if (selectedGrade == null && selectedSubject == null && currentSubScreen == "home") {
                    SmartXTopAppBar(
                        isDarkMode = isDarkMode,
                        onToggleDark = { isDarkMode = !isDarkMode },
                        language = language,
                        onToggleLanguage = { language = if (language == "EN") "AMH" else "EN" },
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                } else {
                    QuizTopAppBarRefactored(
                        title = when {
                            selectedUnit != null -> "$selectedSubject - $selectedUnit"
                            selectedSubject != null -> "Grade $selectedGrade - $selectedSubject"
                            selectedGrade != null -> "Grade $selectedGrade ${Loc.t("courses", language)}"
                            currentSubScreen == "courses" -> Loc.t("courses_title", language)
                            currentSubScreen == "profile" -> Loc.t("profile", language)
                            currentSubScreen == "settings" -> Loc.t("settings_title", language)
                            else -> Loc.t("app_name", language)
                        },
                        onBack = {
                            when {
                                selectedUnit != null -> viewModel.selectUnit(null)
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
                            language = language,
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
                    .background(
                        Brush.verticalGradient(
                            colors = if (isDarkMode) {
                                listOf(Color(0xFF0F172A), Color(0xFF020617))
                            } else {
                                listOf(Color(0xFFEFF6FF), Color(0xFFF8FAFC))
                            }
                        )
                    )
                    .padding(innerPadding)
            ) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    val isQuizActive by viewModel.isQuizActive.collectAsState()
                    when {
                        selectedSubject != null && selectedUnit != null && isQuizActive -> {
                            if (isQuizFinished) {
                                ScoreScreen(viewModel = viewModel)
                            } else {
                                PlayQuizScreen(viewModel = viewModel)
                            }
                        }
                        selectedSubject != null && selectedUnit != null && !isQuizActive -> {
                            UnitOptionsScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.selectUnit(null) }
                            )
                        }
                        selectedSubject != null -> {
                            UnitSelectionScreen(
                                viewModel = viewModel,
                                onUnitClicked = { unit -> viewModel.selectUnit(unit) }
                            )
                        }
                        selectedGrade != null -> {
                            SubjectSelectionScreen(
                                viewModel = viewModel,
                                onSubjectClicked = { subject -> viewModel.selectSubject(subject) }
                            )
                        }
                        else -> {
                            when (currentSubScreen) {
                                "home" -> SmartXHomeScreen(viewModel = viewModel, isDarkMode = isDarkMode, language = language)
                                "courses" -> PlaceholderScreen(Loc.t("courses_title", language), Icons.Default.MenuBook)
                                "profile" -> BookmarksScreen(viewModel = viewModel, onBackToHome = { currentSubScreen = "home" }) 
                                "settings" -> PlaceholderScreen(Loc.t("settings_title", language), Icons.Default.Settings)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal dialogs for information display
    activeDialog?.let { dialogType ->
        when (dialogType) {
            "profile_editor" -> {
                var tempName by remember { mutableStateOf(userName) }
                AlertDialog(
                    onDismissRequest = { activeDialog = null },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                userName = tempName
                                sharedPref.edit().putString("user_name", tempName).apply()
                                activeDialog = null
                            }
                        ) {
                            Text(if (language == "AMH") "አስቀምጥ" else "Save Changes", fontWeight = FontWeight.Bold, color = Color(0xFF1B5ECF))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { activeDialog = null }) {
                            Text(if (language == "AMH") "ስርዝ" else "Cancel", color = Color.Gray)
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1B5ECF), modifier = Modifier.size(26.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = if (language == "AMH") "መገለጫዬን አሻሽል" else "My Profile Statistics", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    },
                    text = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = if (language == "AMH") "ስምዎ:" else "Enter Your Student Name:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = tempName,
                                onValueChange = { tempName = it },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Student Name") }
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Stats Summary Card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = if (language == "AMH") "የአካዳሚክ ስኬት ማጠቃለያ" else "Academic Success Summary", 
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF1E3A8A)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(if (language == "AMH") "ጠቅላላ የተመለሱ ፈተናዎች" else "Total Solved Quizzes:", fontSize = 12.sp, color = Color.DarkGray)
                                        Text("${historyList.size}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(if (language == "AMH") "ያገኙት የአካዳሚክ ነጥብ" else "Cumulative Score points:", fontSize = 12.sp, color = Color.DarkGray)
                                        Text("$totalPoints pts", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5ECF))
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(if (language == "AMH") "የክብር ማዕረግዎ (Badge)" else "Honorary Badge Status:", fontSize = 12.sp, color = Color.DarkGray)
                                        Text(userBadge, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                                    }
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(20.dp)
                )
            }
            "leaderboard" -> {
                AlertDialog(
                    onDismissRequest = { activeDialog = null },
                    confirmButton = {
                        TextButton(onClick = { activeDialog = null }) {
                            Text("Awesome", fontWeight = FontWeight.Bold, color = Color(0xFF1B5ECF))
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = if (language == "AMH") "የደረጃ ሰንጠረዥ" else "Grades 9-12 Leaderboard", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    },
                    text = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = if (language == "AMH") "በሳምንቱ ከፍተኛ ውጤት ያስመዘገቡ ተማሪዎች:" else "Top Academic Competitors This Week:",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            // Mock Rank data + include current user's actual points dynamically!
                            val competitors = listOf(
                                Triple(1, "Hana Tolosa (G12)", 1480),
                                Triple(2, "Dawit Abera (G11)", 1350),
                                Triple(3, "Yonas Kefelegn (G10)", 1220),
                                Triple(4, userName + " (You)", maxOf(totalPoints, 450)),
                                Triple(5, "Meron Tesfaye (G9)", 420)
                            ).sortedByDescending { it.third }
                            
                            competitors.forEachIndexed { index, (_, name, points) ->
                                val isUser = name.startsWith(userName)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(
                                            if (isUser) Color(0xFFDBEAFE) else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = when (index) {
                                                0 -> "🥇"
                                                1 -> "🥈"
                                                2 -> "🥉"
                                                else -> "  ${index + 1}"
                                            },
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.width(28.dp)
                                        )
                                        Text(
                                            text = name,
                                            fontSize = 13.sp,
                                            fontWeight = if (isUser) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isUser) Color(0xFF1E3A8A) else Color.Black
                                        )
                                    }
                                    Text(
                                        text = "$points pts",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUser) Color(0xFF1B5ECF) else Color.DarkGray
                                    )
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(20.dp)
                )
            }
            "rate_app" -> {
                var ratingSubmitted by remember { mutableStateOf(false) }
                AlertDialog(
                    onDismissRequest = { activeDialog = null },
                    confirmButton = {
                        if (ratingSubmitted) {
                            TextButton(onClick = { activeDialog = null }) {
                                Text("Done", fontWeight = FontWeight.Bold, color = Color(0xFF1B5ECF))
                            }
                        } else {
                            TextButton(
                                onClick = {
                                    ratingSubmitted = true
                                }
                            ) {
                                Text(if (language == "AMH") "አስገባ" else "Submit", fontWeight = FontWeight.Bold, color = Color(0xFF1B5ECF))
                            }
                        }
                    },
                    dismissButton = {
                        if (!ratingSubmitted) {
                            TextButton(onClick = { activeDialog = null }) {
                                Text(if (language == "AMH") "ዝጋ" else "Cancel", color = Color.Gray)
                            }
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = if (language == "AMH") "ደረጃ ይስጡ" else "Rate Smart X Academy", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    },
                    text = {
                        if (ratingSubmitted) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = Color(0xFF10B981), modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (language == "AMH") "አስተያየትዎ በጥሩ ሁኔታ ደርሶናል! እናመሰግናለን::" else "Thank you for your rating! Your feedback makes Smart X Academy better.",
                                    textAlign = TextAlign.Center,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = if (language == "AMH") "ለመተግበሪያው ያለዎትን ፍቅር በኮከብ ይግለጹ:" else "Show your love or share a feature feedback with us:",
                                    fontSize = 13.sp,
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                
                                // Star Rows
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    (1..5).forEach { star ->
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Star $star",
                                            tint = if (star <= userRatingScore) Color(0xFFFBBF24) else Color.Gray.copy(alpha = 0.3f),
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clickable { userRatingScore = star }
                                                .padding(horizontal = 4.dp)
                                        )
                                    }
                                }
                                
                                OutlinedTextField(
                                    value = userFeedbackText,
                                    onValueChange = { userFeedbackText = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text(if (language == "AMH") "የእርስዎ አስተያየት (አማራጭ)..." else "Write your feedback (optional)...") },
                                    maxLines = 3
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(20.dp)
                )
            }
            else -> {
                AlertDialog(
                    onDismissRequest = { activeDialog = null },
                    confirmButton = {
                        TextButton(onClick = { activeDialog = null }) {
                            Text("OK", fontWeight = FontWeight.Bold, color = Color(0xFF1B5ECF))
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when(dialogType) {
                                    "dev" -> Icons.Default.Business
                                    "privacy" -> Icons.Default.Security
                                    "about" -> Icons.Default.Info
                                    else -> Icons.Default.Campaign
                                },
                                contentDescription = null,
                                tint = Color(0xFF1B5ECF),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = when(dialogType) {
                                    "dev" -> Loc.t("developer_title", language)
                                    "privacy" -> Loc.t("privacy_title", language)
                                    "about" -> Loc.t("menu_about", language)
                                    else -> Loc.t("menu_ad", language)
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    },
                    text = {
                        Text(
                            text = when(dialogType) {
                                "dev" -> Loc.t("developer_desc", language)
                                "privacy" -> Loc.t("privacy_desc", language)
                                "about" -> Loc.t("about_app_desc", language)
                                else -> {
                                    "${Loc.t("test_adunit_status", language)}\n\n" +
                                    "Ad Unit ID in use:\nca-app-pub-3940256099942544/6300978111"
                                }
                            },
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    },
                    shape = RoundedCornerShape(20.dp)
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
fun PlaceholderScreen(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
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
            text = Loc.t("app_name", language),
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
                        text = if(language == "EN") "ENG/አማ" else "አማ/ENG", 
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
fun SmartXBottomNav(
    language: String,
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
            label = { Text(Loc.t("home", language)) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF1B5ECF), selectedTextColor = Color(0xFF1B5ECF))
        )
        NavigationBarItem(
            selected = currentScreen == "courses",
            onClick = { onScreenSelected("courses") },
            icon = { Icon(Icons.Default.MenuBook, contentDescription = "Courses") },
            label = { Text(Loc.t("courses", language)) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF1B5ECF), selectedTextColor = Color(0xFF1B5ECF))
        )
        NavigationBarItem(
            selected = currentScreen == "profile",
            onClick = { onScreenSelected("profile") },
            icon = { 
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.CloudOff, contentDescription = "Offline")
                    if (currentScreen != "profile") {
                        PulsingGestureGuide()
                    }
                }
            },
            label = { Text(Loc.t("profile", language)) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF1B5ECF), selectedTextColor = Color(0xFF1B5ECF))
        )
        NavigationBarItem(
            selected = currentScreen == "settings",
            onClick = { onScreenSelected("settings") },
            icon = { Icon(Icons.Default.Public, contentDescription = "Online") },
            label = { Text(Loc.t("settings", language)) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF1B5ECF), selectedTextColor = Color(0xFF1B5ECF))
        )
    }
}

/**
 * Automatically extracts the YouTube Video ID from any standard or shortened YouTube URL.
 * Designed to cleanly handle:
 * - https://youtu.be/FRjnr4UAhNk?si=WK9mMpbtEnCKVlXs (Shortened format)
 * - https://www.youtube.com/watch?v=FRjnr4UAhNk (Standard watch format)
 * - https://youtube.com/embed/FRjnr4UAhNk (Embed format)
 * - https://youtube.com/shorts/FRjnr4UAhNk (Shorts format)
 */
fun extractYoutubeVideoId(url: String): String {
    val trimmedUrl = url.trim()
    val pattern = "(?:youtube(?:-nocookie)?\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})"
    val compiledPattern = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.CASE_INSENSITIVE)
    val matcher = compiledPattern.matcher(trimmedUrl)
    if (matcher.find()) {
        return matcher.group(1) ?: "FRjnr4UAhNk"
    }
    
    // Quick fallback manual parsing
    if (trimmedUrl.contains("youtu.be/")) {
        val path = trimmedUrl.substringAfter("youtu.be/")
        val id = path.substringBefore("?").substringBefore("&")
        if (id.length == 11) return id
    }
    if (trimmedUrl.contains("v=")) {
        val id = trimmedUrl.substringAfter("v=").substringBefore("&").substringBefore("?")
        if (id.length == 11) return id
    }
    return "FRjnr4UAhNk" // Default fallback ID if invalid
}

data class Lesson(
    val id: Int,
    val title: String,
    val duration: String,
    val videoId: String,
    val imageUrl: String,
    val fallbackColors: List<Color>,
    val subject: String,
    val grade: String
)

@Composable
fun LessonCard(
    lesson: Lesson,
    isDarkMode: Boolean,
    language: String,
    onWatchClick: () -> Unit
) {
    val cardBg = if (isDarkMode) Color(0xFF1E293B) else Color.White
    val borderCol = if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
    val fontColor = if (isDarkMode) Color(0xFFF1F5F9) else Color(0xFF0F172A)
    
    Card(
        modifier = Modifier
            .width(225.dp)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, borderCol)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // Thumbnail container with rounded corners
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(colors = lesson.fallbackColors))
                    .clickable { onWatchClick() }
            ) {
                AsyncImage(
                    model = lesson.imageUrl,
                    contentDescription = lesson.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Centered Play overlay (White Translucent circular play badge)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .align(Alignment.Center)
                        .background(Color.White.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play Icon",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                
                // Duration Tag
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = lesson.duration,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Lesson title
            Text(
                text = lesson.title,
                color = fontColor,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp,
                modifier = Modifier.height(34.dp).fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Watch button capsule
            Box(
                modifier = Modifier
                    .background(Color(0xFF0A5296), RoundedCornerShape(50))
                    .clickable { onWatchClick() }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play Icon",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (language == "AMH") "ይመልከቱ" else "Watch",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SmartXHomeScreen(viewModel: QuizViewModel, isDarkMode: Boolean, language: String) {
    val cardColor = if (isDarkMode) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    var isPlayingVideo by remember { mutableStateOf(false) }
    var chosenLauncherGrade by remember { mutableStateOf(9) }
    
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("smartx_first_time_prefs", android.content.Context.MODE_PRIVATE) }
    var showGestureGuide by remember {
        mutableStateOf(prefs.getBoolean("show_swipe_up_guide", true))
    }
    
    var selectedLessonVideoId by remember { mutableStateOf(extractYoutubeVideoId("https://youtu.be/FRjnr4UAhNk?si=WK9mMpbtEnCKVlXs")) }
    var selectedLessonTitle by remember { mutableStateOf("Biology G10: Cell Biology") }
    
    val lessons = remember {
        listOf(
            Lesson(
                id = 1,
                title = "Biology G10: Cell Biology",
                duration = "08:30",
                videoId = extractYoutubeVideoId("https://youtu.be/FRjnr4UAhNk?si=WK9mMpbtEnCKVlXs"),
                imageUrl = "https://images.unsplash.com/photo-1530026405186-ed1ea0ac7a63?auto=format&fit=crop&q=80&w=400",
                fallbackColors = listOf(Color(0xFF0F766E), Color(0xFF134E5E)),
                subject = "Biology",
                grade = "10"
            ),
            Lesson(
                id = 2,
                title = "English G11: Tenses",
                duration = "12:15",
                videoId = extractYoutubeVideoId("https://www.youtube.com/watch?v=b1oleA4_O58"),
                imageUrl = "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?auto=format&fit=crop&q=80&w=400",
                fallbackColors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)),
                subject = "English",
                grade = "11"
            ),
            Lesson(
                id = 3,
                title = "Physics G12: Fluid Mechanics",
                duration = "10:45",
                videoId = extractYoutubeVideoId("https://youtube.com/embed/77nfe4k0G5Y"),
                imageUrl = "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?auto=format&fit=crop&q=80&w=400",
                fallbackColors = listOf(Color(0xFF6366F1), Color(0xFF4338CA)),
                subject = "Physics",
                grade = "12"
            ),
            Lesson(
                id = 4,
                title = "Chemistry G9: Chemical Bonding",
                duration = "09:50",
                videoId = extractYoutubeVideoId("https://youtube.com/shorts/UR4eOf45_No"),
                imageUrl = "https://images.unsplash.com/photo-1603126857599-f6e157fa2fe6?auto=format&fit=crop&q=80&w=400",
                fallbackColors = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
                subject = "Chemistry",
                grade = "9"
            )
        )
    }
    
    val dynamicGrades by viewModel.supabaseGrades.collectAsState()
    val displayGrades = if (dynamicGrades.isNotEmpty()) dynamicGrades else listOf(9, 10, 11, 12)
    val isEmulator = remember {
        val buildHardware = android.os.Build.HARDWARE ?: ""
        val buildFingerprint = android.os.Build.FINGERPRINT ?: ""
        val buildModel = android.os.Build.MODEL ?: ""
        buildHardware.contains("goldfish") || 
        buildHardware.contains("ranchu") || 
        buildFingerprint.startsWith("generic") ||
        buildModel.contains("google_sdk") ||
        buildModel.contains("Emulator") ||
        buildModel.contains("Android SDK")
    }
    var webViewLoadError by remember { mutableStateOf(false) }
    
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val isTablet = screenWidth > 600.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = if (isTablet) 850.dp else 600.dp)
                .align(Alignment.TopCenter),
            contentPadding = PaddingValues(horizontal = if (isTablet) 24.dp else 16.dp, vertical = 12.dp)
        ) {
            // Active Player Area
            if (isPlayingVideo) {
                item {
                    val context = LocalContext.current
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Black),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                        ) {
                            if (!webViewLoadError) {
                                key(selectedLessonVideoId) {
                                    AndroidView(
                                        factory = { ctx ->
                                            try {
                                                android.webkit.WebView(ctx).apply {
                                                    layoutParams = android.view.ViewGroup.LayoutParams(
                                                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                                    )
                                                    
                                                    // Standard web configuration for YouTube HTML5 Embed and standard videos
                                                    settings.javaScriptEnabled = true
                                                    settings.domStorageEnabled = true
                                                    settings.mediaPlaybackRequiresUserGesture = false
                                                    settings.useWideViewPort = true
                                                    settings.loadWithOverviewMode = true
                                                    settings.allowContentAccess = true
                                                    settings.allowFileAccess = true
                                                    
                                                    // Custom mobile user agent to act as a modern browser and bypass Google native API error 152 restricts
                                                    settings.userAgentString = "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.50 Mobile Safari/537.36"
                                                    
                                                    webViewClient = object : android.webkit.WebViewClient() {
                                                        override fun onPageStarted(view: android.webkit.WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                                            super.onPageStarted(view, url, favicon)
                                                            webViewLoadError = false
                                                        }
                                                        
                                                        override fun onReceivedError(
                                                            view: android.webkit.WebView?,
                                                            request: android.webkit.WebResourceRequest?,
                                                            error: android.webkit.WebResourceError?
                                                        ) {
                                                            super.onReceivedError(view, request, error)
                                                            android.util.Log.e("SmartXAppUI", "WebView loading error: ${error?.description}")
                                                            webViewLoadError = true
                                                        }
                                                    }
                                                    
                                                    // Check if URL is YouTube stream versus generic/normal local/cloud video
                                                    val isYouTube = selectedLessonVideoId.length == 11 || 
                                                                    selectedLessonVideoId.contains("youtube") || 
                                                                    selectedLessonVideoId.contains("youtu.be")
                                                    
                                                    if (isYouTube) {
                                                        val cleanId = if (selectedLessonVideoId.length == 11) {
                                                            selectedLessonVideoId
                                                        } else {
                                                            extractYoutubeVideoId(selectedLessonVideoId)
                                                        }
                                                        
                                                        // Loading embedded YouTube player utilizing 'referer' origin to fully bypass Error 152
                                                        val htmlData = """
                                                            <!DOCTYPE html>
                                                            <html>
                                                                <head>
                                                                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                                                                    <style>
                                                                        body { margin: 0; padding: 0; background-color: #000; overflow: hidden; display: flex; align-items: center; justify-content: center; height: 100vh; }
                                                                        iframe { width: 100%; height: 100%; object-fit: contain; border: none; }
                                                                    </style>
                                                                </head>
                                                                <body>
                                                                    <iframe src="https://www.youtube.com/embed/$cleanId?autoplay=1&controls=1&rel=0&showinfo=0&modestbranding=1&enablejsapi=1&origin=https://www.youtube.com" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
                                                                </body>
                                                            </html>
                                                        """.trimIndent()
                                                        loadDataWithBaseURL("https://www.youtube.com", htmlData, "text/html", "UTF-8", null)
                                                    } else {
                                                        // Style the HTML Video player to look beautifully styled like native YouTube
                                                        val rawVideoUrl = selectedLessonVideoId
                                                        val customHtml = """
                                                            <!DOCTYPE html>
                                                            <html>
                                                            <head>
                                                                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                                                                <style>
                                                                    body { margin: 0; padding: 0; background-color: #000; overflow: hidden; display: flex; align-items: center; justify-content: center; height: 100vh; }
                                                                    video { width: 100%; height: 100%; object-fit: contain; background: #000; }
                                                                </style>
                                                            </head>
                                                            <body>
                                                                <video src="$rawVideoUrl" controls autoplay playsinline loop style="width:100%; height:100%;"></video>
                                                            </body>
                                                            </html>
                                                        """.trimIndent()
                                                        loadDataWithBaseURL("https://www.youtube.com", customHtml, "text/html", "UTF-8", null)
                                                    }
                                                }
                                            } catch (t: Throwable) {
                                                android.util.Log.e("SmartXAppUI", "WebView setup failed: ${t.message}")
                                                webViewLoadError = true
                                                android.view.View(ctx)
                                            }
                                        },
                                        update = { /* No-op */ },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFF0F172A))
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SmartDisplay,
                                            contentDescription = null,
                                            tint = Color(0xFFF43F5E),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = if (language == "AMH") "ቪዲዮውን በቀጥታ ይጫወቱ!" else "Watch securely on YouTube App!",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = if (language == "AMH") "የተገደበ ቪዲዮዎችን ያለ ምንም እንከን በቀጥታ ለማጫወት ከታች ያለውን ይንኩ" else "Some formats require launching YouTube directly. Tap below to solve this securely.",
                                            color = Color.LightGray.copy(alpha = 0.8f),
                                            fontSize = 11.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = {
                                                try {
                                                    val url = if (selectedLessonVideoId.length == 11) {
                                                        "https://www.youtube.com/watch?v=$selectedLessonVideoId"
                                                    } else {
                                                        selectedLessonVideoId
                                                    }
                                                    val webIntent = android.content.Intent(
                                                        android.content.Intent.ACTION_VIEW,
                                                        android.net.Uri.parse(url)
                                                    )
                                                    context.startActivity(webIntent)
                                                } catch (e: Exception) {
                                                    android.util.Log.e("SmartXAppUI", "Intent action launch error: ${e.message}")
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (language == "AMH") "በቀጥታ ክፈት" else "Play on YouTube App",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            // Clean visual-only floating circular close control (not a text view / no title / no description)
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.TopEnd)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.62f))
                                    .clickable { isPlayingVideo = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Player",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Today's Lessons Section (Premium Carousel, replacing the static Welcome banner & Social buttons!)
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == "AMH") "የዛሬ ትምህርቶች" else "Today's Lessons",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textColor,
                            letterSpacing = (-0.3).sp
                        )
                        
                        Text(
                            text = if (language == "AMH") "ሁሉንም አሳይ" else "See All",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5ECF),
                            modifier = Modifier
                                .clickable {
                                    selectedLessonVideoId = extractYoutubeVideoId("https://youtu.be/FRjnr4UAhNk?si=WK9mMpbtEnCKVlXs")
                                    selectedLessonTitle = if (language == "AMH") "አጠቃላይ የትምህርት መገልገያ" else "All-Subject Study Guide"
                                    isPlayingVideo = true
                                }
                        )
                    }
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(lessons) { lesson ->
                            LessonCard(
                                lesson = lesson,
                                isDarkMode = isDarkMode,
                                language = language,
                                onWatchClick = {
                                    selectedLessonVideoId = lesson.videoId
                                    selectedLessonTitle = lesson.title
                                    isPlayingVideo = true
                                }
                            )
                        }
                    }
                }
            }
            
            // Explore Your Grade Section Header
            item {
                Column(modifier = Modifier.padding(bottom = 12.dp, top = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Loc.t("explore_grade", language), 
                            fontSize = 22.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            color = textColor,
                            letterSpacing = (-0.5).sp,
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Deluxe Waving Hand Animation
                        val waveTransition = rememberInfiniteTransition(label = "wave_avatar_transition")
                        val waveRotation by waveTransition.animateFloat(
                            initialValue = -16f,
                            targetValue = 16f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 550, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "wave_hand_angle"
                        )
                        
                        Text(
                            text = "👋",
                            fontSize = 28.sp,
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .graphicsLayer {
                                    rotationZ = waveRotation
                                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.7f, 1f)
                                }
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Loc.t("select_grade_desc", language), 
                        fontSize = 13.sp, 
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // High Fidelity Smart-Adapting Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val fallbackIcons = mapOf(
                        9 to Icons.Default.MenuBook,
                        10 to Icons.Default.Science,
                        11 to Icons.Default.Calculate,
                        12 to Icons.Default.School
                    )
                    val fallbackColors = listOf(Color(0xFF3B82F6), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFF8B5CF6))
                    val columns = if (isTablet) 4 else 2
                    val rows = displayGrades.chunked(columns)
                    
                    rows.forEach { rowGrades ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowGrades.forEachIndexed { idx, grade ->
                                val icon = fallbackIcons[grade] ?: Icons.Default.School
                                val color = fallbackColors[grade % fallbackColors.size]
                                val progress = 0.20f + (grade * 0.05f)
                                
                                GradeCard(
                                    grade = grade, color = color, icon = icon, 
                                    subtitle = Loc.t("grade_${grade}_subtitle", language), progress = progress, language = language,
                                    modifier = Modifier.weight(1f), cardColor = cardColor, textColor = textColor
                                ) { viewModel.selectGrade(grade) }
                            }
                            // Fill remaining empty space in Row if the last row isn't full
                            if (rowGrades.size < columns) {
                                repeat(columns - rowGrades.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

        }

        if (showGestureGuide) {
            TikTokGestureGuideOverlay(
                onDismiss = {
                    showGestureGuide = false
                    prefs.edit().putBoolean("show_swipe_up_guide", false).apply()
                }
            )
        }
    }
}

@Composable
fun GradeCard(
    grade: Int, 
    color: Color, 
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    subtitle: String, 
    progress: Float,
    language: String,
    modifier: Modifier = Modifier, 
    cardColor: Color, 
    textColor: Color, 
    onClick: () -> Unit
) {
    // Elegant scale animations for tactile tap feedback
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "grade_scale_tap"
    )

    // Breathing pulse scale transition for prominent full space button
    val infiniteTransition = rememberInfiniteTransition(label = "btn_pulse_transition")
    val buttonPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btn_pulse_scale"
    )

    // Premium entrance cascade delay animation
    var visibleState by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val delayMillis = maxOf(0L, (grade - 9) * 80L)
        kotlinx.coroutines.delay(delayMillis)
        visibleState = true
    }
    
    val entranceAlpha by animateFloatAsState(
        targetValue = if (visibleState) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing),
        label = "grade_entrance_alpha"
    )
    val entranceTranslationY by animateFloatAsState(
        targetValue = if (visibleState) 0f else 30f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "grade_entrance_trans_y"
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                alpha = entranceAlpha
                translationY = entranceTranslationY
            }
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.5.dp, color.copy(alpha = 0.18f)),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPressed) 1.5.dp else 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 12.dp, end = 12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Beautiful Compact Gradient Cover Badge
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(color.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                            .border(1.5.dp, color.copy(alpha = 0.28f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Grade Icon",
                            tint = color,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text(
                        text = if (language == "AMH") "${grade}ኛ ክፍል" else "Grade $grade",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor,
                        letterSpacing = (-0.3).sp,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Clean linear progress track showing status subtly without numbers
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(CircleShape),
                    color = color,
                    trackColor = color.copy(alpha = 0.12f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Full width, premium action button covering the bottom part of the box exactly
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(buttonPulseScale)
                    .background(color, RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
                    .clickable { onClick() }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (language == "AMH") "ትምህርት ጀምር" else "Start Learn",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private data class LocalSubjectTheme(
    val primaryBg: Color,
    val secondaryBg: Color,
    val textColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun UnitOptionsScreen(
    viewModel: QuizViewModel,
    onBack: () -> Unit
) {
    val selectedGrade by viewModel.selectedGrade.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val selectedUnit by viewModel.selectedUnit.collectAsState()
    
    // Theme setup based on subject
    val theme = remember(selectedSubject) {
        when (selectedSubject?.trim()?.lowercase()) {
            "biology" -> LocalSubjectTheme(Color(0xFF10B981), Color(0xFFECFDF5), Color(0xFF065F46), Icons.Default.Spa)
            "chemistry" -> LocalSubjectTheme(Color(0xFFF59E0B), Color(0xFFFFFBEB), Color(0xFF92400E), Icons.Default.Science)
            "mathematics", "maths", "math" -> LocalSubjectTheme(Color(0xFF3B82F6), Color(0xFFEFF6FF), Color(0xFF1E40AF), Icons.Default.Calculate)
            "physics" -> LocalSubjectTheme(Color(0xFF8B5CF6), Color(0xFFF5F3FF), Color(0xFF5B21B6), Icons.Default.Bolt)
            "english" -> LocalSubjectTheme(Color(0xFFEC4899), Color(0xFFFDF2F8), Color(0xFF9D174D), Icons.Default.Translate)
            "civics" -> LocalSubjectTheme(Color(0xFF14B8A6), Color(0xFFF0FDFA), Color(0xFF0F766E), Icons.Default.Gavel)
            "geography" -> LocalSubjectTheme(Color(0xFF06B6D4), Color(0xFFECFEFF), Color(0xFF0891B2), Icons.Default.Public)
            "history" -> LocalSubjectTheme(Color(0xFFEF4444), Color(0xFFFEF2F2), Color(0xFF991B1B), Icons.Default.AutoStories)
            else -> LocalSubjectTheme(Color(0xFF6366F1), Color(0xFFEEF2FF), Color(0xFF3730A3), Icons.Default.Book)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = theme.secondaryBg
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = theme.icon,
                    contentDescription = null,
                    tint = theme.primaryBg,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = selectedUnit ?: "Curriculum Unit",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F172A),
            textAlign = TextAlign.Center,
            letterSpacing = (-0.5).sp
        )
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Text(
            text = "Grade $selectedGrade Curriculum • $selectedSubject",
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(42.dp))
        
        // Start Online / Play Quiz Button
        Button(
            onClick = { viewModel.startActiveQuiz() },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.primaryBg,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Start Online", fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Download Quiz / Offline cache button
        OutlinedButton(
            onClick = { /* Simulated local download and SQL persistence check */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF475569)
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download Quiz", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(36.dp))
        
        TextButton(onClick = onBack) {
            Text("Back to units", color = theme.primaryBg, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

// ==========================================
// INTERACTIVE 8-SUBJECT CURRICULUM PREVIEW HUB
// ==========================================

data class PreviewSubjectInfo(
    val name: String,
    val amharicName: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color,
    val desc: String,
    val amhDesc: String
)

@Composable
fun SubjectPreviewHubSection(
    viewModel: QuizViewModel,
    isDarkMode: Boolean,
    language: String,
    cardColor: Color,
    textColor: Color
) {
    val subjects = remember {
        listOf(
            PreviewSubjectInfo("Mathematics", "ሒሳብ", Icons.Default.Calculate, Color(0xFF3B82F6), Color(0xFFEFF6FF), "Algebra, calculus, geometry, and formulas", "አልጀብራ፣ ካልኩለስ፣ ጂኦሜትሪ እና ቀመሮች"),
            PreviewSubjectInfo("Biology", "ባዮሎጂ", Icons.Default.Spa, Color(0xFF10B981), Color(0xFFECFDF5), "Cell biology, ecology, and human anatomy", "የሴል ባዮሎጂ፣ ሥነ-ምህዳር እና የሰው አካል ጥናት"),
            PreviewSubjectInfo("Chemistry", "ኬሚስትሪ", Icons.Default.Science, Color(0xFFF59E0B), Color(0xFFFFFBEB), "Chemical bonds, reactions, atoms, and compounds", "ኬሚካዊ ትስስሮች፣ ግብረመልሶች፣ አተሞች እና ውህዶች"),
            PreviewSubjectInfo("Physics", "ፊዚክስ", Icons.Default.Bolt, Color(0xFF8B5CF6), Color(0xFFF5F3FF), "Forces, motion, energy, and electronics", "ጉልበት፣ እንቅስቃሴ፣ ኃይል እና ኤሌክትሮኒክስ"),
            PreviewSubjectInfo("English", "እንግሊዝኛ", Icons.Default.Translate, Color(0xFFEC4899), Color(0xFFFDF2F8), "Grammar, tenses, passive voice, and vocabulary", "ሰዋስው፣ ተገብሮ ድምጽ እና የቃላት አጠቃቀም"),
            PreviewSubjectInfo("Civics", "ስነ-ዜጋ", Icons.Default.Gavel, Color(0xFF14B8A6), Color(0xFFF0FDFA), "FDRE Constitution, democracy, rights, and responsibilities", "የኢፌዴሪ ሕገ-መንግሥት፣ ዲሞክራሲ፣ መብቶች እና ኃላፊነት"),
            PreviewSubjectInfo("Geography", "ጂኦግራፊ", Icons.Default.Public, Color(0xFF06B6D4), Color(0xFFECFEFF), "Physical landscapes, climate, map reading, and ecosystems", "ተፈጥሯዊ ገጽታ፣ የአየር ንብረት፣ የካርታ ንባብ እና ሥነ-ምህዳሮች"),
            PreviewSubjectInfo("History", "ታሪክ", Icons.Default.AutoStories, Color(0xFFEF4444), Color(0xFFFEF2F2), "Ethiopian history, ancient civilizations, and global events", "የኢትዮጵያ ታሪክ፣ የጥንት ስልጣኔዎች እና ጦርነቶች")
        )
    }

    var selectedPreviewSubject by remember { mutableStateOf("Biology") }
    val activeSub = remember(selectedPreviewSubject) {
        subjects.find { it.name == selectedPreviewSubject } ?: subjects.first()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        border = BorderStroke(1.5.dp, activeSub.primaryColor.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                 modifier = Modifier.fillMaxWidth(),
                 verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(activeSub.primaryColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = activeSub.icon,
                        contentDescription = null,
                        tint = activeSub.primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (language == "AMH") "የትምህርት ምርጫ ቅድመ-ዕይታ" else "Interactive Subject Preview Hub",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor
                    )
                    Text(
                        text = if (language == "AMH") "ፈጣን ምስል፣ ትምህርታዊ መረጃዎች እና ፈተናዎች" else "Curriculum walkthroughs & instant practice launchers",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal row with 8 custom subject descriptors/icons
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(subjects) { sub ->
                    val isSelected = sub.name == selectedPreviewSubject
                    val circleBg = if (isSelected) sub.primaryColor else if (isDarkMode) Color(0xFF334155) else Color(0xFFF1F5F9)
                    val iconColor = if (isSelected) Color.White else if (isDarkMode) sub.primaryColor else sub.primaryColor.copy(alpha = 0.85f)
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(82.dp)
                            .clickable(
                                onClick = { selectedPreviewSubject = sub.name },
                                interactionSource = remember { MutableInteractionSource() },
                                indication = androidx.compose.foundation.LocalIndication.current
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(circleBg)
                                .then(
                                    if (isSelected) Modifier.border(2.dp, Color.White, CircleShape)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = sub.icon,
                                contentDescription = sub.name,
                                tint = iconColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (language == "AMH") sub.amharicName else sub.name,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) sub.primaryColor else textColor,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Body preview info panel
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkMode) Color(0xFF0F172A) else activeSub.secondaryColor.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, activeSub.primaryColor.copy(alpha = 0.12f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = activeSub.icon,
                                contentDescription = null,
                                tint = activeSub.primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == "AMH") "${activeSub.amharicName} (ስርዓተ-ትምህርት)" else "${activeSub.name} Details",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = activeSub.primaryColor
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "8 Fallback Sets",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == "AMH") activeSub.amhDesc else activeSub.desc,
                        fontSize = 13.sp,
                        color = if (isDarkMode) Color.LightGray else Color(0xFF334155),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dynamic Sample Question display
                    val sampleQuestion = remember(selectedPreviewSubject) {
                        QuestionBank.questions.firstOrNull { it.subject.lowercase() == selectedPreviewSubject.lowercase() }
                    }

                    if (sampleQuestion != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDarkMode) Color(0xFF1E293B) else Color.White)
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LiveHelp,
                                    contentDescription = null,
                                    tint = activeSub.primaryColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == "AMH") "ናሙና ፈተና (ክፍል ${sampleQuestion.grade})" else "SAMPLE QUESTION (Grade ${sampleQuestion.grade})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = activeSub.primaryColor,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = sampleQuestion.questionText,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Render answers as simple non-clickable pills, green background for correct index
                            sampleQuestion.options.forEachIndexed { i, opt ->
                                val isCorrectOption = i == sampleQuestion.correctAnswerIndex
                                val rowBg = if (isCorrectOption) Color(0xFFDCFCE7) else Color.Transparent
                                val rowBorder = if (isCorrectOption) Color(0xFF4ADE80) else if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
                                val labelColor = if (isCorrectOption) Color(0xFF166534) else textColor
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .border(1.dp, rowBorder, RoundedCornerShape(8.dp))
                                        .background(rowBg, RoundedCornerShape(8.dp))
                                        .padding(vertical = 6.dp, horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(if (isCorrectOption) Color(0xFF22C55E) else Color.Gray.copy(alpha = 0.4f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = ('A' + i).toString(),
                                            color = Color.White,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = opt,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isCorrectOption) FontWeight.Bold else FontWeight.Medium,
                                        color = labelColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1-Click launcher options (decreased size and weight balance)
            Text(
                text = if (language == "AMH") "የክፍል ፈተናዎች ፈጣን መምረጫ፦" else "Select grade to launch exam:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(9, 10, 11, 12).forEach { grade ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(width = 68.dp, height = 30.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(activeSub.primaryColor.copy(alpha = 0.12f))
                            .border(1.dp, activeSub.primaryColor.copy(alpha = 0.25f), RoundedCornerShape(15.dp))
                            .clickable {
                                viewModel.selectGrade(grade)
                                viewModel.selectSubject(activeSub.name)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = activeSub.primaryColor,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == "AMH") "ክፍል $grade" else "G-$grade",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = activeSub.primaryColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TikTokGestureGuideOverlay(
    onDismiss: () -> Unit
) {
    // Smooth infinite transition for fallback Compose floating vector
    val infiniteTransition = rememberInfiniteTransition(label = "gesture_swipe")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 70f,
        targetValue = -70f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hand_offset"
    )

    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hand_pulse"
    )

    // Load Lottie composition with a well-known beautiful hand-swipe Lottie JSON stream
    val compositionResult = rememberLottieComposition(
        spec = LottieCompositionSpec.Url("https://lottie.host/e2c608cb-6395-4428-98e3-0b04aa80f55d/kYOn5SRE8E.json")
    )
    val lottieProgress by animateLottieCompositionAsState(
        composition = compositionResult.value,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f))
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.changes.any { it.pressed || !it.pressed }) {
                            onDismiss()
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                // If Lottie loads successfully, show Lottie animation
                if (compositionResult.value != null && !compositionResult.isLoading) {
                    LottieAnimation(
                        composition = compositionResult.value,
                        progress = { lottieProgress },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Fail gracefully to high-fidelity custom Compose-drawn swipe indicator
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Swipe path line trail
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(160.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.05f),
                                            Color(0xFF3B82F6).copy(alpha = 0.6f),
                                            Color(0xFF60A5FA).copy(alpha = 0.1f)
                                        )
                                    )
                                )
                        )

                        // Floating dynamic arrow
                        Icon(
                            imageVector = Icons.Default.KeyboardDoubleArrowUp,
                            contentDescription = null,
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier
                                .size(36.dp)
                                .offset(y = offsetY.dp)
                                .scale(scalePulse)
                        )

                        // Hand gesture icon (pulsing and moving)
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = "Swipe up gesture guide",
                            tint = Color.White,
                            modifier = Modifier
                                .size(64.dp)
                                .offset(y = (offsetY + 36f).dp)
                                .scale(scalePulse)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Help messages mimicking TikTok visual typography with bold accents
            Text(
                text = "Swipe Up to Explore More Lessons",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "እባክዎን ተጨማሪ የትምህርት ክፍሎችን ለማየት ወደ ላይ ያንሸራትቱ",
                color = Color.LightGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(42.dp))

            // Sub-hint indicating touch to close
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Tap or Swipe anywhere to dismiss",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

