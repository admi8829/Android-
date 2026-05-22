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
        
        // AdMob initialization removed for emulator stability
        try {
            // Disabled
        } catch (e: Throwable) { }

        // Set up local Room database, repository and ViewModel
        val database = try {
            AppDatabase.getDatabase(applicationContext)
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Database initialization failed: ${e.message}", e)
            null
        }

        // Create repository safely
        val repository = if (database != null) {
            QuizRepository(
                bookmarkDao = database.bookmarkDao(),
                quizHistoryDao = database.quizHistoryDao()
            )
        } else {
            null
        }
        
        // Let's create a fail-proof model factory
        val viewModelFactory = repository?.let { QuizViewModelFactory(it) }

        // Delegate view model using factory
        val viewModel: QuizViewModel by viewModels { 
            viewModelFactory ?: QuizViewModelFactory(
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
                )
            )
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
}
