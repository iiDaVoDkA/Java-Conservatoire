package com.music.school.enums;

/**
 * Enumeration representing the status of scheduled activities.
 * Used for lessons, room bookings, and exam sessions.
 */
public enum ActivityStatus {
    SCHEDULED("Scheduled", "Activity is scheduled and pending"),
    IN_PROGRESS("In Progress", "Activity is currently ongoing"),
    COMPLETED("Completed", "Activity has been completed successfully"),
    CANCELLED("Cancelled", "Activity has been cancelled"),
    NO_SHOW("No Show", "Participant did not attend");

    private final String displayName;
    private final String description;

    ActivityStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if this status indicates the activity is finalized.
     */
    public boolean isFinalized() {
        return this == COMPLETED || this == CANCELLED || this == NO_SHOW;
    }

    /**
     * Check if this status consumes lesson hours.
     */
    public boolean consumesHours() {
        return this == COMPLETED || this == NO_SHOW;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

