package com.music.school.model.scheduling;

import com.music.school.enums.ActivityStatus;
import com.music.school.interfaces.Schedulable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base class for all scheduled activities.
 * Extended by Lesson, RoomBooking, and ExamSession.
 * 
 * Demonstrates:
 * - Abstract class with shared scheduling logic
 * - Implementation of Schedulable interface
 * - Polymorphic behavior through abstract methods
 */
public abstract class ScheduledActivity implements Schedulable {
    
    protected String id;
    protected LocalDateTime scheduledDateTime;
    protected Duration duration;
    protected ActivityStatus status;
    protected String roomId;
    protected String notes;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    
    /**
     * Default constructor.
     */
    protected ScheduledActivity() {
        this.id = generateId();
        this.status = ActivityStatus.SCHEDULED;
        this.duration = Duration.ofHours(1);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Parameterized constructor.
     */
    protected ScheduledActivity(LocalDateTime scheduledDateTime, Duration duration, String roomId) {
        this();
        this.scheduledDateTime = scheduledDateTime;
        this.duration = duration;
        this.roomId = roomId;
    }
    
    /**
     * Copy constructor - creates a deep copy.
     * @param other the activity to copy
     */
    protected ScheduledActivity(ScheduledActivity other) {
        this.id = other.id;
        this.scheduledDateTime = other.scheduledDateTime;
        this.duration = other.duration;
        this.status = other.status;
        this.roomId = other.roomId;
        this.notes = other.notes;
        this.createdAt = other.createdAt;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Generate a unique identifier.
     */
    private String generateId() {
        return getIdPrefix() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Abstract method to get the ID prefix for this activity type.
     */
    protected abstract String getIdPrefix();
    
    /**
     * Abstract method to get the activity type description.
     */
    public abstract String getActivityType();
    
    /**
     * Abstract method to get detailed activity information.
     */
    public abstract String getDetailedInfo();
    
    /**
     * Abstract method to create a copy of this activity.
     */
    public abstract ScheduledActivity copy();
    
    /**
     * Abstract method to check if this activity consumes lesson hours.
     */
    public abstract boolean consumesLessonHours();
    
    // Implementation of Schedulable interface
    
    @Override
    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }
    
    @Override
    public void setScheduledDateTime(LocalDateTime dateTime) {
        this.scheduledDateTime = dateTime;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public Duration getDuration() {
        return duration;
    }
    
    @Override
    public void setDuration(Duration duration) {
        this.duration = duration;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public ActivityStatus getStatus() {
        return status;
    }
    
    @Override
    public void setStatus(ActivityStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Complete this activity.
     */
    public void complete() {
        this.status = ActivityStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Cancel this activity.
     * @return true if cancellation is allowed, false if within 24 hours
     */
    public boolean cancel() {
        if (!canCancelWithoutPenalty()) {
            // Still cancel but mark as no-show (counts as consumed)
            this.status = ActivityStatus.NO_SHOW;
        } else {
            this.status = ActivityStatus.CANCELLED;
        }
        this.updatedAt = LocalDateTime.now();
        return canCancelWithoutPenalty();
    }
    
    /**
     * Check if this activity can be rescheduled.
     */
    public boolean canReschedule() {
        return status == ActivityStatus.SCHEDULED && canCancelWithoutPenalty();
    }
    
    /**
     * Reschedule this activity to a new time.
     */
    public boolean reschedule(LocalDateTime newDateTime) {
        if (!canReschedule()) {
            return false;
        }
        this.scheduledDateTime = newDateTime;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    /**
     * Get the duration in minutes.
     */
    public long getDurationMinutes() {
        return duration.toMinutes();
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getRoomId() {
        return roomId;
    }
    
    public void setRoomId(String roomId) {
        this.roomId = roomId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduledActivity that = (ScheduledActivity) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s)", 
            id, getActivityType(), scheduledDateTime, status);
    }
}

