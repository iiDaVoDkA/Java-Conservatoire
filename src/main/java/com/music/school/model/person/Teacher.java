package com.music.school.model.person;

import com.music.school.interfaces.Bookable;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Represents a teacher in the music school.
 * Extends Person abstract class and implements Bookable interface.
 * 
 * Demonstrates:
 * - Inheritance from abstract class
 * - Implementation of Bookable interface
 * - Copy constructor for deep copying
 * - Composition with availability schedule
 */
public class Teacher extends Person implements Bookable {
    
    private List<String> specializations;
    private List<String> qualifications;
    private BigDecimal hourlyRate;
    private Map<DayOfWeek, List<TimeRange>> availability;
    private List<TimeSlot> bookedSlots;
    private int yearsOfExperience;
    private String biography;
    
    /**
     * Default constructor.
     */
    public Teacher() {
        super();
        this.specializations = new ArrayList<>();
        this.qualifications = new ArrayList<>();
        this.hourlyRate = BigDecimal.ZERO;
        this.availability = new EnumMap<>(DayOfWeek.class);
        this.bookedSlots = new ArrayList<>();
        this.yearsOfExperience = 0;
    }
    
    /**
     * Parameterized constructor with essential fields.
     */
    public Teacher(String firstName, String lastName, String address,
                   LocalDate dateOfBirth, String phone, String email,
                   BigDecimal hourlyRate, List<String> specializations) {
        super(firstName, lastName, address, dateOfBirth, phone, email);
        this.specializations = new ArrayList<>(specializations);
        this.qualifications = new ArrayList<>();
        this.hourlyRate = hourlyRate;
        this.availability = new EnumMap<>(DayOfWeek.class);
        this.bookedSlots = new ArrayList<>();
        this.yearsOfExperience = 0;
    }
    
    /**
     * Copy constructor - creates a deep copy of another Teacher.
     * @param other the teacher to copy
     */
    public Teacher(Teacher other) {
        super(other);
        this.specializations = new ArrayList<>(other.specializations);
        this.qualifications = new ArrayList<>(other.qualifications);
        this.hourlyRate = other.hourlyRate;
        this.availability = new EnumMap<>(DayOfWeek.class);
        for (Map.Entry<DayOfWeek, List<TimeRange>> entry : other.availability.entrySet()) {
            this.availability.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        this.bookedSlots = new ArrayList<>();
        for (TimeSlot slot : other.bookedSlots) {
            this.bookedSlots.add(new TimeSlot(slot.start(), slot.end(), slot.bookingReference()));
        }
        this.yearsOfExperience = other.yearsOfExperience;
        this.biography = other.biography;
    }
    
    @Override
    protected String getIdPrefix() {
        return "TCH";
    }
    
    @Override
    public String getRole() {
        return "Teacher";
    }
    
    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("           TEACHER INFORMATION          \n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("ID:              %s\n", id));
        sb.append(String.format("Name:            %s\n", getFullName()));
        sb.append(String.format("Email:           %s\n", email));
        sb.append(String.format("Phone:           %s\n", phone));
        sb.append(String.format("Hourly Rate:     €%.2f\n", hourlyRate));
        sb.append(String.format("Experience:      %d years\n", yearsOfExperience));
        sb.append(String.format("Specializations: %s\n", 
            specializations.isEmpty() ? "None" : String.join(", ", specializations)));
        sb.append(String.format("Qualifications:  %s\n", 
            qualifications.isEmpty() ? "None" : String.join(", ", qualifications)));
        sb.append(String.format("Status:          %s\n", active ? "Active" : "Inactive"));
        sb.append("───────────────────────────────────────\n");
        sb.append("Availability:\n");
        if (availability.isEmpty()) {
            sb.append("  No availability set\n");
        } else {
            for (DayOfWeek day : DayOfWeek.values()) {
                List<TimeRange> ranges = availability.get(day);
                if (ranges != null && !ranges.isEmpty()) {
                    sb.append(String.format("  %-10s: ", day));
                    for (int i = 0; i < ranges.size(); i++) {
                        TimeRange range = ranges.get(i);
                        sb.append(String.format("%s-%s", range.start(), range.end()));
                        if (i < ranges.size() - 1) sb.append(", ");
                    }
                    sb.append("\n");
                }
            }
        }
        sb.append("═══════════════════════════════════════\n");
        return sb.toString();
    }
    
    @Override
    public Person copy() {
        return new Teacher(this);
    }
    
    // Implementation of Bookable interface
    
    @Override
    public String getResourceId() {
        return id;
    }
    
    @Override
    public String getResourceName() {
        return getFullName();
    }
    
    @Override
    public boolean isAvailableAt(LocalDateTime dateTime, int durationMinutes) {
        // Check if within general availability
        DayOfWeek day = dateTime.getDayOfWeek();
        LocalTime startTime = dateTime.toLocalTime();
        LocalTime endTime = startTime.plusMinutes(durationMinutes);
        
        List<TimeRange> dayAvailability = availability.get(day);
        if (dayAvailability == null || dayAvailability.isEmpty()) {
            return false;
        }
        
        boolean withinAvailability = dayAvailability.stream()
            .anyMatch(range -> !startTime.isBefore(range.start()) && !endTime.isAfter(range.end()));
        
        if (!withinAvailability) {
            return false;
        }
        
        // Check if not already booked
        LocalDateTime endDateTime = dateTime.plusMinutes(durationMinutes);
        TimeSlot requestedSlot = new TimeSlot(dateTime, endDateTime, "check");
        
        return bookedSlots.stream().noneMatch(slot -> slot.overlaps(requestedSlot));
    }
    
    @Override
    public List<TimeSlot> getBookedSlots() {
        return new ArrayList<>(bookedSlots);
    }
    
    @Override
    public boolean addBooking(TimeSlot slot) {
        if (!isAvailableAt(slot.start(), (int) slot.getDurationMinutes())) {
            return false;
        }
        bookedSlots.add(slot);
        return true;
    }
    
    @Override
    public boolean removeBooking(TimeSlot slot) {
        return bookedSlots.removeIf(s -> 
            s.bookingReference().equals(slot.bookingReference()));
    }
    
    /**
     * Add a specialization (instrument the teacher can teach).
     */
    public void addSpecialization(String instrument) {
        if (!specializations.contains(instrument)) {
            specializations.add(instrument);
        }
    }
    
    /**
     * Remove a specialization.
     */
    public void removeSpecialization(String instrument) {
        specializations.remove(instrument);
    }
    
    /**
     * Check if teacher can teach a specific instrument.
     */
    public boolean canTeach(String instrument) {
        return specializations.stream()
            .anyMatch(s -> s.equalsIgnoreCase(instrument));
    }
    
    /**
     * Add a qualification.
     */
    public void addQualification(String qualification) {
        if (!qualifications.contains(qualification)) {
            qualifications.add(qualification);
        }
    }
    
    /**
     * Set availability for a day.
     */
    public void setAvailability(DayOfWeek day, LocalTime start, LocalTime end) {
        availability.computeIfAbsent(day, k -> new ArrayList<>())
            .add(new TimeRange(start, end));
    }
    
    /**
     * Clear availability for a day.
     */
    public void clearAvailability(DayOfWeek day) {
        availability.remove(day);
    }
    
    /**
     * Inner record for time ranges.
     */
    public record TimeRange(LocalTime start, LocalTime end) {
        public TimeRange {
            if (start == null || end == null) {
                throw new IllegalArgumentException("Start and end times cannot be null");
            }
            if (!end.isAfter(start)) {
                throw new IllegalArgumentException("End time must be after start time");
            }
        }
    }
    
    // Getters and Setters
    
    public List<String> getSpecializations() {
        return new ArrayList<>(specializations);
    }
    
    public void setSpecializations(List<String> specializations) {
        this.specializations = new ArrayList<>(specializations);
    }
    
    public List<String> getQualifications() {
        return new ArrayList<>(qualifications);
    }
    
    public void setQualifications(List<String> qualifications) {
        this.qualifications = new ArrayList<>(qualifications);
    }
    
    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }
    
    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
    
    public Map<DayOfWeek, List<TimeRange>> getAvailability() {
        Map<DayOfWeek, List<TimeRange>> copy = new EnumMap<>(DayOfWeek.class);
        for (Map.Entry<DayOfWeek, List<TimeRange>> entry : availability.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }
    
    public int getYearsOfExperience() {
        return yearsOfExperience;
    }
    
    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
    
    public String getBiography() {
        return biography;
    }
    
    public void setBiography(String biography) {
        this.biography = biography;
    }
}

