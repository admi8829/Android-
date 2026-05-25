package com.example.data

data class Question(
    val id: String,
    val grade: Int, // 9, 10, 11, 12
    val subject: String, // Mathematics, Physics, Chemistry, Biology, English, Civics, Geography, History
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

object QuestionBank {
    val questions = listOf(
        // ==================== GRADE 9 ====================
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
            id = "g9_bio_1",
            grade = 9,
            subject = "Biology",
            questionText = "Which organelle is known as the powerhouse of the cell?",
            options = listOf("Nucleus", "Ribosome", "Mitochondria", "Chloroplast"),
            correctAnswerIndex = 2,
            explanation = "The mitochondria is responsible for generating cellular energy (ATP) through respiration."
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
            id = "g9_phys_1",
            grade = 9,
            subject = "Physics",
            questionText = "Which of the following is a vector quantity?",
            options = listOf("Mass", "Speed", "Velocity", "Temperature"),
            correctAnswerIndex = 2,
            explanation = "Velocity is a vector quantity because it possesses both magnitude (speed) and a specific direction."
        ),
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
            id = "g9_civ_1",
            grade = 9,
            subject = "Civics",
            questionText = "Which virtue describes the habit of telling the truth and acting sincerely?",
            options = listOf("Honesty", "Patriotism", "Industry", "Self-reliance"),
            correctAnswerIndex = 0,
            explanation = "Honesty is the baseline civic virtue of truthfulness and uprightness in community relationships."
        ),
        Question(
            id = "g9_geo_1",
            grade = 9,
            subject = "Geography",
            questionText = "What is the highest mountain peak in Ethiopia, rising to 4,550 meters?",
            options = listOf("Mount Batu", "Mount Tullu Demtu", "Ras Dashen", "Mount Gughe"),
            correctAnswerIndex = 2,
            explanation = "Ras Dashen is the tallest mountain peak in Ethiopia, located in the Simien Mountains Range."
        ),
        Question(
            id = "g9_hist_1",
            grade = 9,
            subject = "History",
            questionText = "Which famous ancient empire was located in northern Ethiopia, known for its obelisks and trade routes?",
            options = listOf("Kingdom of Damot", "Kingdom of Axum", "Zagwe Dynasty", "Adal Sultanate"),
            correctAnswerIndex = 1,
            explanation = "The Axumite Empire was a powerful trading civilization in northern Ethiopia active from the 1st to the 10th century AD."
        ),

        // ==================== GRADE 10 ====================
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
            id = "g10_bio_1",
            grade = 10,
            subject = "Biology",
            questionText = "Which organic component makes up the cell wall of plant cells?",
            options = listOf("Chitin", "Glycogen", "Cellulose", "Peptidoglycan"),
            correctAnswerIndex = 2,
            explanation = "Plant cell walls are primarily constructed of sturdy structural cellulose polymers."
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
            id = "g10_phys_1",
            grade = 10,
            subject = "Physics",
            questionText = "According to Snell's Law, what happens to light when it enters a glass block from air at an angle?",
            options = listOf("It bends away from the normal", "It continues straight", "It bends towards the normal", "It is fully absorbed"),
            correctAnswerIndex = 2,
            explanation = "Since glass is optically denser than air, light slows down and bends towards the normal."
        ),
        Question(
            id = "g10_eng_1",
            grade = 10,
            subject = "English",
            questionText = "Choose the word close in meaning to 'diligent':",
            options = listOf("Lazy", "Hard-working", "Careless", "Clever"),
            correctAnswerIndex = 1,
            explanation = "Diligent means showing care and conscientiousness in one's work or duties; i.e., hard-working."
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
        Question(
            id = "g10_geo_1",
            grade = 10,
            subject = "Geography",
            questionText = "Which Ethiopian lake is the largest by surface area and acts as the source of the Blue Nile?",
            options = listOf("Lake Langano", "Lake Abaya", "Lake Tana", "Lake Ziway"),
            correctAnswerIndex = 2,
            explanation = "Lake Tana, located in the Amhara region, is the largest lake in Ethiopia and the headwaters of the Blue Nile River."
        ),
        Question(
            id = "g10_hist_1",
            grade = 10,
            subject = "History",
            questionText = "In which historic year did the Battle of Adwa take place, confirming Ethiopian sovereignty?",
            options = listOf("1886", "1896", "1906", "1935"),
            correctAnswerIndex = 1,
            explanation = "The decisive Battle of Adwa was fought on March 1, 1896, when Ethiopian forces defeated Italian troops."
        ),

        // ==================== GRADE 11 ====================
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
            id = "g11_bio_1",
            grade = 11,
            subject = "Biology",
            questionText = "Which enzyme is responsible for starting the digestion of starches in the human mouth?",
            options = listOf("Amylase", "Pepsin", "Lipase", "Trypsin"),
            correctAnswerIndex = 0,
            explanation = "Salivary amylase breaks down complex starches into simpler sugars in the oral cavity."
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
            id = "g11_phys_1",
            grade = 11,
            subject = "Physics",
            questionText = "What is the work done by a force of 10N moving an object 5m in the direction of the force?",
            options = listOf("2 Joules", "15 Joules", "50 Joules", "0.5 Joules"),
            correctAnswerIndex = 2,
            explanation = "Work = Force × Displacement = 10 N × 5 m = 50 Joules."
        ),
        Question(
            id = "g11_eng_1",
            grade = 11,
            subject = "English",
            questionText = "Identify the spelling error from the list:",
            options = listOf("Receive", "Achieve", "Belive", "Deceive"),
            correctAnswerIndex = 2,
            explanation = "The correct spelling is 'Believe'. The spelling 'Belive' is incorrect."
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
            id = "g11_geo_1",
            grade = 11,
            subject = "Geography",
            questionText = "Which major rift valley river flow basin is internationally shared between Ethiopia, Kenya and South Sudan?",
            options = listOf("Awash River", "Omo River", "Wabi Shebelle", "Genale-Dawa"),
            correctAnswerIndex = 1,
            explanation = "The Omo River flows primarily southwards from Central Ethiopia to empty into Lake Turkana on the Kenyan border."
        ),
        Question(
            id = "g11_hist_1",
            grade = 11,
            subject = "History",
            questionText = "Who was the legendary Zagwe king responsible for carving the famous 11 rock-hewn churches of Lalibela?",
            options = listOf("King Lalibela", "King Yekuno Amlak", "King Caleb", "King Ezana"),
            correctAnswerIndex = 0,
            explanation = "Emperor Gebre Meskel Lalibela (King Lalibela) directed the physical carving of these monolithic churches in Roha (now Lalibela) in the late 12th century."
        ),

        // ==================== GRADE 12 ====================
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
            id = "g12_bio_1",
            grade = 12,
            subject = "Biology",
            questionText = "What is the physical transport system responsible for carrying water from roots to leaves in plants?",
            options = listOf("Phloem", "Xylem", "Stomata", "Chloroplast"),
            correctAnswerIndex = 1,
            explanation = "Xylem vessels distribute water and solutes upward from roots to structural leaves."
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
            id = "g12_phys_1",
            grade = 12,
            subject = "Physics",
            questionText = "Which phenomenon confirms the wave nature of light?",
            options = listOf("Photoelectric effect", "Interference", "Blackbody radiation", "Compton scattering"),
            correctAnswerIndex = 1,
            explanation = "Interference pattern (like Young's double slit experiment) of light waves confirms its physical wave nature."
        ),
        Question(
            id = "g12_eng_1",
            grade = 12,
            subject = "English",
            questionText = "Identify the passive voice of: 'The teacher praised the students.'",
            options = listOf(
                "The students were praising the teacher.",
                "The students are praised by the teacher.",
                "The students were praised by the teacher.",
                "The teacher is praised by the students."
            ),
            correctAnswerIndex = 2,
            explanation = "The passive voice for simple past is 'were/was + past participle', hence 'The students were praised by the teacher.'"
        ),
        Question(
            id = "g12_civ_1",
            grade = 12,
            subject = "Civics",
            questionText = "Which international organization was co-founded by Ethiopia in 1945?",
            options = listOf("African Union (AU)", "United Nations (UN)", "League of Nations", "Arab League"),
            correctAnswerIndex = 1,
            explanation = "Ethiopia was one of the original 51 co-founders and signees of the United Nations Charter in 1945."
        ),
        Question(
            id = "g12_geo_1",
            grade = 12,
            subject = "Geography",
            questionText = "Which climatic zone describes the hot, arid and dry lowlands of Ethiopia (below 500m elevation)?",
            options = listOf("Dega", "Weyna Dega", "Qola", "Bereha"),
            correctAnswerIndex = 3,
            explanation = "Bereha corresponds to extreme desert and semi-arid lowlands with high temperatures and low rainfall."
        ),
        Question(
            id = "g12_hist_1",
            grade = 12,
            subject = "History",
            questionText = "Who was the ruler of Ethiopia recognized for pioneering modern education and leading the nation through World War II?",
            options = listOf("Emperor Yohannes IV", "Emperor Menelik II", "Emperor Haile Selassie I", "Emperor Tewodros II"),
            correctAnswerIndex = 2,
            explanation = "Emperor Haile Selassie I modernized Ethiopia's schools, health systems, and served as chief sovereign during global WWII adjustments."
        )
    )

    fun getSubjectsForGrade(grade: Int): List<String> {
        return listOf("Mathematics", "Biology", "Chemistry", "Physics", "English", "Civics", "Geography", "History")
    }

    fun getQuestions(grade: Int, subject: String): List<Question> {
        return questions.filter { it.grade == grade && it.subject.lowercase() == subject.lowercase() }
    }
}
