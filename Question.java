package application;

import java.util.List;

public class Question {
    private int id;
    private String asker;
    private String title;
    private String questionText;
    private List<Answer> answers;
    private String description;

    public Question(int id, String asker, String title, String questionText, String description) {
        this.id = id;
        this.asker = asker;
        this.title = title;
        this.questionText = questionText;
        this.description = description;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<Answer> getAnswers() { return answers; }
    public void setAnswers(List<Answer> answers) { this.answers = answers; }

    @Override
    public String toString() {
        return title; 
    }
}
