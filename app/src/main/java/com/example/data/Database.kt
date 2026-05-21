package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "bookmarks")
data class BookmarkedQuestion(
    @PrimaryKey val id: String,
    val grade: Int,
    val subject: String,
    val questionText: String,
    val optionsSerialized: String, // joined by "|||"
    val correctAnswerIndex: Int,
    val explanation: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toQuestion(): Question {
        return Question(
            id = id,
            grade = grade,
            subject = subject,
            questionText = questionText,
            options = optionsSerialized.split("|||"),
            correctAnswerIndex = correctAnswerIndex,
            explanation = explanation
        )
    }

    companion object {
        fun fromQuestion(q: Question): BookmarkedQuestion {
            return BookmarkedQuestion(
                id = q.id,
                grade = q.grade,
                subject = q.subject,
                questionText = q.questionText,
                optionsSerialized = q.options.joinToString("|||"),
                correctAnswerIndex = q.correctAnswerIndex,
                explanation = q.explanation
            )
        }
    }
}

@Entity(tableName = "quiz_history")
data class QuizHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val grade: Int,
    val subject: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Float,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkedQuestion>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :id LIMIT 1)")
    fun isBookmarked(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkedQuestion)

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkedQuestion)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: String)
}

@Dao
interface QuizHistoryDao {
    @Query("SELECT * FROM quiz_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<QuizHistory>>

    @Query("SELECT * FROM quiz_history WHERE grade = :grade AND subject = :subject ORDER BY timestamp DESC")
    fun getHistoryForSubject(grade: Int, subject: String): Flow<List<QuizHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: QuizHistory)

    @Query("DELETE FROM quiz_history")
    suspend fun clearAllHistory()
}

@Database(entities = [BookmarkedQuestion::class, QuizHistory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun quizHistoryDao(): QuizHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ethioquiz_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
