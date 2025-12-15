# Conservatoire Virtuel - Music School Management System

A comprehensive Java application for managing a private music school's operations, including students, teachers, course packages, lesson scheduling, payments, and official examinations.

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [GUI Application](#-gui-application-new)
- [Features](#-features)
- [Architecture](#-architecture)
- [OOP Design Patterns](#-oop-design-patterns)
- [UML Diagrams](#-uml-diagrams)
- [Installation & Running](#-installation--running)
- [Usage Guide](#-usage-guide)
- [Test Data](#-test-data)
- [Project Structure](#-project-structure)

---

## 🎵 Project Overview

**Conservatoire Virtuel** is a music school management system designed to support internal school activities. The system provides comprehensive management of:

- **Students & Teachers** - Registration, profiles, levels, and specializations
- **Course Packages** - Music packages, unlimited lessons, individual lessons
- **Scheduling** - Lesson scheduling with conflict prevention, room bookings
- **Resources** - Room management and instrument rentals
- **Payments** - Invoicing, payment tracking, financial reports
- **Examinations** - Official exam registration, capacity management, results

---

## 🖥️ GUI Application (NEW!)

**Conservatoire Virtuel** now includes a modern JavaFX-based graphical user interface!

### Features
- 📊 **Dashboard** - Real-time statistics and system overview
- 👨‍🎓 **Student Management** - Add, edit, view, and delete students
- 👨‍🏫 **Teacher Management** - Manage teachers and their specializations
- 📦 **Services** - Create course packages, lessons, and instrument rentals
- 📅 **Scheduling** - Schedule lessons and book practice rooms
- 📝 **Exams** - Create exams, register students, and record results
- 💰 **Payments** - Record payments and generate invoices

### Quick Start (GUI)

**Windows:**
```bash
run-gui.bat
```

**Linux/Mac:**
```bash
chmod +x run-gui.sh
./run-gui.sh
```

**Or using Maven directly:**
```bash
mvn clean javafx:run
```

For detailed GUI documentation, see [GUI_README.md](GUI_README.md)

### Console Application

The original console-based application is still available:

**Windows:**
```bash
run-console.bat
```

**Linux/Mac:**
```bash
chmod +x run-console.sh
./run-console.sh
```

---

## ✨ Features

### Core Functionalities

1. **Student Management**
   - Registration with personal details
   - Level tracking (Beginner/Intermediate/Advanced)
   - Preferred instruments
   - Hour tracking (purchased/consumed/remaining)
   - Guardian info for minors

2. **Teacher Management**
   - Specializations and qualifications
   - Availability scheduling
   - Hourly rate management
   - Booking conflict detection

3. **Course Packages & Services**
   - Standard packages (fixed hours)
   - Unlimited packages (max lessons per week)
   - Individual pay-as-you-go lessons
   - Instrument rentals with deposits
   - Room bookings

4. **Scheduling System**
   - **Conflict detection** for teachers, students, and rooms
   - Support for individual and group lessons
   - 24-hour cancellation policy enforcement
   - Automatic resource booking

5. **Exam Management**
   - Exam creation with capacity limits
   - Student registration with deadline
   - Result recording (Pass/Fail/Distinction)
   - Score tracking

6. **Billing & Payments**
   - Invoice generation
   - Payment recording
   - Outstanding balance tracking
   - Monthly revenue reports

---

## 🏗️ Architecture

### Package Structure

```
com.music.school/
├── model/
│   ├── person/          # Student, Teacher (extend Person)
│   ├── service/         # CoursePackage, IndividualLesson, InstrumentRental (extend Service)
│   ├── scheduling/      # Lesson, RoomBooking (extend ScheduledActivity)
│   ├── resource/        # Room, Instrument
│   ├── exam/            # Exam, ExamRegistration
│   └── billing/         # Invoice, Payment
├── interfaces/          # Schedulable, Billable, Bookable
├── enums/               # Level, ActivityStatus, PaymentStatus, etc.
├── service/             # Business logic services
├── repository/          # Data storage
├── data/                # Test data initialization
└── ConservatoireApp.java # Main application
```

---

## 🎯 OOP Design Patterns

### 1. Abstract Classes

**Person (Abstract)**
```java
public abstract class Person {
    // Common attributes: id, firstName, lastName, address, etc.
    protected abstract String getIdPrefix();
    public abstract String getRole();
    public abstract String getDetailedInfo();
    public abstract Person copy();
}
```

Extended by: `Student`, `Teacher`

**Service (Abstract)**
```java
public abstract class Service implements Billable {
    // Common attributes: id, name, price, studentId, etc.
    protected abstract String getIdPrefix();
    public abstract String getDetailedInfo();
    public abstract Service copy();
    public abstract boolean isValid();
}
```

Extended by: `CoursePackage`, `IndividualLesson`, `InstrumentRental`

**ScheduledActivity (Abstract)**
```java
public abstract class ScheduledActivity implements Schedulable {
    // Common attributes: id, dateTime, duration, status, roomId
    protected abstract String getIdPrefix();
    public abstract String getActivityType();
    public abstract boolean consumesLessonHours();
}
```

Extended by: `Lesson`, `RoomBooking`

### 2. Interfaces

**Schedulable Interface**
```java
public interface Schedulable {
    LocalDateTime getScheduledDateTime();
    Duration getDuration();
    ActivityStatus getStatus();
    default boolean conflictsWith(Schedulable other) { ... }
    default boolean canCancelWithoutPenalty() { ... }
}
```

**Billable Interface**
```java
public interface Billable {
    String getBillingId();
    ServiceType getServiceType();
    BigDecimal calculateAmount();
    String getBillingDescription();
    boolean isPaid();
    void markAsPaid();
}
```

**Bookable Interface**
```java
public interface Bookable {
    String getResourceId();
    boolean isAvailableAt(LocalDateTime dateTime, int durationMinutes);
    List<TimeSlot> getBookedSlots();
    boolean addBooking(TimeSlot slot);
}
```

### 3. Polymorphism

**Polymorphic Collections**
```java
// List of Person (contains both Student and Teacher)
List<Person> people = new ArrayList<>();
people.addAll(repository.getAllStudents());
people.addAll(repository.getAllTeachers());

// Polymorphic method call
for (Person person : people) {
    System.out.println(person.getRole()); // Different for Student vs Teacher
}
```

**Polymorphic Billing**
```java
// All services are Billable
List<Billable> billables = new ArrayList<>();
billables.addAll(packages);     // CoursePackage
billables.addAll(lessons);      // IndividualLesson
billables.addAll(rentals);      // InstrumentRental

for (Billable b : billables) {
    invoice.addBillableItem(b);  // Polymorphic call
}
```

### 4. Copy Constructors

Implemented in all major classes for deep copying:

```java
public class Student extends Person {
    // Copy constructor
    public Student(Student other) {
        super(other);
        this.level = other.level;
        this.preferredInstruments = new ArrayList<>(other.preferredInstruments);
        this.packageHours = new HashMap<>(other.packageHours);
        // ... deep copy all fields
    }
}
```

### 5. Encapsulation

- All fields are `private` or `protected`
- Defensive copies in getters:
```java
public List<String> getPreferredInstruments() {
    return new ArrayList<>(preferredInstruments);  // Defensive copy
}
```

---

## 📊 UML Diagrams

### Class Diagram (Key Classes)

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                              <<abstract>>                                     │
│                                 Person                                        │
├──────────────────────────────────────────────────────────────────────────────┤
│ # id: String                                                                  │
│ # firstName: String                                                           │
│ # lastName: String                                                            │
│ # email: String                                                               │
├──────────────────────────────────────────────────────────────────────────────┤
│ + getFullName(): String                                                       │
│ + getRole(): String {abstract}                                                │
│ + copy(): Person {abstract}                                                   │
└──────────────────────────────────────────────────────────────────────────────┘
                    △                               △
                    │                               │
         ┌─────────┴─────────┐           ┌─────────┴─────────┐
         │      Student      │           │      Teacher      │
         ├───────────────────┤           ├───────────────────┤
         │ - level: Level    │           │ - hourlyRate      │
         │ - packageHours    │           │ - specializations │
         ├───────────────────┤           ├───────────────────┤
         │ + getRole()       │           │ + getRole()       │
         │ + consumeHours()  │           │ + canTeach()      │
         └───────────────────┘           │ + isAvailableAt() │
                                         └───────────────────┘
                                                  │
                                                  │ implements
                                                  ▼
                                         ┌───────────────────┐
                                         │   <<interface>>   │
                                         │     Bookable      │
                                         └───────────────────┘

┌──────────────────────────────────────────────────────────────────────────────┐
│                    <<abstract>>              <<interface>>                    │
│                      Service ─────────────────> Billable                      │
├──────────────────────────────────────────────────────────────────────────────┤
│ # id: String                                                                  │
│ # name: String                                                                │
│ # price: BigDecimal                                                           │
├──────────────────────────────────────────────────────────────────────────────┤
│ + calculateAmount(): BigDecimal                                               │
│ + isValid(): boolean {abstract}                                               │
└──────────────────────────────────────────────────────────────────────────────┘
           △                    △                        △
           │                    │                        │
    ┌──────┴──────┐     ┌──────┴──────┐         ┌──────┴──────┐
    │CoursePackage│     │IndividualLes│         │InstrumentRen│
    ├─────────────┤     ├─────────────┤         ├─────────────┤
    │ totalHours  │     │ instrument  │         │ dailyRate   │
    │ usedHours   │     │ duration    │         │ deposit     │
    └─────────────┘     └─────────────┘         └─────────────┘
```

### Sequence Diagram: Scheduling a Lesson

```
┌────────┐      ┌─────────────────┐      ┌──────────────┐      ┌─────────┐      ┌──────┐
│  User  │      │SchedulingService│      │DataRepository│      │ Teacher │      │ Room │
└───┬────┘      └────────┬────────┘      └──────┬───────┘      └────┬────┘      └──┬───┘
    │                    │                      │                   │              │
    │ scheduleLesson()   │                      │                   │              │
    │───────────────────>│                      │                   │              │
    │                    │                      │                   │              │
    │                    │  getTeacher(id)      │                   │              │
    │                    │─────────────────────>│                   │              │
    │                    │<─────────────────────│                   │              │
    │                    │                      │                   │              │
    │                    │  validate: canTeach(instrument)         │              │
    │                    │────────────────────────────────────────>│              │
    │                    │<────────────────────────────────────────│              │
    │                    │                      │                   │              │
    │                    │  checkConflicts()    │                   │              │
    │                    │─────────────────────>│                   │              │
    │                    │<─────────────────────│                   │              │
    │                    │                      │                   │              │
    │                    │  isAvailableAt()     │                   │              │
    │                    │────────────────────────────────────────>│              │
    │                    │<────────────────────────────────────────│              │
    │                    │                      │                   │              │
    │                    │  isAvailableAt()     │                   │              │
    │                    │─────────────────────────────────────────────────────────>│
    │                    │<─────────────────────────────────────────────────────────│
    │                    │                      │                   │              │
    │                    │  addBooking()        │                   │              │
    │                    │────────────────────────────────────────>│              │
    │                    │                      │                   │              │
    │                    │  addBooking()        │                   │              │
    │                    │─────────────────────────────────────────────────────────>│
    │                    │                      │                   │              │
    │                    │  addScheduledActivity(lesson)           │              │
    │                    │─────────────────────>│                   │              │
    │                    │                      │                   │              │
    │<───────────────────│  return Lesson       │                   │              │
    │                    │                      │                   │              │
```

### Activity Diagram: Exam Registration

```
                    ┌─────────────┐
                    │    Start    │
                    └──────┬──────┘
                           │
                           ▼
                    ┌─────────────┐
                    │ Select Exam │
                    └──────┬──────┘
                           │
                           ▼
                    ◇─────────────◇
                   ╱               ╲
              Is Registration      No
                  Open?  ─────────────────────┐
                   ╲               ╱          │
                    ◇─────Yes────◇            │
                           │                  │
                           ▼                  │
                    ◇─────────────◇           │
                   ╱               ╲          │
               Before              No         │
               Deadline? ─────────────────────┤
                   ╲               ╱          │
                    ◇─────Yes────◇            │
                           │                  │
                           ▼                  │
                    ◇─────────────◇           │
                   ╱               ╲          │
              Spots Available?     No         │
                   ╲               ╱          │
                    ◇─────Yes────◇            │
                           │                  │
                           ▼                  │
                    ◇─────────────◇           │
                   ╱               ╲          │
              Already Registered?  Yes ───────┤
                   ╲               ╱          │
                    ◇──────No────◇            │
                           │                  │
                           ▼                  │
                    ┌─────────────┐           │
                    │   Create    │           │
                    │Registration │           │
                    └──────┬──────┘           │
                           │                  │
                           ▼                  │
                    ┌─────────────┐           │
                    │   Return    │           │
                    │   Success   │           │
                    └──────┬──────┘           │
                           │                  │
                           │                  ▼
                           │           ┌─────────────┐
                           │           │   Return    │
                           │           │    Error    │
                           │           └──────┬──────┘
                           │                  │
                           ▼                  ▼
                    ┌─────────────────────────────┐
                    │            End              │
                    └─────────────────────────────┘
```

---

## 🚀 Installation & Running

### Prerequisites

- Java JDK 17 or higher
- Maven 3.6 or higher

### Running the GUI Application (Recommended)

**Option 1: Using launcher scripts**
```bash
# Windows
run-gui.bat

# Linux/Mac
chmod +x run-gui.sh
./run-gui.sh
```

**Option 2: Using Maven**
```bash
mvn clean javafx:run
```

### Running the Console Application

**Option 1: Using launcher scripts**
```bash
# Windows
run-console.bat

# Linux/Mac
chmod +x run-console.sh
./run-console.sh
```

**Option 2: Using Maven**
```bash
mvn clean compile exec:java -Dexec.mainClass="com.music.school.ConservatoireApp"
```

### Build Package

```bash
# Build JAR file
mvn clean package

# The JAR will be created in target/conservatoire-virtuel-1.0.0.jar
```

### Running Tests (if available)

```bash
mvn test
```

---

## 📖 Usage Guide

### Main Menu

```
╔══════════════════════════════════════════╗
║              MAIN MENU                   ║
╠══════════════════════════════════════════╣
║  1. Manage Students and Teachers         ║
║  2. Manage Course Packages & Lessons     ║
║  3. Manage Scheduling and Booking        ║
║  4. Manage Payments and Billing          ║
║  5. Manage Exams and Results             ║
║  6. Demonstrate OOP Concepts             ║
║  0. Exit                                 ║
╚══════════════════════════════════════════╝
```

### Quick Start

1. Run the application
2. Choose "y" when prompted to load test data
3. Navigate through menus using number keys
4. Use option 6 to see OOP demonstrations

---

## 🧪 Test Data

The test data includes:

### People
- **8 Students** (including 3 minors with guardian info)
- **5 Teachers** with various specializations (Piano, Violin, Guitar, Drums, Flute)

### Resources
- **8 Rooms** (Piano Studios, String Room, Guitar Studio, Percussion Room, Ensemble Hall, Practice Rooms)
- **7 Instruments** for rental (Violins, Guitars, Cello, Flute, Clarinet)

### Services
- **5 Course Packages** (standard and unlimited)
- **1 Individual Lesson**
- **1 Instrument Rental**

### Scheduled Activities
- Multiple lessons scheduled for next week
- Group lesson example

### Exams
- 4 upcoming exams (Piano, Violin, Guitar, Music Theory)
- 1 past exam with recorded results

---

## 📁 Project Structure

```
java-project/
├── pom.xml                           # Maven configuration
├── README.md                         # This file
├── GUI_README.md                     # GUI documentation
├── run-gui.bat                       # Windows GUI launcher
├── run-gui.sh                        # Linux/Mac GUI launcher
├── run-console.bat                   # Windows console launcher
├── run-console.sh                    # Linux/Mac console launcher
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── music/
        │           └── school/
        │               ├── ConservatoireApp.java      # Console application
        │               ├── gui/
        │               │   ├── ConservatoireGUI.java  # GUI application
        │               │   └── panels/
        │               │       ├── DashboardPanel.java
        │               │       ├── StudentsPanel.java
        │               │       ├── TeachersPanel.java
        │               │       ├── ServicesPanel.java
        │               │       ├── SchedulingPanel.java
        │               │       ├── ExamsPanel.java
        │               │       └── PaymentsPanel.java
        │               ├── enums/
        │               │   ├── Level.java
        │               │   ├── ActivityStatus.java
        │               │   ├── PaymentStatus.java
        │               │   ├── ServiceType.java
        │               │   └── ExamResult.java
        │               ├── interfaces/
        │               │   ├── Schedulable.java
        │               │   ├── Billable.java
        │               │   └── Bookable.java
        │               ├── model/
        │               │   ├── person/
        │               │   │   ├── Person.java        # Abstract
        │               │   │   ├── Student.java
        │               │   │   └── Teacher.java
        │               │   ├── service/
        │               │   │   ├── Service.java       # Abstract
        │               │   │   ├── CoursePackage.java
        │               │   │   ├── IndividualLesson.java
        │               │   │   └── InstrumentRental.java
        │               │   ├── scheduling/
        │               │   │   ├── ScheduledActivity.java  # Abstract
        │               │   │   ├── Lesson.java
        │               │   │   └── RoomBooking.java
        │               │   ├── resource/
        │               │   │   ├── Room.java
        │               │   │   └── Instrument.java
        │               │   ├── exam/
        │               │   │   └── Exam.java
        │               │   └── billing/
        │               │       ├── Invoice.java
        │               │       └── Payment.java
        │               ├── service/
        │               │   ├── SchedulingService.java
        │               │   ├── PaymentService.java
        │               │   └── ExamService.java
        │               ├── repository/
        │               │   └── DataRepository.java
        │               └── data/
        │                   └── TestDataInitializer.java
        └── resources/
            └── styles.css                # GUI styling
```

---

## 👨‍💻 Author

Conservatoire Virtuel - Music School Management System

---

## 📄 License

This project is for educational purposes.

