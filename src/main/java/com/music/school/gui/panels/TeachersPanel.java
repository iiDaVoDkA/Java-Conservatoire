package com.music.school.gui.panels;

import com.music.school.gui.ConservatoireGUI;
import com.music.school.model.person.Teacher;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeachersPanel {
    
    private final ConservatoireGUI mainApp;
    private final DataRepository repository;
    private VBox view;
    private TableView<Teacher> teacherTable;
    
    public TeachersPanel(ConservatoireGUI mainApp) {
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
        
        Label title = new Label("👨‍🏫 Teachers Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addBtn = new Button("➕ Add Teacher");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                       "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        addBtn.setOnAction(e -> showAddTeacherDialog());
        
        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                           "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> refresh());
        
        header.getChildren().addAll(title, spacer, addBtn, refreshBtn);
        
        // Search bar
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search by name, email, or specialization...");
        searchField.setPrefWidth(400);
        searchField.textProperty().addListener((obs, old, newVal) -> filterTeachers(newVal));
        
        searchBar.getChildren().add(searchField);
        
        // Create table
        createTable();
        
        view.getChildren().addAll(header, searchBar, teacherTable);
        VBox.setVgrow(teacherTable, Priority.ALWAYS);
    }
    
    private void createTable() {
        teacherTable = new TableView<>();
        teacherTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Teacher, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(100);
        
        TableColumn<Teacher, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getFullName()));
        nameCol.setPrefWidth(200);
        
        TableColumn<Teacher, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);
        
        TableColumn<Teacher, String> rateCol = new TableColumn<>("Hourly Rate");
        rateCol.setCellValueFactory(data -> 
            new SimpleStringProperty(String.format("€%.2f", data.getValue().getHourlyRate())));
        rateCol.setPrefWidth(100);
        
        TableColumn<Teacher, String> specializationsCol = new TableColumn<>("Specializations");
        specializationsCol.setCellValueFactory(data -> 
            new SimpleStringProperty(String.join(", ", data.getValue().getSpecializations())));
        specializationsCol.setPrefWidth(200);
        
        TableColumn<Teacher, Void> actionsCol = new TableColumn<>("Actions");
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
                    Teacher teacher = getTableView().getItems().get(getIndex());
                    showTeacherDetails(teacher);
                });
                
                editBtn.setOnAction(e -> {
                    Teacher teacher = getTableView().getItems().get(getIndex());
                    showEditTeacherDialog(teacher);
                });
                
                deleteBtn.setOnAction(e -> {
                    Teacher teacher = getTableView().getItems().get(getIndex());
                    deleteTeacher(teacher);
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
        
        teacherTable.getColumns().addAll(idCol, nameCol, emailCol, rateCol, 
                                         specializationsCol, actionsCol);
    }
    
    private void filterTeachers(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            teacherTable.setItems(FXCollections.observableArrayList(repository.getAllTeachers()));
        } else {
            String lowerSearch = searchText.toLowerCase();
            var filtered = repository.getAllTeachers().stream()
                .filter(t -> t.getFullName().toLowerCase().contains(lowerSearch) ||
                           t.getEmail().toLowerCase().contains(lowerSearch) ||
                           t.getId().toLowerCase().contains(lowerSearch) ||
                           t.getSpecializations().stream().anyMatch(s -> s.toLowerCase().contains(lowerSearch)))
                .toList();
            teacherTable.setItems(FXCollections.observableArrayList(filtered));
        }
    }
    
    private void showAddTeacherDialog() {
        Dialog<Teacher> dialog = new Dialog<>();
        dialog.setTitle("Add New Teacher");
        dialog.setHeaderText("Enter teacher information");
        
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
        TextField rateField = new TextField();
        rateField.setPromptText("Hourly Rate (€)");
        TextField specializationsField = new TextField();
        specializationsField.setPromptText("Specializations (comma-separated)");
        
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
        grid.add(new Label("Hourly Rate:"), 0, 6);
        grid.add(rateField, 1, 6);
        grid.add(new Label("Specializations:"), 0, 7);
        grid.add(specializationsField, 1, 7);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    List<String> specializations = new ArrayList<>();
                    for (String spec : specializationsField.getText().split(",")) {
                        String trimmed = spec.trim();
                        if (!trimmed.isEmpty()) {
                            specializations.add(trimmed);
                        }
                    }
                    
                    Teacher teacher = new Teacher(
                        firstNameField.getText(),
                        lastNameField.getText(),
                        addressField.getText(),
                        dobPicker.getValue(),
                        phoneField.getText(),
                        emailField.getText(),
                        new BigDecimal(rateField.getText()),
                        specializations
                    );
                    
                    return teacher;
                } catch (Exception e) {
                    mainApp.showAlert("Error", "Invalid input: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        Optional<Teacher> result = dialog.showAndWait();
        result.ifPresent(teacher -> {
            repository.addTeacher(teacher);
            mainApp.showAlert("Success", "Teacher added successfully!\nID: " + teacher.getId(), 
                            Alert.AlertType.INFORMATION);
            refresh();
        });
    }
    
    private void showEditTeacherDialog(Teacher teacher) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit Teacher");
        dialog.setHeaderText("Edit teacher information");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField addressField = new TextField(teacher.getAddress());
        TextField phoneField = new TextField(teacher.getPhone());
        TextField emailField = new TextField(teacher.getEmail());
        TextField rateField = new TextField(teacher.getHourlyRate().toString());
        
        grid.add(new Label("Address:"), 0, 0);
        grid.add(addressField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Hourly Rate:"), 0, 3);
        grid.add(rateField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                teacher.setAddress(addressField.getText());
                teacher.setPhone(phoneField.getText());
                teacher.setEmail(emailField.getText());
                teacher.setHourlyRate(new BigDecimal(rateField.getText()));
                return true;
            }
            return false;
        });
        
        dialog.showAndWait().ifPresent(success -> {
            if (success) {
                mainApp.showAlert("Success", "Teacher updated successfully!", Alert.AlertType.INFORMATION);
                refresh();
            }
        });
    }
    
    private void showTeacherDetails(Teacher teacher) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Teacher Details");
        alert.setHeaderText(teacher.getFullName());
        alert.setContentText(teacher.getDetailedInfo());
        alert.showAndWait();
    }
    
    private void deleteTeacher(Teacher teacher) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Teacher");
        confirmation.setHeaderText("Are you sure?");
        confirmation.setContentText("Delete teacher: " + teacher.getFullName() + "?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                repository.removeTeacher(teacher.getId());
                mainApp.showAlert("Success", "Teacher deleted successfully!", Alert.AlertType.INFORMATION);
                refresh();
            }
        });
    }
    
    public void refresh() {
        teacherTable.setItems(FXCollections.observableArrayList(repository.getAllTeachers()));
    }
    
    public VBox getView() {
        return view;
    }
}

