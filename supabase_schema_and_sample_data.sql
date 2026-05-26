-- Supabase Setup Script for Ethiopian Curriculum Quiz App (Single Table Schema)
-- Run this script in your Supabase SQL Editor (https://supabase.com/dashboard/project/_/sql)
--
-- Now, instead of many separate tables, a SINGLE table named "questions" houses
-- all curriculum questions, identified by the "grade_subject_unit" keyword!
-- e.g. 'grade_9_maths_unit_2', 'grade_10_chemistry_unit_1', etc.

-- 1. Create questions table
CREATE TABLE IF NOT EXISTS public.questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    grade_subject_unit TEXT NOT NULL, -- e.g. "grade_9_maths_unit_2", "grade_12_civics_unit_6", etc.
    question TEXT NOT NULL, -- The text of the question
    options JSONB NOT NULL, -- JSON array of options e.g. ["x = 7", "x = 5", "x = 8", "x = 11"]
    correct INTEGER NOT NULL, -- 0-based index of correct option
    explanation TEXT, -- Detailed answer explanation
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Index the grade_subject_unit query column to keep query operations instant!
CREATE INDEX IF NOT EXISTS idx_questions_grade_subject_unit ON public.questions(grade_subject_unit);

-- Enable Row Level Security (RLS)
ALTER TABLE public.questions ENABLE ROW LEVEL SECURITY;

-- Allow read-only public access to pull academic quizzes securely
CREATE POLICY "Allow public read access on questions" 
ON public.questions 
FOR SELECT 
USING (true);

-- 2. Insert Sample Core Ethiopian Curriculum Questions for any grade, subject, and unit
INSERT INTO public.questions (grade_subject_unit, question, options, correct, explanation) VALUES
-- Mathematics - Grade 9, Unit 2
(
    'grade_9_maths_unit_2', 
    'What is the solution set of the linear equation 3x - 5 = 16?', 
    '["x = 7", "x = 5", "x = 8", "x = 11"]'::jsonb, 
    0, 
    'To solve 3x - 5 = 16, add 5 to both sides to get 3x = 21, then divide by 3 to find x = 7.'
),
(
    'grade_9_maths_unit_2', 
    'For which of the following inequalities is x = -2 a valid solution?', 
    '["2x > 5", "x - 4 < -5", "3x + 2 >= -1", "-x < 0"]'::jsonb, 
    1, 
    'Substituting x = -2 into (x - 4 < -5) gives -6 < -5, which is mathematically correct.'
),

-- Biology - Grade 9, Unit 2
(
    'grade_9_biology_unit_2', 
    'Which organelle is known as the powerhouse of the cell?', 
    '["Nucleus", "Ribosome", "Mitochondria", "Chloroplast"]'::jsonb, 
    2, 
    'The mitochondria is responsible for generating cellular energy (ATP) through respiration.'
),
(
    'grade_9_biology_unit_2', 
    'Which of the following processes takes place in the cytoplasm of a cell?', 
    '["Glycolysis", "Krebs Cycle", "Electron Transport Chain", "Photosynthesis"]'::jsonb, 
    0, 
    'Glycolysis is the initial step of cellular respiration and occurs entirely in the cell cytoplasm.'
),

-- Chemistry - Grade 10, Unit 1
(
    'grade_10_chemistry_unit_1',
    'What is the pH level of a completely neutral solution at 25°C?',
    '["pH 1", "pH 5", "pH 7", "pH 14"]'::jsonb,
    2,
    'A neutral solution, such as pure water, has a pH of 7 at standard temperatures.'
),

-- Physics - Grade 9, Unit 1
(
    'grade_9_physics_unit_1',
    'Which of the following is a vector quantity?',
    '["Mass", "Speed", "Velocity", "Temperature"]'::jsonb,
    2,
    'Velocity is a vector quantity because it possesses both magnitude (speed) and a specific direction.'
),

-- Civics - Grade 12, Unit 6
(
    'grade_12_civics_unit_6',
    'Which international organization was co-founded by Ethiopia in 1945?',
    '["African Union (AU)", "United Nations (UN)", "League of Nations", "Arab League"]'::jsonb,
    1,
    'Ethiopia was one of the original 51 co-founders and signees of the United Nations Charter in 1945.'
)
ON CONFLICT (id) DO NOTHING;

-- 3. Create registrations table (for student dynamic registration)
CREATE TABLE IF NOT EXISTS public.registrations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    grade INTEGER NOT NULL,
    school TEXT NOT NULL,
    phone TEXT NOT NULL,
    email TEXT NOT NULL,
    sex TEXT,
    password TEXT,
    difficult_subject TEXT,
    easy_subject TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Index registrations by email for faster query check and logins
CREATE INDEX IF NOT EXISTS idx_registrations_email ON public.registrations(email);

-- Enable Row Level Security (RLS) on registrations
ALTER TABLE public.registrations ENABLE ROW LEVEL SECURITY;

-- Allow open public insert access for user signups
CREATE POLICY "Allow public inserts on registrations" 
ON public.registrations 
FOR INSERT 
WITH CHECK (true);

-- Allow public read access to verify registration status or authenticate
CREATE POLICY "Allow public select on registrations" 
ON public.registrations 
FOR SELECT 
USING (true);

-- Optional Row Level Update policy if students wish to modify subject choices
CREATE POLICY "Allow public updates on registrations" 
ON public.registrations 
FOR UPDATE 
USING (true);

