package com.music.school.gui.panels;

import com.music.school.enums.PaymentStatus;
import com.music.school.gui.ConservatoireGUI;
import com.music.school.model.billing.Invoice;
import com.music.school.model.billing.Payment;
import com.music.school.model.person.Student;
import com.music.school.repository.DataRepository;
import com.music.school.service.PaymentService;
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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class PaymentsPanel {
    
    private final ConservatoireGUI mainApp;
    private final DataRepository repository;
    private final PaymentService paymentService;
    private VBox view;
    private TableView<Payment> paymentTable;
    private Label totalRevenueLabel;
    
    public PaymentsPanel(ConservatoireGUI mainApp) {
        this.mainApp = mainApp;
        this.repository = DataRepository.getInstance();
        this.paymentService = new PaymentService();
        createView();
    }
    
    private void createView() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        
        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("💰 Payments & Billing");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button recordPaymentBtn = new Button("➕ Record Payment");
        recordPaymentBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                                  "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        recordPaymentBtn.setOnAction(e -> showRecordPaymentDialog());
        
        Button createInvoiceBtn = new Button("📄 Create Invoice");
        createInvoiceBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; " +
                                 "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        createInvoiceBtn.setOnAction(e -> showCreateInvoiceDialog());
        
        Button monthlyReportBtn = new Button("📊 Monthly Report");
        monthlyReportBtn.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; " +
                                 "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        monthlyReportBtn.setOnAction(e -> showMonthlyReport());
        
        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                           "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> refresh());
        
        header.getChildren().addAll(title, spacer, recordPaymentBtn, createInvoiceBtn, 
                                    monthlyReportBtn, refreshBtn);
        
        // Stats bar
        HBox statsBar = new HBox(20);
        statsBar.setAlignment(Pos.CENTER_LEFT);
        statsBar.setPadding(new Insets(15));
        statsBar.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        totalRevenueLabel = new Label("Total Revenue: €0.00");
        totalRevenueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        totalRevenueLabel.setStyle("-fx-text-fill: #27ae60;");
        
        statsBar.getChildren().add(totalRevenueLabel);
        
        // Create table
        createTable();
        
        view.getChildren().addAll(header, statsBar, paymentTable);
        VBox.setVgrow(paymentTable, Priority.ALWAYS);
    }
    
    private void createTable() {
        paymentTable = new TableView<>();
        paymentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Payment, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(100);
        
        TableColumn<Payment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getPaymentDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        dateCol.setPrefWidth(150);
        
        TableColumn<Payment, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(data -> {
            Student student = repository.getStudent(data.getValue().getStudentId());
            return new SimpleStringProperty(
                student != null ? student.getFullName() : data.getValue().getStudentId());
        });
        studentCol.setPrefWidth(200);
        
        TableColumn<Payment, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> 
            new SimpleStringProperty(String.format("€%.2f", data.getValue().getAmount())));
        amountCol.setPrefWidth(100);
        
        TableColumn<Payment, String> methodCol = new TableColumn<>("Method");
        methodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        methodCol.setPrefWidth(100);
        
        TableColumn<Payment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStatus().toString()));
        statusCol.setPrefWidth(100);
        
        TableColumn<Payment, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("👁 View");
            private final Button refundBtn = new Button("↩ Refund");
            private final Button deleteBtn = new Button("🗑 Delete");
            
            {
                viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
                refundBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                
                viewBtn.setOnAction(e -> {
                    Payment payment = getTableView().getItems().get(getIndex());
                    showPaymentDetails(payment);
                });
                
                refundBtn.setOnAction(e -> {
                    Payment payment = getTableView().getItems().get(getIndex());
                    refundPayment(payment);
                });
                
                deleteBtn.setOnAction(e -> {
                    Payment payment = getTableView().getItems().get(getIndex());
                    deletePayment(payment);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, viewBtn, refundBtn, deleteBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
        
        paymentTable.getColumns().addAll(idCol, dateCol, studentCol, amountCol, 
                                         methodCol, statusCol, actionsCol);
    }
    
    private void showRecordPaymentDialog() {
        Dialog<Payment> dialog = new Dialog<>();
        dialog.setTitle("Record Payment");
        dialog.setHeaderText("Enter payment information");
        
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
        
        TextField amountField = new TextField();
        amountField.setPromptText("Amount (€)");
        
        ComboBox<String> methodCombo = new ComboBox<>(
            FXCollections.observableArrayList("Cash", "Card", "Transfer", "Check"));
        methodCombo.setValue("Card");
        
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Notes (optional)");
        notesArea.setPrefRowCount(3);
        
        grid.add(new Label("Student:"), 0, 0);
        grid.add(studentCombo, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Payment Method:"), 0, 2);
        grid.add(methodCombo, 1, 2);
        grid.add(new Label("Notes:"), 0, 3);
        grid.add(notesArea, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    Student student = studentCombo.getValue();
                    if (student == null) throw new Exception("Please select a student");
                    
                    Payment payment = paymentService.recordPayment(
                        student.getId(),
                        new BigDecimal(amountField.getText()),
                        methodCombo.getValue()
                    );
                    
                    if (!notesArea.getText().isEmpty()) {
                        payment.setNotes(notesArea.getText());
                    }
                    
                    return payment;
                } catch (Exception e) {
                    mainApp.showAlert("Error", "Invalid input: " + e.getMessage(), 
                                    Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        Optional<Payment> result = dialog.showAndWait();
        result.ifPresent(payment -> {
            mainApp.showAlert("Success", "Payment recorded!\nID: " + payment.getId(), 
                            Alert.AlertType.INFORMATION);
            refresh();
        });
    }
    
    private void showCreateInvoiceDialog() {
        Dialog<Invoice> dialog = new Dialog<>();
        dialog.setTitle("Create Invoice");
        dialog.setHeaderText("Select student for invoice");
        
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
        
        Label infoLabel = new Label(
            "An invoice will be created with all unpaid services for this student.");
        infoLabel.setWrapText(true);
        infoLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        content.getChildren().addAll(new Label("Student:"), studentCombo, infoLabel);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    Student student = studentCombo.getValue();
                    if (student == null) throw new Exception("Please select a student");
                    
                    Invoice invoice = paymentService.createInvoice(student.getId());
                    
                    // Add unpaid services to invoice
                    var unpaidServices = repository.getAllServices().stream()
                        .filter(s -> student.getId().equals(s.getStudentId()))
                        .filter(s -> !s.isPaid())
                        .toList();
                    
                    if (unpaidServices.isEmpty()) {
                        repository.removeInvoice(invoice.getId());
                        throw new Exception("No unpaid services found for this student");
                    }
                    
                    for (var service : unpaidServices) {
                        invoice.addBillableItem(service);
                    }
                    
                    return invoice;
                } catch (Exception e) {
                    mainApp.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        Optional<Invoice> result = dialog.showAndWait();
        result.ifPresent(invoice -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invoice Created");
            alert.setHeaderText("Invoice ID: " + invoice.getId());
            alert.setContentText(invoice.getDetailedInfo());
            alert.showAndWait();
            refresh();
        });
    }
    
    private void showMonthlyReport() {
        YearMonth currentMonth = YearMonth.now();
        String report = paymentService.generateMonthlyReport(currentMonth);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Monthly Revenue Report");
        alert.setHeaderText("Report for " + currentMonth);
        alert.setContentText(report);
        alert.showAndWait();
    }
    
    private void showPaymentDetails(Payment payment) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Payment Details");
        alert.setHeaderText("Payment ID: " + payment.getId());
        alert.setContentText(payment.getDetailedInfo());
        alert.showAndWait();
    }
    
    private void refundPayment(Payment payment) {
        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            mainApp.showAlert("Already Refunded", "This payment has already been refunded.", 
                            Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Refund Payment");
        confirmation.setHeaderText("Are you sure?");
        confirmation.setContentText(String.format(
            "Refund payment of €%.2f?", payment.getAmount()));
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                payment.refund();
                mainApp.showAlert("Success", "Payment refunded!", Alert.AlertType.INFORMATION);
                refresh();
            }
        });
    }
    
    private void deletePayment(Payment payment) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Payment");
        confirmation.setHeaderText("Are you sure?");
        confirmation.setContentText("Delete this payment record?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                repository.removePayment(payment.getId());
                mainApp.showAlert("Success", "Payment deleted!", Alert.AlertType.INFORMATION);
                refresh();
            }
        });
    }
    
    public void refresh() {
        paymentTable.setItems(FXCollections.observableArrayList(repository.getAllPayments()));
        
        // Update total revenue
        BigDecimal totalRevenue = repository.getAllPayments().stream()
            .filter(p -> p.getStatus() == PaymentStatus.PAID)
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        totalRevenueLabel.setText(String.format("Total Revenue: €%.2f", totalRevenue));
    }
    
    public VBox getView() {
        return view;
    }
}

