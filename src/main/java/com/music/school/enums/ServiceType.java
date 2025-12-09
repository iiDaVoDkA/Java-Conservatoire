package com.music.school.enums;

/**
 * Enumeration representing types of services offered by the school.
 */
public enum ServiceType {
    MUSIC_PACKAGE("Music Package", "Package of music lessons"),
    UNLIMITED_PACKAGE("Unlimited Package", "Unlimited lessons for a period"),
    GROUP_LESSON("Group Lesson", "Lesson with multiple students"),
    INDIVIDUAL_LESSON("Individual Lesson", "One-on-one lesson"),
    SINGLE_LESSON("Single Paid Lesson", "Single pay-as-you-go lesson"),
    INSTRUMENT_RENTAL("Instrument Rental", "Rental of musical instruments"),
    ROOM_BOOKING("Room Booking", "Booking of practice rooms");

    private final String displayName;
    private final String description;

    ServiceType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

