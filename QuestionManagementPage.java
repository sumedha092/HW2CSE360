package application;

import databasePart1.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

public class QuestionManagementPage {
    private static final DatabaseHelper DatabaseHelper = new DatabaseHelper();
	private final QuestionAnswerDB questionAnswerDB;
    private final ObservableList<Question> questionList;

    public QuestionManagementPage(QuestionAnswerDB questionAnswerDB) {
        this.questionAnswerDB = questionAnswerDB;
        this.questionList = FXCollections.observableArrayList();
    }
    private void showAnswersPage(Question question, Stage primaryStage) { 
        Stage answerStage = new Stage();
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20;");

        Label titleLabel = new Label("Answers for: " + question.getTitle());
        ListView<Answer> answerListView = new ListView<>();
        ObservableList<Answer> answerList = FXCollections.observableArrayList();

        try {
            List<Answer> answers = questionAnswerDB.getAnswers(question.getId());
            answerList.setAll(answers);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        answerListView.setItems(answerList);
        answerListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Answer answer, boolean empty) {
                super.updateItem(answer, empty);
                if (empty || answer == null) {
                    setText(null);
                } else {
                    setText(answer.getContent()); 
                }
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> answerStage.close()); 

        layout.getChildren().addAll(titleLabel, answerListView, backButton);
        Scene scene = new Scene(layout, 500, 400);
        answerStage.setScene(scene);
        answerStage.setTitle("View Answers");
        answerStage.show();
    }
    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label titleLabel = new Label("Manage Questions");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ListView<Question> questionListView = new ListView<>(questionList);
        questionListView.setCellFactory(param -> new ListCell<Question>() {
            @Override
            protected void updateItem(Question question, boolean empty) {
                super.updateItem(question, empty);
                if (empty || question == null) {
                    setText(null);
                } else {
                    setText(question.getTitle()); // Show only the title
                }
            }
        });

        questionListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { 
                Question selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
                if (selectedQuestion != null) {
                    showAnswersPage(selectedQuestion, primaryStage); 
                }
            }
        });


        Button refreshButton = new Button("Refresh");
        Button addButton = new Button("Add Question");
        Button updateButton = new Button("Update Question");
        Button deleteButton = new Button("Delete Question");
        Button addAnswerButton = new Button("Add Answer");
        Button editAnswerButton = new Button("Edit Answer");
        Button deleteAnswerButton = new Button("Delete Answer");
        Button backButton = new Button("Back to Home");

        refreshButton.setOnAction(e -> loadQuestions());
        addButton.setOnAction(e -> showAddQuestionDialog());

        addAnswerButton.setOnAction(e -> {
            Question selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                showAddAnswerDialog(selectedQuestion);
            }
        });

        editAnswerButton.setOnAction(e -> {
            Question selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                showEditAnswerDialog(selectedQuestion);
            }
        });

        deleteAnswerButton.setOnAction(e -> {
            Question selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                showDeleteAnswerDialog(selectedQuestion); 
            }
        });

        updateButton.setOnAction(e -> {
            Question selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                showUpdateDialog(selectedQuestion);
            }
        });

        deleteButton.setOnAction(e -> {
            Question selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                try {
                    questionAnswerDB.deleteQuestion(selectedQuestion.getId());
                    loadQuestions();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        backButton.setOnAction(e -> {
            UserHomePage homePage = new UserHomePage(DatabaseHelper, questionAnswerDB);
            homePage.show(primaryStage);
        });

        layout.getChildren().addAll(titleLabel, questionListView, refreshButton, addButton, updateButton, deleteButton, addAnswerButton, editAnswerButton, deleteAnswerButton, backButton);
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Question Management");
        loadQuestions();
    }

   
    private void showAddQuestionDialog() {
        Stage dialog = new Stage();
        VBox dialogLayout = new VBox(10);
        dialogLayout.setStyle("-fx-padding: 20;");

        Label titleLabel = new Label("Add New Question");
        TextField titleField = new TextField();
        titleField.setPromptText("Enter question title");
        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Enter question description");
        Button saveButton = new Button("Save");
        Label messageLabel = new Label();

        saveButton.setOnAction(e -> {
            String newTitle = titleField.getText().trim();
            String newDescription = descriptionField.getText().trim();

            if (newTitle.length() >= 5 && newDescription.length() >= 10) {
                boolean success = questionAnswerDB.addQuestion(newTitle, newDescription); 
                if (success) {
                    loadQuestions();
                    dialog.close();
                } else {
                    messageLabel.setText("Error adding question!");
                }
            } else {
                messageLabel.setText("Title must be at least 5 characters & description at least 10!");
            }
        });

        dialogLayout.getChildren().addAll(titleLabel, titleField, descriptionField, saveButton, messageLabel);
        Scene dialogScene = new Scene(dialogLayout, 400, 300);
        dialog.setScene(dialogScene);
        dialog.setTitle("Add Question");
        dialog.show();
    }


   
    private void showDeleteAnswerDialog(Question question) {
        Stage dialog = new Stage();
        VBox dialogLayout = new VBox(10);
        dialogLayout.setStyle("-fx-padding: 20;");

        Label titleLabel = new Label("Delete Answer for: " + question.getTitle());
        ComboBox<Answer> answerDropdown = new ComboBox<>();
        try {
            List<Answer> answers = questionAnswerDB.getAnswers(question.getId());
            answerDropdown.getItems().addAll(answers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        answerDropdown.setPromptText("Select answer to delete");

        Button deleteButton = new Button("Delete");
        Label messageLabel = new Label();

        deleteButton.setOnAction(e -> {
            Answer selectedAnswer = answerDropdown.getValue();
            if (selectedAnswer != null) {
                try {
                    questionAnswerDB.deleteAnswer(selectedAnswer.getId());
                    loadQuestions();
                    dialog.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    messageLabel.setText("Error deleting answer.");
                }
            }
        });

        dialogLayout.getChildren().addAll(titleLabel, answerDropdown, deleteButton, messageLabel);
        Scene dialogScene = new Scene(dialogLayout, 400, 300);
        dialog.setScene(dialogScene);
        dialog.setTitle("Delete Answer");
        dialog.show();
    }
    private void showEditAnswerDialog(Question question) {
        Stage dialog = new Stage();
        VBox dialogLayout = new VBox(10);
        dialogLayout.setStyle("-fx-padding: 20;");

        Label titleLabel = new Label("Edit Answer for: " + question.getTitle());

        ComboBox<Answer> answerDropdown = new ComboBox<>();
        try {
            List<Answer> answers = questionAnswerDB.getAnswers(question.getId());
            answerDropdown.getItems().addAll(answers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        answerDropdown.setPromptText("Select answer to edit");

        TextArea answerField = new TextArea();
        Button saveButton = new Button("Save");
        Label messageLabel = new Label();

        answerDropdown.setOnAction(e -> {
            Answer selectedAnswer = answerDropdown.getValue();
            if (selectedAnswer != null) {
                answerField.setText(selectedAnswer.getContent());
            }
        });

        saveButton.setOnAction(e -> {
            Answer selectedAnswer = answerDropdown.getValue();
            if (selectedAnswer != null) {
                String updatedAnswer = answerField.getText().trim();
                if (updatedAnswer.length() >= 5) {
                    try {
                        questionAnswerDB.updateAnswer(selectedAnswer.getId(), updatedAnswer);
                        loadQuestions();
                        dialog.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        messageLabel.setText("Error updating answer.");
                    }
                } else {
                    messageLabel.setText("Answer must be at least 5 characters!");
                }
            }
        });

        dialogLayout.getChildren().addAll(titleLabel, answerDropdown, answerField, saveButton, messageLabel);
        Scene dialogScene = new Scene(dialogLayout, 400, 300);
        dialog.setScene(dialogScene);
        dialog.setTitle("Edit Answer");
        dialog.show();
    }
    private void showUpdateDialog(Question selectedQuestion) {
        Stage dialog = new Stage();
        VBox dialogLayout = new VBox(10);
        dialogLayout.setStyle("-fx-padding: 20;");

        Label titleLabel = new Label("Update Question");
        TextField titleField = new TextField(selectedQuestion.getTitle());
        TextArea descriptionField = new TextArea(selectedQuestion.getDescription());

        Button saveButton = new Button("Save");
        Label messageLabel = new Label();

        saveButton.setOnAction(e -> {
            String updatedTitle = titleField.getText().trim();
            String updatedDescription = descriptionField.getText().trim();
            if (updatedTitle.length() >= 5 && updatedDescription.length() >= 10) {
                try {
                    questionAnswerDB.updateQuestion(selectedQuestion.getId(), updatedTitle, updatedDescription);
                    loadQuestions();
                    dialog.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    messageLabel.setText("Error updating question.");
                }
            } else {
                messageLabel.setText("Title must be at least 5 characters & description at least 10!");
            }
        });

        dialogLayout.getChildren().addAll(titleLabel, titleField, descriptionField, saveButton, messageLabel);
        Scene dialogScene = new Scene(dialogLayout, 400, 300);
        dialog.setScene(dialogScene);
        dialog.setTitle("Update Question");
        dialog.show();
    }
    private void showAddAnswerDialog(Question question) {
        Stage dialog = new Stage();
        VBox dialogLayout = new VBox(10);
        dialogLayout.setStyle("-fx-padding: 20;");

        Label titleLabel = new Label("Add Answer to: " + question.getTitle());
        TextArea answerField = new TextArea();
        answerField.setPromptText("Enter answer");
        
        Button saveButton = new Button("Save");
        Label messageLabel = new Label();

        saveButton.setOnAction(e -> {
            String newAnswer = answerField.getText().trim(); 

            if (newAnswer.length() >= 5) { 
                try {
                    questionAnswerDB.addAnswer(question.getId(), newAnswer);
                    loadQuestions();
                    dialog.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    messageLabel.setText("Error adding answer.");
                }
            } else {
                messageLabel.setText("Answer must be at least 5 characters!");
            }
        });

        dialogLayout.getChildren().addAll(titleLabel, answerField, saveButton, messageLabel);
        Scene dialogScene = new Scene(dialogLayout, 400, 300);
        dialog.setScene(dialogScene);
        
        dialog.show();  
    }



    private void loadQuestions() {
        System.out.println(" Loading questions from database...");
        List<Question> questions = questionAnswerDB.getQuestions();
        System.out.println(" Questions loaded: " + questions.size());
        questionList.setAll(questions);
    }

} 

   

