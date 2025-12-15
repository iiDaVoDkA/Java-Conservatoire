package com.music.school.gui.panels;

import com.music.school.gui.ConservatoireGUI;
import com.music.school.model.person.Student;
import com.music.school.model.person.Teacher;
import com.music.school.model.resource.Room;
import com.music.school.model.scheduling.Lesson;
import com.music.school.model.scheduling.ScheduledActivity;
import com.music.school.repository.DataRepository;
import com.music.school.service.SchedulingService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SchedulingPanel {
    
    private final ConservatoireGUI mainApp;
    private final DataRepository repository;
    private final SchedulingService schedulingService;
    private VBox view;
    private TableView<ScheduledActivity> activityTable;
    private DatePicker dateFilter;
    
    public SchedulingPanel(ConservatoireGUI mainApp) {
        this.mainApp = mainApp;
        this.repository = DataRepository.getInstance();
        this.schedulingService = new SchedulingService();
        createView();
    }
    
    private void createView() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        
        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("📅 Scheduling Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button scheduleLessonBtn = new Button("➕ Schedule Lesson");
        scheduleLessonBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                                   "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        scheduleLessonBtn.setOnAction(e -> showScheduleLessonDialog());
        
        Button bookRoomBtn = new Button("📍 Book Practice Room");
        bookRoomBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; " +
                            "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        bookRoomBtn.setOnAction(e -> showBookRoomDialog());
        
        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                           "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> refresh());
        
        header.getChildren().addAll(title, spacer, scheduleLessonBtn, bookRoomBtn, refreshBtn);
        
        // Filter bar
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Filter by date:");
        dateFilter = new DatePicker();
        dateFilter.setPromptText("Select date");
        dateFilter.setOnAction(e -> applyFilter());
        
        Button clearFilterBtn = new Button("Clear Filter");
        clearFilterBtn.setOnAction(e -> {
            dateFilter.setValue(null);
            applyFilter();
        });
        
        Button todayBtn = new Button("📆 Today");
        todayBtn.setStyle("-fx-background-color: #16a085; -fx-text-fill: white; -fx-cursor: hand;");
        todayBtn.setOnAction(e -> {
            dateFilter.setValue(LocalDate.now());
            applyFilter();
        });
        
        filterBar.getChildren().addAll(filterLabel, dateFilter, clearFilterBtn, todayBtn);
        
        // Create table
        createTable();
        
        view.getChildren().addAll(header, filterBar, activityTable);
        VBox.setVgrow(activityTable, Priority.ALWAYS);
    }
    
    private void createTable() {
        activityTable = new TableView<>();
        activityTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<ScheduledActivity, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(100);
        
        TableColumn<ScheduledActivity, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getActivityType()));
        typeCol.setPrefWidth(120);
        
        TableColumn<ScheduledActivity, String> dateTimeCol = new TableColumn<>("Date & Time");
        dateTimeCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getScheduledDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        dateTimeCol.setPrefWidth(150);
        
        TableColumn<ScheduledActivity, String> durationCol = new TableColumn<>("Duration");
        durationCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getDurationMinutes() + " min"));
        durationCol.setPrefWidth(100);
        
        TableColumn<ScheduledActivity, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(data -> {
            Room room = repository.getRoom(data.getValue().getRoomId());
            return new SimpleStringProperty(room != null ? room.getName() : "N/A");
        });
        roomCol.setPrefWidth(150);
        
        TableColumn<ScheduledActivity, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStatus().toString()));
        statusCol.setPrefWidth(120);
        
        TableColumn<ScheduledActivity, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(250);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("👁 View");
            private final Button completeBtn = new Button("✓ Complete");
            private final Button cancelBtn = new Button("✗ Cancel");
            
            {
                viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
                completeBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
                cancelBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                
                viewBtn.setOnAction(e -> {
                    ScheduledActivity activity = getTableView().getItems().get(getIndex());
                    showActivityDetails(activity);
                });
                
                completeBtn.setOnAction(e -> {
                    ScheduledActivity activity = getTableView().getItems().get(getIndex());
                    completeActivity(activity);
                });
                
                cancelBtn.setOnAction(e -> {
                    ScheduledActivity activity = getTableView().getItems().get(getIndex());
                    cancelActivity(activity);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, viewBtn, completeBtn, cancelBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
        
        activityTable.getColumns().addAll(idCol, typeCol, dateTimeCol, durationCol, 
                                          roomCol, statusCol, actionsCol);
    }
    
    private void applyFilter() {
        LocalDate filterDate = dateFilter.getValue();
        var allActivities = repository.getAllScheduledActivities();
        
        if (filterDate == null) {
            activityTable.setItems(FXCollections.observableArrayList(allActivities));
        } else {
            var filtered = allActivities.stream()
                .filter(a -> a.getScheduledDateTime().toLocalDate().equals(filterDate))
                .toList();
            activityTable.setItems(FXCollections.observableArrayList(filtered));
        }
    }
    
    private void showScheduleLessonDialog() {
        Dialog<Lesson> dialog = new Dialog<>();
        dialog.setTitle("Schedule Lesson");
        dialog.setHeaderText("Enter lesson details");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        ComboBox<Teacher> teacherCombo = new ComboBox<>(
            FXCollections.observableArrayList(repository.getAllTeachers()));
        teacherCombo.setPromptText("Select Teacher");
        teacherCombo.setConverter(new javafx.util.StringConverter<Teacher>() {
            @Override
            public String toString(Teacher t) {
                return t == null ? "" : t.getFullName() + " - €" + t.getHourlyRate() + "/hr";
            }
            @Override
            public Teacher fromString(String string) { return null; }
        });
        
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
        
        ComboBox<Room> roomCombo = new ComboBox<>(
            FXCollections.observableArrayList(repository.getAvailableRooms()));
        roomCombo.setPromptText("Select Room");
        roomCombo.setConverter(new javafx.util.StringConverter<Room>() {
            @Override
            public String toString(Room r) {
                return r == null ? "" : r.getName() + " (Cap: " + r.getCapacity() + ")";
            }
            @Override
            public Room fromString(String string) { return null; }
        });
        
        TextField instrumentField = new TextField();
        instrumentField.setPromptText("Instrument");
        
        DatePicker datePicker = new DatePicker(LocalDate.now());
        
        ComboBox<String> timeCombo = new ComboBox<>();
        for (int h = 8; h <= 20; h++) {
            for (int m = 0; m < 60; m += 30) {
                timeCombo.getItems().add(String.format("%02d:%02d", h, m));
            }
        }
        timeCombo.setValue("10:00");
        
        ComboBox<Integer> durationCombo = new ComboBox<>(
            FXCollections.observableArrayList(30, 45, 60, 90, 120));
        durationCombo.setValue(60);
        
        CheckBox groupCheck = new CheckBox("Group Lesson");
        
        grid.add(new Label("Teacher:"), 0, 0);
        grid.add(teacherCombo, 1, 0);
        grid.add(new Label("Student:"), 0, 1);
        grid.add(studentCombo, 1, 1);
        grid.add(new Label("Room:"), 0, 2);
        grid.add(roomCombo, 1, 2);
        grid.add(new Label("Instrument:"), 0, 3);
        grid.add(instrumentField, 1, 3);
        grid.add(new Label("Date:"), 0, 4);
        grid.add(datePicker, 1, 4);
        grid.add(new Label("Time:"), 0, 5);
        grid.add(timeCombo, 1, 5);
        grid.add(new Label("Duration (min):"), 0, 6);
        grid.add(durationCombo, 1, 6);
        grid.add(groupCheck, 0, 7, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    Teacher teacher = teacherCombo.getValue();
                    Student student = studentCombo.getValue();
                    Room room = roomCombo.getValue();
                    
                    if (teacher == null || student == null || room == null) {
                        throw new Exception("Please select teacher, student, and room");
                    }
                    
                    String[] timeParts = timeCombo.getValue().split(":");
                    LocalDateTime dateTime = LocalDateTime.of(
                        datePicker.getValue(),
                        LocalTime.of(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]))
                    );
                    
                    List<String> studentIds = new ArrayList<>();
                    studentIds.add(student.getId());
                    
                    return schedulingService.scheduleLesson(
                        teacher.getId(),
                        studentIds,
                        room.getId(),
                        instrumentField.getText(),
                        dateTime,
                        durationCombo.getValue()
                    );
                } catch (Exception e) {
                    mainApp.showAlert("Error", "Failed to schedule: " + e.getMessage(), 
                                    Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        Optional<Lesson> result = dialog.showAndWait();
        result.ifPresent(lesson -> {
            mainApp.showAlert("Success", "Lesson scheduled!\nID: " + lesson.getId(), 
                            Alert.AlertType.INFORMATION);
            refresh();
        });
    }
    
    private void showBookRoomDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Book Practice Room");
        dialog.setHeaderText("Enter booking details");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
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
        
        ComboBox<Room> roomCombo = new ComboBox<>(
            FXCollections.observableArrayList(repository.getAvailableRooms()));
        roomCombo.setPromptText("Select Room");
        roomCombo.setConverter(new javafx.util.StringConverter<Room>() {
            @Override
            public String toString(Room r) {
                return r == null ? "" : r.getName();
            }
            @Override
            public Room fromString(String string) { return null; }
        });
        
        DatePicker datePicker = new DatePicker(LocalDate.now());
        
        ComboBox<String> timeCombo = new ComboBox<>();
        for (int h = 8; h <= 20; h++) {
            for (int m = 0; m < 60; m += 30) {
                timeCombo.getItems().add(String.format("%02d:%02d", h, m));
            }
        }
        timeCombo.setValue("10:00");
        
        ComboBox<Integer> durationCombo = new ComboBox<>(
            FXCollections.observableArrayList(30, 60, 90, 120, 180));
        durationCombo.setValue(60);
        
        TextField rateField = new TextField("10.00");
        rateField.setPromptText("Hourly Rate (€)");
        
        TextField purposeField = new TextField("Practice");
        purposeField.setPromptText("Purpose");
        
        grid.add(new Label("Student:"), 0, 0);
        grid.add(studentCombo, 1, 0);
        grid.add(new Label("Room:"), 0, 1);
        grid.add(roomCombo, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Time:"), 0, 3);
        grid.add(timeCombo, 1, 3);
        grid.add(new Label("Duration (min):"), 0, 4);
        grid.add(durationCombo, 1, 4);
        grid.add(new Label("Hourly Rate:"), 0, 5);
        grid.add(rateField, 1, 5);
        grid.add(new Label("Purpose:"), 0, 6);
        grid.add(purposeField, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    Student student = studentCombo.getValue();
                    Room room = roomCombo.getValue();
                    
                    if (student == null || room == null) {
                        throw new Exception("Please select student and room");
                    }
                    
                    String[] timeParts = timeCombo.getValue().split(":");
                    LocalDateTime dateTime = LocalDateTime.of(
                        datePicker.getValue(),
                        LocalTime.of(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]))
                    );
                    
                    schedulingService.bookRoom(
                        student.getId(),
                        room.getId(),
                        dateTime,
                        durationCombo.getValue(),
                        new BigDecimal(rateField.getText()),
                        purposeField.getText()
                    );
                    
                    return true;
                } catch (Exception e) {
                    mainApp.showAlert("Error", "Failed to book: " + e.getMessage(), 
                                    Alert.AlertType.ERROR);
                    return false;
                }
            }
            return false;
        });
        
        dialog.showAndWait().ifPresent(success -> {
            if (success) {
                mainApp.showAlert("Success", "Room booked successfully!", 
                                Alert.AlertType.INFORMATION);
                refresh();
            }
        });
    }
    
    private void showActivityDetails(ScheduledActivity activity) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Activity Details");
        alert.setHeaderText(activity.getActivityType());
        alert.setContentText(activity.getDetailedInfo());
        alert.showAndWait();
    }
    
    private void completeActivity(ScheduledActivity activity) {
        try {
            schedulingService.completeActivity(activity.getId());
            mainApp.showAlert("Success", "Activity marked as completed!", 
                            Alert.AlertType.INFORMATION);
            refresh();
        } catch (Exception e) {
            mainApp.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void cancelActivity(ScheduledActivity activity) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Cancel Activity");
        confirmation.setHeaderText("Are you sure?");
        confirmation.setContentText("Cancel this activity?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    schedulingService.cancelActivity(activity.getId());
                    mainApp.showAlert("Success", "Activity cancelled!", 
                                    Alert.AlertType.INFORMATION);
                    refresh();
                } catch (Exception e) {
                    mainApp.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    public void refresh() {
        applyFilter();
    }
    
    public VBox getView() {
        return view;
    }
}

