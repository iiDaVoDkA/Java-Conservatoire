package com.music.school.repository;

import com.music.school.model.billing.Invoice;
import com.music.school.model.billing.Payment;
import com.music.school.model.exam.Exam;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * Central data repository for the application.
 * In a real application, this would be replaced with database persistence.
 * 
 * Demonstrates:
 * - Polymorphic collections (storing different types in same collection)
 * - Centralized data management
 */
public class DataRepository {
    
    private static DataRepository instance;
    
    // Students and Teachers (polymorphic collection via Person parent)
    private final Map<String, Student> students;
    private final Map<String, Teacher> teachers;
    
    // Services (polymorphic collection - CoursePackage, IndividualLesson, InstrumentRental)
    private final Map<String, Service> services;
    
    // Resources
    private final Map<String, Room> rooms;
    private final Map<String, Instrument> instruments;
    
    // Scheduling (polymorphic collection - Lesson, RoomBooking)
    private final Map<String, ScheduledActivity> scheduledActivities;
    
    // Exams
    private final Map<String, Exam> exams;
    
    // Billing
    private final Map<String, Invoice> invoices;
    private final Map<String, Payment> payments;
    
    /**
     * Private constructor for singleton pattern.
     */
    private DataRepository() {
        students = new HashMap<>();
        teachers = new HashMap<>();
        services = new HashMap<>();
        rooms = new HashMap<>();
        instruments = new HashMap<>();
        scheduledActivities = new HashMap<>();
        exams = new HashMap<>();
        invoices = new HashMap<>();
        payments = new HashMap<>();
    }
    
    /**
     * Get the singleton instance.
     */
    public static synchronized DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }
    
    /**
     * Reset the repository (useful for testing).
     */
    public void reset() {
        students.clear();
        teachers.clear();
        services.clear();
        rooms.clear();
        instruments.clear();
        scheduledActivities.clear();
        exams.clear();
        invoices.clear();
        payments.clear();
    }
    
    // ============ STUDENT OPERATIONS ============
    
    public void addStudent(Student student) {
        students.put(student.getId(), student);
    }
    
    public Student getStudent(String id) {
        return students.get(id);
    }
    
    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }
    
    public List<Student> getActiveStudents() {
        return students.values().stream()
            .filter(Student::isActive)
            .collect(Collectors.toList());
    }
    
    public boolean removeStudent(String id) {
        return students.remove(id) != null;
    }
    
    public Student findStudentByEmail(String email) {
        return students.values().stream()
            .filter(s -> email.equalsIgnoreCase(s.getEmail()))
            .findFirst()
            .orElse(null);
    }
    
    // ============ TEACHER OPERATIONS ============
    
    public void addTeacher(Teacher teacher) {
        teachers.put(teacher.getId(), teacher);
    }
    
    public Teacher getTeacher(String id) {
        return teachers.get(id);
    }
    
    public List<Teacher> getAllTeachers() {
        return new ArrayList<>(teachers.values());
    }
    
    public List<Teacher> getActiveTeachers() {
        return teachers.values().stream()
            .filter(Teacher::isActive)
            .collect(Collectors.toList());
    }
    
    public boolean removeTeacher(String id) {
        return teachers.remove(id) != null;
    }
    
    public List<Teacher> findTeachersByInstrument(String instrument) {
        return teachers.values().stream()
            .filter(t -> t.isActive() && t.canTeach(instrument))
            .collect(Collectors.toList());
    }
    
    // ============ SERVICE OPERATIONS (Polymorphic) ============
    
    public void addService(Service service) {
        services.put(service.getId(), service);
    }
    
    public Service getService(String id) {
        return services.get(id);
    }
    
    public List<Service> getAllServices() {
        return new ArrayList<>(services.values());
    }
    
    public boolean removeService(String id) {
        return services.remove(id) != null;
    }
    
    /**
     * Get all course packages (demonstrates polymorphic filtering).
     */
    public List<CoursePackage> getAllCoursePackages() {
        return services.values().stream()
            .filter(s -> s instanceof CoursePackage)
            .map(s -> (CoursePackage) s)
            .collect(Collectors.toList());
    }
    
    /**
     * Get course packages for a specific student.
     */
    public List<CoursePackage> getStudentPackages(String studentId) {
        return services.values().stream()
            .filter(s -> s instanceof CoursePackage)
            .map(s -> (CoursePackage) s)
            .filter(p -> studentId.equals(p.getStudentId()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get all individual lessons.
     */
    public List<IndividualLesson> getAllIndividualLessons() {
        return services.values().stream()
            .filter(s -> s instanceof IndividualLesson)
            .map(s -> (IndividualLesson) s)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all instrument rentals.
     */
    public List<InstrumentRental> getAllInstrumentRentals() {
        return services.values().stream()
            .filter(s -> s instanceof InstrumentRental)
            .map(s -> (InstrumentRental) s)
            .collect(Collectors.toList());
    }
    
    // ============ ROOM OPERATIONS ============
    
    public void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }
    
    public Room getRoom(String id) {
        return rooms.get(id);
    }
    
    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }
    
    public List<Room> getAvailableRooms() {
        return rooms.values().stream()
            .filter(r -> r.isAvailable() && !r.isUnderMaintenance())
            .collect(Collectors.toList());
    }
    
    public boolean removeRoom(String id) {
        return rooms.remove(id) != null;
    }
    
    // ============ INSTRUMENT OPERATIONS ============
    
    public void addInstrument(Instrument instrument) {
        instruments.put(instrument.getId(), instrument);
    }
    
    public Instrument getInstrument(String id) {
        return instruments.get(id);
    }
    
    public List<Instrument> getAllInstruments() {
        return new ArrayList<>(instruments.values());
    }
    
    public List<Instrument> getAvailableInstruments() {
        return instruments.values().stream()
            .filter(i -> i.isAvailable() && !i.isUnderMaintenance() && !i.isRented())
            .collect(Collectors.toList());
    }
    
    public List<Instrument> getInstrumentsByType(String type) {
        return instruments.values().stream()
            .filter(i -> type.equalsIgnoreCase(i.getType()))
            .collect(Collectors.toList());
    }
    
    public boolean removeInstrument(String id) {
        return instruments.remove(id) != null;
    }
    
    // ============ SCHEDULED ACTIVITY OPERATIONS (Polymorphic) ============
    
    public void addScheduledActivity(ScheduledActivity activity) {
        scheduledActivities.put(activity.getId(), activity);
    }
    
    public ScheduledActivity getScheduledActivity(String id) {
        return scheduledActivities.get(id);
    }
    
    public List<ScheduledActivity> getAllScheduledActivities() {
        return new ArrayList<>(scheduledActivities.values());
    }
    
    public boolean removeScheduledActivity(String id) {
        return scheduledActivities.remove(id) != null;
    }
    
    /**
     * Get all lessons (demonstrates polymorphic filtering).
     */
    public List<Lesson> getAllLessons() {
        return scheduledActivities.values().stream()
            .filter(a -> a instanceof Lesson)
            .map(a -> (Lesson) a)
            .collect(Collectors.toList());
    }
    
    /**
     * Get lessons for a specific teacher.
     */
    public List<Lesson> getTeacherLessons(String teacherId) {
        return scheduledActivities.values().stream()
            .filter(a -> a instanceof Lesson)
            .map(a -> (Lesson) a)
            .filter(l -> teacherId.equals(l.getTeacherId()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get lessons for a specific student.
     */
    public List<Lesson> getStudentLessons(String studentId) {
        return scheduledActivities.values().stream()
            .filter(a -> a instanceof Lesson)
            .map(a -> (Lesson) a)
            .filter(l -> l.hasStudent(studentId))
            .collect(Collectors.toList());
    }
    
    /**
     * Get all room bookings.
     */
    public List<RoomBooking> getAllRoomBookings() {
        return scheduledActivities.values().stream()
            .filter(a -> a instanceof RoomBooking)
            .map(a -> (RoomBooking) a)
            .collect(Collectors.toList());
    }
    
    /**
     * Get activities for a specific room.
     */
    public List<ScheduledActivity> getRoomActivities(String roomId) {
        return scheduledActivities.values().stream()
            .filter(a -> roomId.equals(a.getRoomId()))
            .collect(Collectors.toList());
    }
    
    // ============ EXAM OPERATIONS ============
    
    public void addExam(Exam exam) {
        exams.put(exam.getId(), exam);
    }
    
    public Exam getExam(String id) {
        return exams.get(id);
    }
    
    public List<Exam> getAllExams() {
        return new ArrayList<>(exams.values());
    }
    
    public List<Exam> getUpcomingExams() {
        return exams.values().stream()
            .filter(e -> e.getExamDateTime().isAfter(java.time.LocalDateTime.now()))
            .sorted(Comparator.comparing(Exam::getExamDateTime))
            .collect(Collectors.toList());
    }
    
    public List<Exam> getExamsByInstrument(String instrument) {
        return exams.values().stream()
            .filter(e -> instrument.equalsIgnoreCase(e.getInstrument()))
            .collect(Collectors.toList());
    }
    
    public boolean removeExam(String id) {
        return exams.remove(id) != null;
    }
    
    // ============ INVOICE OPERATIONS ============
    
    public void addInvoice(Invoice invoice) {
        invoices.put(invoice.getId(), invoice);
    }
    
    public Invoice getInvoice(String id) {
        return invoices.get(id);
    }
    
    public List<Invoice> getAllInvoices() {
        return new ArrayList<>(invoices.values());
    }
    
    public List<Invoice> getStudentInvoices(String studentId) {
        return invoices.values().stream()
            .filter(i -> studentId.equals(i.getStudentId()))
            .collect(Collectors.toList());
    }
    
    public List<Invoice> getUnpaidInvoices() {
        return invoices.values().stream()
            .filter(i -> i.getStatus().hasOutstandingBalance())
            .collect(Collectors.toList());
    }
    
    public boolean removeInvoice(String id) {
        return invoices.remove(id) != null;
    }
    
    // ============ PAYMENT OPERATIONS ============
    
    public void addPayment(Payment payment) {
        payments.put(payment.getId(), payment);
    }
    
    public Payment getPayment(String id) {
        return payments.get(id);
    }
    
    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments.values());
    }
    
    public List<Payment> getStudentPayments(String studentId) {
        return payments.values().stream()
            .filter(p -> studentId.equals(p.getStudentId()))
            .collect(Collectors.toList());
    }
    
    public boolean removePayment(String id) {
        return payments.remove(id) != null;
    }
    
    // ============ STATISTICS ============
    
    public int getStudentCount() {
        return students.size();
    }
    
    public int getTeacherCount() {
        return teachers.size();
    }
    
    public int getActiveServiceCount() {
        return (int) services.values().stream().filter(Service::isActive).count();
    }
    
    public int getScheduledActivityCount() {
        return scheduledActivities.size();
    }
}

