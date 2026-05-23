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
            "watch_tutorial" to "Watch tutorial: Getting started with the Smart X Academy App.",
            "explore_grade" to "Explore Your Grade",
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
            "watch_tutorial" to "የመማሪያ ቪዲዮ: በስማርት ኤክስ አካዳሚ እንዴት እንደሚጀመር ይመልከቱ።",
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
        var animateIn by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            animateIn = true
            kotlinx.coroutines.delay(2000)
            showSplashScreen = false
        }
        
        val scale by animateFloatAsState(
            targetValue = if (animateIn) 1.0f else 0.82f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "splash_scale"
        )
        val alpha by animateFloatAsState(
            targetValue = if (animateIn) 1.0f else 0.0f,
            animationSpec = tween(durationMillis = 800),
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
                    when {
                        selectedSubject != null && selectedUnit != null -> {
                            if (isQuizFinished) {
                                ScoreScreen(viewModel = viewModel)
                            } else {
                                PlayQuizScreen(viewModel = viewModel)
                            }
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
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Feature Video Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
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
                        // Real inline WebView YouTube Embed with full lifecycle safety
                        AndroidView(
                            factory = { ctx ->
                                try {
                                    android.webkit.WebView(ctx).apply {
                                        // Disable hardware acceleration to bypass native graphics driver crashes in emulators
                                        setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
                                        settings.javaScriptEnabled = true
                                        settings.mediaPlaybackRequiresUserGesture = false
                                        settings.domStorageEnabled = true
                                        webChromeClient = android.webkit.WebChromeClient()
                                        webViewClient = android.webkit.WebViewClient()
                                        loadDataWithBaseURL(
                                            "https://www.youtube.com",
                                            """
                                            <html>
                                            <body style="margin:0;padding:0;background:#000;">
                                                <iframe width="100%" height="100%" src="https://www.youtube.com/embed/FRjnr4UAhNk?autoplay=1" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen style="position:fixed; top:0; left:0; bottom:0; right:0; width:100%; height:100%; border:none; margin:0; padding:0; overflow:hidden; z-index:999999;"></iframe>
                                            </body>
                                            </html>
                                            """.trimIndent(),
                                            "text/html",
                                            "utf-8",
                                            null
                                        )
                                    }
                                } catch (e: Throwable) {
                                    android.util.Log.e("SmartXAppUI", "WebView factory init crashed: ${e.message}", e)
                                    // Set state safely on the main thread loop
                                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                                        webViewLoadError = true
                                    }
                                    // Return plain empty view as safe placeholder fallback
                                    android.view.View(ctx)
                                }
                            },
                            update = { /* No-op */ },
                            onRelease = { view ->
                                try {
                                    if (view is android.webkit.WebView) {
                                        view.stopLoading()
                                        view.destroy()
                                    }
                                } catch (e: Throwable) {
                                    android.util.Log.e("SmartXAppUI", "WebView release crashed: ${e.message}", e)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(148.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } else if (isPlayingVideo && webViewLoadError) {
                        // Fallback Simulated Interactive Video Player to bypass emulator webview errors gracefully
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(148.dp)
                                .clip(RoundedCornerShape(12.dp))
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
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (language == "AMH") "የማስተማሪያ ቪዲዮው በመጫወት ላይ ነው..." else "Tutorial Video Is Successfully Playing...",
                                    color = Color.White,
                                    fontSize = 13.sp,
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
                                Spacer(modifier = Modifier.height(10.dp))
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(1.5.dp)),
                                    color = Color(0xFFF43F5E),
                                    trackColor = Color.White.copy(alpha = 0.15f)
                                )
                            }
                        }
                    } else {
                        // Video thumbnail clickable (plays AdMob test ad monetization first)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(148.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE2E8F0))
                                .clickable {
                                    showingVideoAd = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = Loc.t("welcome", language), 
                                fontSize = 15.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(12.dp)
                            )
                            Icon(
                                Icons.Default.PlayCircleFilled,
                                contentDescription = "Play",
                                tint = Color.Red,
                                modifier = Modifier.size(56.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = Loc.t("watch_tutorial", language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkMode) Color.LightGray else Color.DarkGray
                    )
                }
            }
        }
        
        // Explore Your Grade Section
        item {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(Loc.t("explore_grade", language), fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
                Spacer(modifier = Modifier.height(3.dp))
                Text(Loc.t("select_grade_desc", language), fontSize = 13.sp, color = Color.Gray)
            }
        }

        // Animated Grades Grid with Progress Indicators
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    GradeCard(
                        grade = 9, color = Color(0xFF3B82F6), icon = Icons.Default.MenuBook, 
                        subtitle = Loc.t("grade_9_subtitle", language), progress = 0.35f, language = language,
                        modifier = Modifier.weight(1f), cardColor = cardColor, textColor = textColor
                    ) { viewModel.selectGrade(9) }
                    GradeCard(
                        grade = 10, color = Color(0xFF10B981), icon = Icons.Default.Science, 
                        subtitle = Loc.t("grade_10_subtitle", language), progress = 0.55f, language = language,
                        modifier = Modifier.weight(1f), cardColor = cardColor, textColor = textColor
                    ) { viewModel.selectGrade(10) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    GradeCard(
                        grade = 11, color = Color(0xFFF59E0B), icon = Icons.Default.Calculate, 
                        subtitle = Loc.t("grade_11_subtitle", language), progress = 0.20f, language = language,
                        modifier = Modifier.weight(1f), cardColor = cardColor, textColor = textColor
                    ) { viewModel.selectGrade(11) }
                    GradeCard(
                        grade = 12, color = Color(0xFF8B5CF6), icon = Icons.Default.School, 
                        subtitle = Loc.t("grade_12_subtitle", language), progress = 0.85f, language = language,
                        modifier = Modifier.weight(1f), cardColor = cardColor, textColor = textColor
                    ) { viewModel.selectGrade(12) }
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
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Card(
        modifier = modifier
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, if (isPressed) color.copy(alpha = 0.5f) else Color.Transparent)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(40.dp).background(color, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${Loc.t("explore_grade", language)} $grade", 
                fontSize = 15.sp, 
                fontWeight = FontWeight.ExtraBold, 
                color = textColor
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subtitle, 
                fontSize = 11.sp, 
                color = Color.Gray, 
                lineHeight = 14.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(6.dp))
            // Progress tracker bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(CircleShape),
                color = color,
                trackColor = color.copy(alpha = 0.15f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .align(Alignment.CenterHorizontally)
                    .height(34.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = color,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                Text(
                    text = if (language == "AMH") "ትምህርት ጀምር ➔" else "Start Learn ➔",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
