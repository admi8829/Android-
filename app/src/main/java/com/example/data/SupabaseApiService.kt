package com.example.data

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Header
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

import retrofit2.http.POST
import retrofit2.http.Body

data class RegistrationRequest(
    val name: String,
    val grade: Int,
    val school: String,
    val phone: String,
    val email: String,
    val sex: String? = null,
    val password: String? = null,
    val difficult_subject: String? = null,
    val easy_subject: String? = null
)

data class QuestionDto(
    val id: String?,
    val grade: Int?,
    val subject: String?,
    val question: String?,
    val question_text: String?,
    val questionText: String?,
    val options: Any?, // Can be string or list
    val correct: Int?,
    val correct_answer: Int?,
    val correct_answer_index: Int?,
    val correctAnswerIndex: Int?,
    val explanation: String?,
    val explanation_text: String?
)

interface SupabaseApiService {
    @GET("rest/v1/questions")
    suspend fun getQuestions(
        @Query("select") select: String = "*",
        @Query("grade_subject_unit") filter: String,
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String
    ): List<QuestionDto>

    @POST("rest/v1/registrations")
    suspend fun postRegistration(
        @Body request: RegistrationRequest,
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Header("Prefer") prefer: String = "return=representation"
    ): Any
}

object RetrofitClient {
    private const val BASE_URL = "https://cqrgqkczemoxgcpdlpin.supabase.co/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val instance: SupabaseApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SupabaseApiService::class.java)
    }
}

