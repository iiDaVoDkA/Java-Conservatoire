package com.music.school.interfaces;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface for resources that can be booked.
 * Implemented by rooms, instruments, and teachers.
 * 
 * This interface demonstrates the use of interfaces in OOP to define
 * booking capabilities for various resources.
 */
public interface Bookable {
    
    /**
     * Get the unique identifier of the bookable resource.
     * @return the resource ID
     */
    String getResourceId();
    
    /**
     * Get the name of the bookable resource.
     * @return the resource name
     */
    String getResourceName();
    
    /**
     * Check if the resource is available at a specific time for a duration.
     * @param dateTime the start date and time
     * @param durationMinutes the duration in minutes
     * @return true if available
     */
    boolean isAvailableAt(LocalDateTime dateTime, int durationMinutes);
    
    /**
     * Get a list of times when this resource is booked.
     * @return list of booked time slots
     */
    List<TimeSlot> getBookedSlots();
    
    /**
     * Add a booking for this resource.
     * @param slot the time slot to book
     * @return true if booking was successful
     */
    boolean addBooking(TimeSlot slot);
    
    /**
     * Remove a booking from this resource.
     * @param slot the time slot to remove
     * @return true if removal was successful
     */
    boolean removeBooking(TimeSlot slot);
    
    /**
     * Check if this resource requires maintenance or is temporarily unavailable.
     * @return true if resource is under maintenance
     */
    default boolean isUnderMaintenance() {
        return false;
    }
    
    /**
     * Get a description of the resource for display.
     * @return the resource description
     */
    default String getResourceDescription() {
        return getResourceName() + " [" + getResourceId() + "]";
    }
    
    /**
     * Inner record class representing a time slot.
     */
    record TimeSlot(LocalDateTime start, LocalDateTime end, String bookingReference) {
        
        public TimeSlot {
            if (start == null || end == null) {
                throw new IllegalArgumentException("Start and end times cannot be null");
            }
            if (!end.isAfter(start)) {
                throw new IllegalArgumentException("End time must be after start time");
            }
        }
        
        public boolean overlaps(TimeSlot other) {
            return this.start.isBefore(other.end) && this.end.isAfter(other.start);
        }
        
        public long getDurationMinutes() {
            return java.time.Duration.between(start, end).toMinutes();
        }
    }
}

