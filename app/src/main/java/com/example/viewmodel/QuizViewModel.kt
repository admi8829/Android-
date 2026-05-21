package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BookmarkedQuestion
import com.example.data.Question
import com.example.data.QuizHistory
import com.example.data.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {

    // Current Navigation/Flow State
    private val _selectedGrade = MutableStateFlow<Int?>(null)
    val selectedGrade: StateFlow<Int?> = _selectedGrade.asStateFlow()

    private val _selectedSubject = MutableStateFlow<String?>(null)
    val selectedSubject: StateFlow<String?> = _selectedSubject.asStateFlow()

    // Active Quiz Playthrough State
    private val _activeQuestions = MutableStateFlow<List<Question>>(emptyList())
    val activeQuestions: StateFlow<List<Question>> = _activeQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
    val selectedAnswerIndex: StateFlow<Int?> = _selectedAnswerIndex.asStateFlow()

    private val _isAnswered = MutableStateFlow(false)
    val isAnswered: StateFlow<Boolean> = _isAnswered.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _isQuizFinished = MutableStateFlow(false)
    val isQuizFinished: StateFlow<Boolean> = _isQuizFinished.asStateFlow()

    // Room DB Reactive Feeds
    val bookmarks: StateFlow<List<BookmarkedQuestion>> = repository.allBookmarks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val history: StateFlow<List<QuizHistory>> = repository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Check if current active question is bookmarked
    val isCurrentQuestionBookmarked: StateFlow<Boolean> = combine(
        _activeQuestions,
        _currentQuestionIndex,
        repository.allBookmarks
    ) { questions, index, bookmarksList ->
        if (questions.isEmpty() || index >= questions.size) false
        else bookmarksList.any { it.id == questions[index].id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Grade and Subject Actions
    fun selectGrade(grade: Int?) {
        _selectedGrade.value = grade
        _selectedSubject.value = null // Reset subject
    }

    fun selectSubject(subject: String?) {
        _selectedSubject.value = subject
        if (subject != null && _selectedGrade.value != null) {
            startQuiz(_selectedGrade.value!!, subject)
        }
    }

    // Quiz Control Mechanisms
    private fun startQuiz(grade: Int, subject: String) {
        val quizQs = repository.getQuestions(grade, subject).shuffled()
        _activeQuestions.value = quizQs
        _currentQuestionIndex.value = 0
        _selectedAnswerIndex.value = null
        _isAnswered.value = false
        _score.value = 0
        _isQuizFinished.value = false
    }

    fun selectAnswer(optionIndex: Int) {
        if (_isAnswered.value) return // Disable selection change after submission
        _selectedAnswerIndex.value = optionIndex
    }

    fun submitAnswer() {
        if (_isAnswered.value || _selectedAnswerIndex.value == null) return
        _isAnswered.value = true
        
        val currentQ = _activeQuestions.value.getOrNull(_currentQuestionIndex.value)
        if (currentQ != null && _selectedAnswerIndex.value == currentQ.correctAnswerIndex) {
            _score.value += 1
        }
    }

    fun nextQuestion() {
        val nextIdx = _currentQuestionIndex.value + 1
        if (nextIdx < _activeQuestions.value.size) {
            _currentQuestionIndex.value = nextIdx
            _selectedAnswerIndex.value = null
            _isAnswered.value = false
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        _isQuizFinished.value = true
        val grade = _selectedGrade.value ?: return
        val subject = _selectedSubject.value ?: return
        val finalScore = _score.value
        val total = _activeQuestions.value.size

        viewModelScope.launch {
            repository.saveQuizHistory(grade, subject, finalScore, total)
        }
    }

    fun toggleBookmark() {
        val questionsList = _activeQuestions.value
        val currentIndex = _currentQuestionIndex.value
        if (questionsList.isEmpty() || currentIndex >= questionsList.size) return
        val currentQ = questionsList[currentIndex]

        viewModelScope.launch {
            val isCurrentlySaved = bookmarks.value.any { it.id == currentQ.id }
            if (isCurrentlySaved) {
                repository.removeBookmarkById(currentQ.id)
            } else {
                repository.addBookmark(currentQ)
            }
        }
    }

    fun removeBookmarkDirectly(questionId: String) {
        viewModelScope.launch {
            repository.removeBookmarkById(questionId)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun resetToHome() {
        _selectedGrade.value = null
        _selectedSubject.value = null
        _activeQuestions.value = emptyList()
        _currentQuestionIndex.value = 0
        _selectedAnswerIndex.value = null
        _isAnswered.value = false
        _score.value = 0
        _isQuizFinished.value = false
    }

    // Subjects getter
    fun getSubjectsForSelectedGrade(): List<String> {
        val grade = _selectedGrade.value ?: return emptyList()
        return repository.getSubjectsForGrade(grade)
    }
}

class QuizViewModelFactory(private val repository: QuizRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
