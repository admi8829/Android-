package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.net.URLEncoder

/**
 * Subject schema used to transition data models seamlessly.
 */
data class SupabaseSubject(
    val id: String = "",
    val name: String = "",
    val units: List<String> = emptyList()
)

/**
 * SupabaseQuizRepository handles loading dynamic curriculum elements
 * from future Supabase tables (grades, subjects, units, questions).
 */
class SupabaseQuizRepository(private val context: Context) {

    companion object {
        private const val TAG = "SupabaseQuizRepository"
        
        // Official production Supabase URL and Anon Key
        private const val SUPABASE_URL = "https://cqrgqkczemoxgcpdlpin.supabase.co"
        private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNxcmdxa2N6ZW1veGdjcGRscGluIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk1NjA1NTksImV4cCI6MjA5NTEzNjU1OX0.er1aduQ8-Yx9IxobDiDB4LadrET7xhSXVnVThRy0u_k"
    }

    private val httpClient = OkHttpClient()

    init {
        // Clear any local cache on app launch to force the application to download the fresh rows we just inserted into our live Supabase project.
        try {
            val prefs = context.getSharedPreferences("supabase_quiz_prefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            Log.i(TAG, "Successfully cleared local Supabase caches on app launch.")
        } catch (e: Throwable) {
            Log.e(TAG, "Error clearing local cache prefs: ${e.message}")
        }
    }

    /**
     * Checks if grade cached data is loaded.
     */
    fun isGradeDataLoaded(grade: Int): Boolean {
        val prefs = context.getSharedPreferences("supabase_quiz_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("grade_${grade}_loaded", false)
    }

    private fun setGradeDataLoaded(grade: Int, loaded: Boolean) {
        val prefs = context.getSharedPreferences("supabase_quiz_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("grade_${grade}_loaded", loaded).apply()
    }

    /**
     * Fetches dynamic list of grades
     */
    suspend fun fetchGrades(): List<Int> = withContext(Dispatchers.IO) {
        try {
            val fetched = fetchGradesViaRest()
            if (fetched.isNotEmpty()) return@withContext fetched
        } catch (e: Throwable) {
            Log.w(TAG, "Grades REST fetch had issues: ${e.message}. Using simulated grades.")
        }
        return@withContext listOf(9, 10, 11, 12)
    }

    private fun fetchGradesViaRest(): List<Int> {
        val baseUrl = SUPABASE_URL
        if (baseUrl.contains("your-project-placeholder")) return emptyList()
        val url = "$baseUrl/rest/v1/grades?select=grade"
        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", SUPABASE_ANON_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_ANON_KEY")
            .build()
            
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return emptyList()
            val bodyString = response.body?.string() ?: return emptyList()
            try {
                val jsonArray = JSONArray(bodyString)
                val grades = mutableListOf<Int>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    if (obj.has("grade")) {
                        grades.add(obj.optInt("grade", 9))
                    }
                }
                return grades.sorted()
            } catch (e: Exception) {
                return emptyList()
            }
        }
    }

    /**
     * Fetches dynamic subjects and units for the requested grade.
     */
    suspend fun fetchSubjectsAndUnits(grade: Int): List<SupabaseSubject> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Fetching dynamic subjects and units from Supabase for Grade $grade...")
        
        // 1. Direct REST request to Supabase Postgrest endpoint
        try {
            val fetched = fetchViaRest(grade)
            if (fetched.isNotEmpty()) {
                setGradeDataLoaded(grade, true)
                Log.i(TAG, "Successfully fetched $grade curriculum from Supabase REST.")
                return@withContext fetched
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Direct REST fetch had issues: ${e.message}. Attempting SDK queries...")
        }

        // 2. Querying Supabase via the official Postgrest client SDK
        // Removed to prevent NoClassDefFoundError. We rely safely on OkHttp raw queries.

        // 3. Fallback simulated academic database if database contains no rows or is offline
        Log.i(TAG, "Supabase unconfigured / unpopulated. Returning beautiful dynamic curriculum simulated state...")
        val fallback = getSimulatedSupabaseCurriculum(grade)
        return@withContext fallback
    }

    /**
     * REST query to Postgrest endpoint of future 'subjects' or 'grades' table.
     */
    private fun fetchViaRest(grade: Int): List<SupabaseSubject> {
        val baseUrl = SUPABASE_URL
        if (baseUrl.contains("your-project-placeholder")) {
            return emptyList()
        }
        
        val url = "$baseUrl/rest/v1/subjects?grade=eq.$grade&select=id,name,units"
        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", SUPABASE_ANON_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_ANON_KEY")
            .build()
            
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.w(TAG, "Supabase REST error: ${response.code}")
                return emptyList()
            }
            val bodyString = response.body?.string() ?: return emptyList()
            try {
                val jsonArray = JSONArray(bodyString)
                val subjects = mutableListOf<SupabaseSubject>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val id = obj.optString("id", "")
                    val name = obj.optString("name", "")
                    val rawUnits = obj.optJSONArray("units")
                    val units = mutableListOf<String>()
                    if (rawUnits != null) {
                        for (j in 0 until rawUnits.length()) {
                            units.add(rawUnits.getString(j))
                        }
                    }
                    subjects.add(SupabaseSubject(id = id, name = name, units = units))
                }
                return subjects
            } catch (e: Exception) {
                Log.w(TAG, "Failed to parse subjects: ${e.message}")
                return emptyList()
            }
        }
    }

    /**
     * Fully localized subjects and units list matching curriculum for Grades 9-12.
     */
    private fun getSimulatedSupabaseCurriculum(grade: Int): List<SupabaseSubject> {
        return when (grade) {
            9 -> listOf(
                SupabaseSubject("math_9", "Mathematics", listOf("Unit 1: Number Systems", "Unit 2: Equations and Inequalities", "Unit 3: Geometry")),
                SupabaseSubject("chem_9", "Chemistry", listOf("Unit 1: Structure of the Atom", "Unit 2: Chemical Bonding", "Unit 3: Periodic Classification")),
                SupabaseSubject("bio_9", "Biology", listOf("Unit 1: Introduction to Biology", "Unit 2: Cell Biology", "Unit 3: Enzymes")),
                SupabaseSubject("phys_9", "Physics", listOf("Unit 1: Vectors", "Unit 2: One Dimensional Motion", "Unit 3: Force & Newton's Laws"))
            )
            10 -> listOf(
                SupabaseSubject("math_10", "Mathematics", listOf("Unit 1: Polynomial Functions", "Unit 2: Exponential and Logarithmic", "Unit 3: Trigonometry")),
                SupabaseSubject("chem_10", "Chemistry", listOf("Unit 1: Organic Chemistry", "Unit 2: Hydrocarbons", "Unit 3: Oxygen Containing")),
                SupabaseSubject("bio_10", "Biology", listOf("Unit 1: Biotechnology", "Unit 2: Ecology and Environment", "Unit 3: Human Biology")),
                SupabaseSubject("phys_10", "Physics", listOf("Unit 1: Electrostatics", "Unit 2: Current Electricity", "Unit 3: Electromagnetism"))
            )
            11 -> listOf(
                SupabaseSubject("math_11", "Mathematics", listOf("Unit 1: Sequences and Series", "Unit 2: Matrices", "Unit 3: Solid Geometry")),
                SupabaseSubject("chem_11", "Chemistry", listOf("Unit 1: Fundamental Concepts", "Unit 2: Atomic Structure", "Unit 3: Chemical Bonding")),
                SupabaseSubject("bio_11", "Biology", listOf("Unit 1: Biomolecules", "Unit 2: Cell Biology", "Unit 3: Genetics")),
                SupabaseSubject("phys_11", "Physics", listOf("Unit 1: Measurement", "Unit 2: Vector Quantities", "Unit 3: Kinematics"))
            )
            12 -> listOf(
                SupabaseSubject("math_12", "Mathematics", listOf("Unit 1: Limits and Continuity", "Unit 2: Differential Calculus", "Unit 3: Applications")),
                SupabaseSubject("chem_12", "Chemistry", listOf("Unit 1: Acid-Base Equilibria", "Unit 2: Electrochemistry", "Unit 3: Industrial")),
                SupabaseSubject("bio_12", "Biology", listOf("Unit 1: Genetics and Evolution", "Unit 2: Plant Anatomy", "Unit 3: Animal Anatomy")),
                SupabaseSubject("phys_12", "Physics", listOf("Unit 1: Fluid Mechanics", "Unit 2: Thermodynamics", "Unit 3: Oscillations"))
            )
            else -> emptyList()
        }
    }

    /**
     * Fetches dynamic quiz questions for the selected grade, subject and optional unit.
     */
    suspend fun fetchQuestions(grade: Int, subject: String, unit: String? = null): List<Question> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Fetching dynamic questions from Supabase for Grade $grade, Subject $subject, Unit $unit...")
        
        // 1. Try fetching from live Supabase DB REST endpoint
        try {
            val fetched = fetchQuestionsViaRest(grade, subject, unit)
            if (fetched.isNotEmpty()) {
                Log.i(TAG, "Successfully fetched ${fetched.size} questions from Supabase REST.")
                return@withContext fetched
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Direct REST questions fetch had issues: ${e.message}. Trying SDK select fallback...")
            throw e
        }

        // 2. Try official SDK query client for safety
        // Removed. Relying completely on OkHttp REST queries seamlessly.

        // 3. Robust fallback to built-in QuestionBank
        Log.i(TAG, "Supabase questions unpopulated / unreached. Falling back to local/cached academic database.")
        val localQuestions = QuestionBank.getQuestions(grade, subject)
        if (unit != null) {
            // Filter locally if unit matches any hint in explanation or text
            val unitFiltered = localQuestions.filter {
                it.explanation.contains(unit, ignoreCase = true) || it.questionText.contains(unit, ignoreCase = true)
            }
            if (unitFiltered.isNotEmpty()) return@withContext unitFiltered
        }
        return@withContext localQuestions
    }

    /**
     * Direct robust REST query to Supabase Postgrest endpoint of 'questions' table.
     */
    private fun fetchQuestionsViaRest(grade: Int, subject: String, unit: String? = null): List<Question> {
        val baseUrl = SUPABASE_URL
        if (baseUrl.contains("your-project-placeholder")) {
            return emptyList()
        }
        
        // Url-encode query filter to handle subjects with spaces elegantly (e.g. "Civics")
        val encodedSubject = URLEncoder.encode(subject, "UTF-8")
        var url = "$baseUrl/rest/v1/questions?grade=eq.$grade&subject=eq.$encodedSubject"
        if (unit != null) {
            val encodedUnit = URLEncoder.encode(unit, "UTF-8")
            url += "&unit=eq.$encodedUnit"
        }

        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", SUPABASE_ANON_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_ANON_KEY")
            .build()
            
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.w(TAG, "Supabase Questions REST error: ${response.code}")
                throw Exception("HTTP ${response.code}: ${response.message}")
            }
            val bodyString = response.body?.string() ?: return emptyList()
            try {
                val jsonArray = JSONArray(bodyString)
                val list = mutableListOf<Question>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    
                    val id = obj.optString("id", "")
                    val qGrade = obj.optInt("grade", grade)
                    val qSubject = obj.optString("subject", subject)
                    
                    // Flexible keys for question text
                    val questionText = when {
                        obj.has("questionText") -> obj.optString("questionText", "")
                        obj.has("question_text") -> obj.optString("question_text", "")
                        obj.has("question") -> obj.optString("question", "")
                        else -> ""
                    }
                    
                    // Parse options array or string representation
                    val options = mutableListOf<String>()
                    val rawOptions = obj.optJSONArray("options")
                    if (rawOptions != null) {
                        for (j in 0 until rawOptions.length()) {
                            options.add(rawOptions.getString(j))
                        }
                    } else {
                        val optStr = obj.optString("options", "")
                        if (optStr.isNotEmpty()) {
                            optStr.split(",").forEach { options.add(it.trim()) }
                        }
                    }
                    
                    // Flexible keys for correct answer index
                    val correctAnswerIndex = when {
                        obj.has("correctAnswerIndex") -> obj.optInt("correctAnswerIndex", 0)
                        obj.has("correct_answer_index") -> obj.optInt("correct_answer_index", 0)
                        obj.has("correctAnswer") -> obj.optInt("correctAnswer", 0)
                        obj.has("correct_answer") -> obj.optInt("correct_answer", 0)
                        obj.has("correct") -> obj.optInt("correct", 0)
                        else -> 0
                    }
                    
                    // Flexible keys for explanation
                    val explanation = when {
                        obj.has("explanation") -> obj.optString("explanation", "")
                        obj.has("explanation_text") -> obj.optString("explanation_text", "")
                        else -> ""
                    }
                    
                    list.add(
                        Question(
                            id = id.ifEmpty { "supabase_${grade}_${subject}_$i" },
                            grade = qGrade,
                            subject = qSubject,
                            questionText = questionText,
                            options = options,
                            correctAnswerIndex = correctAnswerIndex,
                            explanation = explanation
                        )
                    )
                }
                return list
            } catch (e: Exception) {
                Log.w(TAG, "Failed to parse questions: ${e.message}")
                throw Exception("JSON Parse Error: ${e.message}")
            }
        }
    }
}
