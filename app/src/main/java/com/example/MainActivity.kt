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
import com.example.ui.QuizAppUI
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.QuizViewModel
import com.example.viewmodel.QuizViewModelFactory
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize the Google Mobile Ads SDK (AdMob)
        try {
            MobileAds.initialize(this) {}
        } catch (e: Throwable) {
            android.util.Log.e("MainActivity", "AdMob initialization failed: ${e.message}", e)
        }

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
                    bookmarkDao = AppDatabase.getDatabase(applicationContext).bookmarkDao(),
                    quizHistoryDao = AppDatabase.getDatabase(applicationContext).quizHistoryDao()
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
                    QuizAppUI(viewModel = viewModel)
                }
            }
        }
    }
}
