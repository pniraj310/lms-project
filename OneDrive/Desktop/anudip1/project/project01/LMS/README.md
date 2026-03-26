# Learning Management System (LMS) - Mini Project

## Project Overview
A mini Learning Management System project with Java frontend (Swing GUI) and SQL database backend. This project includes basic LMS features like user management, course management, and student enrollment tracking.

## Project Structure

```
LMS/
├── frontend/              # Java Swing GUI Application
│   ├── src/
│   │   └── LMSFrame.java  # Main GUI interface
│   └── README.md
├── backend/               # Backend logic and database
│   ├── src/
│   │   ├── DatabaseConnection.java    # Database connection handler
│   │   ├── User.java                  # User model
│   │   ├── Course.java                # Course model
│   │   └── Enrollment.java            # Enrollment model
│   ├── database/
│   │   └── schema.sql                 # Database schema
│   └── README.md
└── docs/
    └── README.md          # Documentation
```

## Features

### Basic Features (Implemented)
1. **User Management** - Create, read, update user accounts
2. **Course Management** - Manage courses and instructors
3. **Enrollment System** - Track student enrollments in courses
4. **Database Integration** - SQL backend with MySQL

## Technologies Used

- **Frontend**: Java Swing (GUI)
- **Backend**: Java with JDBC
- **Database**: PostgreSQL
- **Driver**: PostgreSQL JDBC Connector

## Setup Instructions

### Prerequisites
- JDK 8 or higher
- PostgreSQL Server installed and running
- PostgreSQL JDBC Driver (postgresql.jar)

### Database Setup
1. Connect to PostgreSQL server:
   ```bash
   psql -U postgres
   ```
2. Create the database:
   ```sql
   CREATE DATABASE lms_db;
   \c lms_db
   ```
3. Execute the SQL script from `backend/database/schema.sql`:
   ```bash
   psql -U postgres -d lms_db -f backend/database/schema.sql
   ```
4. Verify tables are created:
   ```sqlpostgresql://localhost:5432/lms_db";
private static final String DB_USER = "postgres
   ```

### Database Configuration
Update credentials in `backend/src/DatabaseConnection.java`:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/lms_db";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_password";
```

### Running the Application

#### Frontend (GUI)
1. Compile the frontend:
   ```bash
   javac -cp . frontend/src/LMSFrame.java
   ```
2. Run the application:
   ```bash
   java -cp . LMSFrame
   ```

#### Backend Operations
To use the backend classes, compile all files:
```bash
javac backend/src/*.java
```

## Database Tables

### users
| Column | Type | Description |
|--------|------|-------------|
| user_id | INT | Primary Key, Auto-increment |
| username | VARCHAR(50) | Unique username |
| email | VARCHAR(100) | Unique email |
| password | VARCHAR(100) | User password |
| role | ENUM | Student, Instructor, or Admin |

### courses
| Column | Type | Description |
|--------|------|-------------|
| course_id | INT | Primary Key, Auto-increment |
| course_name | VARCHAR(100) | Course name |
| description | TEXT | Course description |
| instructor_id | INT | Foreign Key to users |
| credits | INT | Course credits |

### enrollments
| Column | Type | Description |
|--------|------|-------------|
| enrollment_id | INT | Primary Key, Auto-increment |
| user_id | INT | Foreign Key to users |
| course_id | INT | Foreign Key to courses |
| enrollment_date | DATE | Date of enrollment |
| status | ENUM | Active, Completed, or Dropped |

## Future Enhancements

- [ ] User authentication system
- [ ] Assignment and grading system
- [ ] Quiz management
- [ ] Progress tracking
- [ ] Email notifications
- [ ] Attendance tracking
- [ ] RESTful API backend
- [ ] Web-based frontend (HTML/CSS/JS)
- [ ] Mobile application

## Project Authors
Created as a learning project for Java and Database Management.

## License
MIT License - Feel free to use and modify this project.
