package com.example.data

data class Question(
    val id: String,
    val grade: Int, // 9, 10, 11, 12
    val subject: String, // Mathematics, Physics, Chemistry, Biology, English, Civics
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

object QuestionBank {
    val questions = listOf(
        // Grade 9
        Question(
            id = "g9_math_1",
            grade = 9,
            subject = "Mathematics",
            questionText = "What is the solution set of the linear equation 3x - 5 = 16?",
            options = listOf("x = 7", "x = 5", "x = 8", "x = 11"),
            correctAnswerIndex = 0,
            explanation = "To solve 3x - 5 = 16, add 5 to both sides to get 3x = 21, then divide by 3 to find x = 7."
        ),
        Question(
            id = "g9_phys_1",
            grade = 9,
            subject = "Physics",
            questionText = "Which of the following is a vector quantity?",
            options = listOf("Mass", "Speed", "Velocity", "Temperature"),
            correctAnswerIndex = 2,
            explanation = "Velocity is a vector quantity because it possesses both magnitude (speed) and a specific direction."
        ),
        Question(
            id = "g9_chem_1",
            grade = 9,
            subject = "Chemistry",
            questionText = "What is the chemical symbol for Sodium?",
            options = listOf("So", "Na", "S", "Sd"),
            correctAnswerIndex = 1,
            explanation = "The chemical symbol for Sodium is Na, derived from its Latin name 'Natrium'."
        ),
        Question(
            id = "g9_bio_1",
            grade = 9,
            subject = "Biology",
            questionText = "Which organelle is known as the powerhouse of the cell?",
            options = listOf("Nucleus", "Ribosome", "Mitochondria", "Chloroplast"),
            correctAnswerIndex = 2,
            explanation = "The mitochondria is responsible for generating cellular energy (ATP) through respiration."
        ),

        // Grade 10
        Question(
            id = "g10_math_1",
            grade = 10,
            subject = "Mathematics",
            questionText = "If f(x) = x² - 3x + 2, what is f(-2)?",
            options = listOf("0", "12", "6", "8"),
            correctAnswerIndex = 1,
            explanation = "Substitute -2 for x: f(-2) = (-2)² - 3(-2) + 2 = 4 + 6 + 2 = 12."
        ),
        Question(
            id = "g10_phys_1",
            grade = 10,
            subject = "Physics",
            questionText = "According to Snell's Law, what happens to light when it enters a glass block from air at an angle?",
            options = listOf("It bends away from the normal", "It continues straight", "It bends towards the normal", "It is fully absorbed"),
            correctAnswerIndex = 2,
            explanation = "Since glass is optically denser than air, light slows down and bends towards the normal."
        ),
        Question(
            id = "g10_chem_1",
            grade = 10,
            subject = "Chemistry",
            questionText = "What is the pH level of a completely neutral solution at 25°C?",
            options = listOf("pH 1", "pH 5", "pH 7", "pH 14"),
            correctAnswerIndex = 2,
            explanation = "A neutral solution, such as pure water, has a pH of 7 at standard temperatures."
        ),
        Question(
            id = "g10_civ_1",
            grade = 10,
            subject = "Civics",
            questionText = "What is the primary objective of the Ethiopian constitution?",
            options = listOf("To promote unitary state rule", "To protect fundamental human and democratic rights", "To restrict regional development", "To centralize all resource distributions"),
            correctAnswerIndex = 1,
            explanation = "The Constitution of the FDRE primary baseline is safeguarding human rights, self-determination, and democracy."
        ),

        // Grade 11
        Question(
            id = "g11_math_1",
            grade = 11,
            subject = "Mathematics",
            questionText = "What is the limit of (x² - 1)/(x - 1) as x approaches 1?",
            options = listOf("1", "2", "Undefined", "0"),
            correctAnswerIndex = 1,
            explanation = "Factor the numerator: (x - 1)(x + 1)/(x - 1) = x + 1. As x approaches 1, the limit is 1 + 1 = 2."
        ),
        Question(
            id = "g11_phys_1",
            grade = 11,
            subject = "Physics",
            questionText = "What is the work done by a force of 10N moving an object 5m in the direction of the force?",
            options = listOf("2 Joules", "15 Joules", "50 Joules", "0.5 Joules"),
            correctAnswerIndex = 2,
            explanation = "Work = Force × Displacement = 10 N × 5 m = 50 Joules."
        ),
        Question(
            id = "g11_chem_1",
            grade = 11,
            subject = "Chemistry",
            questionText = "Which type of chemical bond involves the equal sharing of electron pairs between atoms?",
            options = listOf("Ionic bond", "Covalent bond", "Metallic bond", "Hydrogen bond"),
            correctAnswerIndex = 1,
            explanation = "A covalent bond consists of the mutual sharing of one or more pairs of electrons between two atoms."
        ),
        Question(
            id = "g11_bio_1",
            grade = 11,
            subject = "Biology",
            questionText = "Which enzyme is responsible for starting the digestion of starches in the human mouth?",
            options = listOf("Amylase", "Pepsin", "Lipase", "Trypsin"),
            correctAnswerIndex = 0,
            explanation = "Salivary amylase breaks down complex starches into simpler sugars in the oral cavity."
        ),

        // Grade 12
        Question(
            id = "g12_math_1",
            grade = 12,
            subject = "Mathematics",
            questionText = "What is the derivative of f(x) = ln(3x) with respect to x?",
            options = listOf("3/x", "1/(3x)", "1/x", "ln(3)"),
            correctAnswerIndex = 2,
            explanation = "Using the chain rule, d/dx[ln(3x)] = (1/(3x)) * 3 = 1/x."
        ),
        Question(
            id = "g12_phys_1",
            grade = 12,
            subject = "Physics",
            questionText = "Which phenomenon confirms the wave nature of light?",
            options = listOf("Photoelectric effect", "Interference", "Blackbody radiation", "Compton scattering"),
            correctAnswerIndex = 1,
            explanation = "Interference pattern (like Young's double slit experiment) of light waves confirms its physical wave nature."
        ),
        Question(
            id = "g12_chem_1",
            grade = 12,
            subject = "Chemistry",
            questionText = "Which element acts as the catalyst in the industrial Haber process to synthesize ammonia?",
            options = listOf("Copper", "Iron", "Platinum", "Nickel"),
            correctAnswerIndex = 1,
            explanation = "Finely divided iron is the metal catalyst used in the Haber process to speed up nitrogen-hydrogen reaction."
        ),
        Question(
            id = "g12_eng_1",
            grade = 12,
            subject = "English",
            questionText = "Identify the grammatically correct passive voice of: 'The teacher praised the students.'",
            options = listOf(
                "The students were praising the teacher.",
                "The students are praised by the teacher.",
                "The students were praised by the teacher.",
                "The teacher is praised by the students."
            ),
            correctAnswerIndex = 2,
            explanation = "Active: Subject (The teacher) + Verb (praised - simple past) + Object (the students). Passive: Object becomes subject + was/were + past participle (praised) + by agent. Hence: 'The students were praised by the teacher.'"
        ),
        
        // Additional diverse questions to enrich grade 9-12 content
        Question(
            id = "g9_eng_1",
            grade = 9,
            subject = "English",
            questionText = "She _____ to school every day since last month.",
            options = listOf("walks", "has walked", "is walking", "walked"),
            correctAnswerIndex = 1,
            explanation = "The presence of 'since' suggests a process that started in the past and continues, which calls for the present perfect tense ('has walked')."
        ),
        Question(
            id = "g10_bio_1",
            grade = 10,
            subject = "Biology",
            questionText = "Which organic component makes up the cell wall of plant cells?",
            options = listOf("Chitin", "Glycogen", "Cellulose", "Peptidoglycan"),
            correctAnswerIndex = 2,
            explanation = "Plant cell walls are primarily constructed of sturdy structural cellulose polymers."
        ),
        Question(
            id = "g11_civ_1",
            grade = 11,
            subject = "Civics",
            questionText = "Which of the following describes the rule of law?",
            options = listOf("No citizen is above the law and all are treated equally", "Only government officials are subject to laws", "Rules can be altered by any individual dynamic needs", "Laws apply only to the regional governments"),
            correctAnswerIndex = 0,
            explanation = "The rule of law ensures that all individuals, institutions, and leaders are accountable to laws that are publicly promulgated and equally enforced."
        ),
        Question(
            id = "g12_civ_1",
            grade = 12,
            subject = "Civics",
            questionText = "Which international organization was co-founded by Ethiopia in 1945?",
            options = listOf("African Union (AU)", "United Nations (UN)", "League of Nations", "Arab League"),
            correctAnswerIndex = 1,
            explanation = "Ethiopia was one of the original 51 co-founders and signees of the United Nations Charter in 1945."
        )
    )
    
    fun getSubjectsForGrade(grade: Int): List<String> {
        return questions.filter { it.grade == grade }
            .map { it.subject }
            .distinct()
            .sorted()
    }

    fun getQuestions(grade: Int, subject: String): List<Question> {
        return questions.filter { it.grade == grade && it.subject.lowercase() == subject.lowercase() }
    }
}
