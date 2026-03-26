import java.util.Scanner;

/**
 * Chatbot Service for LMS
 * Intelligent chatbot that answers student questions
 * 
 * CORE CHATBOT LOGIC:
 * 1. Take user input
 * 2. Convert to lowercase and extract keywords
 * 3. Search database for matching questions
 * 4. Return best answer
 * 5. If no match, show fallback message
 */
public class ChatbotService {
    
    /**
     * Start interactive chatbot session
     */
    public static void startChatbot(int courseId) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n🤖 CHATBOT - ASK YOUR QUESTIONS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Type 'exit' to end chat");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        while (true) {
            System.out.print("\nYou: ");
            String userInput = scanner.nextLine();
            
            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("🤖 Chatbot: Goodbye! See you next time! 👋");
                break;
            }
            
            String response = answerQuestion(userInput, courseId);
            System.out.println("🤖 Chatbot: " + response);
        }
    }
    
    /**
     * CORE CHATBOT LOGIC - Process user input and find answer
     * 
     * Steps:
     * 1. Take input
     * 2. Process input (lowercase, extract keywords)
     * 3. Query database
     * 4. Return response
     * 5. Fallback if no match
     */
    public static String answerQuestion(String userInput, int courseId) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return "I didn't catch that. Can you please rephrase your question?";
        }
        
        // Step 1: Take Input (already done in parameter)
        // Step 2: Process Input
        String processedInput = userInput.toLowerCase().trim();
        
        // Step 3: Query Database
        ChatbotQA qa = ChatbotDAO.searchByKeyword(processedInput);
        
        // Step 4 & 5: Return Response or Fallback
        if (qa != null) {
            return qa.getAnswer();
        } else {
            return "Sorry, I don't understand that question. Try asking about course topics like 'What is Java?' or 'Tell me about OOP'.";
        }
    }
    
    /**
     * Manually search for a chatbot answer by keyword
     */
    public static void searchChatbotAnswer(String keyword) {
        ChatbotQA qa = ChatbotDAO.searchByKeyword(keyword);
        
        if (qa != null) {
            System.out.println("\n💡 ANSWER FOUND:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("Q: " + qa.getQuestion());
            System.out.println("\nA: " + qa.getAnswer());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        } else {
            System.out.println("❌ No answer found for: " + keyword);
        }
    }
    
    /**
     * View all chatbot Q&A for a course
     */
    public static void viewCourseQA(int courseId) {
        java.util.List<ChatbotQA> qaList = ChatbotDAO.getQAByCourse(courseId);
        
        if (qaList.isEmpty()) {
            System.out.println("❌ No Q&A available for this course!");
            return;
        }
        
        System.out.println("\n❓ COURSE Q&A KNOWLEDGE BASE:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        for (ChatbotQA qa : qaList) {
            System.out.println("Q: " + qa.getQuestion());
            System.out.println("A: " + qa.getAnswer());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }
    
    /**
     * Add new Q&A to chatbot knowledge base (Admin only)
     */
    public static boolean addChatbotQA(int courseId, String question, String answer, String keywords) {
        ChatbotQA newQA = new ChatbotQA(courseId, question, answer, keywords);
        
        if (ChatbotDAO.addChatbotQA(newQA)) {
            System.out.println("✅ Q&A added to knowledge base!");
            return true;
        } else {
            System.out.println("❌ Failed to add Q&A!");
            return false;
        }
    }
}
