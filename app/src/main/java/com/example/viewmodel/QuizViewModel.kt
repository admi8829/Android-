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

data class UserProfile(
    val name: String = "",
    val school: String = "",
    val phone: String = "",
    val email: String = "",
    val isRegistered: Boolean = false,
    val sex: String = "Male",
    val password: String = "",
    val grade: Int = 9,
    val difficultSubject: String = "Physics",
    val easySubject: String = "Mathematics",
    val avatarId: String = "avatar_1"
)

class QuizViewModel(
    private val context: android.content.Context,
    private val repository: QuizRepository,
    private val supabaseRepository: SupabaseQuizRepository? = null
) : ViewModel() {

    // User profile registration state
    private val _userProfile = MutableStateFlow<UserProfile>(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _showAnnouncements = MutableStateFlow(false)
    val showAnnouncements = _showAnnouncements.asStateFlow()
    
    private val _announcementTitle = MutableStateFlow<String?>(null)
    val announcementTitle = _announcementTitle.asStateFlow()
    
    private val _announcementBody = MutableStateFlow<String?>(null)
    val announcementBody = _announcementBody.asStateFlow()

    fun triggerAnnouncements(title: String? = null, body: String? = null) {
        _announcementTitle.value = title
        _announcementBody.value = body
        _showAnnouncements.value = true
    }
    
    fun dismissAnnouncements() { 
        _showAnnouncements.value = false 
        _announcementTitle.value = null
        _announcementBody.value = null
    }

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val prefs = context.getSharedPreferences("user_profile_prefs", android.content.Context.MODE_PRIVATE)
        val name = prefs.getString("name", "") ?: ""
        val school = prefs.getString("school", "") ?: ""
        val phone = prefs.getString("phone", "") ?: ""
        val email = prefs.getString("email", "") ?: ""
        val registered = prefs.getBoolean("is_registered", false)
        val sex = prefs.getString("sex", "Male") ?: "Male"
        val password = prefs.getString("password", "") ?: ""
        val grade = prefs.getInt("grade", 9)
        val diffSubject = prefs.getString("difficult_subject", "Physics") ?: "Physics"
        val easySubject = prefs.getString("easy_subject", "Mathematics") ?: "Mathematics"
        val avatarId = prefs.getString("avatar_id", "avatar_1") ?: "avatar_1"
        _userProfile.value = UserProfile(
            name = name,
            school = school,
            phone = phone,
            email = email,
            isRegistered = registered,
            sex = sex,
            password = password,
            grade = grade,
            difficultSubject = diffSubject,
            easySubject = easySubject,
            avatarId = avatarId
        )
    }

    fun registerUser(
        name: String,
        school: String,
        phone: String,
        email: String,
        sex: String = "Male",
        password: String = "1234",
        grade: Int = 9,
        difficultSubject: String = "Physics",
        easySubject: String = "English",
        avatarId: String = "avatar_1",
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val req = com.example.data.RegistrationRequest(
                    name = name,
                    grade = grade,
                    school = school,
                    phone = phone,
                    email = email,
                    sex = sex,
                    password = password,
                    difficult_subject = difficultSubject,
                    easy_subject = easySubject
                )
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    try {
                        com.example.data.RetrofitClient.instance.postRegistration(
                            request = req,
                            apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNxcmdxa2N6ZW1veGdjcGRscGluIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk1NjA1NTksImV4cCI6MjA5NTEzNjU1OX0.er1aduQ8-Yx9IxobDiDB4LadrET7xhSXVnVThRy0u_k",
                            authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNxcmdxa2N6ZW1veGdjcGRscGluIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk1NjA1NTksImV4cCI6MjA5NTEzNjU1OX0.er1aduQ8-Yx9IxobDiDB4LadrET7xhSXVnVThRy0u_k"
                        )
                    } catch (e: Exception) {
                        android.util.Log.e("QuizViewModel", "Failed posting registration online: ${e.message}")
                    }
                }
                
                val prefs = context.getSharedPreferences("user_profile_prefs", android.content.Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putString("name", name)
                    putString("school", school)
                    putString("phone", phone)
                    putString("email", email)
                    putString("sex", sex)
                    putString("password", password)
                    putInt("grade", grade)
                    putString("difficult_subject", difficultSubject)
                    putString("easy_subject", easySubject)
                    putString("avatar_id", avatarId)
                    putBoolean("is_registered", true)
                }.apply()
                
                _userProfile.value = UserProfile(
                    name = name,
                    school = school,
                    phone = phone,
                    email = email,
                    isRegistered = true,
                    sex = sex,
                    password = password,
                    grade = grade,
                    difficultSubject = difficultSubject,
                    easySubject = easySubject,
                    avatarId = avatarId
                )
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    fun loginUser(email: String, password: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val prefs = context.getSharedPreferences("user_profile_prefs", android.content.Context.MODE_PRIVATE)
                val storedEmail = prefs.getString("email", "") ?: ""
                val storedPassword = prefs.getString("password", "") ?: ""
                
                if (storedEmail.isNotEmpty() && storedEmail.equals(email, ignoreCase = true) && storedPassword == password) {
                    prefs.edit().putBoolean("is_registered", true).apply()
                    loadUserProfile()
                    onComplete(true)
                } else if (storedEmail.isEmpty() || email.isNotEmpty()) {
                    val cleanName = email.substringBefore("@").replace(".", " ").capitalize()
                    prefs.edit().apply {
                        putString("name", cleanName)
                        putString("school", "High School")
                        putString("phone", "+251911000000")
                        putString("email", email)
                        putString("sex", "Male")
                        putString("password", password)
                        putInt("grade", 9)
                        putString("difficult_subject", "Physics")
                        putString("easy_subject", "Mathematics")
                        putString("avatar_id", "avatar_1")
                        putBoolean("is_registered", true)
                    }.apply()
                    loadUserProfile()
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    fun updateUserAvatar(avatarId: String) {
        val prefs = context.getSharedPreferences("user_profile_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putString("avatar_id", avatarId).apply()
        _userProfile.value = _userProfile.value.copy(avatarId = avatarId)
    }

    fun isUnit1OfAllSubjectsDownloadedOrPracticed(): Boolean {
        val subjects = getSubjectsForSelectedGrade()
        val downloaded = downloadedUnitsList.value
        val grade = selectedGrade.value ?: 9
        if (subjects.isEmpty()) return true
        for (subj in subjects) {
            val hasUnit1 = downloaded.any { tuple ->
                tuple.grade == grade && 
                tuple.subject.trim().lowercase() == subj.trim().lowercase() && 
                tuple.unit.trim().lowercase().contains("unit 1")
            }
            if (!hasUnit1) {
                return false
            }
        }
        return true
    }

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

    private val _downloadedUnits = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val downloadedUnits: StateFlow<Map<String, Boolean>> = _downloadedUnits.asStateFlow()

    fun isUnitDownloaded(unit: String): Boolean {
        return _downloadedUnits.value[unit] ?: false
    }

    fun downloadUnitQuestions(grade: Int, subject: String, unit: String, onComplete: () -> Unit) {
        if (supabaseRepository == null) return
        viewModelScope.launch {
            supabaseRepository.downloadQuestionsToOffline(grade, subject, unit)
            _downloadedUnits.value = _downloadedUnits.value.toMutableMap().apply { put(unit, true) }
            onComplete()
        }
    }

    fun checkDownloadedStatus(grade: Int, subject: String, units: List<String>) {
        if (supabaseRepository == null) return
        viewModelScope.launch {
            val statusMap = mutableMapOf<String, Boolean>()
            units.forEach { unit ->
                statusMap[unit] = supabaseRepository.isUnitDownloaded(grade, subject, unit)
            }
            _downloadedUnits.value = statusMap
        }
    }
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

    val downloadedUnitsList: StateFlow<List<com.example.data.DownloadedUnitTuple>> = (supabaseRepository?.getAllDownloadedUnitsFlow() ?: kotlinx.coroutines.flow.flowOf(emptyList()))
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
        _selectedUnit.value = null
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
    private val context: android.content.Context,
    private val repository: QuizRepository,
    private val supabaseRepository: SupabaseQuizRepository? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(context, repository, supabaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
