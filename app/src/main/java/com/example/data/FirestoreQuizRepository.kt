package com.example.data

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Data representation of a Subject fetched from Firestore.
 */
data class FirestoreSubject(
    val id: String = "",
    val name: String = "",
    val units: List<String> = emptyList()
)

/**
 * FirestoreQuizRepository provides a secure, robust, and highly cost-optimized approach 
 * for loading Grade 9–12 subjects and units.
 * 
 * DESIGN HIGHLIGHTS:
 * 1. Cache-Aware Flow: Save Firebase queries by checking SharedPreferences.
 * 2. Self-Healing Fallback: If cache is requested but empty / throws exception, we 
 *    automatically query the Firestore server via an elegant fallback loop.
 * 3. Structured Logging & Thread-Safety: Runs entirely on Dispatchers.IO to protect UI thread.
 */
class FirestoreQuizRepository(private val context: Context) {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    
    // Private shared preferences name
    private val prefs = context.getSharedPreferences("smart_x_academic_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "FirestoreQuizRepository"
        private const val FLAG_PREFIX = "grade_"
        private const val FLAG_SUFFIX = "_loaded"
    }

    init {
        // Automatically check and seed Grade 9 data on background thread on start
        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            try {
                seedGrade9DataIfNeeded()
            } catch (e: Throwable) {
                Log.e(TAG, "Automated startup seeding encountered an exception: ${e.message}", e)
            }
        }
    }

    /**
     * Seeds initial Grade 9 curriculum data to Firestore if it hasn't been seeded yet.
     * This guarantees the Firestore database is populated perfectly in one automatic go.
     */
    suspend fun seedGrade9DataIfNeeded() = withContext(Dispatchers.IO) {
        val seededKey = "grade_9_seeded"
        val isAlreadySeeded = prefs.getBoolean(seededKey, false)
        if (isAlreadySeeded) {
            Log.d(TAG, "Grade 9 curriculum already seeded in Firestore.")
            return@withContext
        }

        Log.i(TAG, "Syncing and seeding Grade 9 curriculum data to Firestore backend...")
        val grade9Data = hashMapOf(
            "grade_number" to 9,
            "subjects" to listOf(
                hashMapOf(
                    "id" to "math_9",
                    "name" to "Mathematics",
                    "units" to listOf("Unit 1: Number Systems")
                ),
                hashMapOf(
                    "id" to "chem_9",
                    "name" to "Chemistry",
                    "units" to listOf("Unit 1: Structure of the Atom", "Unit 2: Chemical Bonding")
                ),
                hashMapOf(
                    "id" to "bio_9",
                    "name" to "Biology",
                    "units" to listOf("Unit 1: Introduction to Biology", "Unit 2: Cell Biology")
                ),
                hashMapOf(
                    "id" to "phys_9",
                    "name" to "Physics",
                    "units" to listOf("Unit 1: Vectors", "Unit 2: One Dimensional Motion")
                )
            )
        )

        try {
            db.collection("grades")
                .document("grade_9")
                .set(grade9Data)
                .awaitSafe()
            
            prefs.edit().putBoolean(seededKey, true).apply()
            Log.i(TAG, "Successfully seeded Grade 9 static data to Firestore!")
        } catch (e: Exception) {
            Log.e(TAG, "Failed seeding Grade 9 data to Firestore. Verify connectivity or Firestore Security Rules: ${e.message}", e)
        }
    }

    /**
     * Check if the specific Grade subjects/units data has been fully loaded to local cache.
     */
    fun isGradeDataLoaded(grade: Int): Boolean {
        return prefs.getBoolean("$FLAG_PREFIX${grade}$FLAG_SUFFIX", false)
    }

    /**
     * Set the load state flag for a specific Grade.
     */
    private fun setGradeDataLoaded(grade: Int, loaded: Boolean) {
        prefs.edit().putBoolean("$FLAG_PREFIX${grade}$FLAG_SUFFIX", loaded).apply()
    }

    /**
     * Clears load flags and resets local Cache expectations.
     */
    fun resetGradeDataFlags() {
        prefs.edit().clear().apply()
        Log.d(TAG, "SharedPreferences load flags successfully reset.")
    }

    /**
     * Fetch Subjects and Units for a specific Grade (9, 10, 11, or 12).
     * 
     * Optimizations:
     * - Checked against SharedPreferences flag to minimize network requests.
     * - Uses Source.CACHE for sub-sequent calls.
     * - Gracefully falls back to Source.SERVER if Source.CACHE fails or is empty.
     */
    suspend fun fetchSubjectsAndUnits(grade: Int): List<FirestoreSubject> = withContext(Dispatchers.IO) {
        val flagKey = "$FLAG_PREFIX${grade}$FLAG_SUFFIX"
        val isAlreadyLoaded = prefs.getBoolean(flagKey, false)

        Log.d(TAG, "Request to fetch Grade $grade. Is already locally cached? $isAlreadyLoaded")

        // Determine target primary source
        val preferredSource = if (isAlreadyLoaded) Source.CACHE else Source.SERVER

        try {
            // Attempt to fetch from preferred source
            val subjects = queryFirestoreForGrade(grade, preferredSource)
            
            // If we queried server successfully for the first time, save the success flag
            if (!isAlreadyLoaded && subjects.isNotEmpty()) {
                setGradeDataLoaded(grade, true)
                Log.i(TAG, "Successfully loaded Grade $grade from Server and cached locally.")
            } else if (isAlreadyLoaded && subjects.isEmpty()) {
                // Exceptional case: cache exists but returned zero subjects; try server as fallback
                Log.w(TAG, "Local cache was empty or corrupted for Grade $grade. Querying Server fallback.")
                return@withContext fetchFromFallbackServer(grade)
            }
            
            return@withContext subjects

        } catch (e: Exception) {
            Log.e(TAG, "Failed retrieving data from preferred source ($preferredSource): ${e.message}", e)
            
            // Self-Healing Strategy: If Cache failed, try to fallback to server
            if (preferredSource == Source.CACHE) {
                Log.i(TAG, "Attempting recovery by querying Server...")
                return@withContext fetchFromFallbackServer(grade)
            } else {
                // If the query was server-bound and failed, throw / pass on the exception
                throw e
            }
        }
    }

    /**
     * Secondary helper that queries Firebase Server directly as a fallback measure.
     */
    private suspend fun fetchFromFallbackServer(grade: Int): List<FirestoreSubject> {
        return try {
            val serverSubjects = queryFirestoreForGrade(grade, Source.SERVER)
            if (serverSubjects.isNotEmpty()) {
                setGradeDataLoaded(grade, true)
                Log.i(TAG, "Server fallback query succeeded. Local cache flag updated.")
            }
            serverSubjects
        } catch (serverEx: Exception) {
            Log.e(TAG, "Server fallback call met critical error: ${serverEx.message}", serverEx)
            throw serverEx
        }
    }

    /**
     * Performs the actual Document retrieval and models parser.
     * 
     * Expects a Firestore collection named `grades` where each document ID is `grade_9`, `grade_10`, etc.
     * Document shape:
     * {
     *    "subjects": [
     *       { "id": "math", "name": "Mathematics", "units": ["Unit 1", "Unit 2"] },
     *       { "id": "phys", "name": "Physics", "units": ["Unit 1", "Unit 2"] }
     *    ]
     * }
     */
    private suspend fun queryFirestoreForGrade(grade: Int, source: Source): List<FirestoreSubject> {
        val documentId = "grade_$grade"
        
        // Retrieve snapshot using our custom awaitSafe task wrapper
        val snapshot = db.collection("grades")
            .document(documentId)
            .get(source)
            .awaitSafe()

        if (!snapshot.exists()) {
            Log.w(TAG, "No Firestore document configured for grade: $documentId")
            return emptyList()
        }

        // Parse list of subjects
        val rawSubjects = snapshot.get("subjects") as? List<Map<String, Any>> ?: return emptyList()
        
        return rawSubjects.map { map ->
            val id = map["id"] as? String ?: ""
            val name = map["name"] as? String ?: ""
            val units = map["units"] as? List<String> ?: emptyList()
            FirestoreSubject(id = id, name = name, units = units)
        }
    }

    /**
     * Custom lightweight Task await extension avoiding play-services version mismatches.
     */
    private suspend fun <T> Task<T>.awaitSafe(): T = suspendCancellableCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result
                if (result != null) {
                    continuation.resume(result)
                } else {
                    continuation.resumeWithException(NullPointerException("Firebase Task returned null result."))
                }
            } else {
                continuation.resumeWithException(task.exception ?: Exception("Unknown Firestore Task error"))
            }
        }
        continuation.invokeOnCancellation {
            // Cancel task handle if appropriate in standard runtime implementations
        }
    }
}
