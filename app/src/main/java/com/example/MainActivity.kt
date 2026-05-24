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
        
        Thread.setDefaultUncaughtExceptionHandler(MyUncaughtExceptionHandler())
        
        // Ensure safe MobileAds initialization
        try {
            MobileAds.initialize(this) {}
        } catch (e: Throwable) {
            android.util.Log.e("MainActivity", "MobileAds initialization failed safely: ${e.message}")
        }

        // Request notifications runtime permission for Android 13+ devices
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
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
            com.example.data.SupabaseQuizRepository(applicationContext)
        } catch (e: Throwable) {
            android.util.Log.e("MainActivity", "SupabaseQuizRepository initialization failed safely: ${e.message}", e)
            null
        }
        
        // Let's create a fail-proof model factory
        val customFactory = repository?.let { QuizViewModelFactory(it, supabaseRepository) }
        val defaultFactory = QuizViewModelFactory(
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
}
