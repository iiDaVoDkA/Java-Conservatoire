package com.music.school.gui.panels;

import com.music.school.gui.ConservatoireGUI;
import com.music.school.repository.DataRepository;
import com.music.school.service.PaymentService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.math.BigDecimal;

public class DashboardPanel {
    
    private final DataRepository repository;
    private VBox view;
    
    public DashboardPanel(ConservatoireGUI mainApp) {
        this.repository = DataRepository.getInstance();
        createView();
    }
    
    private void createView() {
        view = new VBox(20);
        view.setPadding(new Insets(20));
        
        Label title = new Label("📊 Dashboard");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        
        Label subtitle = new Label("System Overview");
        subtitle.setFont(Font.font("Arial", 16));
        subtitle.setStyle("-fx-text-fill: #7f8c8d;");
        
        // Statistics cards
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);
        statsGrid.setPadding(new Insets(20, 0, 0, 0));
        
        // Make columns equal width
        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(33.33);
            statsGrid.getColumnConstraints().add(col);
        }
        
        view.getChildren().addAll(title, subtitle, statsGrid);
    }
    
    private VBox createStatCard(String icon, String title, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(48));
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 16));
        titleLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        return card;
    }
    
    private VBox createInfoCard(String title, String content) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        Label contentLabel = new Label(content);
        contentLabel.setFont(Font.font("Arial", 14));
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-text-fill: #555;");
        
        card.getChildren().addAll(titleLabel, contentLabel);
        return card;
    }
    
    public void refresh() {
        // Clear and rebuild stats
        GridPane statsGrid = (GridPane) view.getChildren().get(2);
        statsGrid.getChildren().clear();
        
        // Row 1 - Main stats
        statsGrid.add(createStatCard("👨‍🎓", "Students", 
            String.valueOf(repository.getStudentCount()), "#3498db"), 0, 0);
        statsGrid.add(createStatCard("👨‍🏫", "Teachers", 
            String.valueOf(repository.getTeacherCount()), "#2ecc71"), 1, 0);
        statsGrid.add(createStatCard("📦", "Active Services", 
            String.valueOf(repository.getActiveServiceCount()), "#9b59b6"), 2, 0);
        
        // Row 2 - More stats
        statsGrid.add(createStatCard("📅", "Scheduled Activities", 
            String.valueOf(repository.getScheduledActivityCount()), "#e67e22"), 0, 1);
        statsGrid.add(createStatCard("📝", "Exams", 
            String.valueOf(repository.getAllExams().size()), "#e74c3c"), 1, 1);
        
        // Calculate total revenue
        BigDecimal totalRevenue = repository.getAllPayments().stream()
            .filter(p -> p.getStatus() == com.music.school.enums.PaymentStatus.PAID)
            .map(p -> p.getAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        statsGrid.add(createStatCard("💰", "Total Revenue", 
            String.format("€%.2f", totalRevenue), "#27ae60"), 2, 1);
        
        // Add info cards below stats
        if (view.getChildren().size() > 3) {
            view.getChildren().remove(3, view.getChildren().size());
        }
        
        HBox infoRow = new HBox(20);
        infoRow.setPadding(new Insets(20, 0, 0, 0));
        
        VBox systemInfo = createInfoCard("📌 System Status", 
            "All systems operational. " + repository.getActiveServiceCount() + 
            " active services being delivered.");
        
        VBox quickActions = createInfoCard("⚡ Quick Actions", 
            "Use the sidebar to navigate between different sections:\n" +
            "• Manage Students & Teachers\n" +
            "• Schedule Lessons\n" +
            "• Process Payments\n" +
            "• Register for Exams");
        
        infoRow.getChildren().addAll(systemInfo, quickActions);
        HBox.setHgrow(systemInfo, Priority.ALWAYS);
        HBox.setHgrow(quickActions, Priority.ALWAYS);
        
        view.getChildren().add(infoRow);
    }
    
    public VBox getView() {
        return view;
    }
}

