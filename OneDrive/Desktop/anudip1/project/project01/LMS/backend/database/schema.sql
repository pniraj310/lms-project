-- LMS Database Schema for PostgreSQL
-- Create database (run this separately if database doesn't exist)
-- CREATE DATABASE lms_db;

-- Create ENUM types
CREATE TYPE user_role AS ENUM('Student', 'Instructor', 'Admin');
CREATE TYPE enrollment_status AS ENUM('Active', 'Completed', 'Dropped');

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role user_role NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Courses Table
CREATE TABLE IF NOT EXISTS courses (
    course_id SERIAL PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,
    description TEXT,
    instructor_id INT NOT NULL,
    credits INT DEFAULT 3,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (instructor_id) REFERENCES users(user_id)
);

-- Enrollments Table (with progress tracking)
CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    course_id INT NOT NULL,
    enrollment_date DATE NOT NULL,
    progress FLOAT DEFAULT 0.0,
    status enrollment_status DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    UNIQUE (user_id, course_id)
);

-- Lessons Table
CREATE TABLE IF NOT EXISTS lessons (
    lesson_id SERIAL PRIMARY KEY,
    course_id INT NOT NULL,
    lesson_title VARCHAR(200) NOT NULL,
    lesson_content TEXT,
    lesson_order INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- Quiz Questions Table
CREATE TABLE IF NOT EXISTS questions (
    question_id SERIAL PRIMARY KEY,
    course_id INT NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(255),
    option_b VARCHAR(255),
    option_c VARCHAR(255),
    option_d VARCHAR(255),
    correct_option VARCHAR(1),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- Quiz Results Table
CREATE TABLE IF NOT EXISTS results (
    result_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    course_id INT NOT NULL,
    score INT,
    total_questions INT,
    percentage FLOAT,
    attempt_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- Chatbot Q&A Table
CREATE TABLE IF NOT EXISTS chatbot_qa (
    qa_id SERIAL PRIMARY KEY,
    course_id INT,
    question VARCHAR(500) NOT NULL,
    answer TEXT NOT NULL,
    keywords VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- Lesson Completion Tracking
CREATE TABLE IF NOT EXISTS lesson_completion (
    completion_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    lesson_id INT NOT NULL,
    completion_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (lesson_id) REFERENCES lessons(lesson_id),
    UNIQUE (user_id, lesson_id)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_course_instructor ON courses(instructor_id);
CREATE INDEX IF NOT EXISTS idx_enrollment_user ON enrollments(user_id);
CREATE INDEX IF NOT EXISTS idx_enrollment_course ON enrollments(course_id);
CREATE INDEX IF NOT EXISTS idx_lesson_course ON lessons(course_id);
CREATE INDEX IF NOT EXISTS idx_question_course ON questions(course_id);
CREATE INDEX IF NOT EXISTS idx_result_user ON results(user_id);
CREATE INDEX IF NOT EXISTS idx_chatbot_course ON chatbot_qa(course_id);

-- Sample Data (Optional)
INSERT INTO users (username, email, password, role) VALUES
('admin_user', 'admin@lms.com', 'admin123', 'Admin'),
('instructor_john', 'john@lms.com', 'john123', 'Instructor'),
('student_alice', 'alice@lms.com', 'alice123', 'Student'),
('student_bob', 'bob@lms.com', 'bob123', 'Student');

INSERT INTO courses (course_name, description, instructor_id, credits) VALUES
('Java Programming', 'Learn Java basics and advanced concepts', 2, 4),
('Database Design', 'Master SQL and database design', 2, 3),
('Web Development', 'Full-stack web development course', 2, 4);

INSERT INTO enrollments (user_id, course_id, enrollment_date, progress, status) VALUES
(3, 1, '2024-01-15', 0.0, 'Active'),
(3, 2, '2024-01-20', 0.0, 'Active'),
(4, 1, '2024-02-01', 0.0, 'Active');

-- Sample Lessons
INSERT INTO lessons (course_id, lesson_title, lesson_content, lesson_order) VALUES
(1, 'Introduction to Java', 'Learn Java fundamentals', 1),
(1, 'Object Oriented Programming', 'Understanding OOP concepts', 2),
(1, 'Collections Framework', 'Working with collections', 3),
(1, 'Exception Handling', 'Error handling in Java', 4),
(1, 'Multithreading', 'Concurrent programming in Java', 5);

-- Sample Quiz Questions
INSERT INTO questions (course_id, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES
(1, 'What is Java?', 'Programming Language', 'Coffee', 'Island', 'None', 'A'),
(1, 'Which keyword is used for inheritance?', 'implements', 'extends', 'inherits', 'extends to', 'B'),
(1, 'What is the parent class of all classes?', 'Parent', 'Main', 'Object', 'Class', 'C'),
(1, 'Which is not an OOP concept?', 'Polymorphism', 'Inheritance', 'Encapsulation', 'Compilation', 'D');

-- Sample Chatbot Q&A
INSERT INTO chatbot_qa (course_id, question, answer, keywords) VALUES
(1, 'What is Java?', 'Java is a high-level, class-based, object-oriented programming language created by Sun Microsystems. It is platform-independent and runs on the Java Virtual Machine (JVM).', 'java definition what'),
(1, 'What are the features of Java?', 'Main features: Platform independence, Object-oriented, Simple and secure, Multithreading, Dynamic, Robust, Architectural neutral', 'java features'),
(1, 'What is OOP?', 'Object-Oriented Programming (OOP) is a programming paradigm that uses objects and classes. It provides concepts like encapsulation, inheritance, and polymorphism.', 'oop object oriented programming'),
(1, 'What is a class?', 'A class is a blueprint for creating objects. It contains attributes (variables) and methods (functions).', 'class definition'),
(1, 'What is inheritance?', 'Inheritance is a mechanism by which a class can inherit properties and methods from another class using the extends keyword.', 'inheritance extends');
