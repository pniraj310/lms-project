/**
 * Quiz Service for LMS
 * Handles quiz taking, scoring, and result saving
 */
public class QuizService {
    
    /**
     * View all quiz questions for a course
     */
    public static void viewQuizQuestions(int courseId) {
        java.util.List<Question> questions = QuestionDAO.getQuestionsByCourse(courseId);
        
        if (questions.isEmpty()) {
            System.out.println("❌ No quiz questions available for this course!");
            return;
        }
        
        System.out.println("\n❓ QUIZ QUESTIONS FOR COURSE " + courseId);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Total Questions: " + questions.size());
        
        for (Question q : questions) {
            System.out.println("\nQ" + q.getQuestionId() + ": " + q.getQuestionText());
            System.out.println("   A) " + q.getOptionA());
            System.out.println("   B) " + q.getOptionB());
            System.out.println("   C) " + q.getOptionC());
            System.out.println("   D) " + q.getOptionD());
        }
    }
    
    /**
     * Take a quiz and calculate score
     * @param userId Student ID
     * @param courseId Course ID
     * @param userAnswers List of user's answers (A, B, C, or D)
     * @return QuizResult object with score and percentage
     */
    public static QuizResult takeQuiz(int userId, int courseId, String[] userAnswers) {
        java.util.List<Question> questions = QuestionDAO.getQuestionsByCourse(courseId);
        
        if (questions.isEmpty()) {
            System.out.println("❌ No questions available for this course!");
            return null;
        }
        
        if (userAnswers == null || userAnswers.length != questions.size()) {
            System.out.println("❌ Number of answers does not match number of questions!");
            return null;
        }
        
        int score = 0;
        System.out.println("\n📝 QUIZ RESULTS:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // Compare answers
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String userAnswer = userAnswers[i].toUpperCase();
            String correctAnswer = q.getCorrectOption().toUpperCase();
            
            if (userAnswer.equals(correctAnswer)) {
                score++;
                System.out.println("Q" + (i + 1) + ": ✅ CORRECT");
            } else {
                System.out.println("Q" + (i + 1) + ": ❌ INCORRECT (Correct: " + correctAnswer + ")");
            }
        }
        
        // Calculate percentage
        int totalQuestions = questions.size();
        float percentage = (score * 100.0f) / totalQuestions;
        
        // Create quiz result
        QuizResult result = new QuizResult(userId, courseId, score, totalQuestions, percentage);
        
        // Save result to database
        if (ResultDAO.addResult(result)) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("Score: " + score + "/" + totalQuestions);
            System.out.println("Percentage: " + String.format("%.2f", percentage) + "%");
            System.out.println("✅ Result saved successfully!");
        }
        
        return result;
    }
    
    /**
     * View quiz results for a user in a course
     */
    public static void viewQuizResults(int userId, int courseId) {
        java.util.List<QuizResult> results = ResultDAO.getResultsByUserAndCourse(userId, courseId);
        
        if (results.isEmpty()) {
            System.out.println("❌ No quiz attempts yet!");
            return;
        }
        
        System.out.println("\n📊 QUIZ RESULTS HISTORY:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        for (QuizResult result : results) {
            System.out.println("Attempt Date: " + result.getAttemptDate());
            System.out.println("Score: " + result.getScore() + "/" + result.getTotalQuestions());
            System.out.println("Percentage: " + String.format("%.2f", result.getPercentage()) + "%");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }
    
    /**
     * Get latest quiz result for a user in a course
     */
    public static QuizResult getLatestQuizResult(int userId, int courseId) {
        return ResultDAO.getLatestResult(userId, courseId);
    }
    
    /**
     * Get user's average score across all courses
     */
    public static float getAverageScore(int userId) {
        return ResultDAO.getAverageScore(userId);
    }
}
