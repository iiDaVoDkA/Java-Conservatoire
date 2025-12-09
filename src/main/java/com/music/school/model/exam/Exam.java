package com.music.school.model.exam;

import com.music.school.enums.ExamResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents an official exam organized by the school.
 * 
 * Demonstrates:
 * - Copy constructor for deep copying
 * - Encapsulation with defensive copies
 * - Business rule enforcement (capacity limits)
 */
public class Exam {
    
    private String id;
    private String name;
    private String instrument;
    private LocalDateTime examDateTime;
    private int durationMinutes;
    private int maxCapacity;
    private String roomId;
    private String examinerId; // External examiner or teacher
    private BigDecimal registrationFee;
    private String description;
    private Map<String, ExamRegistration> registrations;
    private boolean registrationOpen;
    private LocalDateTime registrationDeadline;
    
    /**
     * Default constructor.
     */
    public Exam() {
        this.id = "EXM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.registrations = new HashMap<>();
        this.registrationOpen = true;
        this.maxCapacity = 20;
        this.durationMinutes = 120;
    }
    
    /**
     * Parameterized constructor.
     */
    public Exam(String name, String instrument, LocalDateTime examDateTime,
                int maxCapacity, BigDecimal registrationFee) {
        this();
        this.name = name;
        this.instrument = instrument;
        this.examDateTime = examDateTime;
        this.maxCapacity = maxCapacity;
        this.registrationFee = registrationFee;
        this.registrationDeadline = examDateTime.minusDays(7); // Default: 7 days before
    }
    
    /**
     * Copy constructor - creates a deep copy.
     * @param other the exam to copy
     */
    public Exam(Exam other) {
        this.id = other.id;
        this.name = other.name;
        this.instrument = other.instrument;
        this.examDateTime = other.examDateTime;
        this.durationMinutes = other.durationMinutes;
        this.maxCapacity = other.maxCapacity;
        this.roomId = other.roomId;
        this.examinerId = other.examinerId;
        this.registrationFee = other.registrationFee;
        this.description = other.description;
        this.registrations = new HashMap<>();
        for (Map.Entry<String, ExamRegistration> entry : other.registrations.entrySet()) {
            this.registrations.put(entry.getKey(), new ExamRegistration(entry.getValue()));
        }
        this.registrationOpen = other.registrationOpen;
        this.registrationDeadline = other.registrationDeadline;
    }
    
    /**
     * Create a copy of this exam.
     */
    public Exam copy() {
        return new Exam(this);
    }
    
    /**
     * Register a student for this exam.
     * @param studentId the student to register
     * @return the registration or null if failed
     */
    public ExamRegistration registerStudent(String studentId) {
        // Check if registration is open
        if (!registrationOpen) {
            return null;
        }
        
        // Check if deadline has passed
        if (LocalDateTime.now().isAfter(registrationDeadline)) {
            return null;
        }
        
        // Check capacity
        if (registrations.size() >= maxCapacity) {
            return null;
        }
        
        // Check if already registered
        if (registrations.containsKey(studentId)) {
            return null;
        }
        
        ExamRegistration registration = new ExamRegistration(id, studentId, registrationFee);
        registrations.put(studentId, registration);
        return registration;
    }
    
    /**
     * Cancel a student's registration.
     */
    public boolean cancelRegistration(String studentId) {
        return registrations.remove(studentId) != null;
    }
    
    /**
     * Record the result for a student.
     */
    public boolean recordResult(String studentId, ExamResult result, Integer score) {
        ExamRegistration registration = registrations.get(studentId);
        if (registration == null) {
            return false;
        }
        registration.setResult(result);
        registration.setScore(score);
        return true;
    }
    
    /**
     * Check if registration is available.
     */
    public boolean canRegister() {
        return registrationOpen 
            && LocalDateTime.now().isBefore(registrationDeadline)
            && registrations.size() < maxCapacity;
    }
    
    /**
     * Get the number of available spots.
     */
    public int getAvailableSpots() {
        return maxCapacity - registrations.size();
    }
    
    /**
     * Get all registered student IDs.
     */
    public List<String> getRegisteredStudentIds() {
        return new ArrayList<>(registrations.keySet());
    }
    
    /**
     * Get a registration for a student.
     */
    public ExamRegistration getRegistration(String studentId) {
        ExamRegistration reg = registrations.get(studentId);
        return reg != null ? new ExamRegistration(reg) : null;
    }
    
    /**
     * Get all registrations (defensive copy).
     */
    public List<ExamRegistration> getAllRegistrations() {
        List<ExamRegistration> list = new ArrayList<>();
        for (ExamRegistration reg : registrations.values()) {
            list.add(new ExamRegistration(reg));
        }
        return list;
    }
    
    /**
     * Close registration.
     */
    public void closeRegistration() {
        this.registrationOpen = false;
    }
    
    /**
     * Open registration.
     */
    public void openRegistration() {
        this.registrationOpen = true;
    }
    
    /**
     * Get pass rate for this exam.
     */
    public double getPassRate() {
        if (registrations.isEmpty()) return 0;
        long passed = registrations.values().stream()
            .filter(r -> r.getResult() != null && r.getResult().isPassing())
            .count();
        return (passed * 100.0) / registrations.size();
    }
    
    /**
     * Get detailed information about the exam.
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("           EXAM INFORMATION            \n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("ID:              %s\n", id));
        sb.append(String.format("Name:            %s\n", name));
        sb.append(String.format("Instrument:      %s\n", instrument));
        sb.append(String.format("Date/Time:       %s\n", examDateTime));
        sb.append(String.format("Duration:        %d minutes\n", durationMinutes));
        sb.append(String.format("Room:            %s\n", roomId != null ? roomId : "TBA"));
        sb.append(String.format("Examiner:        %s\n", examinerId != null ? examinerId : "TBA"));
        sb.append(String.format("Registration Fee: €%.2f\n", registrationFee));
        sb.append(String.format("Capacity:        %d/%d registered\n", 
            registrations.size(), maxCapacity));
        sb.append(String.format("Registration:    %s\n", 
            registrationOpen ? "Open" : "Closed"));
        sb.append(String.format("Deadline:        %s\n", registrationDeadline));
        if (description != null) {
            sb.append(String.format("Description:     %s\n", description));
        }
        sb.append("═══════════════════════════════════════\n");
        return sb.toString();
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getInstrument() {
        return instrument;
    }
    
    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }
    
    public LocalDateTime getExamDateTime() {
        return examDateTime;
    }
    
    public void setExamDateTime(LocalDateTime examDateTime) {
        this.examDateTime = examDateTime;
    }
    
    public int getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public int getMaxCapacity() {
        return maxCapacity;
    }
    
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    
    public String getRoomId() {
        return roomId;
    }
    
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    
    public String getExaminerId() {
        return examinerId;
    }
    
    public void setExaminerId(String examinerId) {
        this.examinerId = examinerId;
    }
    
    public BigDecimal getRegistrationFee() {
        return registrationFee;
    }
    
    public void setRegistrationFee(BigDecimal registrationFee) {
        this.registrationFee = registrationFee;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isRegistrationOpen() {
        return registrationOpen;
    }
    
    public void setRegistrationOpen(boolean registrationOpen) {
        this.registrationOpen = registrationOpen;
    }
    
    public LocalDateTime getRegistrationDeadline() {
        return registrationDeadline;
    }
    
    public void setRegistrationDeadline(LocalDateTime registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exam exam = (Exam) o;
        return Objects.equals(id, exam.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s)", id, name, instrument, examDateTime);
    }
    
    /**
     * Inner class for exam registration.
     */
    public static class ExamRegistration {
        private String registrationId;
        private String examId;
        private String studentId;
        private LocalDateTime registrationDate;
        private BigDecimal fee;
        private boolean feePaid;
        private ExamResult result;
        private Integer score;
        
        public ExamRegistration() {
            this.registrationId = "REG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            this.registrationDate = LocalDateTime.now();
            this.feePaid = false;
            this.result = ExamResult.PENDING;
        }
        
        public ExamRegistration(String examId, String studentId, BigDecimal fee) {
            this();
            this.examId = examId;
            this.studentId = studentId;
            this.fee = fee;
        }
        
        /**
         * Copy constructor.
         */
        public ExamRegistration(ExamRegistration other) {
            this.registrationId = other.registrationId;
            this.examId = other.examId;
            this.studentId = other.studentId;
            this.registrationDate = other.registrationDate;
            this.fee = other.fee;
            this.feePaid = other.feePaid;
            this.result = other.result;
            this.score = other.score;
        }
        
        // Getters and Setters
        public String getRegistrationId() { return registrationId; }
        public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }
        public String getExamId() { return examId; }
        public void setExamId(String examId) { this.examId = examId; }
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        public LocalDateTime getRegistrationDate() { return registrationDate; }
        public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }
        public BigDecimal getFee() { return fee; }
        public void setFee(BigDecimal fee) { this.fee = fee; }
        public boolean isFeePaid() { return feePaid; }
        public void setFeePaid(boolean feePaid) { this.feePaid = feePaid; }
        public ExamResult getResult() { return result; }
        public void setResult(ExamResult result) { this.result = result; }
        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
        
        @Override
        public String toString() {
            return String.format("[%s] Student: %s, Result: %s%s", 
                registrationId, studentId, result,
                score != null ? " (Score: " + score + ")" : "");
        }
    }
}

