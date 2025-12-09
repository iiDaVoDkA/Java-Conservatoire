package com.music.school.enums;

/**
 * Enumeration representing exam results.
 */
public enum ExamResult {
    PENDING("Pending", "Result not yet available"),
    PASS("Pass", "Student passed the exam"),
    FAIL("Fail", "Student did not pass the exam"),
    DISTINCTION("Distinction", "Student passed with distinction"),
    ABSENT("Absent", "Student was absent from the exam");

    private final String displayName;
    private final String description;

    ExamResult(String displayName, String description) {
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
     * Check if this result indicates the student passed.
     */
    public boolean isPassing() {
        return this == PASS || this == DISTINCTION;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

