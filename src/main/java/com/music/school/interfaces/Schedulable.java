package com.music.school.interfaces;

import com.music.school.enums.ActivityStatus;
import java.time.LocalDateTime;
import java.time.Duration;

/**
 * Interface for entities that can be scheduled.
 * Implemented by lessons, room bookings, and exam sessions.
 * 
 * This interface demonstrates the use of interfaces in OOP to define
 * independent capabilities across different types.
 */
public interface Schedulable {
    
    /**
     * Get the scheduled date and time.
     * @return the start date and time of the scheduled activity
     */
    LocalDateTime getScheduledDateTime();
    
    /**
     * Set the scheduled date and time.
     * @param dateTime the new date and time
     */
    void setScheduledDateTime(LocalDateTime dateTime);
    
    /**
     * Get the duration of the scheduled activity.
     * @return the duration
     */
    Duration getDuration();
    
    /**
     * Set the duration of the scheduled activity.
     * @param duration the new duration
     */
    void setDuration(Duration duration);
    
    /**
     * Get the current status of the activity.
     * @return the activity status
     */
    ActivityStatus getStatus();
    
    /**
     * Update the status of the activity.
     * @param status the new status
     */
    void setStatus(ActivityStatus status);
    
    /**
     * Get the end time of the scheduled activity.
     * @return the end date and time
     */
    default LocalDateTime getEndDateTime() {
        return getScheduledDateTime().plus(getDuration());
    }
    
    /**
     * Check if this activity conflicts with another schedulable.
     * @param other the other schedulable to check against
     * @return true if there is a time conflict
     */
    default boolean conflictsWith(Schedulable other) {
        if (other == null) return false;
        
        LocalDateTime thisStart = this.getScheduledDateTime();
        LocalDateTime thisEnd = this.getEndDateTime();
        LocalDateTime otherStart = other.getScheduledDateTime();
        LocalDateTime otherEnd = other.getEndDateTime();
        
        // Check if time periods overlap
        return thisStart.isBefore(otherEnd) && thisEnd.isAfter(otherStart);
    }
    
    /**
     * Check if this activity can be cancelled (not within 24 hours).
     * @return true if cancellation is allowed without penalty
     */
    default boolean canCancelWithoutPenalty() {
        return LocalDateTime.now().plusHours(24).isBefore(getScheduledDateTime());
    }
    
    /**
     * Check if this activity is in the past.
     * @return true if the activity has passed
     */
    default boolean isPast() {
        return LocalDateTime.now().isAfter(getEndDateTime());
    }
    
    /**
     * Check if this activity is currently ongoing.
     * @return true if the activity is happening now
     */
    default boolean isOngoing() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(getScheduledDateTime()) && now.isBefore(getEndDateTime());
    }
    
    /**
     * Get a formatted string representation of the schedule.
     * @return formatted schedule string
     */
    default String getScheduleDescription() {
        return String.format("%s - %s (%d min)", 
            getScheduledDateTime().toString(),
            getEndDateTime().toString(),
            getDuration().toMinutes());
    }
}

