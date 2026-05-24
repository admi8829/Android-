package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BookmarkedQuestion
import com.example.data.Question
import com.example.data.QuizHistory
import com.example.data.QuizRepository
import com.example.data.SupabaseQuizRepository
import com.example.data.SupabaseSubject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QuizViewModel(
    private val repository: QuizRepository,
    private val supabaseRepository: SupabaseQuizRepository? = null
) : ViewModel() {

    // Supabase Integration State Flows
    private val _supabaseGrades = MutableStateFlow<List<Int>>(emptyList())
    val supabaseGrades: StateFlow<List<Int>> = _supabaseGrades.asStateFlow()

    private val _supabaseSubjects = MutableStateFlow<List<SupabaseSubject>>(emptyList())
    val supabaseSubjects: StateFlow<List<SupabaseSubject>> = _supabaseSubjects.asStateFlow()

    private val _isSupabaseLoading = MutableStateFlow(false)
    val isSupabaseLoading: StateFlow<Boolean> = _isSupabaseLoading.asStateFlow()

    private val _supabaseError = MutableStateFlow<String?>(null)
    val supabaseError: StateFlow<String?> = _supabaseError.asStateFlow()

    private val _supabaseCacheHit = MutableStateFlow<Boolean?>(null)
    val supabaseCacheHit: StateFlow<Boolean?> = _supabaseCacheHit.asStateFlow()

    // Current Navigation/Flow State
    private val _selectedGrade = MutableStateFlow<Int?>(null)
    val selectedGrade: StateFlow<Int?> = _selectedGrade.asStateFlow()

    private val _selectedSubject = MutableStateFlow<String?>(null)
    val selectedSubject: StateFlow<String?> = _selectedSubject.asStateFlow()

    private val _selectedUnit = MutableStateFlow<String?>(null)
    val selectedUnit: StateFlow<String?> = _selectedUnit.asStateFlow()

    private val _isQuizActive = MutableStateFlow(false)
    val isQuizActive: StateFlow<Boolean> = _isQuizActive.asStateFlow()

    private val _timerSeconds = MutableStateFlow(30)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

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
    init {
        loadSupabaseGrades()
    }

    fun loadSupabaseGrades() {
        val supabase = supabaseRepository ?: return
        viewModelScope.launch {
            _isSupabaseLoading.value = true
            try {
                val grades = supabase.fetchGrades()
                _supabaseGrades.value = grades
            } catch (e: Throwable) {
                // Ignore, using defaults
            } finally {
                _isSupabaseLoading.value = false
            }
        }
    }

    fun selectGrade(grade: Int?) {
        _selectedGrade.value = grade
        _selectedSubject.value = null // Reset subject
        _selectedUnit.value = null // Reset unit
        _isQuizActive.value = false
        _supabaseError.value = null
        _supabaseSubjects.value = emptyList()
        _supabaseCacheHit.value = null
        
        if (grade != null) {
            loadSupabaseSubjects(grade)
        }
    }

    fun loadSupabaseSubjects(grade: Int) {
        val supabase = supabaseRepository ?: return
        viewModelScope.launch {
            _isSupabaseLoading.value = true
            _supabaseError.value = null
            try {
                // Record cached state before retrieval for user feedback
                val isCached = supabase.isGradeDataLoaded(grade)
                _supabaseCacheHit.value = isCached
                
                val subjects = supabase.fetchSubjectsAndUnits(grade)
                _supabaseSubjects.value = subjects
                if (subjects.isEmpty()) {
                    _supabaseError.value = "No dynamic elements found in Supabase for Grade $grade."
                }
            } catch (e: Throwable) {
                _supabaseError.value = "Supabase Fetch Error: ${e.message}"
            } finally {
                _isSupabaseLoading.value = false
            }
        }
    }

    fun selectSubject(subject: String?) {
        _selectedSubject.value = subject
        _selectedUnit.value = null // Reset unit when clicking back or changing subject
        _isQuizActive.value = false
    }

    fun selectUnit(unit: String?) {
        _selectedUnit.value = unit
        _isQuizActive.value = false // Select but do not start play sequence yet
    }

    fun startActiveQuiz() {
        val grade = _selectedGrade.value ?: return
        val subject = _selectedSubject.value ?: return
        val unit = _selectedUnit.value ?: return
        _isQuizActive.value = true
        _isQuizFinished.value = false
        _currentQuestionIndex.value = 0
        _selectedAnswerIndex.value = null
        _isAnswered.value = false
        _score.value = 0
        _activeQuestions.value = emptyList() // clear and trigger loading screen state
        
        // Reset and start timer
        _timerSeconds.value = 30
        
        viewModelScope.launch {
            _isSupabaseLoading.value = true
            _supabaseError.value = null
            try {
                val fetchedQs = supabaseRepository?.fetchQuestions(grade, subject, unit)
                if (fetchedQs != null && fetchedQs.isNotEmpty()) {
                    _activeQuestions.value = fetchedQs.shuffled()
                    startTimer()
                } else {
                    _supabaseError.value = "Supabase questions are unpopulated or unreached for $subject Unit: $unit. Check your 'questions' table."
                }
            } catch (e: Throwable) {
                _supabaseError.value = "Supabase Fetch Error: ${e.message ?: "Failed questions fetch"}"
            } finally {
                _isSupabaseLoading.value = false
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (_isQuizActive.value && !_isQuizFinished.value && _timerSeconds.value > 0) {
                kotlinx.coroutines.delay(1000)
                _timerSeconds.value -= 1
            }
            if (_timerSeconds.value == 0 && !_isAnswered.value) {
                submitAnswer()
            }
        }
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
            _timerSeconds.value = 30 // Reset timer for next question!
        } else {
            finishQuiz()
        }
    }

    fun prevQuestion() {
        val prevIdx = _currentQuestionIndex.value - 1
        if (prevIdx >= 0) {
            _currentQuestionIndex.value = prevIdx
            _selectedAnswerIndex.value = null
            _isAnswered.value = false
            _timerSeconds.value = 30
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
        _selectedUnit.value = null
        _activeQuestions.value = emptyList()
        _currentQuestionIndex.value = 0
        _selectedAnswerIndex.value = null
        _isAnswered.value = false
        _score.value = 0
        _isQuizFinished.value = false
        _isQuizActive.value = false
        _supabaseError.value = null
    }

    // Subjects getter
    fun getSubjectsForSelectedGrade(): List<String> {
        val grade = _selectedGrade.value ?: return emptyList()
        return repository.getSubjectsForGrade(grade)
    }
}

class QuizViewModelFactory(
    private val repository: QuizRepository,
    private val supabaseRepository: SupabaseQuizRepository? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(repository, supabaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
