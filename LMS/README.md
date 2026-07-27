# 📚 Learning Management System (LMS)
## Java + JDBC + MySQL + Swing | Complete Project

---

## 🏗️ STEP 1 — Project Architecture

```
USER  ──► LoginFrame (UI)
              │
              ├─► StudentDashboard  ──► CourseDAO / QuizDAO / ResultDAO / ChatbotDAO
              ├─► TeacherDashboard  ──► CourseDAO / QuizDAO / ResultDAO
              └─► AdminDashboard    ──► UserDAO / CourseDAO / ChatbotDAO
                        │
              All DAOs  ──► DBConnection (JDBC) ──► MySQL (lms_db)
```

**Layers:**
| Layer | Package | Purpose |
|-------|---------|---------|
| UI (View) | `lms.ui` | Swing screens — Login, Student, Teacher, Admin |
| DAO (Data) | `lms.dao` | All SQL queries via PreparedStatement |
| Model | `lms.model` | Plain Java objects (User, Course, Quiz…) |
| DB | `lms.db` | DBConnection singleton |
| Util | `lms.util` | UIHelper — reusable Swing styling |

---

## 🗄️ STEP 2 — Database Schema

Run `sql/schema.sql` in MySQL Workbench or terminal.

**Tables:**
| Table | Purpose |
|-------|---------|
| `users` | Students, teachers, admins |
| `courses` | Courses created by teachers |
| `enrollments` | Student–course enrollment mapping |
| `quizzes` | Quizzes linked to courses |
| `questions` | MCQ questions for each quiz |
| `results` | Quiz attempt scores |
| `progress` | Course completion percentage per student |
| `chatbot_qa` | Keyword → Answer pairs for chatbot |

---

## 📁 STEP 3 — Project Folder Structure

```
LMS/
├── src/
│   └── lms/
│       ├── Main.java                   ← Entry point
│       ├── db/
│       │   └── DBConnection.java       ← JDBC singleton
│       ├── model/
│       │   ├── User.java
│       │   ├── Course.java
│       │   ├── Quiz.java
│       │   ├── Question.java
│       │   ├── Result.java
│       │   └── Progress.java
│       ├── dao/
│       │   ├── UserDAO.java            ← Login / Register
│       │   ├── CourseDAO.java          ← Course & Enrollment
│       │   ├── QuizDAO.java            ← Quiz & Questions
│       │   ├── ResultDAO.java          ← Results & Progress
│       │   └── ChatbotDAO.java         ← Chatbot Q&A
│       ├── ui/
│       │   ├── LoginFrame.java         ← Login + Register screen
│       │   ├── StudentDashboard.java   ← Student main screen
│       │   ├── TeacherDashboard.java   ← Teacher main screen
│       │   ├── AdminDashboard.java     ← Admin main screen
│       │   └── QuizAttemptDialog.java  ← Quiz attempt popup
│       └── util/
│           └── UIHelper.java           ← Swing styling helpers
├── sql/
│   └── schema.sql                      ← Full DB schema + sample data
├── lib/
│   └── mysql-connector-java-8.0.33.jar ← Place JDBC JAR here
├── build.sh                            ← Linux/Mac compile+run
├── build.bat                           ← Windows compile+run
└── README.md
```

---

## 🔌 STEP 4 — JDBC Connection Setup

File: `src/lms/db/DBConnection.java`

Change these 3 values to match your MySQL:
```java
private static final String URL      = "jdbc:mysql://localhost:3306/lms_db?useSSL=false&serverTimezone=UTC";
private static final String USER     = "root";
private static final String PASSWORD = "your_password_here";   // ← CHANGE THIS
```

---

## 🧑‍💻 STEP 5 — Modules Explained

### Module 1: User Authentication (`UserDAO.java + LoginFrame.java`)
- `login(username, password)` → SELECT with PreparedStatement
- `register(User)` → INSERT, checks for duplicate username/email
- Role-based routing: student → StudentDashboard, teacher → TeacherDashboard, admin → AdminDashboard

### Module 2: Course Management (`CourseDAO.java`)
- Teacher: `addCourse()`, `deleteCourse()`, `getCoursesByTeacher()`
- Student: `getAllCourses()`, `enrollStudent()`, `getEnrolledCourses()`
- Enrollment creates a progress row at 0% automatically

### Module 3: Quiz System (`QuizDAO.java + QuizAttemptDialog.java`)
- Teacher adds quizzes and MCQ questions via UI forms
- `QuizAttemptDialog` walks student through each question with radio buttons
- Auto-evaluates: compares selected answer with `correct_option` in DB

### Module 4: Result & Progress (`ResultDAO.java`)
- `saveResult()` stores score, then calls `updateProgress()`
- Progress formula: `(distinct quizzes attempted / total quizzes in course) × 100`
- Uses MySQL `INSERT ... ON DUPLICATE KEY UPDATE` for upsert

### Module 5: Chatbot (`ChatbotDAO.java`)
- Loads all keyword→answer pairs from `chatbot_qa` table into a HashMap
- `getResponse(message)` checks if the user message CONTAINS any keyword
- Admin can add new Q&A pairs via AdminDashboard

---

## 🖥️ STEP 10 — UI Overview

| Screen | Components Used |
|--------|----------------|
| LoginFrame | JTabbedPane, JTextField, JPasswordField, JComboBox |
| StudentDashboard | JTabbedPane with 6 tabs: Courses, All Courses, Quizzes, Results, Progress, Chatbot |
| TeacherDashboard | JTabbedPane with 5 tabs: Courses, Add Course, Quizzes, Add Questions, Results |
| AdminDashboard | JTabbedPane with 4 tabs: Users, Courses, Chatbot, Stats |
| QuizAttemptDialog | JDialog (modal), JRadioButton, JProgressBar |

---

## 🧪 STEP 11 — Sample Test Accounts

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Teacher | teacher1 | teach123 |
| Teacher | teacher2 | teach456 |
| Student | student1 | stud123 |
| Student | student2 | stud456 |
| Student | student3 | stud789 |

---

## 🚀 STEP 12 — How to Run the Project

### Prerequisites
1. **Java JDK 8+** installed → `java -version`
2. **MySQL 8.x** installed and running
3. **MySQL Connector/J JAR** → [Download here](https://dev.mysql.com/downloads/connector/j/) → place in `lib/`

### Step-by-step

**Step A: Create the database**
```sql
-- In MySQL Workbench or terminal:
mysql -u root -p
source /path/to/LMS/sql/schema.sql;
```

**Step B: Configure DB password**
```java
// Edit: src/lms/db/DBConnection.java
private static final String PASSWORD = "your_mysql_password";
```

**Step C: Compile and run**

*Linux / Mac:*
```bash
cd LMS
chmod +x build.sh
bash build.sh
```

*Windows:*
```cmd
cd LMS
build.bat
```

*Manual (any OS):*
```bash
# Compile
javac -cp "lib/mysql-connector-java-8.0.33.jar" -d bin $(find src -name "*.java")

# Run
java -cp "bin:lib/mysql-connector-java-8.0.33.jar" lms.Main
# Windows: use semicolon → "bin;lib/mysql-connector-java-8.0.33.jar"
```

### IntelliJ IDEA Setup
1. File → Open → select `LMS/` folder
2. Right-click `lib/mysql-connector-java-8.0.33.jar` → Add as Library
3. Right-click `src/` → Mark Directory as → Sources Root
4. Run `lms.Main`

---

## 🎓 STEP 13 — Viva Questions & Answers

### Q1. What is JDBC? Why do we use it?
**A:** JDBC (Java Database Connectivity) is an API that allows Java programs to interact with relational databases like MySQL. It provides classes like `Connection`, `PreparedStatement`, and `ResultSet` to execute SQL queries from Java code.

### Q2. What is the difference between `Statement` and `PreparedStatement`?
**A:** `PreparedStatement` is pre-compiled and uses `?` placeholders for parameters — it prevents SQL injection and is faster when the same query runs multiple times. `Statement` builds SQL as a plain string and is vulnerable to injection attacks.

### Q3. What is the Singleton design pattern? Where is it used?
**A:** Singleton ensures only ONE instance of a class is created. In this project, `DBConnection` uses Singleton so only one database connection is shared across the entire application, avoiding multiple connections overhead.

### Q4. What is the DAO pattern?
**A:** DAO (Data Access Object) separates database logic from business logic. Each DAO class (`UserDAO`, `CourseDAO`, etc.) handles all SQL for one table/entity, keeping code organized and reusable.

### Q5. What is OOP? Name the four pillars used in this project.
**A:**
- **Encapsulation**: Model classes have private fields with public getters/setters
- **Inheritance**: All Swing components extend JFrame, JDialog, JPanel
- **Polymorphism**: `ActionListener` interface implemented differently in each button
- **Abstraction**: DAOs abstract DB operations; users call `addCourse()` without knowing the SQL

### Q6. How is the quiz auto-evaluated?
**A:** Each `Question` stores a `correct_option` (A/B/C/D). In `QuizAttemptDialog`, the student selects a radio button. On submit, `question.isCorrect(selectedChar)` is called which compares the student's answer with the correct answer. The score increments for each correct answer.

### Q7. How is progress calculated?
**A:** After each quiz attempt, `updateProgress()` in `ResultDAO` runs:
1. Finds the course the quiz belongs to
2. Counts total quizzes in that course
3. Counts how many unique quizzes the student has attempted
4. Formula: `(attempted / total) × 100`
5. Updates the `progress` table using `ON DUPLICATE KEY UPDATE`

### Q8. What is SQL injection? How is it prevented here?
**A:** SQL injection is an attack where malicious SQL is injected via user input. Example: entering `' OR '1'='1` as a password. Prevented by using `PreparedStatement` with `?` placeholders — the driver escapes special characters automatically.

### Q9. What is normalization? What normal forms does this schema follow?
**A:** Normalization eliminates data redundancy. This schema follows up to 3NF (Third Normal Form) — atomic values (1NF), no partial dependencies (2NF), no transitive dependencies (3NF). Course title is stored once in `courses`, not repeated in `enrollments`.

### Q10. How does the chatbot work?
**A:** It's a rule-based keyword matching system. At startup, `ChatbotDAO` loads all keyword→answer pairs from the `chatbot_qa` table into a `HashMap`. When a message arrives, `getResponse()` checks if the message contains any keyword (case-insensitive). If matched, returns the stored answer. Otherwise, returns a default "I don't know" message.

### Q11. What is the role of `SwingUtilities.invokeLater()` in Main.java?
**A:** Swing is not thread-safe. All Swing components must be created and modified on the Event Dispatch Thread (EDT). `SwingUtilities.invokeLater()` schedules the code to run on the EDT, preventing race conditions and UI glitches.

### Q12. What is a Foreign Key? Give an example from this project.
**A:** A foreign key is a column that references the primary key of another table, enforcing referential integrity. Example: `enrollments.student_id` references `users.id` — you cannot enroll a student who doesn't exist. `ON DELETE CASCADE` means if a user is deleted, their enrollments are automatically deleted too.

### Q13. What is the difference between `executeQuery()` and `executeUpdate()`?
**A:**
- `executeQuery()` is used for SELECT statements — returns a `ResultSet`
- `executeUpdate()` is used for INSERT, UPDATE, DELETE — returns the number of rows affected

### Q14. How would you improve this project in production?
**A:**
- Hash passwords using BCrypt instead of plain text
- Add session management with token-based auth
- Use connection pooling (HikariCP) instead of a single connection
- Add input validation on all forms
- Add pagination for large result sets
- Implement email notifications for quiz results
