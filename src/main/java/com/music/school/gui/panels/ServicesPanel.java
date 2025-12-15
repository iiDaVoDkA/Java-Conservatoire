package com.music.school.gui.panels;

import com.music.school.gui.ConservatoireGUI;
import com.music.school.model.person.Student;
import com.music.school.model.resource.Instrument;
import com.music.school.model.service.CoursePackage;
import com.music.school.model.service.IndividualLesson;
import com.music.school.model.service.InstrumentRental;
import com.music.school.model.service.Service;
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
import java.time.LocalDate;
import java.util.Optional;

public class ServicesPanel {
    
    private final ConservatoireGUI mainApp;
    private final DataRepository repository;
    private VBox view;
    private TableView<Service> serviceTable;
    private ComboBox<String> typeFilter;
    
    public ServicesPanel(ConservatoireGUI mainApp) {
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
        
        Label title = new Label("📦 Services Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addPackageBtn = new Button("➕ Course Package");
        addPackageBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                              "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        addPackageBtn.setOnAction(e -> showAddPackageDialog());
        
        Button addLessonBtn = new Button("➕ Individual Lesson");
        addLessonBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; " +
                             "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        addLessonBtn.setOnAction(e -> showAddLessonDialog());
        
        Button rentInstrumentBtn = new Button("➕ Rent Instrument");
        rentInstrumentBtn.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; " +
                                  "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        rentInstrumentBtn.setOnAction(e -> showRentInstrumentDialog());
        
        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                           "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> refresh());
        
        header.getChildren().addAll(title, spacer, addPackageBtn, addLessonBtn, 
                                    rentInstrumentBtn, refreshBtn);
        
        // Filter bar
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Filter by type:");
        typeFilter = new ComboBox<>(FXCollections.observableArrayList(
            "All", "Course Packages", "Individual Lessons", "Instrument Rentals"));
        typeFilter.setValue("All");
        typeFilter.setOnAction(e -> applyFilter());
        
        filterBar.getChildren().addAll(filterLabel, typeFilter);
        
        // Create table
        createTable();
        
        view.getChildren().addAll(header, filterBar, serviceTable);
        VBox.setVgrow(serviceTable, Priority.ALWAYS);
    }
    
    private void createTable() {
        serviceTable = new TableView<>();
        serviceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Service, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(100);
        
        TableColumn<Service, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> {
            Service s = data.getValue();
            String type = s instanceof CoursePackage ? "Package" :
                         s instanceof IndividualLesson ? "Lesson" :
                         s instanceof InstrumentRental ? "Rental" : "Service";
            return new SimpleStringProperty(type);
        });
        typeCol.setPrefWidth(100);
        
        TableColumn<Service, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        TableColumn<Service, String> studentCol = new TableColumn<>("Student ID");
        studentCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        studentCol.setPrefWidth(120);
        
        TableColumn<Service, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(data -> 
            new SimpleStringProperty(String.format("€%.2f", data.getValue().calculateAmount())));
        priceCol.setPrefWidth(100);
        
        TableColumn<Service, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().isPaid() ? "✓ Paid" : "✗ Unpaid"));
        statusCol.setPrefWidth(100);
        
        TableColumn<Service, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("👁 View");
            private final Button payBtn = new Button("💰 Pay");
            private final Button deleteBtn = new Button("🗑 Delete");
            
            {
                viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
                payBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                
                viewBtn.setOnAction(e -> {
                    Service service = getTableView().getItems().get(getIndex());
                    showServiceDetails(service);
                });
                
                payBtn.setOnAction(e -> {
                    Service service = getTableView().getItems().get(getIndex());
                    markAsPaid(service);
                });
                
                deleteBtn.setOnAction(e -> {
                    Service service = getTableView().getItems().get(getIndex());
                    deleteService(service);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, viewBtn, payBtn, deleteBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
        
        serviceTable.getColumns().addAll(idCol, typeCol, nameCol, studentCol, 
                                         priceCol, statusCol, actionsCol);
    }
    
    private void applyFilter() {
        String filter = typeFilter.getValue();
        var allServices = repository.getAllServices();
        
        var filtered = switch (filter) {
            case "Course Packages" -> allServices.stream()
                .filter(s -> s instanceof CoursePackage).toList();
            case "Individual Lessons" -> allServices.stream()
                .filter(s -> s instanceof IndividualLesson).toList();
            case "Instrument Rentals" -> allServices.stream()
                .filter(s -> s instanceof InstrumentRental).toList();
            default -> allServices;
        };
        
        serviceTable.setItems(FXCollections.observableArrayList(filtered));
    }
    
    private void showAddPackageDialog() {
        Dialog<CoursePackage> dialog = new Dialog<>();
        dialog.setTitle("Create Course Package");
        dialog.setHeaderText("Enter package information");
        
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
        
        TextField nameField = new TextField();
        nameField.setPromptText("Package Name");
        TextField priceField = new TextField();
        priceField.setPromptText("Price (€)");
        TextField instrumentField = new TextField();
        instrumentField.setPromptText("Instrument");
        TextField hoursField = new TextField();
        hoursField.setPromptText("Total Hours");
        TextField monthsField = new TextField();
        monthsField.setPromptText("Duration (months)");
        CheckBox unlimitedCheck = new CheckBox("Unlimited Package");
        
        grid.add(new Label("Student:"), 0, 0);
        grid.add(studentCombo, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Instrument:"), 0, 3);
        grid.add(instrumentField, 1, 3);
        grid.add(unlimitedCheck, 0, 4, 2, 1);
        grid.add(new Label("Hours:"), 0, 5);
        grid.add(hoursField, 1, 5);
        grid.add(new Label("Duration (months):"), 0, 6);
        grid.add(monthsField, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    Student student = studentCombo.getValue();
                    if (student == null) throw new Exception("Please select a student");
                    
                    LocalDate startDate = LocalDate.now();
                    int months = Integer.parseInt(monthsField.getText());
                    LocalDate endDate = startDate.plusMonths(months);
                    
                    CoursePackage pkg;
                    if (unlimitedCheck.isSelected()) {
                        pkg = new CoursePackage(
                            nameField.getText(),
                            new BigDecimal(priceField.getText()),
                            student.getId(),
                            instrumentField.getText(),
                            startDate, endDate, 3
                        );
                    } else {
                        int hours = Integer.parseInt(hoursField.getText());
                        pkg = new CoursePackage(
                            nameField.getText(),
                            new BigDecimal(priceField.getText()),
                            student.getId(),
                            hours,
                            instrumentField.getText(),
                            startDate, endDate
                        );
                        student.addPackageHours(pkg.getId(), hours);
                    }
                    
                    return pkg;
                } catch (Exception e) {
                    mainApp.showAlert("Error", "Invalid input: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        Optional<CoursePackage> result = dialog.showAndWait();
        result.ifPresent(pkg -> {
            repository.addService(pkg);
            mainApp.showAlert("Success", "Course package created!\nID: " + pkg.getId(), 
                            Alert.AlertType.INFORMATION);
            refresh();
        });
    }
    
    private void showAddLessonDialog() {
        Dialog<IndividualLesson> dialog = new Dialog<>();
        dialog.setTitle("Create Individual Lesson");
        dialog.setHeaderText("Enter lesson information");
        
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
        
        TextField instrumentField = new TextField();
        instrumentField.setPromptText("Instrument");
        TextField durationField = new TextField("60");
        durationField.setPromptText("Duration (minutes)");
        TextField priceField = new TextField();
        priceField.setPromptText("Price (€)");
        
        grid.add(new Label("Student:"), 0, 0);
        grid.add(studentCombo, 1, 0);
        grid.add(new Label("Instrument:"), 0, 1);
        grid.add(instrumentField, 1, 1);
        grid.add(new Label("Duration (min):"), 0, 2);
        grid.add(durationField, 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(priceField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    Student student = studentCombo.getValue();
                    if (student == null) throw new Exception("Please select a student");
                    
                    IndividualLesson lesson = new IndividualLesson(
                        instrumentField.getText() + " Individual Lesson",
                        new BigDecimal(priceField.getText()),
                        student.getId(),
                        instrumentField.getText(),
                        Integer.parseInt(durationField.getText()),
                        null
                    );
                    
                    return lesson;
                } catch (Exception e) {
                    mainApp.showAlert("Error", "Invalid input: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        Optional<IndividualLesson> result = dialog.showAndWait();
        result.ifPresent(lesson -> {
            repository.addService(lesson);
            mainApp.showAlert("Success", "Individual lesson created!\nID: " + lesson.getId(), 
                            Alert.AlertType.INFORMATION);
            refresh();
        });
    }
    
    private void showRentInstrumentDialog() {
        var availableInstruments = repository.getAvailableInstruments();
        if (availableInstruments.isEmpty()) {
            mainApp.showAlert("No Instruments", "No instruments available for rental.", 
                            Alert.AlertType.WARNING);
            return;
        }
        
        Dialog<InstrumentRental> dialog = new Dialog<>();
        dialog.setTitle("Rent Instrument");
        dialog.setHeaderText("Enter rental information");
        
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
        
        ComboBox<Instrument> instrumentCombo = new ComboBox<>(
            FXCollections.observableArrayList(availableInstruments));
        instrumentCombo.setPromptText("Select Instrument");
        instrumentCombo.setConverter(new javafx.util.StringConverter<Instrument>() {
            @Override
            public String toString(Instrument i) {
                return i == null ? "" : i.getName() + " - €" + i.getDailyRentalRate() + "/day";
            }
            @Override
            public Instrument fromString(String string) { return null; }
        });
        
        TextField daysField = new TextField("30");
        daysField.setPromptText("Duration (days)");
        
        grid.add(new Label("Student:"), 0, 0);
        grid.add(studentCombo, 1, 0);
        grid.add(new Label("Instrument:"), 0, 1);
        grid.add(instrumentCombo, 1, 1);
        grid.add(new Label("Duration (days):"), 0, 2);
        grid.add(daysField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    Student student = studentCombo.getValue();
                    Instrument instrument = instrumentCombo.getValue();
                    if (student == null || instrument == null) 
                        throw new Exception("Please select student and instrument");
                    
                    int days = Integer.parseInt(daysField.getText());
                    LocalDate endDate = LocalDate.now().plusDays(days);
                    
                    InstrumentRental rental = new InstrumentRental(
                        student.getId(),
                        instrument.getId(),
                        instrument.getName(),
                        instrument.getDailyRentalRate(),
                        instrument.getDepositRequired(),
                        LocalDate.now(),
                        endDate
                    );
                    
                    instrument.rentTo(student.getId());
                    return rental;
                } catch (Exception e) {
                    mainApp.showAlert("Error", "Invalid input: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        Optional<InstrumentRental> result = dialog.showAndWait();
        result.ifPresent(rental -> {
            repository.addService(rental);
            mainApp.showAlert("Success", "Instrument rented!\nID: " + rental.getId(), 
                            Alert.AlertType.INFORMATION);
            refresh();
        });
    }
    
    private void showServiceDetails(Service service) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Service Details");
        alert.setHeaderText(service.getName());
        alert.setContentText(service.getDetailedInfo());
        alert.showAndWait();
    }
    
    private void markAsPaid(Service service) {
        if (service.isPaid()) {
            mainApp.showAlert("Already Paid", "This service is already marked as paid.", 
                            Alert.AlertType.INFORMATION);
            return;
        }
        
        service.setPaid(true);
        mainApp.showAlert("Success", "Service marked as paid!", Alert.AlertType.INFORMATION);
        refresh();
    }
    
    private void deleteService(Service service) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Service");
        confirmation.setHeaderText("Are you sure?");
        confirmation.setContentText("Delete service: " + service.getName() + "?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                repository.removeService(service.getId());
                mainApp.showAlert("Success", "Service deleted!", Alert.AlertType.INFORMATION);
                refresh();
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

