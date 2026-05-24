package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import kotlinx.coroutines.launch

// Dynamic Localization Vocabulary
object Loc {
    fun t(key: String, lang: String): String {
        val en = mapOf(
            "app_name" to "Smart X Academy",
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
            "profile" to "Profile",
            "settings" to "Settings",
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
            "profile" to "መገለጫ",
            "settings" to "ቅንብሮች",
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
        // Modal navigation drawer
        ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            ) {
                // Header layout for Drawer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1B5ECF), Color(0xFF0F46A4))
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = Loc.t("app_name", language),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Grades 9–12 Q&A Hub",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Menu items
                NavigationDrawerItem(
                    label = { Text(Loc.t("menu_dev", language), fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        activeDialog = "dev"
                    },
                    icon = { Icon(Icons.Default.Business, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                NavigationDrawerItem(
                    label = { Text(Loc.t("menu_privacy", language), fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        activeDialog = "privacy"
                    },
                    icon = { Icon(Icons.Default.Security, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                NavigationDrawerItem(
                    label = { Text(Loc.t("menu_about", language), fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        activeDialog = "about"
                    },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
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
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text(Loc.t("profile", language)) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF1B5ECF), selectedTextColor = Color(0xFF1B5ECF))
        )
        NavigationBarItem(
            selected = currentScreen == "settings",
            onClick = { onScreenSelected("settings") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text(Loc.t("settings", language)) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF1B5ECF), selectedTextColor = Color(0xFF1B5ECF))
        )
    }
}

@Composable
fun SmartXHomeScreen(viewModel: QuizViewModel, isDarkMode: Boolean, language: String) {
    val cardColor = if (isDarkMode) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    var isPlayingVideo by remember { mutableStateOf(false) }
    var showingVideoAd by remember { mutableStateOf(false) }
    var chosenLauncherGrade by remember { mutableStateOf(9) }
    
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
    var webViewLoadError by remember { mutableStateOf(isEmulator) }
    
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
            // Feature Video Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val context = LocalContext.current
                        if (showingVideoAd) {
                            // Display the interactive AdMob video test ad unit overlay
                            AdMobVideoPreRollAd(
                                onAdCompleted = {
                                    showingVideoAd = false
                                    isPlayingVideo = true
                                }
                            )
                        } else if (isPlayingVideo && !webViewLoadError) {
                            // Real Native pierfrancescosoffritti YouTube Player
                            AndroidView(
                                factory = { ctx ->
                                    try {
                                        com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView(ctx).apply {
                                            addYouTubePlayerListener(object : com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener() {
                                                override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                                                    val videoId = "FRjnr4UAhNk" // Smart X Academy tutorial
                                                    youTubePlayer.cueVideo(videoId, 0f)
                                                }
                                            })
                                        }
                                    } catch (e: Throwable) {
                                        android.util.Log.e("SmartXAppUI", "Native YouTube player failed, falling back safely: ${e.message}", e)
                                        android.view.View(ctx)
                                    }
                                },
                                update = { /* No-op */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        } else if (isPlayingVideo && webViewLoadError) {
                            // Fallback Simulated Interactive Video Player to bypass emulator webview errors gracefully
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF0F172A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SmartDisplay,
                                        contentDescription = null,
                                        tint = Color(0xFFF43F5E),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (language == "AMH") "የማስተማሪያ ቪዲዮው በመጫወት ላይ ነው..." else "Tutorial Video Is Successfully Playing...",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = if (language == "AMH") "አካዳሚክ ማብራሪያዎች እና ምርጥ የፈተና አሰራር ዘዴዎች" else "Academic walkthroughs & strategy guides designed for success",
                                        color = Color.LightGray.copy(alpha = 0.8f),
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    LinearProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth(0.5f)
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = Color(0xFFF43F5E),
                                        trackColor = Color.White.copy(alpha = 0.15f)
                                    )
                                }
                            }
                        } else {
                            // Video thumbnail clickable
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                                        )
                                    )
                                    .clickable {
                                        showingVideoAd = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = Loc.t("welcome", language), 
                                        fontSize = 16.sp, 
                                        fontWeight = FontWeight.Black, 
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                    Icon(
                                        Icons.Default.PlayCircleFilled,
                                        contentDescription = "Play",
                                        tint = Color(0xFFF43F5E),
                                        modifier = Modifier.size(64.dp)
                                    )
                                }
                            }
                        }
                        
                        // SOCIAL BUTTONS: Join Telegram (Animated) & YouTube Channel (Themed)
                        Spacer(modifier = Modifier.height(12.dp))
                        val infiniteTransition = rememberInfiniteTransition(label = "social_effects")
                        
                        // Ultra-smooth scaling pulse for Telegram
                        val teleScale by infiniteTransition.animateFloat(
                            initialValue = 1.0f,
                            targetValue = 1.04f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(900, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "tele_scale"
                        )
                        
                        // Subtle playful rotation wiggle
                        val teleRotate by infiniteTransition.animateFloat(
                            initialValue = -4f,
                            targetValue = 4f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(750, easing = LinearOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "tele_rotate"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Telegram CTA Card (with premium glowing blue action)
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .graphicsLayer {
                                        scaleX = teleScale
                                        scaleY = teleScale
                                    }
                                    .clickable {
                                        try {
                                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                                data = android.net.Uri.parse("https://t.me/smartxacademy")
                                            }
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            android.widget.Toast.makeText(context, "Telegram link: t.me/smartxacademy", android.widget.Toast.LENGTH_LONG).show()
                                        }
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDarkMode) Color(0xFF0369A1).copy(alpha = 0.25f) else Color(0xFFE0F2FE)
                                ),
                                border = BorderStroke(1.5.dp, Color(0xFF0EA5E9))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Telegram Link",
                                        tint = Color(0xFF0EA5E9),
                                        modifier = Modifier
                                            .size(18.dp)
                                            .graphicsLayer {
                                                rotationZ = -30f + teleRotate
                                            }
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (language == "AMH") "ቴሌግራም ይቀላቀሉ" else "Join Telegram",
                                        color = if (isDarkMode) Color(0xFFBAE6FD) else Color(0xFF0369A1),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            // YouTube Card
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        try {
                                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                                data = android.net.Uri.parse("https://youtube.com/@smartxacademy")
                                            }
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            android.widget.Toast.makeText(context, "YouTube: @smartxacademy", android.widget.Toast.LENGTH_LONG).show()
                                        }
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDarkMode) Color(0xFF991B1B).copy(alpha = 0.2f) else Color(0xFFFEE2E2)
                                ),
                                border = BorderStroke(1.5.dp, Color(0xFFEF4444))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "YouTube Link",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (language == "AMH") "ዩቲዩብ ቻናል" else "YouTube Channel",
                                        color = if (isDarkMode) Color(0xFFFCA5A5) else Color(0xFFB91C1C),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Explore Your Grade Section Header
            item {
                Column(modifier = Modifier.padding(bottom = 12.dp, top = 4.dp)) {
                    Text(
                        text = Loc.t("explore_grade", language), 
                        fontSize = 22.sp, 
                        fontWeight = FontWeight.ExtraBold, 
                        color = textColor,
                        letterSpacing = (-0.5).sp
                    )
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
                    val fallbackIcons = listOf(Icons.Default.MenuBook, Icons.Default.Science, Icons.Default.Calculate, Icons.Default.School)
                    val fallbackColors = listOf(Color(0xFF3B82F6), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFF8B5CF6))
                    val columns = if (isTablet) 4 else 2
                    val rows = displayGrades.chunked(columns)
                    
                    rows.forEach { rowGrades ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowGrades.forEachIndexed { idx, grade ->
                                val icon = fallbackIcons[grade % fallbackIcons.size]
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
    var isPressed by remember { mutableStateOf(false) }
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
        kotlinx.coroutines.delay((grade - 9) * 80L)
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
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.5.dp, color.copy(alpha = 0.18f)),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPressed) 1.5.dp else 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Beautiful Gradient Cover Badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(color.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                        .border(1.5.dp, color.copy(alpha = 0.28f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Grade Icon",
                        tint = color,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                
                Text(
                    text = if (language == "AMH") "${grade}ኛ ክፍል" else "Grade $grade",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor,
                    letterSpacing = (-0.3).sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            // Clean linear progress track showing status subtly without numbers
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = color,
                trackColor = color.copy(alpha = 0.12f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Vibrant, high-visibility, full wide length button to guide user
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(buttonPulseScale)
                    .height(42.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = color,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (language == "AMH") "ትምህርት ጀምር" else "Start Learn",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
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
            "biology" -> LocalSubjectTheme(Color(0xFF10B981), Color(0xFFECFDF5), Color(0xFF065F46), Icons.Default.Book)
            "chemistry" -> LocalSubjectTheme(Color(0xFFF59E0B), Color(0xFFFFFBEB), Color(0xFF92400E), Icons.Default.Class)
            "mathematics", "maths", "math" -> LocalSubjectTheme(Color(0xFF3B82F6), Color(0xFFEFF6FF), Color(0xFF1E40AF), Icons.Default.School)
            "physics" -> LocalSubjectTheme(Color(0xFF8B5CF6), Color(0xFFF5F3FF), Color(0xFF5B21B6), Icons.Default.Refresh)
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
