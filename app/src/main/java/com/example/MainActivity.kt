package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.AppDatabase
import com.example.data.QuizRepository
import com.example.data.BookmarkDao
import com.example.data.QuizHistoryDao
import com.example.ui.SmartXAppUI
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.QuizViewModel
import com.example.viewmodel.QuizViewModelFactory
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            val jsCacheDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/js")
            val wasmCacheDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/wasm")
            if (!jsCacheDir.exists()) jsCacheDir.mkdirs()
            if (!wasmCacheDir.exists()) wasmCacheDir.mkdirs()
        } catch (e: Exception) {
            // Ignore
        }
        
        Thread.setDefaultUncaughtExceptionHandler(MyUncaughtExceptionHandler())
        
        // Ensure safe MobileAds initialization
        // Force reload marker
        try {
            MobileAds.initialize(this) {}
        } catch (e: Throwable) {
            android.util.Log.e("MainActivity", "MobileAds initialization failed safely: ${e.message}")
        }

        // Set up local Room database, repository and ViewModel
        val database = try {
            AppDatabase.getDatabase(applicationContext)
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Database initialization failed: ${e.message}", e)
            null
        }

        // Create repository safely
        val repository = try {
            if (database != null) {
                QuizRepository(
                    bookmarkDao = database.bookmarkDao(),
                    quizHistoryDao = database.quizHistoryDao()
                )
            } else null
        } catch (e: Throwable) {
            android.util.Log.e("MainActivity", "DAO initialization failed, falling back to mock: ${e.message}", e)
            null
        }
        
        // Create Supabase repository safely
        val supabaseRepository = try {
            if (database != null) {
                com.example.data.SupabaseQuizRepository(applicationContext, database)
            } else null
        } catch (e: Throwable) {
            android.util.Log.e("MainActivity", "SupabaseQuizRepository initialization failed safely: ${e.message}", e)
            null
        }
        
        // Let's create a fail-proof model factory
        val customFactory = repository?.let { QuizViewModelFactory(applicationContext, it, supabaseRepository) }
        val defaultFactory = QuizViewModelFactory(
            applicationContext,
            QuizRepository(
                bookmarkDao = object : BookmarkDao {
                    override fun getAllBookmarks() = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.BookmarkedQuestion>())
                    override fun isBookmarked(id: String) = kotlinx.coroutines.flow.flowOf(false)
                    override suspend fun insertBookmark(bookmark: com.example.data.BookmarkedQuestion) {}
                    override suspend fun deleteBookmark(bookmark: com.example.data.BookmarkedQuestion) {}
                    override suspend fun deleteBookmarkById(id: String) {}
                },
                quizHistoryDao = object : QuizHistoryDao {
                    override fun getAllHistory() = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.QuizHistory>())
                    override fun getHistoryForSubject(grade: Int, subject: String) = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.QuizHistory>())
                    override suspend fun insertHistory(history: com.example.data.QuizHistory) {}
                    override suspend fun clearAllHistory() {}
                }
            ),
            supabaseRepository
        )
        
        val factory = customFactory ?: defaultFactory

        // Create view model using factory
        val viewModel = androidx.lifecycle.ViewModelProvider(this, factory)[QuizViewModel::class.java]

        if (intent?.getBooleanExtra("open_notifications", false) == true) {
            val title = intent.getStringExtra("notification_title")
            val body = intent.getStringExtra("notification_body")
            viewModel.triggerAnnouncements(title, body)
        }

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SmartXAppUI(viewModel = viewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra("open_notifications", false)) {
            val database = try {
                AppDatabase.getDatabase(applicationContext)
            } catch (e: Exception) { null }
            val repository = if (database != null) QuizRepository(database.bookmarkDao(), database.quizHistoryDao()) else null
            val supabaseRepository = if (database != null) com.example.data.SupabaseQuizRepository(applicationContext, database) else null
            
            val factory = if (repository != null) {
                QuizViewModelFactory(applicationContext, repository, supabaseRepository)
            } else {
                QuizViewModelFactory(applicationContext, QuizRepository(object : BookmarkDao {
                    override fun getAllBookmarks() = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.BookmarkedQuestion>())
                    override fun isBookmarked(id: String) = kotlinx.coroutines.flow.flowOf(false)
                    override suspend fun insertBookmark(bookmark: com.example.data.BookmarkedQuestion) {}
                    override suspend fun deleteBookmark(bookmark: com.example.data.BookmarkedQuestion) {}
                    override suspend fun deleteBookmarkById(id: String) {}
                }, object : QuizHistoryDao {
                    override fun getAllHistory() = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.QuizHistory>())
                    override fun getHistoryForSubject(grade: Int, subject: String) = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.QuizHistory>())
                    override suspend fun insertHistory(history: com.example.data.QuizHistory) {}
                    override suspend fun clearAllHistory() {}
                }), supabaseRepository)
            }
            val viewModel = androidx.lifecycle.ViewModelProvider(this, factory)[QuizViewModel::class.java]
            val title = intent.getStringExtra("notification_title")
            val body = intent.getStringExtra("notification_body")
            viewModel.triggerAnnouncements(title, body)
        }
    }
}
