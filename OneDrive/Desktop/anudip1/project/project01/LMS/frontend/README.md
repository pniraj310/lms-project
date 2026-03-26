# Frontend Module

## Overview
This folder contains the Java Swing GUI for the Learning Management System.

## Components

### LMSFrame.java
Main GUI application with tabbed interface for:
- **Login Tab** - User login and registration
- **Users Tab** - User management
- **Courses Tab** - Course management
- **Enrollment Tab** - Enrollment tracking

## Prerequisites
- JDK 8 or higher
- No external dependencies (uses Java Swing built-in)

## How to Run

1. Compile:
   ```bash
   javac src/LMSFrame.java
   ```

2. Run:
   ```bash
   java -cp src LMSFrame
   ```

## Features
- Multi-tabbed interface for different modules
- Table views for data display
- Form inputs for data entry
- Login panel with authentication fields

## Future Enhancements
- Connect to backend database
- Implement actual login functionality
- Add CRUD operations
- Implement user role-based views
- Add data validation
