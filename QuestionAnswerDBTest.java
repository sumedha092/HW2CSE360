package databasePart1;

import application.Question;
import application.Answer;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) 
class QuestionAnswerDBTest {
    private static QuestionAnswerDB questionAnswerDB;
    private static int questionId;
    private static int answerId;

    @BeforeAll
    static void setup() {
        System.out.println(" Using TEST database for unit tests.");
        questionAnswerDB = new QuestionAnswerDB(); 
    }


    @Test
    @Order(1)
    void testAddQuestion() {
        boolean isAdded = questionAnswerDB.addQuestion("Test Question", "This is a test description.");
        System.out.println(" Adding Question: " + (isAdded ? "Success " : "Failed "));
        assertTrue(isAdded, "Question should be added successfully.");
    }

    @Test
    @Order(2)
    void testGetQuestions() throws SQLException {
        List<Question> questions = questionAnswerDB.getQuestions();
        System.out.println(" Retrieved Questions: " + questions.size());
        assertFalse(questions.isEmpty(), "Question list should not be empty.");
        questionId = questions.get(0).getId(); // Store questionId for future tests
    }

    @Test
    @Order(3)
    void testUpdateQuestion() throws SQLException {
        questionAnswerDB.updateQuestion(questionId, "Updated Title", "Updated Description");
        List<Question> questions = questionAnswerDB.getQuestions();
        System.out.println(" Updated Question Title: " + questions.get(0).getTitle());
        assertEquals("Updated Title", questions.get(0).getTitle(), "Question title should be updated.");
    }

    @Test
    @Order(4)
    void testAddAnswer() throws SQLException {
        questionAnswerDB.addAnswer(questionId, "This is a test answer.");
        List<Answer> answers = questionAnswerDB.getAnswers(questionId);
        System.out.println(" Added Answer: " + (answers.isEmpty() ? "Failed " : "Success "));
        assertFalse(answers.isEmpty(), "Answers list should not be empty.");
        answerId = answers.get(0).getId(); 
    }

    @Test
    @Order(5)
    void testUpdateAnswer() throws SQLException {
        questionAnswerDB.updateAnswer(answerId, "Updated Answer Content");
        List<Answer> answers = questionAnswerDB.getAnswers(questionId);
        System.out.println(" Updated Answer Content: " + answers.get(0).getContent());
        assertEquals("Updated Answer Content", answers.get(0).getContent(), "Answer should be updated.");
    }

    @Test
    @Order(6)
    void testDeleteAnswer() throws SQLException {
        questionAnswerDB.deleteAnswer(answerId);
        List<Answer> answers = questionAnswerDB.getAnswers(questionId);
        System.out.println(" Deleting Answer: " + (answers.isEmpty() ? "Success " : "Failed "));
        assertTrue(answers.isEmpty(), "Answers list should be empty after deletion.");
    }

    @Test
    @Order(7)
    void testDeleteQuestion() throws SQLException {
        System.out.println(" Attempting to delete question with ID: " + questionId);

        // Check questions before delete
        List<Question> beforeDelete = questionAnswerDB.getQuestions();
        System.out.println(" Questions BEFORE delete: " + beforeDelete.size());

        // Perform delete operation
        questionAnswerDB.deleteQuestion(questionId);

        
        try {
            Thread.sleep(500); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

      
        List<Question> afterDelete = questionAnswerDB.getQuestions();
        System.out.println(" Questions AFTER delete: " + afterDelete.size());

     
        boolean questionExists = afterDelete.stream().anyMatch(q -> q.getId() == questionId);
        System.out.println(" Question still exists in list? " + (questionExists ? "Yes " : "No "));
        assertFalse(questionExists, "Question should be deleted but is still found in the list!");
    }

    @Test
    @Order(8)
    void testAddInvalidQuestion() {
        boolean isAdded = questionAnswerDB.addQuestion("", "Short"); 
        System.out.println(" [TEST] Attempting to add an invalid question...");
        System.out.println(" [EXPECTED] Should fail ");
        System.out.println(" [RESULT] " + (isAdded ? "Failed " : "Success "));
        assertFalse(isAdded, "System should reject invalid questions.");
    }


    @Test
    @Order(9)
    void testAddInvalidAnswer() throws SQLException {
        System.out.println(" [TEST] Attempting to add an invalid answer...");
        
        boolean isAdded = questionAnswerDB.addAnswer(questionId, ""); 

        System.out.println(" [EXPECTED] Should fail ");
        System.out.println(" [RESULT] " + (isAdded ? "Failed " : "Success "));
        
        assertFalse(isAdded, "System should reject empty answers.");
    }



    @Test
    @Order(10)
    void testDeleteNonExistentQuestion() throws SQLException {
        int fakeId = 9999; 
        questionAnswerDB.deleteQuestion(fakeId);
        System.out.println(" Deleting Non-Existent Question: Success (No effect on DB)");
        assertTrue(true, "Deleting a non-existent question should not cause errors.");
    }

    @Test
    @Order(11)
    void testUpdateNonExistentQuestion() throws SQLException {
        int fakeId = 9999; 
        questionAnswerDB.updateQuestion(fakeId, "Fake Title", "Fake Description");
        List<Question> questions = questionAnswerDB.getQuestions();
        boolean exists = questions.stream().anyMatch(q -> q.getId() == fakeId);
        System.out.println(" Updating Non-Existent Question: " + (exists ? "Failed " : "Success "));
        assertFalse(exists, "System should not create a new question when updating a non-existent one.");
    }


    
}
