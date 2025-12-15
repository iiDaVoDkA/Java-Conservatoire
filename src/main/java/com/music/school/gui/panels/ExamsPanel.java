package com.music.school.gui.panels;

import com.music.school.enums.ExamResult;
import com.music.school.gui.ConservatoireGUI;
import com.music.school.model.exam.Exam;
import com.music.school.model.person.Student;
import com.music.school.repository.DataRepository;
import com.music.school.service.ExamService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ExamsPanel {
    
    private final ConservatoireGUI mainApp;
    private final DataRepository repository;
    private final ExamService examService;
    private VBox view;
    private TableView<Exam> examTable;
    private CheckBox upcomingOnlyCheck;
    
    public ExamsPanel(ConservatoireGUI mainApp) {
        this.mainApp = mainApp;
        this.repository = DataRepository.getInstance();
        this.examService = new ExamService();
        createView();
    }
    
    private void createView() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        
        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("📝 Exams Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button createExamBtn = new Button("➕ Create Exam");
        createExamBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                              "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        createExamBtn.setOnAction(e -> showCreateExamDialog());
        
        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                           "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> refresh());
        
        header.getChildren().addAll(title, spacer, createExamBtn, refreshBtn);
        
        // Filter bar
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        
        upcomingOnlyCheck = new CheckBox("Show upcoming exams only");
        upcomingOnlyCheck.setOnAction(e -> applyFilter());
        
        filterBar.getChildren().add(upcomingOnlyCheck);
        
        // Create table
        createTable();
        
        view.getChildren().addAll(header, filterBar, examTable);
        VBox.setVgrow(examTable, Priority.ALWAYS);
    }
    
    private void createTable() {
        examTable = new TableView<>();
        examTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Exam, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(100);
        
        TableColumn<Exam, String> nameCol = new TableColumn<>("Exam Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(250);
        
        TableColumn<Exam, String> instrumentCol = new TableColumn<>("Instrument");
        instrumentCol.setCellValueFactory(new PropertyValueFactory<>("instrument"));
        instrumentCol.setPrefWidth(120);
        
        TableColumn<Exam, String> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getExamDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        dateCol.setPrefWidth(150);
        
        TableColumn<Exam, String> capacityCol = new TableColumn<>("Capacity");
        capacityCol.setCellValueFactory(data -> 
            new SimpleStringProperty(
                data.getValue().getRegisteredStudentIds().size() + "/" + 
                data.getValue().getMaxCapacity()));
        capacityCol.setPrefWidth(100);
        
        TableColumn<Exam, String> feeCol = new TableColumn<>("Fee");
        feeCol.setCellValueFactory(data -> 
            new SimpleStringProperty(String.format("€%.2f", data.getValue().getRegistrationFee())));
        feeCol.setPrefWidth(100);
        
        TableColumn<Exam, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(300);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("👁 View");
            private final Button registerBtn = new Button("➕ Register");
            private final Button recordBtn = new Button("✏ Record Result");
            private final Button statsBtn = new Button("📊 Stats");
            
            {
                viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
                registerBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
                recordBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-cursor: hand;");
                statsBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-cursor: hand;");
                
                viewBtn.setOnAction(e -> {
                    Exam exam = getTableView().getItems().get(getIndex());
                    showExamDetails(exam);
                });
                
                registerBtn.setOnAction(e -> {
                    Exam exam = getTableView().getItems().get(getIndex());
                    showRegisterStudentDialog(exam);
                });
                
                recordBtn.setOnAction(e -> {
                    Exam exam = getTableView().getItems().get(getIndex());
                    showRecordResultDialog(exam);
                });
                
                statsBtn.setOnAction(e -> {
                    Exam exam = getTableView().getItems().get(getIndex());
                    showExamStatistics(exam);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(3, viewBtn, registerBtn, recordBtn, statsBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
        
        examTable.getColumns().addAll(idCol, nameCol, instrumentCol, dateCol, 
                                      capacityCol, feeCol, actionsCol);
    }
    
    private void applyFilter() {
        var allExams = repository.getAllExams();
        
        if (upcomingOnlyCheck.isSelected()) {
            var upcoming = allExams.stream()
                .filter(e -> e.getExamDateTime().isAfter(LocalDateTime.now()))
                .toList();
            examTable.setItems(FXCollections.observableArrayList(upcoming));
        } else {
            examTable.setItems(FXCollections.observableArrayList(allExams));
        }
    }
    
    private void showCreateExamDialog() {
        Dialog<Exam> dialog = new Dialog<>();
        dialog.setTitle("Create Exam");
        dialog.setHeaderText("Enter exam information");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Exam Name");
        
        TextField instrumentField = new TextField();
        instrumentField.setPromptText("Instrument");
        
        DatePicker datePicker = new DatePicker(LocalDate.now().plusMonths(1));
        
        ComboBox<String> timeCombo = new ComboBox<>();
        for (int h = 8; h <= 20; h++) {
            for (int m = 0; m < 60; m += 30) {
                timeCombo.getItems().add(String.format("%02d:%02d", h, m));
            }
        }
        timeCombo.setValue("09:00");
        
        TextField capacityField = new TextField("20");
        capacityField.setPromptText("Maximum Capacity");
        
        TextField feeField = new TextField();
        feeField.setPromptText("Registration Fee (€)");
        
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description (optional)");
        descriptionArea.setPrefRowCount(3);
        
        grid.add(new Label("Exam Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Instrument:"), 0, 1);
        grid.add(instrumentField, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Time:"), 0, 3);
        grid.add(timeCombo, 1, 3);
        grid.add(new Label("Max Capacity:"), 0, 4);
        grid.add(capacityField, 1, 4);
        grid.add(new Label("Registration Fee:"), 0, 5);
        grid.add(feeField, 1, 5);
        grid.add(new Label("Description:"), 0, 6);
        grid.add(descriptionArea, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    String[] timeParts = timeCombo.getValue().split(":");
                    LocalDateTime dateTime = LocalDateTime.of(
                        datePicker.getValue(),
                        LocalTime.of(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]))
                    );
                    
                    Exam exam = examService.createExam(
                        nameField.getText(),
                        instrumentField.getText(),
                        dateTime,
                        Integer.parseInt(capacityField.getText()),
                        new BigDecimal(feeField.getText())
                    );
                    
                    if (!descriptionArea.getText().isEmpty()) {
                        exam.setDescription(descriptionArea.getText());
                    }
                    
                    return exam;
                } catch (Exception e) {
                    mainApp.showAlert("Error", "Invalid input: " + e.getMessage(), 
                                    Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        Optional<Exam> result = dialog.showAndWait();
        result.ifPresent(exam -> {
            mainApp.showAlert("Success", "Exam created!\nID: " + exam.getId(), 
                            Alert.AlertType.INFORMATION);
            refresh();
        });
    }
    
    private void showRegisterStudentDialog(Exam exam) {
        if (!exam.canRegister()) {
            mainApp.showAlert("Registration Closed", 
                            "This exam is full or registration is closed.", 
                            Alert.AlertType.WARNING);
            return;
        }
        
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Register Student for Exam");
        dialog.setHeaderText(exam.getName());
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        ComboBox<Student> studentCombo = new ComboBox<>(
            FXCollections.observableArrayList(repository.getAllStudents()));
        studentCombo.setPromptText("Select Student");
        studentCombo.setConverter(new javafx.util.StringConverter<Student>() {
            @Override
            public String toString(Student s) {
                return s == null ? "" : s.getFullName() + " (" + s.getId() + ")";
            }
            @Override
            public Student fromString(String string) { return null; }
        });
        
        Label infoLabel = new Label(String.format(
            "Available spots: %d\nRegistration fee: €%.2f",
            exam.getAvailableSpots(), exam.getRegistrationFee()));
        
        content.getChildren().addAll(new Label("Select Student:"), studentCombo, infoLabel);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    Student student = studentCombo.getValue();
                    if (student == null) throw new Exception("Please select a student");
                    
                    examService.registerStudentForExam(exam.getId(), student.getId());
                    return true;
                } catch (Exception e) {
                    mainApp.showAlert("Error", "Registration failed: " + e.getMessage(), 
                                    Alert.AlertType.ERROR);
                    return false;
                }
            }
            return false;
        });
        
        dialog.showAndWait().ifPresent(success -> {
            if (success) {
                mainApp.showAlert("Success", "Student registered for exam!", 
                                Alert.AlertType.INFORMATION);
                refresh();
            }
        });
    }
    
    private void showRecordResultDialog(Exam exam) {
        if (exam.getRegisteredStudentIds().isEmpty()) {
            mainApp.showAlert("No Registrations", 
                            "No students registered for this exam.", 
                            Alert.AlertType.WARNING);
            return;
        }
        
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Record Exam Result");
        dialog.setHeaderText(exam.getName());
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        ComboBox<String> studentCombo = new ComboBox<>();
        for (String studentId : exam.getRegisteredStudentIds()) {
            Student student = repository.getStudent(studentId);
            String display = studentId;
            if (student != null) {
                display = student.getFullName() + " (" + studentId + ")";
            }
            studentCombo.getItems().add(display);
        }
        studentCombo.setPromptText("Select Student");
        
        ComboBox<ExamResult> resultCombo = new ComboBox<>(
            FXCollections.observableArrayList(ExamResult.values()));
        resultCombo.setPromptText("Select Result");
        
        TextField scoreField = new TextField();
        scoreField.setPromptText("Score (0-100, optional)");
        
        grid.add(new Label("Student:"), 0, 0);
        grid.add(studentCombo, 1, 0);
        grid.add(new Label("Result:"), 0, 1);
        grid.add(resultCombo, 1, 1);
        grid.add(new Label("Score:"), 0, 2);
        grid.add(scoreField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    String studentDisplay = studentCombo.getValue();
                    if (studentDisplay == null) throw new Exception("Please select a student");
                    
                    // Extract student ID from display string
                    String studentId = studentDisplay.substring(
                        studentDisplay.lastIndexOf("(") + 1, 
                        studentDisplay.lastIndexOf(")"));
                    
                    ExamResult result = resultCombo.getValue();
                    if (result == null) throw new Exception("Please select a result");
                    
                    Integer score = null;
                    if (!scoreField.getText().isEmpty()) {
                        score = Integer.parseInt(scoreField.getText());
                    }
                    
                    examService.recordExamResult(exam.getId(), studentId, result, score);
                    return true;
                } catch (Exception e) {
                    mainApp.showAlert("Error", "Failed to record result: " + e.getMessage(), 
                                    Alert.AlertType.ERROR);
                    return false;
                }
            }
            return false;
        });
        
        dialog.showAndWait().ifPresent(success -> {
            if (success) {
                mainApp.showAlert("Success", "Exam result recorded!", 
                                Alert.AlertType.INFORMATION);
                refresh();
            }
        });
    }
    
    private void showExamDetails(Exam exam) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exam Details");
        alert.setHeaderText(exam.getName());
        alert.setContentText(exam.getDetailedInfo());
        alert.showAndWait();
    }
    
    private void showExamStatistics(Exam exam) {
        String stats = examService.getExamStatistics(exam.getId());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exam Statistics");
        alert.setHeaderText(exam.getName());
        alert.setContentText(stats);
        alert.showAndWait();
    }
    
    public void refresh() {
        applyFilter();
    }
    
    public VBox getView() {
        return view;
    }
}

