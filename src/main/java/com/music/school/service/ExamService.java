package com.music.school.service;

import com.music.school.enums.ExamResult;
import com.music.school.model.exam.Exam;
import com.music.school.model.exam.Exam.ExamRegistration;
import com.music.school.model.person.Student;
import com.music.school.repository.DataRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing exam operations.
 * 
 * Demonstrates:
 * - Business rule enforcement (capacity limits, registration deadlines)
 * - Exam result management
 */
public class ExamService {
    
    private final DataRepository repository;
    
    public ExamService() {
        this.repository = DataRepository.getInstance();
    }
    
    /**
     * Create a new exam.
     */
    public Exam createExam(String name, String instrument, LocalDateTime examDateTime,
                           int maxCapacity, BigDecimal registrationFee) {
        Exam exam = new Exam(name, instrument, examDateTime, maxCapacity, registrationFee);
        repository.addExam(exam);
        return exam;
    }
    
    /**
     * Register a student for an exam.
     * Implements business rules for registration.
     */
    public ExamRegistration registerStudentForExam(String examId, String studentId) {
        Exam exam = repository.getExam(examId);
        if (exam == null) {
            throw new IllegalArgumentException("Exam not found: " + examId);
        }
        
        Student student = repository.getStudent(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }
        
        // Check if registration is open
        if (!exam.isRegistrationOpen()) {
            throw new IllegalStateException("Registration is closed for this exam");
        }
        
        // Check if deadline has passed
        if (LocalDateTime.now().isAfter(exam.getRegistrationDeadline())) {
            throw new IllegalStateException("Registration deadline has passed");
        }
        
        // Check capacity
        if (exam.getAvailableSpots() <= 0) {
            throw new IllegalStateException("Exam is at maximum capacity");
        }
        
        // Check if student is already registered
        if (exam.getRegisteredStudentIds().contains(studentId)) {
            throw new IllegalStateException("Student is already registered for this exam");
        }
        
        // Register the student
        ExamRegistration registration = exam.registerStudent(studentId);
        if (registration == null) {
            throw new IllegalStateException("Registration failed");
        }
        
        return registration;
    }
    
    /**
     * Cancel a student's exam registration.
     */
    public boolean cancelExamRegistration(String examId, String studentId) {
        Exam exam = repository.getExam(examId);
        if (exam == null) {
            throw new IllegalArgumentException("Exam not found: " + examId);
        }
        
        return exam.cancelRegistration(studentId);
    }
    
    /**
     * Record exam result for a student.
     */
    public boolean recordExamResult(String examId, String studentId, 
                                     ExamResult result, Integer score) {
        Exam exam = repository.getExam(examId);
        if (exam == null) {
            throw new IllegalArgumentException("Exam not found: " + examId);
        }
        
        // Validate score range if provided
        if (score != null && (score < 0 || score > 100)) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
        
        return exam.recordResult(studentId, result, score);
    }
    
    /**
     * Get all upcoming exams.
     */
    public List<Exam> getUpcomingExams() {
        return repository.getUpcomingExams();
    }
    
    /**
     * Get exams with available registration.
     */
    public List<Exam> getExamsWithOpenRegistration() {
        return repository.getAllExams().stream()
            .filter(Exam::canRegister)
            .sorted(Comparator.comparing(Exam::getExamDateTime))
            .collect(Collectors.toList());
    }
    
    /**
     * Get all exams for a specific instrument.
     */
    public List<Exam> getExamsByInstrument(String instrument) {
        return repository.getExamsByInstrument(instrument);
    }
    
    /**
     * Get exam results for a student.
     */
    public Map<String, ExamRegistration> getStudentExamResults(String studentId) {
        Map<String, ExamRegistration> results = new HashMap<>();
        
        for (Exam exam : repository.getAllExams()) {
            ExamRegistration reg = exam.getRegistration(studentId);
            if (reg != null) {
                results.put(exam.getName(), reg);
            }
        }
        
        return results;
    }
    
    /**
     * Get student's exam history summary.
     */
    public String getStudentExamSummary(String studentId) {
        Student student = repository.getStudent(studentId);
        if (student == null) {
            return "Student not found";
        }
        
        Map<String, ExamRegistration> results = getStudentExamResults(studentId);
        
        long passed = results.values().stream()
            .filter(r -> r.getResult() != null && r.getResult().isPassing())
            .count();
        
        long failed = results.values().stream()
            .filter(r -> r.getResult() == ExamResult.FAIL)
            .count();
        
        long pending = results.values().stream()
            .filter(r -> r.getResult() == ExamResult.PENDING)
            .count();
        
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("       STUDENT EXAM SUMMARY            \n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("Student:    %s\n", student.getFullName()));
        sb.append(String.format("Student ID: %s\n", studentId));
        sb.append("───────────────────────────────────────\n");
        sb.append(String.format("Total Exams: %d\n", results.size()));
        sb.append(String.format("Passed:      %d\n", passed));
        sb.append(String.format("Failed:      %d\n", failed));
        sb.append(String.format("Pending:     %d\n", pending));
        sb.append("───────────────────────────────────────\n");
        sb.append("Details:\n");
        for (Map.Entry<String, ExamRegistration> entry : results.entrySet()) {
            ExamRegistration reg = entry.getValue();
            sb.append(String.format("  - %s: %s", entry.getKey(), reg.getResult()));
            if (reg.getScore() != null) {
                sb.append(String.format(" (Score: %d)", reg.getScore()));
            }
            sb.append("\n");
        }
        sb.append("═══════════════════════════════════════\n");
        
        return sb.toString();
    }
    
    /**
     * Close registration for an exam.
     */
    public void closeExamRegistration(String examId) {
        Exam exam = repository.getExam(examId);
        if (exam == null) {
            throw new IllegalArgumentException("Exam not found: " + examId);
        }
        exam.closeRegistration();
    }
    
    /**
     * Get pass rate for an exam.
     */
    public double getExamPassRate(String examId) {
        Exam exam = repository.getExam(examId);
        if (exam == null) {
            throw new IllegalArgumentException("Exam not found: " + examId);
        }
        return exam.getPassRate();
    }
    
    /**
     * Get exam statistics.
     */
    public String getExamStatistics(String examId) {
        Exam exam = repository.getExam(examId);
        if (exam == null) {
            return "Exam not found";
        }
        
        List<ExamRegistration> registrations = exam.getAllRegistrations();
        
        long passed = registrations.stream()
            .filter(r -> r.getResult() != null && r.getResult().isPassing())
            .count();
        
        OptionalDouble avgScore = registrations.stream()
            .filter(r -> r.getScore() != null)
            .mapToInt(ExamRegistration::getScore)
            .average();
        
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("          EXAM STATISTICS              \n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("Exam:        %s\n", exam.getName()));
        sb.append(String.format("Instrument:  %s\n", exam.getInstrument()));
        sb.append(String.format("Date:        %s\n", exam.getExamDateTime()));
        sb.append("───────────────────────────────────────\n");
        sb.append(String.format("Registered:  %d/%d\n", registrations.size(), exam.getMaxCapacity()));
        sb.append(String.format("Pass Rate:   %.1f%%\n", exam.getPassRate()));
        if (avgScore.isPresent()) {
            sb.append(String.format("Avg Score:   %.1f\n", avgScore.getAsDouble()));
        }
        sb.append("═══════════════════════════════════════\n");
        
        return sb.toString();
    }
    
    /**
     * Mark exam registration fee as paid.
     */
    public void markExamFeePaid(String examId, String studentId) {
        Exam exam = repository.getExam(examId);
        if (exam == null) {
            throw new IllegalArgumentException("Exam not found: " + examId);
        }
        
        ExamRegistration reg = exam.getRegistration(studentId);
        if (reg == null) {
            throw new IllegalArgumentException("Student is not registered for this exam");
        }
        
        // Get the actual registration from the exam's internal map
        for (ExamRegistration r : exam.getAllRegistrations()) {
            if (r.getStudentId().equals(studentId)) {
                r.setFeePaid(true);
                break;
            }
        }
    }
}

