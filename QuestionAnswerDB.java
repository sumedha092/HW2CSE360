package databasePart1;

import application.Question;
import application.Answer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database operations for questions and answers.
 */
public class QuestionAnswerDB {
    private static String DB_URL = "jdbc:h2:file:./FoundationDatabase;AUTO_SERVER=TRUE"; 
    private static final String USER = "sa";
    private static final String PASS = "";

    public QuestionAnswerDB() {
        try {
            createTables(); 
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

  
    public void createTables() throws SQLException {
        String createQuestionsTable = "CREATE TABLE IF NOT EXISTS QUESTIONS (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "title VARCHAR(255) NOT NULL, " +
                "description TEXT NOT NULL)";

        String createAnswersTable = "CREATE TABLE IF NOT EXISTS ANSWERS (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "questionId INT NOT NULL, " +
                "content TEXT NOT NULL, " +
                "FOREIGN KEY (questionId) REFERENCES QUESTIONS(id) ON DELETE CASCADE)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createQuestionsTable);
            stmt.execute(createAnswersTable);
            System.out.println("Tables ensured to exist.");
        }
    }

    public boolean addQuestion(String title, String description) {
        if (title.length() < 5 || description.length() < 10) {
            System.out.println(" Error: Question title or description is too short.");
            return false; 
        }
        String query = "INSERT INTO QUESTIONS (title, description) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();
        String query = "SELECT * FROM QUESTIONS";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                questions.add(new Question(
                        rs.getInt("id"),
                        "Unknown",
                        rs.getString("title"),
                        "N/A",
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public void updateQuestion(int id, String newTitle, String newDescription) throws SQLException {
        String query = "UPDATE QUESTIONS SET title = ?, description = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newTitle);
            pstmt.setString(2, newDescription);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        }
    }

    public void deleteQuestion(int id) throws SQLException {
        String checkQuery = "SELECT * FROM QUESTIONS WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                System.out.println(" Error: No question found with ID: " + id);
                return;
            }
        }
        
        String deleteQuery = "DELETE FROM QUESTIONS WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Successfully deleted question with ID: " + id);
            } else {
                System.out.println(" Failed to delete question. It may not exist.");
            }
        }
    }


    public boolean addAnswer(int questionId, String content) throws SQLException {
        if (content.length() < 10) {
            System.out.println("Error: Answer must be at least 10 characters!");
            return false; 
        }
        String query = "INSERT INTO answers (questionId, content) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            pstmt.setString(2, content);
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        }
    }


    public void updateAnswer(int answerId, String newContent) throws SQLException {
        String query = "UPDATE ANSWERS SET content = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newContent);
            pstmt.setInt(2, answerId);
            pstmt.executeUpdate();
        }
    }

    public void deleteAnswer(int answerId) throws SQLException {
        String query = "DELETE FROM ANSWERS WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, answerId);
            pstmt.executeUpdate();
        }
    }

    public List<Answer> getAnswers(int questionId) throws SQLException {
        List<Answer> answers = new ArrayList<>();
        String query = "SELECT * FROM ANSWERS WHERE questionId = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                answers.add(new Answer(rs.getInt("id"), rs.getInt("questionId"), "Unknown", rs.getString("content")));
            }
        }
        return answers;
    }

 

   
    public QuestionAnswerDB(String dbUrl) {
        this.DB_URL = dbUrl;
    }

   

    public void clearDatabase() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM QUESTIONS");
            stmt.execute("DELETE FROM ANSWERS");
            System.out.println("🧹 Test database cleaned!");
        }
    }
}
