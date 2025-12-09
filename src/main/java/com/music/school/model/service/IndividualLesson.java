package com.music.school.model.service;

import com.music.school.enums.ServiceType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a single individual lesson service.
 * Extends Service abstract class.
 * 
 * Demonstrates:
 * - Inheritance from abstract class
 * - Copy constructor
 * - Single paid lesson tracking
 */
public class IndividualLesson extends Service {
    
    private String instrument;
    private int durationMinutes;
    private String teacherId;
    private String scheduledActivityId;
    private boolean consumed;
    
    /**
     * Default constructor.
     */
    public IndividualLesson() {
        super();
        this.durationMinutes = 60;
        this.consumed = false;
    }
    
    /**
     * Parameterized constructor.
     */
    public IndividualLesson(String name, BigDecimal price, String studentId,
                            String instrument, int durationMinutes, String teacherId) {
        super(name, ServiceType.SINGLE_LESSON, price, studentId);
        this.instrument = instrument;
        this.durationMinutes = durationMinutes;
        this.teacherId = teacherId;
        this.consumed = false;
        this.startDate = LocalDate.now();
        this.endDate = LocalDate.now().plusMonths(1); // Valid for 1 month
    }
    
    /**
     * Copy constructor - creates a deep copy.
     * @param other the lesson to copy
     */
    public IndividualLesson(IndividualLesson other) {
        super(other);
        this.instrument = other.instrument;
        this.durationMinutes = other.durationMinutes;
        this.teacherId = other.teacherId;
        this.scheduledActivityId = other.scheduledActivityId;
        this.consumed = other.consumed;
    }
    
    @Override
    protected String getIdPrefix() {
        return "IND";
    }
    
    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("       INDIVIDUAL LESSON INFO          \n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("ID:          %s\n", id));
        sb.append(String.format("Name:        %s\n", name));
        sb.append(String.format("Instrument:  %s\n", instrument));
        sb.append(String.format("Duration:    %d minutes\n", durationMinutes));
        sb.append(String.format("Price:       €%.2f\n", price));
        sb.append(String.format("Student ID:  %s\n", studentId));
        sb.append(String.format("Teacher ID:  %s\n", teacherId != null ? teacherId : "Not assigned"));
        sb.append(String.format("Status:      %s\n", consumed ? "Consumed" : "Available"));
        sb.append(String.format("Paid:        %s\n", paid ? "Yes" : "No"));
        sb.append("═══════════════════════════════════════\n");
        return sb.toString();
    }
    
    @Override
    public Service copy() {
        return new IndividualLesson(this);
    }
    
    @Override
    public boolean isValid() {
        return active && !consumed && !isExpired();
    }
    
    /**
     * Mark this lesson as consumed.
     */
    public void markAsConsumed() {
        this.consumed = true;
    }
    
    /**
     * Link this lesson to a scheduled activity.
     */
    public void linkToScheduledActivity(String activityId) {
        this.scheduledActivityId = activityId;
    }
    
    // Getters and Setters
    
    public String getInstrument() {
        return instrument;
    }
    
    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }
    
    public int getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public String getTeacherId() {
        return teacherId;
    }
    
    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
    
    public String getScheduledActivityId() {
        return scheduledActivityId;
    }
    
    public void setScheduledActivityId(String scheduledActivityId) {
        this.scheduledActivityId = scheduledActivityId;
    }
    
    public boolean isConsumed() {
        return consumed;
    }
    
    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }
}

