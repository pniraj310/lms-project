/**
 * Lesson Service for LMS
 * Handles lesson viewing and progress tracking
 */
public class LessonService {
    
    /**
     * View all lessons in a course
     */
    public static void viewCourseLessons(int courseId) {
        java.util.List<Lesson> lessons = LessonDAO.getLessonsByCourse(courseId);
        
        if (lessons.isEmpty()) {
            System.out.println("❌ No lessons available in this course!");
            return;
        }
        
        System.out.println("\n📖 COURSE LESSONS:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        for (Lesson lesson : lessons) {
            System.out.println("Lesson " + lesson.getLessonOrder() + ": " + lesson.getLessonTitle());
        }
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Total Lessons: " + lessons.size());
    }
    
    /**
     * View specific lesson content
     */
    public static void viewLesson(int lessonId) {
        Lesson lesson = LessonDAO.getLessonById(lessonId);
        
        if (lesson == null) {
            System.out.println("❌ Lesson not found!");
            return;
        }
        
        System.out.println("\n📖 LESSON CONTENT:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Title: " + lesson.getLessonTitle());
        System.out.println("Content:\n" + lesson.getLessonContent());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * Mark lesson as complete and update progress
     * Progress increments by (100 / total_lessons) for each lesson
     */
    public static boolean completeLesson(int userId, int courseId, int lessonId) {
        // Get total lessons in course
        int totalLessons = LessonDAO.getTotalLessonsInCourse(courseId);
        
        if (totalLessons == 0) {
            System.out.println("❌ No lessons in this course!");
            return false;
        }
        
        // Calculate progress increment
        float progressIncrement = 100.0f / totalLessons;
        
        // Update enrollment progress
        if (EnrollmentDAO.updateProgress(userId, courseId, progressIncrement)) {
            float newProgress = EnrollmentDAO.getProgress(userId, courseId);
            
            System.out.println("✅ Lesson completed successfully!");
            System.out.println("   Progress increased by: " + progressIncrement + "%");
            System.out.println("   Current Progress: " + newProgress + "%");
            
            // Mark lesson as completed
            markLessonAsCompleted(userId, lessonId);
            
            return true;
        }
        
        System.out.println("❌ Failed to complete lesson!");
        return false;
    }
    
    /**
     * Mark lesson as completed in lesson_completion table
     */
    private static boolean markLessonAsCompleted(int userId, int lessonId) {
        String sql = "INSERT INTO lesson_completion (user_id, lesson_id) VALUES (?, ?) ON CONFLICT (user_id, lesson_id) DO NOTHING";
        
        try (java.sql.Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, lessonId);
            pstmt.executeUpdate();
            return true;
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get user's progress in a course
     */
    public static void displayProgress(int userId, int courseId) {
        float progress = EnrollmentDAO.getProgress(userId, courseId);
        
        System.out.println("\n📊 PROGRESS TRACKING:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Course ID: " + courseId);
        System.out.println("Progress: " + String.format("%.2f", progress) + "%");
        
        // Show progress bar
        System.out.print("Progress Bar: [");
        int filled = (int) (progress / 5);
        for (int i = 0; i < 20; i++) {
            if (i < filled) System.out.print("█");
            else System.out.print("░");
        }
        System.out.println("]");
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
