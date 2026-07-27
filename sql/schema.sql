-- ============================================
-- LMS Database Schema — PostgreSQL Version
-- SochTech | Niraj Patel | Final Year Project
-- PostgreSQL 18 Compatible
-- ============================================

-- Drop and recreate database (run this in psql as superuser)
-- CREATE DATABASE lms_db;
-- \c lms_db

-- Users Table
-- NOTE: PostgreSQL uses SERIAL instead of AUTO_INCREMENT
--       ENUM replaced with VARCHAR + CHECK constraint
CREATE TABLE IF NOT EXISTS users (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    username    VARCHAR(50)  UNIQUE NOT NULL,
    email       VARCHAR(100) UNIQUE NOT NULL,
    password    VARCHAR(100) NOT NULL,
    role        VARCHAR(10)  NOT NULL DEFAULT 'student'
                CHECK (role IN ('student', 'teacher', 'admin')),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Courses Table
CREATE TABLE IF NOT EXISTS courses (
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(150) NOT NULL,
    description TEXT,
    teacher_id  INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Enrollments Table
-- NOTE: INSERT IGNORE → INSERT ... ON CONFLICT DO NOTHING in PostgreSQL
CREATE TABLE IF NOT EXISTS enrollments (
    id          SERIAL PRIMARY KEY,
    student_id  INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    course_id   INT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (student_id, course_id)
);

-- Quizzes Table
CREATE TABLE IF NOT EXISTS quizzes (
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(150) NOT NULL,
    course_id   INT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Questions Table
CREATE TABLE IF NOT EXISTS questions (
    id              SERIAL PRIMARY KEY,
    quiz_id         INT NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    question_text   TEXT NOT NULL,
    option_a        VARCHAR(200) NOT NULL,
    option_b        VARCHAR(200) NOT NULL,
    option_c        VARCHAR(200) NOT NULL,
    option_d        VARCHAR(200) NOT NULL,
    correct_option  CHAR(1) NOT NULL
);

-- Results Table
CREATE TABLE IF NOT EXISTS results (
    id           SERIAL PRIMARY KEY,
    student_id   INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    quiz_id      INT NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    score        INT NOT NULL,
    total        INT NOT NULL,
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Progress Table
-- NOTE: ON DUPLICATE KEY UPDATE → ON CONFLICT DO UPDATE in PostgreSQL
CREATE TABLE IF NOT EXISTS progress (
    id          SERIAL PRIMARY KEY,
    student_id  INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    course_id   INT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    percentage  DOUBLE PRECISION DEFAULT 0,
    UNIQUE (student_id, course_id)
);

-- Chatbot QA Table
CREATE TABLE IF NOT EXISTS chatbot_qa (
    id      SERIAL PRIMARY KEY,
    keyword VARCHAR(100) NOT NULL,
    answer  TEXT NOT NULL
);

-- ============================================
-- Sample Data
-- ============================================

INSERT INTO users (name, username, email, password, role) VALUES
('Admin User',   'admin',    'admin@lms.com',    'admin123', 'admin'),
('Raj Sharma',   'teacher1', 'raj@lms.com',      'teach123', 'teacher'),
('Priya Mehta',  'teacher2', 'priya@lms.com',    'teach456', 'teacher'),
('Niraj Patel',  'student1', 'niraj@lms.com',    'stud123',  'student'),
('Ankit Singh',  'student2', 'ankit@lms.com',    'stud456',  'student'),
('Sneha Patil',  'student3', 'sneha@lms.com',    'stud789',  'student');

INSERT INTO courses (title, description, teacher_id) VALUES
('Java Programming Basics',        'Learn core Java concepts from scratch.', 2),
('Data Structures & Algorithms',   'Master DSA with Java implementations.',  2),
('Web Development with React',     'Build modern web apps using React.js.',  3),
('Database Management Systems',    'SQL, normalization, and JDBC.',          3);

INSERT INTO enrollments (student_id, course_id) VALUES
(4, 1), (4, 2), (5, 1), (5, 3), (6, 2), (6, 4);

INSERT INTO quizzes (title, course_id) VALUES
('Java Basics Quiz 1',  1),
('Java OOP Quiz 2',     1),
('DSA Arrays Quiz',     2),
('React Intro Quiz',    3),
('SQL Basics Quiz',     4);

INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES
(1, 'Which keyword is used to create a class in Java?', 'class', 'Class', 'create', 'new', 'A'),
(1, 'What is the size of int in Java?', '2 bytes', '4 bytes', '8 bytes', '1 byte', 'B'),
(1, 'Which method is the entry point of a Java program?', 'start()', 'main()', 'run()', 'init()', 'B'),
(1, 'What is JVM?', 'Java Virtual Memory', 'Java Visual Machine', 'Java Virtual Machine', 'Java Variable Method', 'C'),
(1, 'Which of these is not a primitive type?', 'int', 'char', 'String', 'boolean', 'C'),
(2, 'Which pillar of OOP hides internal details?', 'Inheritance', 'Polymorphism', 'Encapsulation', 'Abstraction', 'C'),
(2, 'Which keyword enables inheritance in Java?', 'implements', 'extends', 'inherits', 'super', 'B'),
(2, 'What is method overloading?', 'Same name, different parameters', 'Different name, same parameters', 'Same name, same parameters', 'None', 'A'),
(2, 'Which is an abstract class keyword?', 'interface', 'abstract', 'virtual', 'override', 'B'),
(2, 'Constructor is called when?', 'Class is defined', 'Object is created', 'Method is called', 'Program starts', 'B'),
(3, 'Time complexity of binary search?', 'O(n)', 'O(n2)', 'O(log n)', 'O(1)', 'C'),
(3, 'Which data structure uses LIFO?', 'Queue', 'Stack', 'LinkedList', 'Array', 'B'),
(3, 'Array index starts at?', '1', '-1', '0', 'depends', 'C'),
(3, 'Which sorting is O(n log n) average?', 'Bubble Sort', 'Selection Sort', 'Merge Sort', 'Insertion Sort', 'C'),
(3, 'Linked list node contains?', 'Only data', 'Only pointer', 'Data and pointer', 'Index', 'C');

INSERT INTO chatbot_qa (keyword, answer) VALUES
('hello',    'Hello! Welcome to LMS. How can I help you today?'),
('hi',       'Hi there! Ask me anything about the LMS system.'),
('course',   'You can browse all courses from the All Courses tab and enroll in any course.'),
('enroll',   'Go to the All Courses tab and click Enroll to join any course.'),
('quiz',     'Quizzes are available in the Quizzes tab. Attempt them to track your progress.'),
('result',   'Your quiz results are visible in the Results tab with your score and percentage.'),
('progress', 'Your course progress is shown in the Progress tab as a percentage.'),
('teacher',  'Teachers can create courses, add quizzes, and view student results.'),
('admin',    'Admin manages users, courses, and chatbot Q&A pairs.'),
('password', 'Contact your admin to reset your password.'),
('help',     'I can help with courses, quizzes, results, and progress. Just ask!'),
('logout',   'Click the Logout button on your dashboard to safely exit.'),
('register', 'Click the Register tab on the login screen to create a new account.');
