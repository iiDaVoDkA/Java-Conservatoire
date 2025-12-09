package com.music.school.model.scheduling;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Represents a room booking (practice room rental).
 * Extends ScheduledActivity abstract class.
 * 
 * Demonstrates:
 * - Inheritance from abstract class
 * - Copy constructor
 * - Room rental specific logic
 */
public class RoomBooking extends ScheduledActivity {
    
    private String studentId;
    private BigDecimal hourlyRate;
    private String purpose; // Practice, Rehearsal, Recording, etc.
    private boolean paid;
    
    /**
     * Default constructor.
     */
    public RoomBooking() {
        super();
        this.paid = false;
    }
    
    /**
     * Parameterized constructor.
     */
    public RoomBooking(LocalDateTime scheduledDateTime, Duration duration, String roomId,
                       String studentId, BigDecimal hourlyRate, String purpose) {
        super(scheduledDateTime, duration, roomId);
        this.studentId = studentId;
        this.hourlyRate = hourlyRate;
        this.purpose = purpose;
        this.paid = false;
    }
    
    /**
     * Copy constructor - creates a deep copy.
     * @param other the booking to copy
     */
    public RoomBooking(RoomBooking other) {
        super(other);
        this.studentId = other.studentId;
        this.hourlyRate = other.hourlyRate;
        this.purpose = other.purpose;
        this.paid = other.paid;
    }
    
    @Override
    protected String getIdPrefix() {
        return "BKG";
    }
    
    @Override
    public String getActivityType() {
        return "Room Booking";
    }
    
    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("        ROOM BOOKING INFORMATION       \n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("ID:          %s\n", id));
        sb.append(String.format("Room ID:     %s\n", roomId));
        sb.append(String.format("Student ID:  %s\n", studentId));
        sb.append(String.format("Purpose:     %s\n", purpose != null ? purpose : "Practice"));
        sb.append(String.format("Date/Time:   %s\n", scheduledDateTime));
        sb.append(String.format("Duration:    %d minutes\n", getDurationMinutes()));
        sb.append(String.format("Hourly Rate: €%.2f\n", hourlyRate));
        sb.append(String.format("Total Cost:  €%.2f\n", calculateCost()));
        sb.append(String.format("Status:      %s\n", status));
        sb.append(String.format("Paid:        %s\n", paid ? "Yes" : "No"));
        if (notes != null && !notes.isEmpty()) {
            sb.append(String.format("Notes:       %s\n", notes));
        }
        sb.append("═══════════════════════════════════════\n");
        return sb.toString();
    }
    
    @Override
    public ScheduledActivity copy() {
        return new RoomBooking(this);
    }
    
    @Override
    public boolean consumesLessonHours() {
        // Room bookings don't consume lesson hours
        return false;
    }
    
    /**
     * Calculate the total cost of this booking.
     */
    public BigDecimal calculateCost() {
        if (hourlyRate == null) return BigDecimal.ZERO;
        double hours = getDurationMinutes() / 60.0;
        return hourlyRate.multiply(BigDecimal.valueOf(hours));
    }
    
    /**
     * Mark this booking as paid.
     */
    public void markAsPaid() {
        this.paid = true;
    }
    
    // Getters and Setters
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }
    
    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
    
    public String getPurpose() {
        return purpose;
    }
    
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    
    public boolean isPaid() {
        return paid;
    }
    
    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}

