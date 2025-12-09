package com.music.school.model.scheduling;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a scheduled lesson.
 * Extends ScheduledActivity abstract class.
 * 
 * Demonstrates:
 * - Inheritance from abstract class
 * - Copy constructor
 * - Support for both individual and group lessons
 */
public class Lesson extends ScheduledActivity {
    
    private String teacherId;
    private List<String> studentIds;
    private String instrument;
    private String instrumentId; // If using school instrument
    private boolean isGroupLesson;
    private String packageId; // Reference to course package if applicable
    private String serviceId; // Reference to individual lesson service if applicable
    
    /**
     * Default constructor.
     */
    public Lesson() {
        super();
        this.studentIds = new ArrayList<>();
        this.isGroupLesson = false;
    }
    
    /**
     * Constructor for individual lesson.
     */
    public Lesson(LocalDateTime scheduledDateTime, Duration duration, String roomId,
                  String teacherId, String studentId, String instrument) {
        super(scheduledDateTime, duration, roomId);
        this.teacherId = teacherId;
        this.studentIds = new ArrayList<>();
        this.studentIds.add(studentId);
        this.instrument = instrument;
        this.isGroupLesson = false;
    }
    
    /**
     * Constructor for group lesson.
     */
    public Lesson(LocalDateTime scheduledDateTime, Duration duration, String roomId,
                  String teacherId, List<String> studentIds, String instrument) {
        super(scheduledDateTime, duration, roomId);
        this.teacherId = teacherId;
        this.studentIds = new ArrayList<>(studentIds);
        this.instrument = instrument;
        this.isGroupLesson = studentIds.size() > 1;
    }
    
    /**
     * Copy constructor - creates a deep copy.
     * @param other the lesson to copy
     */
    public Lesson(Lesson other) {
        super(other);
        this.teacherId = other.teacherId;
        this.studentIds = new ArrayList<>(other.studentIds);
        this.instrument = other.instrument;
        this.instrumentId = other.instrumentId;
        this.isGroupLesson = other.isGroupLesson;
        this.packageId = other.packageId;
        this.serviceId = other.serviceId;
    }
    
    @Override
    protected String getIdPrefix() {
        return "LES";
    }
    
    @Override
    public String getActivityType() {
        return isGroupLesson ? "Group Lesson" : "Individual Lesson";
    }
    
    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("           LESSON INFORMATION          \n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("ID:          %s\n", id));
        sb.append(String.format("Type:        %s\n", getActivityType()));
        sb.append(String.format("Instrument:  %s\n", instrument));
        sb.append(String.format("Date/Time:   %s\n", scheduledDateTime));
        sb.append(String.format("Duration:    %d minutes\n", getDurationMinutes()));
        sb.append(String.format("Teacher ID:  %s\n", teacherId));
        sb.append(String.format("Students:    %s\n", String.join(", ", studentIds)));
        sb.append(String.format("Room ID:     %s\n", roomId != null ? roomId : "Not assigned"));
        if (instrumentId != null) {
            sb.append(String.format("Instrument:  %s (school instrument)\n", instrumentId));
        }
        sb.append(String.format("Status:      %s\n", status));
        if (packageId != null) {
            sb.append(String.format("Package:     %s\n", packageId));
        }
        if (notes != null && !notes.isEmpty()) {
            sb.append(String.format("Notes:       %s\n", notes));
        }
        sb.append("═══════════════════════════════════════\n");
        return sb.toString();
    }
    
    @Override
    public ScheduledActivity copy() {
        return new Lesson(this);
    }
    
    @Override
    public boolean consumesLessonHours() {
        // Lesson hours are consumed if completed or cancelled within 24h
        return status.consumesHours();
    }
    
    /**
     * Add a student to this lesson (for group lessons).
     */
    public void addStudent(String studentId) {
        if (!studentIds.contains(studentId)) {
            studentIds.add(studentId);
            if (studentIds.size() > 1) {
                isGroupLesson = true;
            }
        }
    }
    
    /**
     * Remove a student from this lesson.
     */
    public boolean removeStudent(String studentId) {
        return studentIds.remove(studentId);
    }
    
    /**
     * Get the number of students in this lesson.
     */
    public int getStudentCount() {
        return studentIds.size();
    }
    
    /**
     * Check if a specific student is enrolled.
     */
    public boolean hasStudent(String studentId) {
        return studentIds.contains(studentId);
    }
    
    /**
     * Get the hours consumed per student.
     */
    public int getHoursPerStudent() {
        return (int) Math.ceil(getDurationMinutes() / 60.0);
    }
    
    // Getters and Setters
    
    public String getTeacherId() {
        return teacherId;
    }
    
    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
    
    /**
     * Get a defensive copy of student IDs.
     */
    public List<String> getStudentIds() {
        return new ArrayList<>(studentIds);
    }
    
    public void setStudentIds(List<String> studentIds) {
        this.studentIds = new ArrayList<>(studentIds);
        this.isGroupLesson = studentIds.size() > 1;
    }
    
    public String getInstrument() {
        return instrument;
    }
    
    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }
    
    public String getInstrumentId() {
        return instrumentId;
    }
    
    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }
    
    public boolean isGroupLesson() {
        return isGroupLesson;
    }
    
    public void setGroupLesson(boolean groupLesson) {
        isGroupLesson = groupLesson;
    }
    
    public String getPackageId() {
        return packageId;
    }
    
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }
    
    public String getServiceId() {
        return serviceId;
    }
    
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}

