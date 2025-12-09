package com.music.school.model.person;

import com.music.school.enums.Level;

import java.time.LocalDate;
import java.util.*;

/**
 * Represents a student in the music school.
 * Extends Person abstract class and implements copy constructor.
 * 
 * Demonstrates:
 * - Inheritance from abstract class
 * - Copy constructor for deep copying
 * - Encapsulation with defensive copies
 */
public class Student extends Person {
    
    private Level level;
    private List<String> preferredInstruments;
    private Map<String, Integer> packageHours; // packageId -> remaining hours
    private List<String> enrolledPackageIds;
    private int totalPurchasedHours;
    private int totalConsumedHours;
    private String parentGuardianName; // For minors
    private String parentGuardianPhone;
    
    /**
     * Default constructor.
     */
    public Student() {
        super();
        this.level = Level.BEGINNER;
        this.preferredInstruments = new ArrayList<>();
        this.packageHours = new HashMap<>();
        this.enrolledPackageIds = new ArrayList<>();
        this.totalPurchasedHours = 0;
        this.totalConsumedHours = 0;
    }
    
    /**
     * Parameterized constructor with essential fields.
     */
    public Student(String firstName, String lastName, String address, 
                   LocalDate dateOfBirth, String phone, String email, Level level) {
        super(firstName, lastName, address, dateOfBirth, phone, email);
        this.level = level;
        this.preferredInstruments = new ArrayList<>();
        this.packageHours = new HashMap<>();
        this.enrolledPackageIds = new ArrayList<>();
        this.totalPurchasedHours = 0;
        this.totalConsumedHours = 0;
    }
    
    /**
     * Copy constructor - creates a deep copy of another Student.
     * @param other the student to copy
     */
    public Student(Student other) {
        super(other);
        this.level = other.level;
        this.preferredInstruments = new ArrayList<>(other.preferredInstruments);
        this.packageHours = new HashMap<>(other.packageHours);
        this.enrolledPackageIds = new ArrayList<>(other.enrolledPackageIds);
        this.totalPurchasedHours = other.totalPurchasedHours;
        this.totalConsumedHours = other.totalConsumedHours;
        this.parentGuardianName = other.parentGuardianName;
        this.parentGuardianPhone = other.parentGuardianPhone;
    }
    
    @Override
    protected String getIdPrefix() {
        return "STU";
    }
    
    @Override
    public String getRole() {
        return "Student";
    }
    
    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("           STUDENT INFORMATION          \n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("ID:            %s\n", id));
        sb.append(String.format("Name:          %s\n", getFullName()));
        sb.append(String.format("Email:         %s\n", email));
        sb.append(String.format("Phone:         %s\n", phone));
        sb.append(String.format("Address:       %s\n", address));
        sb.append(String.format("Date of Birth: %s (Age: %d)\n", dateOfBirth, getAge()));
        sb.append(String.format("Level:         %s\n", level));
        sb.append(String.format("Instruments:   %s\n", 
            preferredInstruments.isEmpty() ? "None" : String.join(", ", preferredInstruments)));
        sb.append(String.format("Purchased:     %d hours\n", totalPurchasedHours));
        sb.append(String.format("Consumed:      %d hours\n", totalConsumedHours));
        sb.append(String.format("Remaining:     %d hours\n", getRemainingHours()));
        sb.append(String.format("Status:        %s\n", active ? "Active" : "Inactive"));
        if (isMinor() && parentGuardianName != null) {
            sb.append(String.format("Guardian:      %s (%s)\n", parentGuardianName, parentGuardianPhone));
        }
        sb.append("═══════════════════════════════════════\n");
        return sb.toString();
    }
    
    @Override
    public Person copy() {
        return new Student(this);
    }
    
    /**
     * Get the total remaining hours across all packages.
     * @return total remaining hours
     */
    public int getRemainingHours() {
        return totalPurchasedHours - totalConsumedHours;
    }
    
    /**
     * Add hours from a package purchase.
     * @param packageId the package ID
     * @param hours the hours to add
     */
    public void addPackageHours(String packageId, int hours) {
        packageHours.merge(packageId, hours, Integer::sum);
        enrolledPackageIds.add(packageId);
        totalPurchasedHours += hours;
    }
    
    /**
     * Consume hours from available packages.
     * @param hours the hours to consume
     * @return true if hours were successfully consumed
     */
    public boolean consumeHours(int hours) {
        if (getRemainingHours() < hours) {
            return false;
        }
        totalConsumedHours += hours;
        
        // Deduct from packages (FIFO - first in, first out)
        int remaining = hours;
        for (String packageId : enrolledPackageIds) {
            if (remaining <= 0) break;
            int available = packageHours.getOrDefault(packageId, 0);
            int toConsume = Math.min(available, remaining);
            packageHours.put(packageId, available - toConsume);
            remaining -= toConsume;
        }
        return true;
    }
    
    /**
     * Add a preferred instrument.
     * @param instrument the instrument to add
     */
    public void addPreferredInstrument(String instrument) {
        if (!preferredInstruments.contains(instrument)) {
            preferredInstruments.add(instrument);
        }
    }
    
    /**
     * Remove a preferred instrument.
     * @param instrument the instrument to remove
     */
    public void removePreferredInstrument(String instrument) {
        preferredInstruments.remove(instrument);
    }
    
    /**
     * Check if student has hours available.
     * @param hours the hours needed
     * @return true if hours are available
     */
    public boolean hasAvailableHours(int hours) {
        return getRemainingHours() >= hours;
    }
    
    // Getters and Setters
    
    public Level getLevel() {
        return level;
    }
    
    public void setLevel(Level level) {
        this.level = level;
    }
    
    /**
     * Get a defensive copy of preferred instruments.
     */
    public List<String> getPreferredInstruments() {
        return new ArrayList<>(preferredInstruments);
    }
    
    public void setPreferredInstruments(List<String> preferredInstruments) {
        this.preferredInstruments = new ArrayList<>(preferredInstruments);
    }
    
    /**
     * Get a defensive copy of package hours.
     */
    public Map<String, Integer> getPackageHours() {
        return new HashMap<>(packageHours);
    }
    
    /**
     * Get a defensive copy of enrolled package IDs.
     */
    public List<String> getEnrolledPackageIds() {
        return new ArrayList<>(enrolledPackageIds);
    }
    
    public int getTotalPurchasedHours() {
        return totalPurchasedHours;
    }
    
    public int getTotalConsumedHours() {
        return totalConsumedHours;
    }
    
    public String getParentGuardianName() {
        return parentGuardianName;
    }
    
    public void setParentGuardianName(String parentGuardianName) {
        this.parentGuardianName = parentGuardianName;
    }
    
    public String getParentGuardianPhone() {
        return parentGuardianPhone;
    }
    
    public void setParentGuardianPhone(String parentGuardianPhone) {
        this.parentGuardianPhone = parentGuardianPhone;
    }
}

