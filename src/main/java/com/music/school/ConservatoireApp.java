package com.music.school;

import com.music.school.data.TestDataInitializer;
import com.music.school.enums.ActivityStatus;
import com.music.school.enums.ExamResult;
import com.music.school.enums.Level;
import com.music.school.interfaces.Billable;
import com.music.school.model.billing.Invoice;
import com.music.school.model.billing.Payment;
import com.music.school.model.exam.Exam;
import com.music.school.model.person.Person;
import com.music.school.model.person.Student;
import com.music.school.model.person.Teacher;
import com.music.school.model.resource.Instrument;
import com.music.school.model.resource.Room;
import com.music.school.model.scheduling.Lesson;
import com.music.school.model.scheduling.RoomBooking;
import com.music.school.model.scheduling.ScheduledActivity;
import com.music.school.model.service.CoursePackage;
import com.music.school.model.service.IndividualLesson;
import com.music.school.model.service.InstrumentRental;
import com.music.school.model.service.Service;
import com.music.school.repository.DataRepository;
import com.music.school.service.ExamService;
import com.music.school.service.PaymentService;
import com.music.school.service.SchedulingService;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main console application for Conservatoire Virtuel Music School Management System.
 * 
 * This application demonstrates:
 * - Use of abstract classes (Person, Service, ScheduledActivity)
 * - Use of interfaces (Schedulable, Billable, Bookable)
 * - Polymorphism through collections and method calls
 * - Copy constructors for key classes
 * - Good OOP practices (inheritance, composition, encapsulation)
 */
public class ConservatoireApp {
    
    private static final Scanner scanner = new Scanner(System.in);
    private static final DataRepository repository = DataRepository.getInstance();
    private static final SchedulingService schedulingService = new SchedulingService();
    private static final PaymentService paymentService = new PaymentService();
    private static final ExamService examService = new ExamService();
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public static void main(String[] args) {
        printWelcomeBanner();
        
        // Initialize test data
        System.out.print("Would you like to load test data? (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        if (choice.equals("y") || choice.equals("yes")) {
            new TestDataInitializer().initializeAll();
        }
        
        // Main menu loop
        boolean running = true;
        while (running) {
            printMainMenu();
            int option = readIntOption("Enter your choice: ", 0, 6);
            
            switch (option) {
                case 1 -> manageStudentsAndTeachers();
                case 2 -> manageCoursePackagesAndLessons();
                case 3 -> manageSchedulingAndBooking();
                case 4 -> managePaymentsAndBilling();
                case 5 -> manageExamsAndResults();
                case 6 -> demonstrateOOPConcepts();
                case 0 -> {
                    running = false;
                    System.out.println("\nThank you for using Conservatoire Virtuel. Goodbye!");
                }
            }
        }
        
        scanner.close();
    }
    
    // ================== WELCOME BANNER ==================
    
    private static void printWelcomeBanner() {
        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                ║");
        System.out.println("║           ♪ ♫  CONSERVATOIRE VIRTUEL  ♫ ♪                      ║");
        System.out.println("║                                                                ║");
        System.out.println("║           Music School Management System                       ║");
        System.out.println("║                    Version 1.0                                 ║");
        System.out.println("║                                                                ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    // ================== MAIN MENU ==================
    
    private static void printMainMenu() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║              MAIN MENU                   ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  1. Manage Students and Teachers         ║");
        System.out.println("║  2. Manage Course Packages & Lessons     ║");
        System.out.println("║  3. Manage Scheduling and Booking        ║");
        System.out.println("║  4. Manage Payments and Billing          ║");
        System.out.println("║  5. Manage Exams and Results             ║");
        System.out.println("║  6. Demonstrate OOP Concepts             ║");
        System.out.println("║  0. Exit                                 ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }
    
    // ================== 1. STUDENTS AND TEACHERS ==================
    
    private static void manageStudentsAndTeachers() {
        boolean back = false;
        while (!back) {
            System.out.println("\n═══ STUDENTS AND TEACHERS MANAGEMENT ═══");
            System.out.println("1. List all students");
            System.out.println("2. List all teachers");
            System.out.println("3. View student details");
            System.out.println("4. View teacher details");
            System.out.println("5. Add new student");
            System.out.println("6. Add new teacher");
            System.out.println("7. Update student level");
            System.out.println("8. Search by name");
            System.out.println("0. Back to main menu");
            
            int option = readIntOption("Enter your choice: ", 0, 8);
            
            switch (option) {
                case 1 -> listAllStudents();
                case 2 -> listAllTeachers();
                case 3 -> viewStudentDetails();
                case 4 -> viewTeacherDetails();
                case 5 -> addNewStudent();
                case 6 -> addNewTeacher();
                case 7 -> updateStudentLevel();
                case 8 -> searchByName();
                case 0 -> back = true;
            }
        }
    }
    
    private static void listAllStudents() {
        List<Student> students = repository.getAllStudents();
        System.out.println("\n═══ ALL STUDENTS (" + students.size() + ") ═══");
        if (students.isEmpty()) {
            System.out.println("No students registered.");
            return;
        }
        
        System.out.printf("%-15s %-25s %-15s %-12s %s%n", 
            "ID", "Name", "Level", "Hours Left", "Instruments");
        System.out.println("─".repeat(80));
        
        // Demonstrates polymorphic iteration
        for (Student student : students) {
            System.out.printf("%-15s %-25s %-15s %-12d %s%n",
                student.getId(),
                student.getFullName(),
                student.getLevel(),
                student.getRemainingHours(),
                String.join(", ", student.getPreferredInstruments()));
        }
    }
    
    private static void listAllTeachers() {
        List<Teacher> teachers = repository.getAllTeachers();
        System.out.println("\n═══ ALL TEACHERS (" + teachers.size() + ") ═══");
        if (teachers.isEmpty()) {
            System.out.println("No teachers registered.");
            return;
        }
        
        System.out.printf("%-15s %-25s %-10s %s%n", 
            "ID", "Name", "Rate/hr", "Specializations");
        System.out.println("─".repeat(80));
        
        for (Teacher teacher : teachers) {
            System.out.printf("%-15s %-25s €%-9.2f %s%n",
                teacher.getId(),
                teacher.getFullName(),
                teacher.getHourlyRate(),
                String.join(", ", teacher.getSpecializations()));
        }
    }
    
    private static void viewStudentDetails() {
        String id = readString("Enter student ID: ");
        Student student = repository.getStudent(id);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        System.out.println(student.getDetailedInfo());
    }
    
    private static void viewTeacherDetails() {
        String id = readString("Enter teacher ID: ");
        Teacher teacher = repository.getTeacher(id);
        if (teacher == null) {
            System.out.println("Teacher not found.");
            return;
        }
        System.out.println(teacher.getDetailedInfo());
    }
    
    private static void addNewStudent() {
        System.out.println("\n═══ ADD NEW STUDENT ═══");
        String firstName = readString("First name: ");
        String lastName = readString("Last name: ");
        String address = readString("Address: ");
        LocalDate dob = readDate("Date of birth (yyyy-MM-dd): ");
        String phone = readString("Phone: ");
        String email = readString("Email: ");
        
        System.out.println("Level: 1. Beginner  2. Intermediate  3. Advanced");
        int levelChoice = readIntOption("Choose level: ", 1, 3);
        Level level = Level.values()[levelChoice - 1];
        
        Student student = new Student(firstName, lastName, address, dob, phone, email, level);
        
        String instrument = readString("Preferred instrument (or press Enter to skip): ");
        if (!instrument.isEmpty()) {
            student.addPreferredInstrument(instrument);
        }
        
        if (student.isMinor()) {
            System.out.println("Student is a minor. Please provide guardian information.");
            String guardianName = readString("Guardian name: ");
            String guardianPhone = readString("Guardian phone: ");
            student.setParentGuardianName(guardianName);
            student.setParentGuardianPhone(guardianPhone);
        }
        
        repository.addStudent(student);
        System.out.println("\n✓ Student added successfully!");
        System.out.println("  ID: " + student.getId());
    }
    
    private static void addNewTeacher() {
        System.out.println("\n═══ ADD NEW TEACHER ═══");
        String firstName = readString("First name: ");
        String lastName = readString("Last name: ");
        String address = readString("Address: ");
        LocalDate dob = readDate("Date of birth (yyyy-MM-dd): ");
        String phone = readString("Phone: ");
        String email = readString("Email: ");
        BigDecimal rate = readBigDecimal("Hourly rate (€): ");
        
        List<String> specializations = new ArrayList<>();
        System.out.println("Enter specializations (one per line, empty line to finish):");
        while (true) {
            String spec = readString("  Instrument: ");
            if (spec.isEmpty()) break;
            specializations.add(spec);
        }
        
        Teacher teacher = new Teacher(firstName, lastName, address, dob, phone, email, 
                                       rate, specializations);
        
        String qualification = readString("Primary qualification: ");
        if (!qualification.isEmpty()) {
            teacher.addQualification(qualification);
        }
        
        repository.addTeacher(teacher);
        System.out.println("\n✓ Teacher added successfully!");
        System.out.println("  ID: " + teacher.getId());
    }
    
    private static void updateStudentLevel() {
        String id = readString("Enter student ID: ");
        Student student = repository.getStudent(id);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        
        System.out.println("Current level: " + student.getLevel());
        System.out.println("New level: 1. Beginner  2. Intermediate  3. Advanced");
        int levelChoice = readIntOption("Choose new level: ", 1, 3);
        Level newLevel = Level.values()[levelChoice - 1];
        
        student.setLevel(newLevel);
        System.out.println("✓ Level updated to " + newLevel);
    }
    
    private static void searchByName() {
        String searchTerm = readString("Enter name to search: ").toLowerCase();
        
        System.out.println("\n═══ SEARCH RESULTS ═══");
        
        // Demonstrates polymorphism - searching through Person subclasses
        List<Person> results = new ArrayList<>();
        
        for (Student s : repository.getAllStudents()) {
            if (s.getFullName().toLowerCase().contains(searchTerm)) {
                results.add(s);
            }
        }
        
        for (Teacher t : repository.getAllTeachers()) {
            if (t.getFullName().toLowerCase().contains(searchTerm)) {
                results.add(t);
            }
        }
        
        if (results.isEmpty()) {
            System.out.println("No results found.");
            return;
        }
        
        System.out.printf("Found %d result(s):%n", results.size());
        for (Person person : results) {
            // Polymorphic method call - getRole() is different for Student/Teacher
            System.out.println("  " + person.getSummary());
        }
    }
    
    // ================== 2. COURSE PACKAGES AND LESSONS ==================
    
    private static void manageCoursePackagesAndLessons() {
        boolean back = false;
        while (!back) {
            System.out.println("\n═══ COURSE PACKAGES & LESSONS ═══");
            System.out.println("1. List all course packages");
            System.out.println("2. List individual lessons");
            System.out.println("3. View package details");
            System.out.println("4. Create new package for student");
            System.out.println("5. Purchase individual lesson");
            System.out.println("6. List instrument rentals");
            System.out.println("7. Rent instrument to student");
            System.out.println("8. View student services summary");
            System.out.println("0. Back to main menu");
            
            int option = readIntOption("Enter your choice: ", 0, 8);
            
            switch (option) {
                case 1 -> listAllPackages();
                case 2 -> listIndividualLessons();
                case 3 -> viewPackageDetails();
                case 4 -> createNewPackage();
                case 5 -> purchaseIndividualLesson();
                case 6 -> listInstrumentRentals();
                case 7 -> rentInstrument();
                case 8 -> viewStudentServicesSummary();
                case 0 -> back = true;
            }
        }
    }
    
    private static void listAllPackages() {
        List<CoursePackage> packages = repository.getAllCoursePackages();
        System.out.println("\n═══ ALL COURSE PACKAGES (" + packages.size() + ") ═══");
        
        if (packages.isEmpty()) {
            System.out.println("No packages found.");
            return;
        }
        
        System.out.printf("%-15s %-30s %-12s %-10s %s%n",
            "ID", "Name", "Student", "Hours", "Status");
        System.out.println("─".repeat(85));
        
        // Polymorphic collection - CoursePackage extends Service
        for (CoursePackage pkg : packages) {
            System.out.printf("%-15s %-30s %-12s %-10s %s%n",
                pkg.getId(),
                pkg.getName(),
                pkg.getStudentId(),
                pkg.isUnlimited() ? "Unlimited" : pkg.getRemainingHours() + "/" + pkg.getTotalHours(),
                pkg.getStatusDescription());
        }
    }
    
    private static void listIndividualLessons() {
        List<IndividualLesson> lessons = repository.getAllIndividualLessons();
        System.out.println("\n═══ INDIVIDUAL LESSONS (" + lessons.size() + ") ═══");
        
        if (lessons.isEmpty()) {
            System.out.println("No individual lessons found.");
            return;
        }
        
        for (IndividualLesson lesson : lessons) {
            System.out.printf("[%s] %s - %s (%d min) - %s%n",
                lesson.getId(),
                lesson.getInstrument(),
                lesson.getStudentId(),
                lesson.getDurationMinutes(),
                lesson.isConsumed() ? "Consumed" : "Available");
        }
    }
    
    private static void viewPackageDetails() {
        String id = readString("Enter package ID: ");
        Service service = repository.getService(id);
        
        if (service == null) {
            System.out.println("Package not found.");
            return;
        }
        
        if (service instanceof CoursePackage pkg) {
            System.out.println(pkg.getDetailedInfo());
        } else {
            System.out.println(service.getDetailedInfo());
        }
    }
    
    private static void createNewPackage() {
        System.out.println("\n═══ CREATE NEW COURSE PACKAGE ═══");
        
        listAllStudents();
        String studentId = readString("\nEnter student ID: ");
        Student student = repository.getStudent(studentId);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        
        String name = readString("Package name: ");
        BigDecimal price = readBigDecimal("Price (€): ");
        String instrument = readString("Instrument: ");
        
        System.out.println("Package type: 1. Standard (fixed hours)  2. Unlimited");
        int type = readIntOption("Choose type: ", 1, 2);
        
        LocalDate startDate = LocalDate.now();
        int months = readIntOption("Duration (months): ", 1, 12);
        LocalDate endDate = startDate.plusMonths(months);
        
        CoursePackage pkg;
        if (type == 1) {
            int hours = readIntOption("Total hours: ", 1, 100);
            pkg = new CoursePackage(name, price, studentId, hours, instrument, startDate, endDate);
            student.addPackageHours(pkg.getId(), hours);
        } else {
            int maxPerWeek = readIntOption("Max lessons per week: ", 1, 7);
            pkg = new CoursePackage(name, price, studentId, instrument, startDate, endDate, maxPerWeek);
        }
        
        repository.addService(pkg);
        System.out.println("\n✓ Package created successfully!");
        System.out.println("  ID: " + pkg.getId());
    }
    
    private static void purchaseIndividualLesson() {
        System.out.println("\n═══ PURCHASE INDIVIDUAL LESSON ═══");
        
        String studentId = readString("Enter student ID: ");
        Student student = repository.getStudent(studentId);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        
        String instrument = readString("Instrument: ");
        int duration = readIntOption("Duration (minutes): ", 30, 120);
        BigDecimal price = readBigDecimal("Price (€): ");
        
        IndividualLesson lesson = new IndividualLesson(
            instrument + " Individual Lesson",
            price, studentId, instrument, duration, null);
        
        repository.addService(lesson);
        System.out.println("\n✓ Individual lesson purchased!");
        System.out.println("  ID: " + lesson.getId());
    }
    
    private static void listInstrumentRentals() {
        List<InstrumentRental> rentals = repository.getAllInstrumentRentals();
        System.out.println("\n═══ INSTRUMENT RENTALS (" + rentals.size() + ") ═══");
        
        if (rentals.isEmpty()) {
            System.out.println("No active rentals.");
            return;
        }
        
        for (InstrumentRental rental : rentals) {
            System.out.printf("[%s] %s - Student: %s - %s to %s - %s%n",
                rental.getId(),
                rental.getInstrumentName(),
                rental.getStudentId(),
                rental.getStartDate(),
                rental.getEndDate(),
                rental.isInstrumentReturned() ? "Returned" : "Active");
        }
    }
    
    private static void rentInstrument() {
        System.out.println("\n═══ RENT INSTRUMENT ═══");
        
        List<Instrument> available = repository.getAvailableInstruments();
        if (available.isEmpty()) {
            System.out.println("No instruments available for rental.");
            return;
        }
        
        System.out.println("Available instruments:");
        for (Instrument inst : available) {
            System.out.println("  " + inst);
        }
        
        String instrumentId = readString("\nEnter instrument ID: ");
        Instrument instrument = repository.getInstrument(instrumentId);
        if (instrument == null || !instrument.isAvailable()) {
            System.out.println("Instrument not available.");
            return;
        }
        
        String studentId = readString("Enter student ID: ");
        Student student = repository.getStudent(studentId);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        
        int days = readIntOption("Rental duration (days): ", 1, 365);
        LocalDate endDate = LocalDate.now().plusDays(days);
        
        InstrumentRental rental = new InstrumentRental(
            studentId, instrumentId, instrument.getName(),
            instrument.getDailyRentalRate(), instrument.getDepositRequired(),
            LocalDate.now(), endDate);
        
        instrument.rentTo(studentId);
        repository.addService(rental);
        
        System.out.println("\n✓ Instrument rented successfully!");
        System.out.println(rental.getDetailedInfo());
    }
    
    private static void viewStudentServicesSummary() {
        String studentId = readString("Enter student ID: ");
        Student student = repository.getStudent(studentId);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        
        System.out.println("\n═══ SERVICES FOR " + student.getFullName() + " ═══");
        
        // Get all services for this student - demonstrates polymorphic collection
        List<Service> studentServices = repository.getAllServices().stream()
            .filter(s -> studentId.equals(s.getStudentId()))
            .toList();
        
        if (studentServices.isEmpty()) {
            System.out.println("No services found.");
            return;
        }
        
        BigDecimal totalValue = BigDecimal.ZERO;
        for (Service service : studentServices) {
            // Polymorphic method call - calculateAmount() varies by service type
            System.out.println("  " + service);
            totalValue = totalValue.add(service.calculateAmount());
        }
        
        System.out.println("─".repeat(50));
        System.out.printf("Total services value: €%.2f%n", totalValue);
    }
    
    // ================== 3. SCHEDULING AND BOOKING ==================
    
    private static void manageSchedulingAndBooking() {
        boolean back = false;
        while (!back) {
            System.out.println("\n═══ SCHEDULING AND BOOKING ═══");
            System.out.println("1. View all scheduled activities");
            System.out.println("2. View today's schedule");
            System.out.println("3. Schedule a new lesson");
            System.out.println("4. Book a practice room");
            System.out.println("5. View lesson details");
            System.out.println("6. Cancel an activity");
            System.out.println("7. Complete an activity");
            System.out.println("8. List available rooms");
            System.out.println("9. View teacher schedule");
            System.out.println("0. Back to main menu");
            
            int option = readIntOption("Enter your choice: ", 0, 9);
            
            switch (option) {
                case 1 -> viewAllScheduledActivities();
                case 2 -> viewTodaySchedule();
                case 3 -> scheduleNewLesson();
                case 4 -> bookPracticeRoom();
                case 5 -> viewLessonDetails();
                case 6 -> cancelActivity();
                case 7 -> completeActivity();
                case 8 -> listAvailableRooms();
                case 9 -> viewTeacherSchedule();
                case 0 -> back = true;
            }
        }
    }
    
    private static void viewAllScheduledActivities() {
        List<ScheduledActivity> activities = repository.getAllScheduledActivities();
        System.out.println("\n═══ ALL SCHEDULED ACTIVITIES (" + activities.size() + ") ═══");
        
        if (activities.isEmpty()) {
            System.out.println("No activities scheduled.");
            return;
        }
        
        System.out.printf("%-15s %-20s %-20s %-10s %s%n",
            "ID", "Type", "Date/Time", "Duration", "Status");
        System.out.println("─".repeat(85));
        
        // Polymorphic iteration - Lesson and RoomBooking have same base class
        for (ScheduledActivity activity : activities) {
            System.out.printf("%-15s %-20s %-20s %-10s %s%n",
                activity.getId(),
                activity.getActivityType(), // Polymorphic call
                activity.getScheduledDateTime().format(DATETIME_FORMATTER),
                activity.getDurationMinutes() + " min",
                activity.getStatus());
        }
    }
    
    private static void viewTodaySchedule() {
        List<ScheduledActivity> today = schedulingService.getTodayActivities();
        System.out.println("\n═══ TODAY'S SCHEDULE ═══");
        
        if (today.isEmpty()) {
            System.out.println("No activities scheduled for today.");
            return;
        }
        
        for (ScheduledActivity activity : today) {
            System.out.printf("%s - %s (%s)%n",
                activity.getScheduledDateTime().toLocalTime(),
                activity.getActivityType(),
                activity.getId());
        }
    }
    
    private static void scheduleNewLesson() {
        System.out.println("\n═══ SCHEDULE NEW LESSON ═══");
        
        // Select teacher
        listAllTeachers();
        String teacherId = readString("\nEnter teacher ID: ");
        Teacher teacher = repository.getTeacher(teacherId);
        if (teacher == null) {
            System.out.println("Teacher not found.");
            return;
        }
        
        // Select students
        listAllStudents();
        System.out.println("\nEnter student IDs (comma-separated for group lesson):");
        String studentInput = readString("Student ID(s): ");
        List<String> studentIds = new ArrayList<>();
        for (String id : studentInput.split(",")) {
            String trimmedId = id.trim();
            if (repository.getStudent(trimmedId) != null) {
                studentIds.add(trimmedId);
            }
        }
        
        if (studentIds.isEmpty()) {
            System.out.println("No valid students selected.");
            return;
        }
        
        // Select room
        System.out.println("\nAvailable rooms:");
        for (Room room : repository.getAvailableRooms()) {
            System.out.println("  " + room);
        }
        String roomId = readString("Enter room ID: ");
        
        // Instrument and time
        String instrument = readString("Instrument: ");
        LocalDateTime dateTime = readDateTime("Date and time (yyyy-MM-dd HH:mm): ");
        int duration = readIntOption("Duration (minutes): ", 30, 180);
        
        try {
            Lesson lesson = schedulingService.scheduleLesson(
                teacherId, studentIds, roomId, instrument, dateTime, duration);
            System.out.println("\n✓ Lesson scheduled successfully!");
            System.out.println(lesson.getDetailedInfo());
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void bookPracticeRoom() {
        System.out.println("\n═══ BOOK PRACTICE ROOM ═══");
        
        String studentId = readString("Enter student ID: ");
        if (repository.getStudent(studentId) == null) {
            System.out.println("Student not found.");
            return;
        }
        
        System.out.println("\nAvailable rooms:");
        for (Room room : repository.getAvailableRooms()) {
            System.out.println("  " + room);
        }
        String roomId = readString("Enter room ID: ");
        
        LocalDateTime dateTime = readDateTime("Date and time (yyyy-MM-dd HH:mm): ");
        int duration = readIntOption("Duration (minutes): ", 30, 240);
        BigDecimal hourlyRate = readBigDecimal("Hourly rate (€): ");
        String purpose = readString("Purpose (e.g., Practice, Rehearsal): ");
        
        try {
            RoomBooking booking = schedulingService.bookRoom(
                studentId, roomId, dateTime, duration, hourlyRate, purpose);
            System.out.println("\n✓ Room booked successfully!");
            System.out.println(booking.getDetailedInfo());
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void viewLessonDetails() {
        String id = readString("Enter activity ID: ");
        ScheduledActivity activity = repository.getScheduledActivity(id);
        if (activity == null) {
            System.out.println("Activity not found.");
            return;
        }
        System.out.println(activity.getDetailedInfo());
    }
    
    private static void cancelActivity() {
        String id = readString("Enter activity ID to cancel: ");
        try {
            boolean withoutPenalty = schedulingService.cancelActivity(id);
            if (withoutPenalty) {
                System.out.println("✓ Activity cancelled without penalty.");
            } else {
                System.out.println("✓ Activity cancelled (within 24h - counts as consumed).");
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void completeActivity() {
        String id = readString("Enter activity ID to complete: ");
        try {
            schedulingService.completeActivity(id);
            System.out.println("✓ Activity marked as completed.");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void listAvailableRooms() {
        List<Room> rooms = repository.getAvailableRooms();
        System.out.println("\n═══ AVAILABLE ROOMS ═══");
        for (Room room : rooms) {
            System.out.println(room.getDetailedInfo());
        }
    }
    
    private static void viewTeacherSchedule() {
        String teacherId = readString("Enter teacher ID: ");
        Teacher teacher = repository.getTeacher(teacherId);
        if (teacher == null) {
            System.out.println("Teacher not found.");
            return;
        }
        
        System.out.println("\n═══ SCHEDULE FOR " + teacher.getFullName() + " ═══");
        
        List<Lesson> lessons = repository.getTeacherLessons(teacherId);
        if (lessons.isEmpty()) {
            System.out.println("No lessons scheduled.");
            return;
        }
        
        lessons.stream()
            .filter(l -> l.getStatus() == ActivityStatus.SCHEDULED)
            .sorted((a, b) -> a.getScheduledDateTime().compareTo(b.getScheduledDateTime()))
            .forEach(l -> System.out.printf("%s - %s - %s%n",
                l.getScheduledDateTime().format(DATETIME_FORMATTER),
                l.getInstrument(),
                l.getStudentIds()));
    }
    
    // ================== 4. PAYMENTS AND BILLING ==================
    
    private static void managePaymentsAndBilling() {
        boolean back = false;
        while (!back) {
            System.out.println("\n═══ PAYMENTS AND BILLING ═══");
            System.out.println("1. View all payments");
            System.out.println("2. View student payment summary");
            System.out.println("3. Record a new payment");
            System.out.println("4. Create invoice for student");
            System.out.println("5. View unpaid invoices");
            System.out.println("6. Monthly revenue report");
            System.out.println("7. Revenue by service type");
            System.out.println("8. Mark service as paid");
            System.out.println("0. Back to main menu");
            
            int option = readIntOption("Enter your choice: ", 0, 8);
            
            switch (option) {
                case 1 -> viewAllPayments();
                case 2 -> viewStudentPaymentSummary();
                case 3 -> recordNewPayment();
                case 4 -> createInvoice();
                case 5 -> viewUnpaidInvoices();
                case 6 -> viewMonthlyRevenue();
                case 7 -> viewRevenueByServiceType();
                case 8 -> markServiceAsPaid();
                case 0 -> back = true;
            }
        }
    }
    
    private static void viewAllPayments() {
        List<Payment> payments = repository.getAllPayments();
        System.out.println("\n═══ ALL PAYMENTS (" + payments.size() + ") ═══");
        
        if (payments.isEmpty()) {
            System.out.println("No payments recorded.");
            return;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        for (Payment payment : payments) {
            System.out.println("  " + payment);
            if (payment.getStatus() == com.music.school.enums.PaymentStatus.PAID) {
                total = total.add(payment.getAmount());
            }
        }
        System.out.println("─".repeat(50));
        System.out.printf("Total received: €%.2f%n", total);
    }
    
    private static void viewStudentPaymentSummary() {
        String studentId = readString("Enter student ID: ");
        System.out.println(paymentService.getStudentPaymentSummary(studentId));
    }
    
    private static void recordNewPayment() {
        System.out.println("\n═══ RECORD NEW PAYMENT ═══");
        
        String studentId = readString("Enter student ID: ");
        if (repository.getStudent(studentId) == null) {
            System.out.println("Student not found.");
            return;
        }
        
        BigDecimal amount = readBigDecimal("Amount (€): ");
        System.out.println("Payment method: 1. Cash  2. Card  3. Transfer  4. Check");
        int method = readIntOption("Choose method: ", 1, 4);
        String[] methods = {"Cash", "Card", "Transfer", "Check"};
        
        Payment payment = paymentService.recordPayment(studentId, amount, methods[method - 1]);
        System.out.println("\n✓ Payment recorded!");
        System.out.println(payment.getDetailedInfo());
    }
    
    private static void createInvoice() {
        System.out.println("\n═══ CREATE INVOICE ═══");
        
        String studentId = readString("Enter student ID: ");
        if (repository.getStudent(studentId) == null) {
            System.out.println("Student not found.");
            return;
        }
        
        Invoice invoice = paymentService.createInvoice(studentId);
        
        // Add services to invoice - demonstrates Billable interface usage
        List<Service> unpaidServices = repository.getAllServices().stream()
            .filter(s -> studentId.equals(s.getStudentId()))
            .filter(s -> !s.isPaid())
            .toList();
        
        if (unpaidServices.isEmpty()) {
            System.out.println("No unpaid services found for this student.");
            repository.removeInvoice(invoice.getId());
            return;
        }
        
        System.out.println("\nUnpaid services:");
        for (int i = 0; i < unpaidServices.size(); i++) {
            Billable billable = unpaidServices.get(i);
            System.out.printf("  %d. %s - €%.2f%n", i + 1, 
                billable.getBillingDescription(), billable.calculateAmount());
        }
        
        System.out.println("\nAdd all to invoice? (y/n): ");
        if (readString("").toLowerCase().startsWith("y")) {
            for (Service service : unpaidServices) {
                invoice.addBillableItem(service);
            }
        }
        
        System.out.println(invoice.getDetailedInfo());
    }
    
    private static void viewUnpaidInvoices() {
        List<Invoice> unpaid = paymentService.getUnpaidInvoices();
        System.out.println("\n═══ UNPAID INVOICES (" + unpaid.size() + ") ═══");
        
        if (unpaid.isEmpty()) {
            System.out.println("No unpaid invoices.");
            return;
        }
        
        for (Invoice invoice : unpaid) {
            System.out.println("  " + invoice);
        }
    }
    
    private static void viewMonthlyRevenue() {
        YearMonth month = YearMonth.now();
        System.out.println(paymentService.generateMonthlyReport(month));
    }
    
    private static void viewRevenueByServiceType() {
        System.out.println("\n═══ REVENUE BY SERVICE TYPE ═══");
        var breakdown = paymentService.getRevenueByServiceType();
        
        if (breakdown.isEmpty()) {
            System.out.println("No revenue data available.");
            return;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        for (var entry : breakdown.entrySet()) {
            System.out.printf("  %-25s: €%.2f%n", entry.getKey(), entry.getValue());
            total = total.add(entry.getValue());
        }
        System.out.println("─".repeat(40));
        System.out.printf("  %-25s: €%.2f%n", "TOTAL", total);
    }
    
    private static void markServiceAsPaid() {
        String serviceId = readString("Enter service ID: ");
        try {
            paymentService.markServiceAsPaid(serviceId);
            System.out.println("✓ Service marked as paid.");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }
    
    // ================== 5. EXAMS AND RESULTS ==================
    
    private static void manageExamsAndResults() {
        boolean back = false;
        while (!back) {
            System.out.println("\n═══ EXAMS AND RESULTS ═══");
            System.out.println("1. View all exams");
            System.out.println("2. View upcoming exams");
            System.out.println("3. View exam details");
            System.out.println("4. Create new exam");
            System.out.println("5. Register student for exam");
            System.out.println("6. Record exam result");
            System.out.println("7. View student exam history");
            System.out.println("8. View exam statistics");
            System.out.println("0. Back to main menu");
            
            int option = readIntOption("Enter your choice: ", 0, 8);
            
            switch (option) {
                case 1 -> viewAllExams();
                case 2 -> viewUpcomingExams();
                case 3 -> viewExamDetails();
                case 4 -> createNewExam();
                case 5 -> registerStudentForExam();
                case 6 -> recordExamResult();
                case 7 -> viewStudentExamHistory();
                case 8 -> viewExamStatistics();
                case 0 -> back = true;
            }
        }
    }
    
    private static void viewAllExams() {
        List<Exam> exams = repository.getAllExams();
        System.out.println("\n═══ ALL EXAMS (" + exams.size() + ") ═══");
        
        if (exams.isEmpty()) {
            System.out.println("No exams found.");
            return;
        }
        
        System.out.printf("%-15s %-30s %-12s %-15s %s%n",
            "ID", "Name", "Instrument", "Date", "Registration");
        System.out.println("─".repeat(85));
        
        for (Exam exam : exams) {
            System.out.printf("%-15s %-30s %-12s %-15s %d/%d%n",
                exam.getId(),
                exam.getName(),
                exam.getInstrument(),
                exam.getExamDateTime().toLocalDate(),
                exam.getRegisteredStudentIds().size(),
                exam.getMaxCapacity());
        }
    }
    
    private static void viewUpcomingExams() {
        List<Exam> upcoming = examService.getUpcomingExams();
        System.out.println("\n═══ UPCOMING EXAMS ═══");
        
        if (upcoming.isEmpty()) {
            System.out.println("No upcoming exams.");
            return;
        }
        
        for (Exam exam : upcoming) {
            String status = exam.canRegister() ? "Open" : "Closed";
            System.out.printf("%s - %s (%s) - Registration: %s - %d spots left%n",
                exam.getExamDateTime().toLocalDate(),
                exam.getName(),
                exam.getInstrument(),
                status,
                exam.getAvailableSpots());
        }
    }
    
    private static void viewExamDetails() {
        String examId = readString("Enter exam ID: ");
        Exam exam = repository.getExam(examId);
        if (exam == null) {
            System.out.println("Exam not found.");
            return;
        }
        System.out.println(exam.getDetailedInfo());
        
        System.out.println("Registered students:");
        for (Exam.ExamRegistration reg : exam.getAllRegistrations()) {
            Student student = repository.getStudent(reg.getStudentId());
            String name = student != null ? student.getFullName() : reg.getStudentId();
            System.out.printf("  - %s: %s%n", name, reg.getResult());
        }
    }
    
    private static void createNewExam() {
        System.out.println("\n═══ CREATE NEW EXAM ═══");
        
        String name = readString("Exam name: ");
        String instrument = readString("Instrument: ");
        LocalDateTime dateTime = readDateTime("Exam date and time (yyyy-MM-dd HH:mm): ");
        int capacity = readIntOption("Maximum capacity: ", 1, 100);
        BigDecimal fee = readBigDecimal("Registration fee (€): ");
        
        Exam exam = examService.createExam(name, instrument, dateTime, capacity, fee);
        
        String description = readString("Description (optional): ");
        if (!description.isEmpty()) {
            exam.setDescription(description);
        }
        
        System.out.println("\n✓ Exam created successfully!");
        System.out.println("  ID: " + exam.getId());
    }
    
    private static void registerStudentForExam() {
        System.out.println("\n═══ REGISTER STUDENT FOR EXAM ═══");
        
        viewUpcomingExams();
        
        String examId = readString("\nEnter exam ID: ");
        String studentId = readString("Enter student ID: ");
        
        try {
            Exam.ExamRegistration reg = examService.registerStudentForExam(examId, studentId);
            System.out.println("\n✓ Student registered successfully!");
            System.out.println("  Registration ID: " + reg.getRegistrationId());
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void recordExamResult() {
        System.out.println("\n═══ RECORD EXAM RESULT ═══");
        
        String examId = readString("Enter exam ID: ");
        Exam exam = repository.getExam(examId);
        if (exam == null) {
            System.out.println("Exam not found.");
            return;
        }
        
        System.out.println("Registered students:");
        for (String studentId : exam.getRegisteredStudentIds()) {
            Student student = repository.getStudent(studentId);
            System.out.println("  - " + studentId + ": " + 
                (student != null ? student.getFullName() : "Unknown"));
        }
        
        String studentId = readString("\nEnter student ID: ");
        
        System.out.println("Result: 1. Pass  2. Fail  3. Distinction  4. Absent");
        int resultChoice = readIntOption("Choose result: ", 1, 4);
        ExamResult[] results = {ExamResult.PASS, ExamResult.FAIL, 
                                 ExamResult.DISTINCTION, ExamResult.ABSENT};
        ExamResult result = results[resultChoice - 1];
        
        Integer score = null;
        if (result != ExamResult.ABSENT) {
            String scoreInput = readString("Score (0-100, or press Enter to skip): ");
            if (!scoreInput.isEmpty()) {
                score = Integer.parseInt(scoreInput);
            }
        }
        
        try {
            examService.recordExamResult(examId, studentId, result, score);
            System.out.println("✓ Result recorded successfully.");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void viewStudentExamHistory() {
        String studentId = readString("Enter student ID: ");
        System.out.println(examService.getStudentExamSummary(studentId));
    }
    
    private static void viewExamStatistics() {
        String examId = readString("Enter exam ID: ");
        System.out.println(examService.getExamStatistics(examId));
    }
    
    // ================== 6. OOP DEMONSTRATIONS ==================
    
    private static void demonstrateOOPConcepts() {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("        OBJECT-ORIENTED PROGRAMMING DEMONSTRATIONS");
        System.out.println("═".repeat(60));
        
        System.out.println("\n1. ABSTRACT CLASSES");
        System.out.println("─".repeat(40));
        System.out.println("The system uses two main abstract classes:");
        System.out.println("  • Person - Base class for Student and Teacher");
        System.out.println("  • Service - Base class for CoursePackage, IndividualLesson, InstrumentRental");
        System.out.println("  • ScheduledActivity - Base class for Lesson, RoomBooking");
        
        // Demonstrate polymorphic collection of Person
        System.out.println("\nPolymorphic Person collection:");
        List<Person> people = new ArrayList<>();
        people.addAll(repository.getAllStudents());
        people.addAll(repository.getAllTeachers());
        
        System.out.printf("  Total people in system: %d%n", people.size());
        for (int i = 0; i < Math.min(3, people.size()); i++) {
            Person p = people.get(i);
            System.out.printf("    %s - Role: %s%n", p.getFullName(), p.getRole());
        }
        
        System.out.println("\n2. INTERFACES");
        System.out.println("─".repeat(40));
        System.out.println("The system implements three key interfaces:");
        System.out.println("  • Schedulable - For lessons, room bookings (conflict detection)");
        System.out.println("  • Billable - For services that can be invoiced");
        System.out.println("  • Bookable - For resources that can be reserved (rooms, instruments, teachers)");
        
        // Demonstrate Billable interface
        System.out.println("\nBillable items (polymorphic):");
        List<Billable> billables = new ArrayList<>();
        billables.addAll(repository.getAllCoursePackages());
        billables.addAll(repository.getAllIndividualLessons());
        billables.addAll(repository.getAllInstrumentRentals());
        
        BigDecimal total = BigDecimal.ZERO;
        for (Billable b : billables) {
            total = total.add(b.calculateAmount());
        }
        System.out.printf("  Total billable items: %d, Total value: €%.2f%n", 
            billables.size(), total);
        
        System.out.println("\n3. POLYMORPHISM");
        System.out.println("─".repeat(40));
        System.out.println("Demonstrated through:");
        System.out.println("  • Polymorphic collections (List<Person>, List<Service>, List<Schedulable>)");
        System.out.println("  • Polymorphic method calls (getRole(), calculateAmount(), getActivityType())");
        System.out.println("  • Dynamic dispatch (overridden methods in subclasses)");
        
        // Demonstrate polymorphic method calls
        System.out.println("\nPolymorphic getActivityType() calls:");
        for (ScheduledActivity activity : repository.getAllScheduledActivities()) {
            System.out.printf("  %s → %s%n", activity.getId(), activity.getActivityType());
            if (repository.getAllScheduledActivities().indexOf(activity) >= 2) {
                System.out.println("  ...");
                break;
            }
        }
        
        System.out.println("\n4. COPY CONSTRUCTORS");
        System.out.println("─".repeat(40));
        System.out.println("Implemented in key classes to support:");
        System.out.println("  • Deep copying of objects");
        System.out.println("  • Protection of internal state");
        System.out.println("  • Safe object duplication");
        
        // Demonstrate copy constructor
        if (!repository.getAllStudents().isEmpty()) {
            Student original = repository.getAllStudents().get(0);
            Student copy = new Student(original);
            
            System.out.println("\nStudent copy demonstration:");
            System.out.printf("  Original: %s (ID: %s)%n", original.getFullName(), original.getId());
            System.out.printf("  Copy:     %s (ID: %s)%n", copy.getFullName(), copy.getId());
            System.out.printf("  Same object? %s%n", original == copy ? "Yes" : "No");
            System.out.printf("  Equal IDs? %s%n", original.getId().equals(copy.getId()) ? "Yes" : "No");
        }
        
        System.out.println("\n5. ENCAPSULATION & COMPOSITION");
        System.out.println("─".repeat(40));
        System.out.println("Demonstrated through:");
        System.out.println("  • Private fields with public getters/setters");
        System.out.println("  • Defensive copies in getters (lists, maps)");
        System.out.println("  • Composition: Teacher has TimeRanges, Student has PackageHours");
        System.out.println("  • Data validation in setters");
        
        System.out.println("\n" + "═".repeat(60));
        System.out.println("        END OF OOP DEMONSTRATIONS");
        System.out.println("═".repeat(60));
        
        readString("\nPress Enter to continue...");
    }
    
    // ================== UTILITY METHODS ==================
    
    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private static int readIntOption(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    private static BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a valid number.");
            }
        }
    }
    
    private static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Use yyyy-MM-dd.");
            }
        }
    }
    
    private static LocalDateTime readDateTime(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDateTime.parse(scanner.nextLine().trim(), DATETIME_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid datetime format. Use yyyy-MM-dd HH:mm.");
            }
        }
    }
}

