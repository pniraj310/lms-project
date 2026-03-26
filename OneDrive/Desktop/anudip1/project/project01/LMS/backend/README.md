# Backend Module

## Overview
This folder contains the backend logic and database connectivity for the Learning Management System.

## Components

### Models
- **User.java** - User entity with getters/setters
- **Course.java** - Course entity
- **Enrollment.java** - Enrollment entity

### Database
- **DatabaseConnection.java** - Handles PostgreSQL database connections
- **schema.sql** - PostgreSQL database schema and initial data

## Prerequisites
- JDK 8 or higher
- PostgreSQL Server 12+
- PostgreSQL JDBC Driver (postgresql.jar)

## Database Setup

1. Connect to PostgreSQL:
   ```bash
   psql -U postgres
   ```

2. Create the database:
   ```sql
   CREATE DATABASE lms_db;
   \c lms_db
   ```

3. Execute the schema:
   ```bash
   psql -U postgres -d lms_db -f backend/database/schema.sql
   ```

4. Verify setup:
   ```sql
   \dt
   ```

## Configuration
Update database credentials in `DatabaseConnection.java`:
```java
DB_URL = "jdbc:postgresql://localhost:5432/lms_db"
DB_USER = "postgres"
DB_PASSWORD = "your_password"
```

## How to Compile

```bash
javac -cp .:postgresql.jar src/*.java
```

## How to Use

Import and use the models:
```java
import java.sql.Connection;

Connection conn = DatabaseConnection.getConnection();
// Perform database operations
DatabaseConnection.closeConnection(conn);
```

## Future Enhancements
- Add DAO (Data Access Object) classes
- Implement service layer
- Add business logic
- Error handling and logging
- Transaction management
