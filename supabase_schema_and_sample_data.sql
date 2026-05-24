-- Supabase Setup Script for Quiz App
-- Run this script in your Supabase SQL Editor (https://supabase.com/dashboard/project/_/sql)

-- 1. Create grades table
CREATE TABLE IF NOT EXISTS public.grades (
    id SERIAL PRIMARY KEY,
    grade INTEGER NOT NULL UNIQUE
);

-- 2. Create subjects table
CREATE TABLE IF NOT EXISTS public.subjects (
    id TEXT PRIMARY KEY,
    grade INTEGER NOT NULL,
    name TEXT NOT NULL,
    units JSONB DEFAULT '[]'::jsonb
);

-- 3. Create questions table
CREATE TABLE IF NOT EXISTS public.questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    grade INTEGER NOT NULL,
    subject TEXT NOT NULL,
    unit TEXT,
    question_text TEXT NOT NULL,
    options JSONB NOT NULL,
    correct_answer_index INTEGER NOT NULL,
    explanation_text TEXT
);

-- Enable Row Level Security (RLS)
ALTER TABLE public.grades ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.subjects ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.questions ENABLE ROW LEVEL SECURITY;

-- Add policies to allow everyone to read the data (required for the app to work without user authentication)
-- If these policies already exist, running this again is safe but might throw a "policy already exists" notice.
CREATE POLICY "Allow public read access on grades" ON public.grades FOR SELECT USING (true);
CREATE POLICY "Allow public read access on subjects" ON public.subjects FOR SELECT USING (true);
CREATE POLICY "Allow public read access on questions" ON public.questions FOR SELECT USING (true);


-- 4. Insert Sample Data

-- Clear existing sample data if you want a fresh start (optional, uncomment below to use)
-- TRUNCATE TABLE public.grades, public.subjects, public.questions CASCADE;

-- Insert Grades
INSERT INTO public.grades (grade) VALUES 
(9), (10), (11), (12)
ON CONFLICT (grade) DO NOTHING;

-- Insert Subjects
INSERT INTO public.subjects (id, grade, name, units) VALUES
('math_9', 9, 'Mathematics', '["Unit 1: Number Systems", "Unit 2: Equations", "Unit 3: Geometry"]'::jsonb),
('bio_9', 9, 'Biology', '["Unit 1: Intro", "Unit 2: Cells"]'::jsonb),
('chem_10', 10, 'Chemistry', '["Unit 1: Organic Chemistry", "Unit 2: Hydrocarbons"]'::jsonb),
('phys_11', 11, 'Physics', '["Unit 1: Measurement", "Unit 2: Vectors"]'::jsonb)
ON CONFLICT (id) DO NOTHING;

-- Insert Questions
INSERT INTO public.questions (grade, subject, unit, question_text, options, correct_answer_index, explanation_text) VALUES
(9, 'Mathematics', 'Unit 1: Number Systems', 'What is 5 + 7?', '["10", "11", "12", "13"]'::jsonb, 2, '5 + 7 equals 12.'),
(9, 'Mathematics', 'Unit 1: Number Systems', 'Which of these is a prime number?', '["4", "6", "9", "11"]'::jsonb, 3, '11 is only divisible by 1 and itself.'),
(9, 'Biology', 'Unit 2: Cells', 'What is the powerhouse of the cell?', '["Nucleus", "Mitochondria", "Ribosome", "Endoplasmic Reticulum"]'::jsonb, 1, 'Mitochondria generate most of the cell''s supply of ATP.'),
(10, 'Chemistry', 'Unit 1: Organic Chemistry', 'What is the chemical formula for methane?', '["CO2", "H2O", "CH4", "O2"]'::jsonb, 2, 'Methane is an alkane with the chemical formula CH4.'),
(11, 'Physics', 'Unit 2: Vectors', 'Which of the following is a vector quantity?', '["Mass", "Temperature", "Speed", "Velocity"]'::jsonb, 3, 'Velocity has both magnitude and direction, making it a vector.');
