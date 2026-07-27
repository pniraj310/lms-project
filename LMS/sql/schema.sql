-- ============================================================
--  LEARNING MANAGEMENT SYSTEM (LMS) - Database Schema
--  Database: MySQL 8.x
-- ============================================================

-- Step 1: Create and select the database
CREATE DATABASE IF NOT EXISTS lms_db;
USE lms_db;

-- ============================================================
-- TABLE 1: users
-- Stores all registered users (students, teachers, admins)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,           -- stored as plain text (use hashing in production)
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    role        ENUM('student','teacher','admin') NOT NULL DEFAULT 'student',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLE 2: courses
-- Teacher creates courses; students enroll
-- ============================================================
CREATE TABLE IF NOT EXISTS courses (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(150) NOT NULL,
    description  TEXT,
    teacher_id   INT NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- TABLE 3: enrollments
-- Tracks which student enrolled in which course
-- ============================================================
CREATE TABLE IF NOT EXISTS enrollments (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    student_id   INT NOT NULL,
    course_id    INT NOT NULL,
    enrolled_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uniq_enrollment (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id)  REFERENCES courses(id) ON DELETE CASCADE
);

-- ============================================================
-- TABLE 4: quizzes
-- Each quiz belongs to a course
-- ============================================================
CREATE TABLE IF NOT EXISTS quizzes (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    course_id   INT NOT NULL,
    title       VARCHAR(150) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- ============================================================
-- TABLE 5: questions
-- Multiple-choice questions belonging to a quiz
-- ============================================================
CREATE TABLE IF NOT EXISTS questions (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id         INT NOT NULL,
    question_text   TEXT NOT NULL,
    option_a        VARCHAR(255) NOT NULL,
    option_b        VARCHAR(255) NOT NULL,
    option_c        VARCHAR(255) NOT NULL,
    option_d        VARCHAR(255) NOT NULL,
    correct_option  CHAR(1) NOT NULL,               -- 'A', 'B', 'C', or 'D'
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- ============================================================
-- TABLE 6: results
-- Stores quiz attempt scores for students
-- ============================================================
CREATE TABLE IF NOT EXISTS results (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    student_id   INT NOT NULL,
    quiz_id      INT NOT NULL,
    score        INT NOT NULL DEFAULT 0,
    total        INT NOT NULL DEFAULT 0,             -- total questions attempted
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id)    REFERENCES quizzes(id) ON DELETE CASCADE
);

-- ============================================================
-- TABLE 7: progress
-- Tracks course completion percentage per student
-- ============================================================
CREATE TABLE IF NOT EXISTS progress (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    student_id          INT NOT NULL,
    course_id           INT NOT NULL,
    completion_percent  DECIMAL(5,2) DEFAULT 0.00,
    last_updated        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uniq_progress (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id)  REFERENCES courses(id) ON DELETE CASCADE
);

-- ============================================================
-- TABLE 8: chatbot_qa
-- Stores Q&A pairs for the rule-based chatbot
-- ============================================================
CREATE TABLE IF NOT EXISTS chatbot_qa (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    keyword   VARCHAR(100) NOT NULL,       -- keyword/trigger word
    question  VARCHAR(255) NOT NULL,
    answer    TEXT NOT NULL
);

-- ============================================================
-- SAMPLE DATA
-- ============================================================

-- Default admin user  (password: admin123)
INSERT INTO users (username, password, full_name, email, role) VALUES
('admin',   'admin123',   'System Admin',    'admin@lms.com',    'admin'),
('teacher1','teach123',   'Prof. Sharma',    'sharma@lms.com',   'teacher'),
('teacher2','teach456',   'Prof. Gupta',     'gupta@lms.com',    'teacher'),
('student1','stud123',    'Niraj Patil',     'niraj@lms.com',    'student'),
('student2','stud456',    'Priya Joshi',     'priya@lms.com',    'student'),
('student3','stud789',    'Amit Kumar',      'amit@lms.com',     'student');

-- Sample courses
INSERT INTO courses (title, description, teacher_id) VALUES
('Java Programming Basics',  'Learn core Java, OOP, and JDBC from scratch.',       2),
('Database Management (SQL)','Covers SQL, normalization, and DBMS concepts.',      2),
('Web Development 101',      'HTML, CSS, JavaScript fundamentals.',                3),
('Python for Beginners',     'Introduction to Python programming language.',       3);

-- Enroll students
INSERT INTO enrollments (student_id, course_id) VALUES
(4, 1), (4, 2),
(5, 1), (5, 3),
(6, 2), (6, 4);

-- Sample quizzes
INSERT INTO quizzes (course_id, title) VALUES
(1, 'Java Basics Quiz'),
(2, 'SQL Fundamentals Quiz'),
(3, 'HTML & CSS Quiz');

-- Questions for Quiz 1 (Java Basics)
INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES
(1, 'Which keyword is used to create a class in Java?',
   'class', 'Class', 'define', 'struct', 'A'),
(1, 'What is the default value of an int variable in Java?',
   '1', 'null', '0', 'undefined', 'C'),
(1, 'Which method is the entry point of a Java program?',
   'start()', 'run()', 'main()', 'init()', 'C'),
(1, 'Which of the following is NOT an OOP concept?',
   'Inheritance', 'Compilation', 'Polymorphism', 'Encapsulation', 'B'),
(1, 'What does JVM stand for?',
   'Java Variable Machine', 'Java Virtual Machine', 'Java Verified Module', 'Java Vital Memory', 'B');

-- Questions for Quiz 2 (SQL Fundamentals)
INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES
(2, 'Which SQL command retrieves data from a table?',
   'INSERT', 'UPDATE', 'SELECT', 'DELETE', 'C'),
(2, 'Which constraint ensures unique values in a column?',
   'NOT NULL', 'PRIMARY KEY', 'FOREIGN KEY', 'CHECK', 'B'),
(2, 'What does JOIN do in SQL?',
   'Combines rows from two tables', 'Deletes duplicate rows', 'Creates a new table', 'Sorts the result', 'A'),
(2, 'Which clause filters groups in SQL?',
   'WHERE', 'HAVING', 'GROUP BY', 'ORDER BY', 'B'),
(2, 'What is the full form of DBMS?',
   'Data Base Management Set', 'Database Management System', 'Data Byte Management System', 'None', 'B');

-- Questions for Quiz 3 (HTML & CSS)
INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES
(3, 'What does HTML stand for?',
   'Hyper Text Markup Language', 'High Text Machine Language', 'Hyper Transfer Markup Language', 'None', 'A'),
(3, 'Which HTML tag creates a hyperlink?',
   '<link>', '<href>', '<a>', '<url>', 'C'),
(3, 'Which CSS property changes text color?',
   'font-color', 'text-color', 'color', 'foreground', 'C'),
(3, 'What is the correct CSS selector for all <p> elements?',
   '.p', '#p', 'p', '*p', 'C'),
(3, 'Which tag is used for the largest heading in HTML?',
   '<h6>', '<head>', '<h1>', '<title>', 'C');

-- Chatbot Q&A pairs
INSERT INTO chatbot_qa (keyword, question, answer) VALUES
('hello',     'Hello',                         'Hello! Welcome to the LMS Chatbot. How can I help you today?'),
('hi',        'Hi there',                      'Hi! I am your LMS assistant. Ask me anything about courses or quizzes!'),
('enroll',    'How do I enroll in a course?',  'Go to the Student Dashboard, click "View Courses", select a course and click "Enroll".'),
('quiz',      'How do I take a quiz?',         'After enrolling in a course, go to "My Quizzes" tab and click "Attempt Quiz".'),
('result',    'How to see my results?',        'Click on "My Results" in the Student Dashboard to view all your quiz scores.'),
('progress',  'How is progress calculated?',   'Progress is based on quizzes attempted vs total quizzes in a course. It updates automatically after each quiz.'),
('teacher',   'How to add a course?',          'Login as a teacher, go to Teacher Dashboard, click "Add Course" and fill in the details.'),
('password',  'How to change password?',       'Password change feature will be added in a future update. Contact admin for now.'),
('contact',   'How to contact admin?',         'Email the admin at admin@lms.com for any issues.'),
('help',      'I need help',                   'Sure! You can ask me about: enrollment, quizzes, results, progress, or courses.'),
('bye',       'Goodbye',                       'Goodbye! Happy Learning! See you soon.'),
('thanks',    'Thank you',                     'You are welcome! Keep learning and growing!');

-- Initial progress records (0%)
INSERT INTO progress (student_id, course_id, completion_percent) VALUES
(4, 1, 0.00), (4, 2, 0.00),
(5, 1, 0.00), (5, 3, 0.00),
(6, 2, 0.00), (6, 4, 0.00);
