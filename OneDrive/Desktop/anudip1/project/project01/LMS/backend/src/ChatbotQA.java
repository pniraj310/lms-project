/**
 * ChatbotQA Model for LMS
 * Represents chatbot Q&A pairs for student support
 */
public class ChatbotQA {
    private int qaId;
    private int courseId;
    private String question;
    private String answer;
    private String keywords;

    public ChatbotQA(int qaId, int courseId, String question, String answer, String keywords) {
        this.qaId = qaId;
        this.courseId = courseId;
        this.question = question;
        this.answer = answer;
        this.keywords = keywords;
    }

    public ChatbotQA(int courseId, String question, String answer, String keywords) {
        this.courseId = courseId;
        this.question = question;
        this.answer = answer;
        this.keywords = keywords;
    }

    public ChatbotQA(String question, String answer, String keywords) {
        this.question = question;
        this.answer = answer;
        this.keywords = keywords;
    }

    // Getters and Setters
    public int getQaId() { return qaId; }
    public void setQaId(int qaId) { this.qaId = qaId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    @Override
    public String toString() {
        return "ChatbotQA{" +
                "qaId=" + qaId +
                ", question='" + question + '\'' +
                '}';
    }
}
