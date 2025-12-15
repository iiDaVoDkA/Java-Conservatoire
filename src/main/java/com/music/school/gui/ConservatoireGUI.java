package com.music.school.gui;

import com.music.school.data.TestDataInitializer;
import com.music.school.gui.panels.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Main JavaFX GUI Application for Conservatoire Virtuel.
 */
public class ConservatoireGUI extends Application {
    
    private BorderPane mainLayout;
    private StackPane contentArea;
    
    // Panels
    private DashboardPanel dashboardPanel;
    private StudentsPanel studentsPanel;
    private TeachersPanel teachersPanel;
    private ServicesPanel servicesPanel;
    private SchedulingPanel schedulingPanel;
    private ExamsPanel examsPanel;
    private PaymentsPanel paymentsPanel;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("♪ Conservatoire Virtuel - Music School Management System");
        
        // Ask to load test data
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Load Test Data");
        alert.setHeaderText("Initialize Test Data");
        alert.setContentText("Would you like to load test data for demonstration?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            new TestDataInitializer().initializeAll();
        }
        
        // Create main layout
        mainLayout = new BorderPane();
        mainLayout.setTop(createHeader());
        mainLayout.setLeft(createSidebar());
        
        // Create content area
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: #f5f5f5;");
        mainLayout.setCenter(contentArea);
        
        // Initialize panels
        initializePanels();
        
        // Show dashboard by default
        showDashboard();
        
        // Create scene
        Scene scene = new Scene(mainLayout, 1400, 900);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #3498db); -fx-padding: 20;");
        header.setAlignment(Pos.CENTER);
        
        Label title = new Label("♪ CONSERVATOIRE VIRTUEL ♫");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setStyle("-fx-text-fill: white;");
        
        Label subtitle = new Label("Music School Management System");
        subtitle.setFont(Font.font("Arial", 16));
        subtitle.setStyle("-fx-text-fill: #ecf0f1;");
        
        header.getChildren().addAll(title, subtitle);
        return header;
    }
    
    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #34495e; -fx-min-width: 220;");
        
        Button dashboardBtn = createSidebarButton("📊 Dashboard", this::showDashboard);
        Button studentsBtn = createSidebarButton("👨‍🎓 Students", this::showStudents);
        Button teachersBtn = createSidebarButton("👨‍🏫 Teachers", this::showTeachers);
        Button servicesBtn = createSidebarButton("📦 Services", this::showServices);
        Button schedulingBtn = createSidebarButton("📅 Scheduling", this::showScheduling);
        Button examsBtn = createSidebarButton("📝 Exams", this::showExams);
        Button paymentsBtn = createSidebarButton("💰 Payments", this::showPayments);
        
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #7f8c8d;");
        
        Button exitBtn = createSidebarButton("🚪 Exit", () -> System.exit(0));
        exitBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold;");
        
        sidebar.getChildren().addAll(
            dashboardBtn, studentsBtn, teachersBtn, servicesBtn,
            schedulingBtn, examsBtn, paymentsBtn, separator, exitBtn
        );
        
        return sidebar;
    }
    
    private Button createSidebarButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; " +
                    "-fx-font-size: 14; -fx-padding: 12; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #3498db; -fx-text-fill: white; " +
            "-fx-font-size: 14; -fx-padding: 12; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: white; " +
            "-fx-font-size: 14; -fx-padding: 12; -fx-cursor: hand;"));
        btn.setOnAction(e -> action.run());
        return btn;
    }
    
    private void initializePanels() {
        dashboardPanel = new DashboardPanel(this);
        studentsPanel = new StudentsPanel(this);
        teachersPanel = new TeachersPanel(this);
        servicesPanel = new ServicesPanel(this);
        schedulingPanel = new SchedulingPanel(this);
        examsPanel = new ExamsPanel(this);
        paymentsPanel = new PaymentsPanel(this);
    }
    
    public void showDashboard() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(dashboardPanel.getView());
        dashboardPanel.refresh();
    }
    
    public void showStudents() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(studentsPanel.getView());
        studentsPanel.refresh();
    }
    
    public void showTeachers() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(teachersPanel.getView());
        teachersPanel.refresh();
    }
    
    public void showServices() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(servicesPanel.getView());
        servicesPanel.refresh();
    }
    
    public void showScheduling() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(schedulingPanel.getView());
        schedulingPanel.refresh();
    }
    
    public void showExams() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(examsPanel.getView());
        examsPanel.refresh();
    }
    
    public void showPayments() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(paymentsPanel.getView());
        paymentsPanel.refresh();
    }
    
    public void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

