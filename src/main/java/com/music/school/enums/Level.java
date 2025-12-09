package com.music.school.enums;

/**
 * Enumeration representing student skill levels.
 * Used to categorize students and match them with appropriate teachers and courses.
 */
public enum Level {
    BEGINNER("Beginner", 1),
    INTERMEDIATE("Intermediate", 2),
    ADVANCED("Advanced", 3);

    private final String displayName;
    private final int order;

    Level(String displayName, int order) {
        this.displayName = displayName;
        this.order = order;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getOrder() {
        return order;
    }

    /**
     * Check if this level is higher than another level.
     */
    public boolean isHigherThan(Level other) {
        return this.order > other.order;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

