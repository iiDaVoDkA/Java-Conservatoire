package com.music.school.gui.panels;

import com.music.school.enums.Level;
import com.music.school.gui.ConservatoireGUI;
import com.music.school.model.person.Student;
import com.music.school.repository.DataRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Optional;

public class StudentsPanel {
    
    private final ConservatoireGUI mainApp;
    private final DataRepository repository;
    private VBox view;
    private TableView<Student> studentTable;
    
    public StudentsPanel(ConservatoireGUI mainApp) {
        this.mainApp = mainApp;
        this.repository = DataRepository.getInstance();
        createView();
    }
    
    private void createView() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        
        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("👨‍🎓 Students Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addBtn = new Button("➕ Add Student");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                       "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        addBtn.setOnAction(e -> showAddStudentDialog());
        
        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                           "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> refresh());
        
        header.getChildren().addAll(title, spacer, addBtn, refreshBtn);
        
        // Search bar
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search by name, email, or ID...");
        searchField.setPrefWidth(400);
        searchField.textProperty().addListener((obs, old, newVal) -> filterStudents(newVal));
        
        searchBar.getChildren().add(searchField);
        
        // Create table
        createTable();
        
        view.getChildren().addAll(header, searchBar, studentTable);
        VBox.setVgrow(studentTable, Priority.ALWAYS);
    }
    
    private void createTable() {
        studentTable = new TableView<>();
        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Student, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(100);
        
        TableColumn<Student, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getFullName()));
        nameCol.setPrefWidth(200);
        
        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        nameCol.setPrefWidth(200);
        
        TableColumn<Student, String> levelCol = new TableColumn<>("Level");
        levelCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getLevel().toString()));
        levelCol.setPrefWidth(120);
        
        TableColumn<Student, String> instrumentsCol = new TableColumn<>("Instruments");
        instrumentsCol.setCellValueFactory(data -> 
            new SimpleStringProperty(String.join(", ", data.getValue().getPreferredInstruments())));
        instrumentsCol.setPrefWidth(150);
        
        TableColumn<Student, String> hoursCol = new TableColumn<>("Hours Left");
        hoursCol.setCellValueFactory(data -> 
            new SimpleStringProperty(String.valueOf(data.getValue().getRemainingHours())));
        hoursCol.setPrefWidth(100);
        
        TableColumn<Student, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(250);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("👁 View");
            private final Button editBtn = new Button("✏ Edit");
            private final Button deleteBtn = new Button("🗑 Delete");
            
            {
                viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
                editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                
                viewBtn.setOnAction(e -> {
                    Student student = getTableView().getItems().get(getIndex());
                    showStudentDetails(student);
                });
                
                editBtn.setOnAction(e -> {
                    Student student = getTableView().getItems().get(getIndex());
                    showEditStudentDialog(student);
                });
                
                deleteBtn.setOnAction(e -> {
                    Student student = getTableView().getItems().get(getIndex());
                    deleteStudent(student);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, viewBtn, editBtn, deleteBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
        
        studentTable.getColumns().addAll(idCol, nameCol, emailCol, levelCol, 
                                         instrumentsCol, hoursCol, actionsCol);
    }
    
    private void filterStudents(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            studentTable.setItems(FXCollections.observableArrayList(repository.getAllStudents()));
        } else {
            String lowerSearch = searchText.toLowerCase();
            var filtered = repository.getAllStudents().stream()
                .filter(s -> s.getFullName().toLowerCase().contains(lowerSearch) ||
                           s.getEmail().toLowerCase().contains(lowerSearch) ||
                           s.getId().toLowerCase().contains(lowerSearch))
                .toList();
            studentTable.setItems(FXCollections.observableArrayList(filtered));
        }
    }
    
    private void showAddStudentDialog() {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Add New Student");
        dialog.setHeaderText("Enter student information");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("Date of Birth");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        ComboBox<Level> levelCombo = new ComboBox<>(FXCollections.observableArrayList(Level.values()));
        levelCombo.setPromptText("Select Level");
        TextField instrumentField = new TextField();
        instrumentField.setPromptText("Preferred Instrument");
        
        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Address:"), 0, 2);
        grid.add(addressField, 1, 2);
        grid.add(new Label("Date of Birth:"), 0, 3);
        grid.add(dobPicker, 1, 3);
        grid.add(new Label("Phone:"), 0, 4);
        grid.add(phoneField, 1, 4);
        grid.add(new Label("Email:"), 0, 5);
        grid.add(emailField, 1, 5);
        grid.add(new Label("Level:"), 0, 6);
        grid.add(levelCombo, 1, 6);
        grid.add(new Label("Instrument:"), 0, 7);
        grid.add(instrumentField, 1, 7);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    Student student = new Student(
                        firstNameField.getText(),
                        lastNameField.getText(),
                        addressField.getText(),
                        dobPicker.getValue(),
                        phoneField.getText(),
                        emailField.getText(),
                        levelCombo.getValue()
                    );
                    
                    if (!instrumentField.getText().isEmpty()) {
                        student.addPreferredInstrument(instrumentField.getText());
                    }
                    
                    return student;
                } catch (Exception e) {
                    mainApp.showAlert("Error", "Invalid input: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        Optional<Student> result = dialog.showAndWait();
        result.ifPresent(student -> {
            repository.addStudent(student);
            mainApp.showAlert("Success", "Student added successfully!\nID: " + student.getId(), 
                            Alert.AlertType.INFORMATION);
            refresh();
        });
    }
    
    private void showEditStudentDialog(Student student) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit Student");
        dialog.setHeaderText("Edit student information");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField addressField = new TextField(student.getAddress());
        TextField phoneField = new TextField(student.getPhone());
        TextField emailField = new TextField(student.getEmail());
        ComboBox<Level> levelCombo = new ComboBox<>(FXCollections.observableArrayList(Level.values()));
        levelCombo.setValue(student.getLevel());
        
        grid.add(new Label("Address:"), 0, 0);
        grid.add(addressField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Level:"), 0, 3);
        grid.add(levelCombo, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                student.setAddress(addressField.getText());
                student.setPhone(phoneField.getText());
                student.setEmail(emailField.getText());
                student.setLevel(levelCombo.getValue());
                return true;
            }
            return false;
        });
        
        dialog.showAndWait().ifPresent(success -> {
            if (success) {
                mainApp.showAlert("Success", "Student updated successfully!", Alert.AlertType.INFORMATION);
                refresh();
            }
        });
    }
    
    private void showStudentDetails(Student student) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Student Details");
        alert.setHeaderText(student.getFullName());
        alert.setContentText(student.getDetailedInfo());
        alert.showAndWait();
    }
    
    private void deleteStudent(Student student) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Student");
        confirmation.setHeaderText("Are you sure?");
        confirmation.setContentText("Delete student: " + student.getFullName() + "?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                repository.removeStudent(student.getId());
                mainApp.showAlert("Success", "Student deleted successfully!", Alert.AlertType.INFORMATION);
                refresh();
            }
        });
    }
    
    public void refresh() {
        studentTable.setItems(FXCollections.observableArrayList(repository.getAllStudents()));
    }
    
    public VBox getView() {
        return view;
    }
}

