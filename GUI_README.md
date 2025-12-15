# Conservatoire Virtuel - GUI Application

## Overview

This is a comprehensive JavaFX-based GUI application for the Conservatoire Virtuel Music School Management System. The application provides a modern, user-friendly interface to manage all aspects of a music school.

## Features

### 📊 Dashboard
- Real-time statistics overview
- Active students, teachers, and services count
- Total revenue tracking
- System status monitoring

### 👨‍🎓 Students Management
- View all students in a table format
- Add new students with detailed information
- Edit student details (address, phone, email, level)
- View detailed student information
- Delete students
- Search and filter students

### 👨‍🏫 Teachers Management
- View all teachers with their specializations
- Add new teachers with hourly rates
- Edit teacher information
- View detailed teacher profiles
- Delete teachers
- Filter by specialization

### 📦 Services Management
- Course packages (standard and unlimited)
- Individual lessons
- Instrument rentals
- Filter by service type
- Mark services as paid
- Create new services with student assignment

### 📅 Scheduling Management
- Schedule lessons with teachers and students
- Book practice rooms
- View all scheduled activities
- Filter activities by date
- Complete or cancel activities
- Today's schedule quick view

### 📝 Exams Management
- Create new exams with capacity limits
- Register students for exams
- Record exam results (Pass/Fail/Distinction/Absent)
- View exam statistics
- Filter upcoming exams
- Track exam registrations

### 💰 Payments & Billing
- Record payments from students
- Create invoices for unpaid services
- View all payment transactions
- Process refunds
- Generate monthly revenue reports
- Track payment methods

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Option 1: Using Maven (Recommended)
```bash
mvn clean javafx:run
```

### Option 2: Using Maven Exec Plugin
```bash
mvn clean compile exec:java -Dexec.mainClass="com.music.school.gui.ConservatoireGUI"
```

### Option 3: Build and Run JAR
```bash
mvn clean package
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar target/conservatoire-virtuel-1.0.0.jar
```

### Console Application (Original)
To run the original console-based application:
```bash
mvn clean compile exec:java -Dexec.mainClass="com.music.school.ConservatoireApp"
```

## Initial Setup

When the application starts:
1. You'll be prompted to load test data
2. Click "OK" to initialize with sample data for demonstration
3. The dashboard will display with system overview

## Navigation

The application uses a sidebar navigation:
- **📊 Dashboard** - System overview and statistics
- **👨‍🎓 Students** - Student management
- **👨‍🏫 Teachers** - Teacher management
- **📦 Services** - Course packages, lessons, and rentals
- **📅 Scheduling** - Lesson scheduling and room bookings
- **📝 Exams** - Exam management and results
- **💰 Payments** - Payment processing and billing
- **🚪 Exit** - Close the application

## Key Features

### Smart Filtering
- Filter students by name, email, or ID
- Filter teachers by name or specialization
- Filter services by type
- Filter activities by date
- Filter exams (upcoming only option)

### Action Buttons
Each table row provides quick actions:
- **👁 View** - View detailed information
- **✏ Edit** - Modify records
- **🗑 Delete** - Remove records
- **Additional actions** based on context (schedule, register, etc.)

### Forms and Dialogs
All data entry is done through intuitive dialog forms with:
- Input validation
- Clear labels and placeholders
- Dropdown selections for related entities
- Date and time pickers

### Real-time Updates
- All views refresh automatically after operations
- Statistics update in real-time
- Revenue tracking updates instantly

## Design Principles

### OOP Concepts Demonstrated
1. **Polymorphism** - Tables display different types of services, activities, etc.
2. **Encapsulation** - Repository pattern for data management
3. **Abstraction** - Panel-based architecture
4. **Inheritance** - All panels extend base GUI components

### User Experience
- Clean, modern interface with Material Design influences
- Color-coded actions (green for create, blue for view, orange for edit, red for delete)
- Responsive layouts that adapt to window size
- Consistent styling across all panels
- Clear visual hierarchy

### Color Scheme
- **Primary**: Blue (#3498db) - Information, neutral actions
- **Success**: Green (#27ae60) - Create, confirm, success
- **Warning**: Orange (#f39c12) - Edit, caution
- **Danger**: Red (#e74c3c) - Delete, cancel
- **Info**: Purple (#8e44ad) - Statistics, special features

## Tips

1. **Load Test Data**: Always start with test data to explore features
2. **Use Filters**: Utilize filters to quickly find specific records
3. **Today Button**: Use the "Today" button in scheduling to see today's activities
4. **Monthly Reports**: Generate monthly revenue reports from the Payments panel
5. **Exam Statistics**: View pass rates and scores in the Exams panel

## Architecture

```
gui/
├── ConservatoireGUI.java (Main application)
└── panels/
    ├── DashboardPanel.java
    ├── StudentsPanel.java
    ├── TeachersPanel.java
    ├── ServicesPanel.java
    ├── SchedulingPanel.java
    ├── ExamsPanel.java
    └── PaymentsPanel.java

resources/
└── styles.css (CSS styling)
```

## Technologies Used

- **JavaFX 21** - GUI framework
- **Maven** - Build and dependency management
- **Java 17** - Core programming language
- **CSS** - Styling and theming

## Troubleshooting

### Application won't start
- Ensure Java 17+ is installed: `java -version`
- Verify Maven is installed: `mvn -version`
- Clean and rebuild: `mvn clean install`

### JavaFX errors
- On some systems, you may need to install JavaFX SDK separately
- Set the `PATH_TO_FX` environment variable to JavaFX SDK lib directory

### No data displayed
- Click "Load Test Data" when starting the application
- Or manually add data through the various management panels

## Support

For issues or questions, please refer to the main project README or contact the development team.

---

**Conservatoire Virtuel** - Modern Music School Management System
Version 1.0 - JavaFX GUI Edition

