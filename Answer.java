package application;

public class Answer {
    private int id;
    private int questionId;
    private String answerer;
    private String content;

    public Answer(int id, int questionId, String answerer, String content) {
        this.id = id;
        this.questionId = questionId;
        this.answerer = answerer;
        this.content = content;
    }

    public int getId() { return id; }
    public String getContent() { return content; }

    @Override
    public String toString() {
        return content; 
    }
}
