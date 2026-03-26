import java.util.Scanner;
import java.time.LocalDate;

/**
 * SMART LMS CONTROLLER
 * Main application class that orchestrates all LMS functionality
 * 
 * COMPLETE SYSTEM FLOW:
 * 👨‍🎓 STUDENT: Register → Login → View Courses → Enroll → 
 *               Study Lessons → Update Progress → Take Quiz → 
 *               Store Result → Ask Chatbot → View Dashboard
 * 
 * 👨‍💼 ADMIN: Login → Manage Courses → Add Lessons → 
 *           Add Questions → Manage Users → View Dashboard
 */
public class LMSController {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;
    
    public static void main(String[] args) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║     🎓 WELCOME TO SMART LMS (Java + SQL + JDBC) 🎓     ║");
        System.out.println("║          Learning Management System v1.0               ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
        
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = authenticationMenu();
            } else {
                running = mainMenu();
            }
        }
        
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║      Thank you for using Smart LMS! Goodbye! 👋        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
        scanner.close();
    }
    
    /**
     * Authentication Menu - Register/Login
     */
    private static boolean authenticationMenu() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║              AUTHENTICATION MENU                        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("1. Register (New User)");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("\nChoose an option (1-3): ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                registerUser();
                break;
            case "2":
                loginUser();
                break;
            case "3":
                return false;
            default:
                System.out.println("❌ Invalid option! Please try again.");
        }
        
        return true;
    }
    
    /**
     * Register new user
     */
    private static void registerUser() {
        System.out.println("\n📝 USER REGISTRATION");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        System.out.print("Username: ");
        String username = scanner.nextLine();
        
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        System.out.println("Role: (Student/Instructor/Admin)");
        System.out.print("Select Role (default=Student): ");
        String role = scanner.nextLine();
        if (role.isEmpty()) role = "Student";
        
        AuthenticationService.register(username, email, password, role);
    }
    
    /**
     * Login user
     */
    private static void loginUser() {
        System.out.println("\n🔐 LOGIN");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        User user = AuthenticationService.login(email, password);
        if (user != null) {
            currentUser = user;
        }
    }
    
    /**
     * Main Menu - After Login
     */
    private static boolean mainMenu() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║                    MAIN MENU                            ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("Welcome, " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        System.out.println();
        System.out.println("1. View Dashboard");
        System.out.println("2. Explore Courses");
        
        if (currentUser.getRole().equals("Student")) {
            System.out.println("3. My Courses");
            System.out.println("4. Study Lessons");
            System.out.println("5. Take Quiz");
            System.out.println("6. Chat with Chatbot");
        } else if (currentUser.getRole().equals("Admin")) {
            System.out.println("3. Add Course");
            System.out.println("4. Add Lesson");
            System.out.println("5. Add Quiz Question");
            System.out.println("6. Manage Chatbot Q&A");
        } else if (currentUser.getRole().equals("Instructor")) {
            System.out.println("3. My Courses");
            System.out.println("4. Add Lesson");
            System.out.println("5. View Students");
        }
        
        System.out.println("0. Logout");
        System.out.print("\nChoose an option: ");
        
        String choice = scanner.nextLine();
        
        switch (currentUser.getRole()) {
            case "Student":
                return studentMenuHandler(choice);
            case "Admin":
                return adminMenuHandler(choice);
            case "Instructor":
                return instructorMenuHandler(choice);
            default:
                System.out.println("❌ Unknown role!");
                return true;
        }
    }
    
    /**
     * Student Menu Handler
     */
    private static boolean studentMenuHandler(String choice) {
        switch (choice) {
            case "1":
                DashboardService.displayStudentDashboard(currentUser);
                break;
            case "2":
                EnrollmentService.viewAllCourses();
                break;
            case "3":
                EnrollmentService.viewMyEnrolledCourses(currentUser.getUserId());
                studentEnrollmentMenu();
                break;
            case "4":
                lessonMenu();
                break;
            case "5":
                quizMenu();
                break;
            case "6":
                ChatbotService.startChatbot(1);
                break;
            case "0":
                currentUser = null;
                System.out.println("✅ Logged out successfully!");
                break;
            default:
                System.out.println("❌ Invalid option!");
        }
        return true;
    }
    
    /**
     * Student Enrollment Menu
     */
    private static void studentEnrollmentMenu() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║            STUDENT ENROLLMENT MENU                     ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("1. Enroll in a Course");
        System.out.println("2. Drop a Course");
        System.out.println("3. View Course Progress");
        System.out.println("4. Back to Main Menu");
        System.out.print("\nChoose an option: ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                System.out.print("Enter Course ID to enroll: ");
                try {
                    int courseId = Integer.parseInt(scanner.nextLine());
                    EnrollmentService.enrollInCourse(currentUser.getUserId(), courseId);
                } catch (NumberFormatException e) {
                    System.out.println("❌ Invalid Course ID!");
                }
                break;
            case "2":
                System.out.print("Enter Course ID to drop: ");
                try {
                    int courseId = Integer.parseInt(scanner.nextLine());
                    EnrollmentService.dropCourse(currentUser.getUserId(), courseId);
                } catch (NumberFormatException e) {
                    System.out.println("❌ Invalid Course ID!");
                }
                break;
            case "3":
                System.out.print("Enter Course ID: ");
                try {
                    int courseId = Integer.parseInt(scanner.nextLine());
                    LessonService.displayProgress(currentUser.getUserId(), courseId);
                } catch (NumberFormatException e) {
                    System.out.println("❌ Invalid Course ID!");
                }
                break;
            case "4":
                break;
            default:
                System.out.println("❌ Invalid option!");
        }
    }
    
    /**
     * Lesson Menu
     */
    private static void lessonMenu() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║              LESSON MENU                               ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.print("Enter Course ID: ");
        
        try {
            int courseId = Integer.parseInt(scanner.nextLine());
            LessonService.viewCourseLessons(courseId);
            
            System.out.print("\nEnter Lesson ID to view (0 to go back): ");
            int lessonId = Integer.parseInt(scanner.nextLine());
            
            if (lessonId > 0) {
                LessonService.viewLesson(lessonId);
                
                System.out.print("Mark this lesson as complete? (yes/no): ");
                String response = scanner.nextLine();
                
                if (response.equalsIgnoreCase("yes")) {
                    LessonService.completeLesson(currentUser.getUserId(), courseId, lessonId);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input!");
        }
    }
    
    /**
     * Quiz Menu
     */
    private static void quizMenu() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║              QUIZ MENU                                 ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.print("Enter Course ID: ");
        
        try {
            int courseId = Integer.parseInt(scanner.nextLine());
            java.util.List<Question> questions = QuestionDAO.getQuestionsByCourse(courseId);
            
            if (questions.isEmpty()) {
                System.out.println("❌ No quiz questions available!");
                return;
            }
            
            System.out.println("\n❓ QUIZ - Answer the following questions:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            String[] answers = new String[questions.size()];
            
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                System.out.println("\nQ" + (i + 1) + ": " + q.getQuestionText());
                System.out.println("   A) " + q.getOptionA());
                System.out.println("   B) " + q.getOptionB());
                System.out.println("   C) " + q.getOptionC());
                System.out.println("   D) " + q.getOptionD());
                
                System.out.print("Your Answer: ");
                answers[i] = scanner.nextLine();
            }
            
            // Take quiz and get results
            QuizService.takeQuiz(currentUser.getUserId(), courseId, answers);
            
            // View results
            QuizService.viewQuizResults(currentUser.getUserId(), courseId);
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input!");
        }
    }
    
    /**
     * Admin Menu Handler
     */
    private static boolean adminMenuHandler(String choice) {
        switch (choice) {
            case "1":
                DashboardService.displayAdminDashboard(currentUser);
                break;
            case "2":
                EnrollmentService.viewAllCourses();
                break;
            case "3":
                addCourseMenu();
                break;
            case "4":
                addLessonMenu();
                break;
            case "5":
                addQuestionMenu();
                break;
            case "6":
                manageChatbotMenu();
                break;
            case "0":
                currentUser = null;
                System.out.println("✅ Logged out successfully!");
                break;
            default:
                System.out.println("❌ Invalid option!");
        }
        return true;
    }
    
    /**
     * Add Course Menu
     */
    private static void addCourseMenu() {
        System.out.println("\n📚 ADD NEW COURSE");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        System.out.print("Course Name: ");
        String courseName = scanner.nextLine();
        
        System.out.print("Description: ");
        String description = scanner.nextLine();
        
        System.out.print("Credits: ");
        int credits = Integer.parseInt(scanner.nextLine());
        
        Course newCourse = new Course(courseName, description, currentUser.getUserId(), credits);
        
        if (CourseDAO.addCourse(newCourse)) {
            System.out.println("✅ Course added successfully!");
        } else {
            System.out.println("❌ Failed to add course!");
        }
    }
    
    /**
     * Add Lesson Menu
     */
    private static void addLessonMenu() {
        System.out.println("\n📖 ADD NEW LESSON");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        System.out.print("Course ID: ");
        int courseId = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Lesson Title: ");
        String title = scanner.nextLine();
        
        System.out.print("Lesson Content: ");
        String content = scanner.nextLine();
        
        System.out.print("Lesson Order: ");
        int order = Integer.parseInt(scanner.nextLine());
        
        Lesson newLesson = new Lesson(courseId, title, content, order);
        
        if (LessonDAO.addLesson(newLesson)) {
            System.out.println("✅ Lesson added successfully!");
        } else {
            System.out.println("❌ Failed to add lesson!");
        }
    }
    
    /**
     * Add Question Menu
     */
    private static void addQuestionMenu() {
        System.out.println("\n❓ ADD NEW QUIZ QUESTION");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        System.out.print("Course ID: ");
        int courseId = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Question Text: ");
        String text = scanner.nextLine();
        
        System.out.print("Option A: ");
        String optionA = scanner.nextLine();
        
        System.out.print("Option B: ");
        String optionB = scanner.nextLine();
        
        System.out.print("Option C: ");
        String optionC = scanner.nextLine();
        
        System.out.print("Option D: ");
        String optionD = scanner.nextLine();
        
        System.out.print("Correct Option (A/B/C/D): ");
        String correct = scanner.nextLine().toUpperCase();
        
        Question newQuestion = new Question(courseId, text, optionA, optionB, optionC, optionD, correct);
        
        if (QuestionDAO.addQuestion(newQuestion)) {
            System.out.println("✅ Question added successfully!");
        } else {
            System.out.println("❌ Failed to add question!");
        }
    }
    
    /**
     * Manage Chatbot Menu
     */
    private static void manageChatbotMenu() {
        System.out.println("\n🤖 MANAGE CHATBOT Q&A");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("1. View Chatbot Q&A");
        System.out.println("2. Add Q&A");
        System.out.println("3. Test Chatbot");
        System.out.println("4. Back to Main Menu");
        System.out.print("\nChoose an option: ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                System.out.print("Enter Course ID (0 for all): ");
                int courseId = Integer.parseInt(scanner.nextLine());
                if (courseId == 0) {
                    java.util.List<ChatbotQA> allQA = ChatbotDAO.getAllQA();
                    System.out.println("\n📋 Total Q&As: " + allQA.size());
                } else {
                    ChatbotService.viewCourseQA(courseId);
                }
                break;
            case "2":
                System.out.print("Course ID: ");
                courseId = Integer.parseInt(scanner.nextLine());
                
                System.out.print("Question: ");
                String question = scanner.nextLine();
                
                System.out.print("Answer: ");
                String answer = scanner.nextLine();
                
                System.out.print("Keywords (comma-separated): ");
                String keywords = scanner.nextLine();
                
                ChatbotService.addChatbotQA(courseId, question, answer, keywords);
                break;
            case "3":
                System.out.print("Ask a question: ");
                String userQuestion = scanner.nextLine();
                String response = ChatbotService.answerQuestion(userQuestion, 1);
                System.out.println("Chatbot: " + response);
                break;
            case "4":
                break;
            default:
                System.out.println("❌ Invalid option!");
        }
    }
    
    /**
     * Instructor Menu Handler
     */
    private static boolean instructorMenuHandler(String choice) {
        switch (choice) {
            case "1":
                DashboardService.displayInstructorDashboard(currentUser);
                break;
            case "2":
                EnrollmentService.viewAllCourses();
                break;
            case "3":
                // View instructor's courses - already in dashboard
                System.out.println("Instructor courses are shown in the dashboard.");
                break;
            case "4":
                addLessonMenu();
                break;
            case "5":
                // View students
                System.out.println("\nStudent viewing for instructors - coming soon!");
                break;
            case "0":
                currentUser = null;
                System.out.println("✅ Logged out successfully!");
                break;
            default:
                System.out.println("❌ Invalid option!");
        }
        return true;
    }
}
