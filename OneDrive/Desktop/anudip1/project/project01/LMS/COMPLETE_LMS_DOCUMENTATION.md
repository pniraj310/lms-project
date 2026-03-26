# 🎓 Smart LMS Backend - Java + JDBC + PostgreSQL

## Complete Learning Management System

A comprehensive **Learning Management System** built with **Java**, **JDBC**, and **PostgreSQL** database featuring authentication, course management, progress tracking, quizzes, and an AI chatbot.

---

## 🧠 CORE SYSTEM ARCHITECTURE

```
User → Course → Lesson → Quiz → Progress → Chatbot
Everything revolves around user_id + course_id
```

---

## 📋 COMPLETE SYSTEM FLOW

### 👨‍🎓 STUDENT FLOW
```
Register → Login → View Courses → Enroll → 
Study Lessons → Update Progress → 
Take Quiz → Store Result → Ask Chatbot → View Dashboard
```

### 👨‍💼 ADMIN FLOW
```
Login → Add Course → Add Lessons → 
Add Quiz Questions → Add Chatbot Q&A
```

---

## 🗄️ DATABASE SCHEMA

### 8 Core Tables:
1. **users** - User accounts (Students, Instructors, Admins)
2. **courses** - Course information
3. **enrollments** - Student course enrollments with progress (0-100%)
4. **lessons** - Course lessons/modules
5. **questions** - Quiz questions with multiple choice options
6. **results** - Quiz attempt results and scores
7. **chatbot_qa** - Chatbot Q&A knowledge base
8. **lesson_completion** - Track which lessons each student completed

---

## 📦 PROJECT STRUCTURE

```
backend/src/
│
├── Models (Entity Classes)
│   ├── User.java
│   ├── Course.java
│   ├── Enrollment.java (with progress: 0-100%)
│   ├── Lesson.java
│   ├── Question.java
│   ├── QuizResult.java
│   └── ChatbotQA.java
│
├── DAOs (Database Operations)
│   ├── DatabaseConnection.java
│   ├── UserDAO.java
│   ├── CourseDAO.java
│   ├── EnrollmentDAO.java (with progress methods)
│   ├── LessonDAO.java
│   ├── QuestionDAO.java
│   ├── ResultDAO.java
│   └── ChatbotDAO.java
│
├── Services (Business Logic)
│   ├── AuthenticationService.java (Register/Login)
│   ├── EnrollmentService.java (Course enrollment)
│   ├── LessonService.java (Progress tracking)
│   ├── QuizService.java (Quiz + Scoring)
│   ├── ChatbotService.java (AI Chatbot)
│   └── DashboardService.java (Dashboards)
│
├── Controllers
│   └── LMSController.java (Main application)
│
└── Testing
    └── TestDatabase.java
```

---

## 🔐 A. AUTHENTICATION LOGIC

### Registration:
```java
boolean success = AuthenticationService.register(
    "username", "email@lms.com", "password123", "Student"
);
```
- ✅ Validates username, email (must contain @), password (min 6 chars)
- ✅ Inserts into `users` table
- ✅ Roles: Student, Instructor, Admin

### Login:
```java
User user = AuthenticationService.login("email@lms.com", "password123");
```
- ✅ Compares email + password from database
- ✅ Returns User object if match found
- ✅ Returns null if login fails

---

## 📚 B. COURSE ENROLLMENT LOGIC

### View All Courses:
```java
EnrollmentService.viewAllCourses();
```
**SQL:**
```sql
SELECT * FROM courses;
```

### Enroll in Course:
```java
EnrollmentService.enrollInCourse(userId, courseId);
```
**SQL:**
```sql
INSERT INTO enrollments(user_id, course_id, enrollment_date, progress, status) 
VALUES (?, ?, NOW(), 0.0, 'Active');
```
- ✅ Progress starts at **0%**
- ✅ Status: "Active"
- ✅ Prevents duplicate enrollments

---

## 📖 C. LESSON LEARNING LOGIC

### Show Lessons in Course:
```java
LessonService.viewCourseLessons(courseId);
```
**SQL:**
```sql
SELECT * FROM lessons WHERE course_id = ? ORDER BY lesson_order;
```

### Mark Lesson as Complete:
```java
LessonService.completeLesson(userId, courseId, lessonId);
```

**PROGRESS CALCULATION** 💡:
```
progressIncrement = 100 / totalLessonsInCourse

Example: 5 lessons in course → each lesson = 20%
- Complete Lesson 1 → Progress: 20%
- Complete Lesson 2 → Progress: 40%
- Complete Lesson 3 → Progress: 60%
- Complete Lesson 4 → Progress: 80%
- Complete Lesson 5 → Progress: 100%
```

**SQL:**
```sql
UPDATE enrollments 
SET progress = LEAST(100, progress + 20.0)
WHERE user_id = ? AND course_id = ?;
```

---

## 📊 D. PROGRESS TRACKING LOGIC

### Get Current Progress:
```java
float progress = EnrollmentService.getCourseProgress(userId, courseId);
```

### Display Progress with Visual Bar:
```java
LessonService.displayProgress(userId, courseId);
```
**Output:**
```
Progress: 60%
Progress Bar: [████████░░░░░░░░░░░]
```

---

## ✅ E. QUIZ MODULE LOGIC

### Load Quiz Questions:
```java
List<Question> questions = QuestionDAO.getQuestionsByCourse(courseId);
```
**SQL:**
```sql
SELECT * FROM questions WHERE course_id = ? ORDER BY question_id;
```

### Quiz Flow:
```
FOR EACH question:
    Show: "Q: What is Java?"
    Show: "A) Programming Language  B) Coffee  C) Island  D) None"
    Get: User's answer (A, B, C, or D)
    Compare: userAnswer == correctAnswer
END FOR
```

### Score Calculation:
```java
int score = 0;
for (int i = 0; i < questions.size(); i++) {
    if (userAnswers[i].equals(questions.get(i).getCorrectOption())) {
        score++;  // Correct answer!
    }
}
float percentage = (score * 100.0f) / totalQuestions;
```

### Save Result:
```java
QuizResult result = new QuizResult(userId, courseId, score, totalQuestions, percentage);
ResultDAO.addResult(result);
```
**SQL:**
```sql
INSERT INTO results(user_id, course_id, score, total_questions, percentage) 
VALUES (?, ?, ?, ?, ?);
```

---

## 🤖 F. CHATBOT LOGIC (AI Module) 🔥

### THE 5-SECOND CHATBOT ALGORITHM:

**Step 1: Take User Input**
```
User: "What is Java?"
```

**Step 2: Process Input**
```java
String input = userInput.toLowerCase().trim();
// input = "what is java"
```

**Step 3: Query Database with Intelligent Search**
```java
ChatbotQA result = ChatbotDAO.searchByKeyword(input);
```

**Search Algorithm:**
```
1. EXACT MATCH CHECK:
   - Search questions containing user input
   - If found → Return answer immediately
   
2. KEYWORD MATCH CHECK:
   - Split stored keywords: "java definition language"
   - Check if any keyword matches user input
   - If found → Return answer
   
3. FALLBACK:
   - If no match → "Sorry, I don't understand..."
```

**Step 4 & 5: Return Answer or Fallback**
```java
if (result != null) {
    return result.getAnswer();  // ✅ Answer found!
} else {
    return "Sorry, I don't understand...";  // Fallback
}
```

### Example Knowledge Base:

| Question | Answer | Keywords |
|----------|--------|----------|
| "What is Java?" | "Java is a programming language created by Sun Microsystems..." | "java definition what language" |
| "Explain OOP" | "Object-Oriented Programming uses objects and classes..." | "oop object oriented programming" |
| "What is inheritance?" | "Inheritance allows a class to inherit properties from another class..." | "inheritance extends parent child" |

### SQL Queries:
```sql
-- Get all Q&A for knowledge base
SELECT * FROM chatbot_qa;

-- Search by keyword
SELECT answer FROM chatbot_qa 
WHERE keywords LIKE '%java%'
LIMIT 1;
```

### Usage:
```java
// Interactive chatbot
ChatbotService.startChatbot(courseId);

// Or single question
String response = ChatbotService.answerQuestion("What is Java?", courseId);
```

---

## 🛠️ INSTALLATION & SETUP

### 1. Install PostgreSQL
```bash
# Windows: Download from https://www.postgresql.org/download/windows/
# Default: username=postgres, password=123 (in schema)
```

### 2. Create Database
```bash
psql -U postgres
```
```sql
CREATE DATABASE lms_db;
```

### 3. Run Schema
```bash
psql -U postgres -d lms_db -f backend/database/schema.sql
```

### 4. Update Connection (if needed)
Edit `backend/src/DatabaseConnection.java`:
```java
private static final String DB_URL = "jdbc:postgresql://localhost:5432/lms_db";
private static final String DB_USER = "postgres";
private static final String DB_PASSWORD = "123";
```

### 5. Add PostgreSQL JDBC Driver
- Download: `postgresql-42.7.1.jar`
- Place in: `lib/postgresql.jar`

### 6. Compile & Run

**Compile:**
```bash
cd project/project01/LMS
javac -cp .;lib/postgresql.jar -d bin backend/src/*.java frontend/src/*.java
```

**Run:**
```bash
java -cp .\bin;.\lib\postgresql.jar backend.src.LMSController
```

---

## 📖 USAGE EXAMPLES

### Example 1: Student Registration & Login
```java
// Register
AuthenticationService.register("alice", "alice@lms.com", "pass123", "Student");

// Login
User user = AuthenticationService.login("alice@lms.com", "pass123");
// Output: ✅ Login successful! Welcome, alice!
```

### Example 2: Enroll & Learn
```java
// View courses
EnrollmentService.viewAllCourses();

// Enroll in course 1
EnrollmentService.enrollInCourse(user.getUserId(), 1);
// Output: ✅ Successfully enrolled in: Java Programming

// Complete lessons
LessonService.completeLesson(user.getUserId(), 1, 1);
// Output: ✅ Lesson completed! Progress increased by: 20%

// Check progress
LessonService.displayProgress(user.getUserId(), 1);
// Output: Progress: 20% [████░░░░░░░░░░░░░░░]
```

### Example 3: Take Quiz
```java
// View questions
QuizService.viewQuizQuestions(1);

// Take quiz
String[] answers = {"A", "B", "C", "D"};
QuizService.takeQuiz(user.getUserId(), 1, answers);

// Get results
QuizService.viewQuizResults(user.getUserId(), 1);
```

### Example 4: Chat with Chatbot
```java
// Start interactive chat
ChatbotService.startChatbot(1);

// Or ask specific question
String response = ChatbotService.answerQuestion("What is Java?", 1);
```

---

## 📚 DAO METHODS REFERENCE

### UserDAO
```java
UserDAO.addUser(User)                    // Add new user
UserDAO.getUserById(int)                 // Get by ID
UserDAO.getAllUsers()                    // Get all users
UserDAO.updateUser(User)                 // Update user
UserDAO.deleteUser(int)                  // Delete user
```

### CourseDAO
```java
CourseDAO.addCourse(Course)              // Add course
CourseDAO.getCourseById(int)             // Get by ID
CourseDAO.getAllCourses()                // Get all courses
CourseDAO.updateCourse(Course)           // Update course
CourseDAO.deleteCourse(int)              // Delete course
```

### EnrollmentDAO
```java
EnrollmentDAO.addEnrollment(Enrollment)  // Enroll student
EnrollmentDAO.updateProgress(userId, courseId, increment)  // Update progress
EnrollmentDAO.setProgress(userId, courseId, newProgress)  // Set progress directly
EnrollmentDAO.getProgress(userId, courseId)  // Get progress %
EnrollmentDAO.getEnrollmentsByUser(int)  // Get user's enrollments
EnrollmentDAO.getEnrollmentsByCourse(int)  // Get course enrollments
```

### LessonDAO
```java
LessonDAO.addLesson(Lesson)              // Add lesson
LessonDAO.getLessonById(int)             // Get by ID
LessonDAO.getLessonsByCourse(int)        // Get all in course
LessonDAO.getTotalLessonsInCourse(int)   // Count lessons
LessonDAO.updateLesson(Lesson)           // Update lesson
LessonDAO.deleteLesson(int)              // Delete lesson
```

### QuestionDAO
```java
QuestionDAO.addQuestion(Question)        // Add question
QuestionDAO.getQuestionById(int)         // Get by ID
QuestionDAO.getQuestionsByCourse(int)    // Get all in course
QuestionDAO.getTotalQuestionsByCourse(int)  // Count questions
QuestionDAO.updateQuestion(Question)     // Update question
QuestionDAO.deleteQuestion(int)          // Delete question
```

### ResultDAO
```java
ResultDAO.addResult(QuizResult)          // Save quiz result
ResultDAO.getResultById(int)             // Get by ID
ResultDAO.getResultsByUserAndCourse(userId, courseId)  // Get all attempts
ResultDAO.getLatestResult(userId, courseId)  // Get latest attempt
ResultDAO.getAverageScore(userId)        // Get average across all courses
```

### ChatbotDAO
```java
ChatbotDAO.addChatbotQA(ChatbotQA)       // Add Q&A
ChatbotDAO.getQAById(int)                // Get by ID
ChatbotDAO.getQAByCourse(int)            // Get course Q&As
ChatbotDAO.getAllQA()                    // Get all Q&As
ChatbotDAO.searchByKeyword(String)       // 🔥 CORE CHATBOT LOGIC
ChatbotDAO.updateChatbotQA(ChatbotQA)    // Update Q&A
ChatbotDAO.deleteQA(int)                 // Delete Q&A
```

---

## 🎯 KEY ALGORITHMS EXPLAINED

### Algorithm 1: Progress Calculation
```
When student completes a lesson:
   totalLessons = 5
   progressIncrement = 100 / 5 = 20%
   newProgress = MIN(oldProgress + 20, 100)
   
Result: Each lesson = 20% progress
Maximum: 100%
```

### Algorithm 2: Quiz Scoring
```
totalQuestions = 4
correctAnswers = 3
percentage = (3 / 4) * 100 = 75%

Grade: A (75%)
```

### Algorithm 3: Chatbot Search
```
userInput = "what is java"
Step 1: Check exact match in questions
   → "What is Java?" found!
   → Return answer

Step 2: Check keywords (if no exact match)
   keywords = "java definition language"
   → "java" matches in input
   → Return answer

Step 3: Fallback (if nothing matches)
   → Return "Sorry, I don't understand..."
```

---

## 🧪 SAMPLE DATA INCLUDED

### Pre-loaded Data (from schema.sql):
- **4 Users**: 1 Admin, 1 Instructor, 2 Students
- **3 Courses**: Java Programming, Database Design, Web Development
- **5 Lessons**: Introduction to Java, OOP, Collections, etc.
- **4 Quiz Questions**: Multiple choice with answers
- **5 Chatbot Q&As**: Java, OOP, Inheritance, Class, etc.

---

## 📊 DATABASE QUICK REFERENCE

### Check Progress for Student:
```sql
SELECT u.username, c.course_name, e.progress 
FROM enrollments e
JOIN users u ON e.user_id = u.user_id
JOIN courses c ON e.course_id = c.course_id
WHERE u.user_id = 3;
```

### Get Quiz Scores:
```sql
SELECT user_id, score, total_questions, percentage, attempt_date 
FROM results 
WHERE user_id = 3 
ORDER BY attempt_date DESC;
```

### Search Chatbot Knowledge Base:
```sql
SELECT question, answer 
FROM chatbot_qa 
WHERE keywords LIKE '%java%';
```

---

## 🚀 FEATURES

✅ **Complete Authentication** - Register/Login with role-based access control
✅ **Course Management** - Create, view, enroll, drop courses
✅ **Progress Tracking** - Automatic calculation (0-100%)
✅ **Lesson Management** - Organize lessons by order
✅ **Quiz System** - Multiple choice with instant scoring
✅ **Intelligent Chatbot** - Keyword-based Q&A system
✅ **Role-Based Dashboards** - Student/Admin/Instructor views
✅ **Data Persistence** - All operations saved to PostgreSQL
✅ **Input Validation** - Secure data entry

---

## 🔒 Security Notes

### Current Implementation:
- ✅ SQL prepared statements (prevents SQL injection)
- ✅ Input validation
- ✅ Connection pooling with try-with-resources

### Production Recommendations:
- ⚠️ Hash passwords using BCrypt (don't store plain text)
- ⚠️ Implement SSL/TLS for database connections
- ⚠️ Add session management/JWT tokens
- ⚠️ Implement rate limiting on API endpoints
- ⚠️ Add logging and audit trail
- ⚠️ Use environment variables for credentials

---

## 🤝 PROJECT ORGANIZATION

### To Add New Feature:
1. Create **Model** class (e.g., `Feedback.java`)
2. Create **DAO** class (e.g., `FeedbackDAO.java`)
3. Create **Service** class (e.g., `FeedbackService.java`)
4. Update **LMSController** with menu option

### MVC Pattern Followed:
- **Model**: Entity classes (User, Course, etc.)
- **View**: Console output in Controller & Services
- **Controller**: LMSController handles menu flow
- **DAO**: Database layer abstraction

---

## 📄 COMPLETE FILE LISTING

**Core Files:**
- `DatabaseConnection.java` - DB connection management
- `AuthenticationService.java` - Login/Register logic
- `EnrollmentService.java` - Course enrollment logic
- `LessonService.java` - Lesson & progress logic
- `QuizService.java` - Quiz taking & scoring logic
- `ChatbotService.java` - Chatbot AI logic
- `DashboardService.java` - Dashboard generation
- `LMSController.java` - Main application

**Entity Models:**
- `User.java`, `Course.java`, `Enrollment.java`
- `Lesson.java`, `Question.java`, `QuizResult.java`, `ChatbotQA.java`

**Data Access Objects:**
- `UserDAO.java`, `CourseDAO.java`, `EnrollmentDAO.java`
- `LessonDAO.java`, `QuestionDAO.java`, `ResultDAO.java`, `ChatbotDAO.java`

---

## 📝 COMPILE & RUN COMMANDS

```bash
# Navigate to LMS directory
cd project/project01/LMS

# Compile all Java files
javac -cp .;lib/postgresql.jar -d bin backend/src/*.java frontend/src/*.java

# Run main application
java -cp .\bin;.\lib\postgresql.jar backend.src.LMSController

# To run specific class
java -cp .\bin backend.src.TestDatabase
```

---

## 🎓 Learning Outcomes

After using this LMS, you'll understand:
- ✅ JDBC database connectivity
- ✅ DAO design pattern
- ✅ Service layer architecture
- ✅ User authentication & authorization
- ✅ SQL queries & optimization
- ✅ Progress tracking algorithms
- ✅ Basic AI/Chatbot implementation
- ✅ Role-based access control

---

## 📞 SUPPORT & DOCUMENTATION

For code walkthroughs and additional documentation, refer to inline comments in each class.

**Happy Learning! 🎓**

---

**Version**: 1.0  
**Last Updated**: March 2026  
**Built With**: Java, JDBC, PostgreSQL
