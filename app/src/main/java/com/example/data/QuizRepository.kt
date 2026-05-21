package com.example.data

import kotlinx.coroutines.flow.Flow

class QuizRepository(
    private val bookmarkDao: BookmarkDao,
    private val quizHistoryDao: QuizHistoryDao
) {
    val allBookmarks: Flow<List<BookmarkedQuestion>> = bookmarkDao.getAllBookmarks()
    val allHistory: Flow<List<QuizHistory>> = quizHistoryDao.getAllHistory()

    fun isBookmarked(id: String): Flow<Boolean> = bookmarkDao.isBookmarked(id)

    suspend fun addBookmark(question: Question) {
        bookmarkDao.insertBookmark(BookmarkedQuestion.fromQuestion(question))
    }

    suspend fun removeBookmarkById(id: String) {
        bookmarkDao.deleteBookmarkById(id)
    }

    suspend fun saveQuizHistory(grade: Int, subject: String, score: Int, total: Int) {
        val percentage = if (total > 0) (score.toFloat() / total * 100) else 0f
        quizHistoryDao.insertHistory(
            QuizHistory(
                grade = grade,
                subject = subject,
                score = score,
                totalQuestions = total,
                percentage = percentage
            )
        )
    }

    suspend fun clearHistory() {
        quizHistoryDao.clearAllHistory()
    }

    fun getQuestions(grade: Int, subject: String): List<Question> {
        return QuestionBank.getQuestions(grade, subject)
    }

    fun getSubjectsForGrade(grade: Int): List<String> {
        return QuestionBank.getSubjectsForGrade(grade)
    }
}
