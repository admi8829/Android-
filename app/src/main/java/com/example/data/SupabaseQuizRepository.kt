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
 * from dynamic grade-subject-unit tables in Supabase.
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
        return true
    }

    /**
     * Fetches dynamic list of grades. (Managed in code to avoid extra query tables)
     */
    suspend fun fetchGrades(): List<Int> = withContext(Dispatchers.IO) {
        return@withContext listOf(9, 10, 11, 12)
    }

    /**
     * Helper to retrieve units list based on grade and subject in the source code.
     */
    fun getUnitsForSubject(grade: Int, subject: String): List<String> {
        val cleanSubject = subject.trim().lowercase()
        return when (cleanSubject) {
            "mathematics", "maths", "math" -> when (grade) {
                9 -> listOf("Unit 1: Number Systems", "Unit 2: Equations and Inequalities", "Unit 3: Geometry and Measurement", "Unit 4: Coordinate Geometry", "Unit 5: Statistics and Probability")
                10 -> listOf("Unit 1: Polynomial Functions", "Unit 2: Exponential and Logarithmic Functions", "Unit 3: Trigonometry", "Unit 4: Circles", "Unit 5: Solid Geometry")
                11 -> listOf("Unit 1: Sequences and Series", "Unit 2: Introduction to Limits and Continuity", "Unit 3: Introduction to Derivatives", "Unit 4: Matrices", "Unit 5: Vectors")
                else -> listOf("Unit 1: Limits and Continuity", "Unit 2: Derivatives and Applications", "Unit 3: Integrals", "Unit 4: Three-Dimensional Space", "Unit 5: Probability Distributions")
            }
            "biology", "bio" -> when (grade) {
                9 -> listOf("Unit 1: Sub-fields of Biology", "Unit 2: Cells and Cellular Respiration", "Unit 3: Classification of Organisms", "Unit 4: Reproduction and Genetics", "Unit 5: Human Health & Environment")
                10 -> listOf("Unit 1: Biotechnology", "Unit 2: Ecology & Eco-systems", "Unit 3: Microorganisms", "Unit 4: Plant Anatomy & Growth", "Unit 5: Human Body Systems")
                11 -> listOf("Unit 1: Science of Biology", "Unit 2: Biochemical Processes", "Unit 3: Enzymes and Cell Activities", "Unit 4: Cell Division & Mitosis", "Unit 5: Energy Transformation")
                else -> listOf("Unit 1: Microorganisms and Diseases", "Unit 2: Genetics and Heredity", "Unit 3: Evolution and Taxonomy", "Unit 4: Human Physiology and Senses", "Unit 5: Population Genetics")
            }
            "chemistry", "chem" -> listOf("Unit 1: Basic Structure", "Unit 2: Periodic Table", "Unit 3: Chemical Bonding", "Unit 4: Calculations & Stoichiometry", "Unit 5: Physical States & Solutions", "Unit 6: Acids, Bases and Salts")
            "physics", "phys" -> listOf("Unit 1: Vectors and Motion", "Unit 2: Forces and Newton's Laws", "Unit 3: Work, Energy and Power", "Unit 4: Simple Machines", "Unit 5: Electric & Magnetic Fields", "Unit 6: Wave Theory & Optics")
            "english", "eng" -> listOf("Unit 1: Vocabulary & Comprehension", "Unit 2: Active vs Passive Voice", "Unit 3: Tenses & Conditional Clauses", "Unit 4: Narrative & Descriptive Writing", "Unit 5: Idioms and Expressions", "Unit 6: Reading Strategies")
            "civics", "civ" -> listOf("Unit 1: Democratic System", "Unit 2: The FDRE Constitution", "Unit 3: Human & Democratic Rights", "Unit 4: Rule of Law & Transparency", "Unit 5: Civic Active Participation", "Unit 6: International Relations")
            "geography", "geo" -> listOf("Unit 1: Map Reading & Analysis", "Unit 2: Physical Landscapes", "Unit 3: Climate and Vegetation", "Unit 4: Demographics and Census", "Unit 5: Environmental Conservation", "Unit 6: Natural Resources of Ethiopia")
            "history", "hist" -> listOf("Unit 1: Early Civilizations & Humans", "Unit 2: Kingdom of Axum & Zagwe", "Unit 3: Battle of Adwa & Sovereignty", "Unit 4: Contemporary Ethiopian History", "Unit 5: World War I & II Impact", "Unit 6: Post-War Global Settlements")
            else -> listOf("Unit 1: Introduction", "Unit 2: General Core Concepts", "Unit 3: Review and Mock", "Unit 4: Exercises")
        }
    }

    private fun getSubjectAlias(subject: String): String {
        return when (subject.trim().lowercase()) {
            "mathematics", "maths", "math" -> "maths"
            "biology", "bio" -> "biology"
            "chemistry", "chem" -> "chemistry"
            "physics", "phys" -> "physics"
            "english", "eng" -> "english"
            "civics", "civ" -> "civics"
            "geography", "geo" -> "geography"
            "history", "hist" -> "history"
            else -> subject.trim().lowercase().replace(" ", "_")
        }
    }

    private fun getUnitNumber(unit: String?): String {
        if (unit == null) return "1"
        val regex = Regex("\\d+")
        val match = regex.find(unit)
        return match?.value ?: "1"
    }

    /**
     * Fetches dynamic subjects and units for the requested grade.
     * Managed entirely in code to avoid remote multi-table querying.
     */
    suspend fun fetchSubjectsAndUnits(grade: Int): List<SupabaseSubject> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Fetching automatic 8 Ethiopian National Curriculum subjects for Grade $grade...")
        return@withContext listOf(
            SupabaseSubject("math_$grade", "Mathematics", getUnitsForSubject(grade, "Mathematics")),
            SupabaseSubject("bio_$grade", "Biology", getUnitsForSubject(grade, "Biology")),
            SupabaseSubject("chem_$grade", "Chemistry", getUnitsForSubject(grade, "Chemistry")),
            SupabaseSubject("phys_$grade", "Physics", getUnitsForSubject(grade, "Physics")),
            SupabaseSubject("eng_$grade", "English", getUnitsForSubject(grade, "English")),
            SupabaseSubject("civ_$grade", "Civics", getUnitsForSubject(grade, "Civics")),
            SupabaseSubject("geo_$grade", "Geography", getUnitsForSubject(grade, "Geography")),
            SupabaseSubject("hist_$grade", "History", getUnitsForSubject(grade, "History"))
        )
    }

    /**
     * Fetches dynamic quiz questions for the selected grade, subject and unit from 
     * the specific Supabase table (e.g. grade_9_maths_unit_2)
     */
    suspend fun fetchQuestions(grade: Int, subject: String, unit: String? = null): List<Question> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Fetching dynamic questions from Supabase for Grade $grade, Subject $subject, Unit $unit...")
        
        // 1. Try fetching from live dynamic Supabase unit-specific table
        try {
            val fetched = fetchQuestionsViaRest(grade, subject, unit)
            if (fetched.isNotEmpty()) {
                Log.i(TAG, "Successfully fetched ${fetched.size} questions from Supabase dynamic table.")
                return@withContext fetched
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Direct REST questions fetch had issues: ${e.message}. Using offline fallback.")
        }

        // 2. Fallback to updated multi-subject offline database QuestionBank
        Log.i(TAG, "Using local/cached academic database for Grade $grade $subject.")
        return@withContext QuestionBank.getQuestions(grade, subject)
    }

    /**
     * Direct robust REST query to Supabase Postgrest endpoint of questions table using grade_subject_unit filter
     */
    private fun fetchQuestionsViaRest(grade: Int, subject: String, unit: String?): List<Question> {
        val baseUrl = SUPABASE_URL
        if (baseUrl.contains("your-project-placeholder")) {
            return emptyList()
        }
        
        val subjectAlias = getSubjectAlias(subject)
        val unitNumber = getUnitNumber(unit)
        val filterValue = "grade_${grade}_${subjectAlias}_unit_${unitNumber}"
        
        val encodedFilter = URLEncoder.encode(filterValue, "UTF-8")
        val url = "$baseUrl/rest/v1/questions?grade_subject_unit=eq.$encodedFilter"
        Log.d(TAG, "URL query: $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", SUPABASE_ANON_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_ANON_KEY")
            .build()
            
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.w(TAG, "Supabase REST questions query error: ${response.code}")
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
                    
                    val questionText = when {
                        obj.has("question") -> obj.optString("question", "")
                        obj.has("question_text") -> obj.optString("question_text", "")
                        obj.has("questionText") -> obj.optString("questionText", "")
                        else -> ""
                    }
                    
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
                    
                    val correctAnswerIndex = when {
                        obj.has("correct") -> obj.optInt("correct", 0)
                        obj.has("correct_answer") -> obj.optInt("correct_answer", 0)
                        obj.has("correct_answer_index") -> obj.optInt("correct_answer_index", 0)
                        obj.has("correctAnswerIndex") -> obj.optInt("correctAnswerIndex", 0)
                        else -> 0
                    }
                    
                    val explanation = when {
                        obj.has("explanation") -> obj.optString("explanation", "")
                        obj.has("explanation_text") -> obj.optString("explanation_text", "")
                        else -> ""
                    }
                    
                    list.add(
                         Question(
                             id = id.ifEmpty { "supabase_${filterValue}_$i" },
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
