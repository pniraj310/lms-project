# 📚 Learning Management System (LMS)
### Built with Java + JDBC + MySQL + Swing
#### Developed by: Niraj Patel | SochTech | Final Year Project

---

## 🎯 Project Overview

A fully functional **desktop-based Learning Management System** built using core Java technologies. This project demonstrates real-world implementation of **JDBC, DAO Pattern, Singleton, OOP Principles, and Swing GUI**.

---

## 🏗️ Architecture

```
USER  ──► LoginFrame (UI)
              │
              ├─► StudentDashboard  ──► CourseDAO / QuizDAO / ResultDAO / ChatbotDAO
              ├─► TeacherDashboard  ──► CourseDAO / QuizDAO / ResultDAO
              └─► AdminDashboard    ──► UserDAO / CourseDAO / ChatbotDAO
                        │
              All DAOs  ──► DBConnection (JDBC Singleton) ──► MySQL (lms_db)
```

| Layer | Package | Purpose |
|---|---|---|
| UI (View) | `lms.ui` | Swing screens — Login, Student, Teacher, Admin |
| DAO (Data) | `lms.dao` | All SQL queries via PreparedStatement |
| Model | `lms.model` | Plain Java objects (User, Course, Quiz…) |
| DB | `lms.db` | DBConnection Singleton |
| Util | `lms.util` | UIHelper — reusable Swing styling |

---

## ✨ Features

### 👨‍🎓 Student
- Register and Login
- Browse all available courses
- Enroll in courses
- Attempt quizzes (MCQ auto-evaluation)
- View quiz results and scores
- Track course completion progress (%)
- Chat with LMS bot

### 🎓 Teacher
- Add and manage courses
- Create quizzes for courses
- Add MCQ questions to quizzes
- View all student results

### 🛡️ Admin
- View and manage all users
- View and delete all courses
- Manage chatbot Q&A pairs
- View system statistics dashboard

---

## 🔧 Tech Stack

| Technology | Purpose |
|---|---|
| Java (JDK 8+) | Core language |
| JDBC | Database connectivity |
| MySQL 8.x | Relational database |
| Swing | Desktop GUI |
| PreparedStatement | SQL injection prevention |
| Singleton Pattern | Single DB connection |
| DAO Pattern | Separation of concerns |

---

## 📁 Project Structure

```
LMS/
├── src/lms/
│   ├── Main.java                    ← Entry point (EDT)
│   ├── db/DBConnection.java         ← JDBC Singleton
│   ├── model/                       ← POJOs
│   │   ├── User.java
│   │   ├── Course.java
│   │   ├── Quiz.java
│   │   ├── Question.java
│   │   ├── Result.java
│   │   └── Progress.java
│   ├── dao/                         ← Data Access Objects
│   │   ├── UserDAO.java
│   │   ├── CourseDAO.java
│   │   ├── QuizDAO.java
│   │   ├── ResultDAO.java
│   │   └── ChatbotDAO.java
│   ├── ui/                          ← Swing screens
│   │   ├── LoginFrame.java
│   │   ├── StudentDashboard.java
│   │   ├── TeacherDashboard.java
│   │   ├── AdminDashboard.java
│   │   └── QuizAttemptDialog.java
│   └── util/UIHelper.java           ← Styling utilities
├── sql/schema.sql                   ← Full DB schema + sample data
├── lib/                             ← Place JDBC JAR here
├── build.bat                        ← Windows build script
└── README.md
```

---

## 🚀 How to Run

### Prerequisites
- Java JDK 8+ → `java -version`
- MySQL 8.x running
- MySQL Connector/J 8.0.33 JAR → [Download here](https://dev.mysql.com/downloads/connector/j/) → place in `lib/`

### Step 1 — Create Database
```sql
mysql -u root -p
source /path/to/LMS/sql/schema.sql;
```

### Step 2 — Configure Password
Edit `src/lms/db/DBConnection.java`:
```java
private static final String PASSWORD = "your_mysql_password";
```

### Step 3 — Compile & Run (Windows)
```
cd LMS
build.bat
```

### IntelliJ IDEA Setup
1. File → Open → select `LMS/` folder
2. Right-click `lib/mysql-connector-java-8.0.33.jar` → Add as Library
3. Right-click `src/` → Mark Directory as → Sources Root
4. Run `lms.Main`

---

## 🧪 Test Accounts

| Role | Username | Password |
|---|---|---|
| Admin | admin | admin123 |
| Teacher | teacher1 | teach123 |
| Teacher | teacher2 | teach456 |
| Student | student1 | stud123 |
| Student | student2 | stud456 |
| Student | student3 | stud789 |

---

## 🎓 Key Concepts Implemented

| Concept | Where Used |
|---|---|
| Singleton Pattern | `DBConnection.java` |
| DAO Pattern | All DAO classes |
| Encapsulation | All Model classes |
| Inheritance | Swing components extend JFrame |
| Polymorphism | ActionListener implementations |
| PreparedStatement | All SQL queries (SQL injection prevention) |
| UPSERT | `ResultDAO.updateProgress()` — ON DUPLICATE KEY UPDATE |
| JOIN queries | CourseDAO, QuizDAO, ResultDAO |
| Event Dispatch Thread | `Main.java` — SwingUtilities.invokeLater() |
| Role-based routing | `LoginFrame.handleLogin()` |
| HashMap | `ChatbotDAO` — in-memory keyword store |

---

## 📊 Database Schema

| Table | Purpose |
|---|---|
| users | Students, teachers, admins |
| courses | Courses created by teachers |
| enrollments | Student–course enrollment mapping |
| quizzes | Quizzes linked to courses |
| questions | MCQ questions for each quiz |
| results | Quiz attempt scores |
| progress | Course completion percentage per student |
| chatbot_qa | Keyword → Answer pairs for chatbot |

---

## 🔐 Security Features

- `PreparedStatement` prevents SQL Injection on all queries
- Role-based access control (Student / Teacher / Admin)
- Password validation on registration
- Admin protection (cannot delete admin user)

---

## 🔮 Future Enhancements

- BCrypt password hashing
- Email notifications for quiz results
- Connection pooling with HikariCP
- Export results to PDF/Excel
- REST API version with Spring Boot

---

## 👨‍💻 Developer

**Niraj Patel**
- GitHub: [github.com/nirajpatel](#)
- Brand: SochTech
- Institute: G.V. Acharya Institute of Engineering & Technology
