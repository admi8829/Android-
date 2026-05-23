package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BookmarkedQuestion
import com.example.data.Question
import com.example.data.QuizHistory
import com.example.data.QuizRepository
import com.example.data.FirestoreQuizRepository
import com.example.data.FirestoreSubject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QuizViewModel(
    private val repository: QuizRepository,
    private val firestoreRepository: FirestoreQuizRepository? = null
) : ViewModel() {

    // Firestore Integration State Flows
    private val _firestoreSubjects = MutableStateFlow<List<FirestoreSubject>>(emptyList())
    val firestoreSubjects: StateFlow<List<FirestoreSubject>> = _firestoreSubjects.asStateFlow()

    private val _isFirestoreLoading = MutableStateFlow(false)
    val isFirestoreLoading: StateFlow<Boolean> = _isFirestoreLoading.asStateFlow()

    private val _firestoreError = MutableStateFlow<String?>(null)
    val firestoreError: StateFlow<String?> = _firestoreError.asStateFlow()

    private val _firestoreCacheHit = MutableStateFlow<Boolean?>(null)
    val firestoreCacheHit: StateFlow<Boolean?> = _firestoreCacheHit.asStateFlow()

    // Current Navigation/Flow State
    private val _selectedGrade = MutableStateFlow<Int?>(null)
    val selectedGrade: StateFlow<Int?> = _selectedGrade.asStateFlow()

    private val _selectedSubject = MutableStateFlow<String?>(null)
    val selectedSubject: StateFlow<String?> = _selectedSubject.asStateFlow()

    private val _selectedUnit = MutableStateFlow<String?>(null)
    val selectedUnit: StateFlow<String?> = _selectedUnit.asStateFlow()

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
        _selectedUnit.value = null // Reset unit
        _firestoreError.value = null
        _firestoreSubjects.value = emptyList()
        _firestoreCacheHit.value = null
        
        if (grade != null) {
            loadFirestoreSubjects(grade)
        }
    }

    fun loadFirestoreSubjects(grade: Int) {
        val firestore = firestoreRepository ?: return
        viewModelScope.launch {
            _isFirestoreLoading.value = true
            _firestoreError.value = null
            try {
                // Record cached state before retrieval for user feedback
                val isCached = firestore.isGradeDataLoaded(grade)
                _firestoreCacheHit.value = isCached
                
                val subjects = firestore.fetchSubjectsAndUnits(grade)
                _firestoreSubjects.value = subjects
                if (subjects.isEmpty()) {
                    _firestoreError.value = "No dynamic elements found in Firestore for Grade $grade."
                }
            } catch (e: Exception) {
                _firestoreError.value = e.message ?: "Failed to fetch Grade $grade. Verify connection."
            } finally {
                _isFirestoreLoading.value = false
            }
        }
    }

    fun selectSubject(subject: String?) {
        _selectedSubject.value = subject
        _selectedUnit.value = null // Reset unit when clicking back or changing subject
    }

    fun selectUnit(unit: String?) {
        _selectedUnit.value = unit
        val subject = _selectedSubject.value
        val grade = _selectedGrade.value
        if (unit != null && subject != null && grade != null) {
            startQuiz(grade, subject, unit)
        }
    }

    // Quiz Control Mechanisms
    private fun startQuiz(grade: Int, subject: String, unit: String? = null) {
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

class QuizViewModelFactory(
    private val repository: QuizRepository,
    private val firestoreRepository: FirestoreQuizRepository? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(repository, firestoreRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
