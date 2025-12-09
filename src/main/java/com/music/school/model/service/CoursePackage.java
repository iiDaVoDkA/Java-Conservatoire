package com.music.school.model.service;

import com.music.school.enums.ServiceType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a course package purchased by a student.
 * Extends Service abstract class.
 * 
 * Demonstrates:
 * - Inheritance from abstract class
 * - Copy constructor
 * - Encapsulation of package-specific logic
 */
public class CoursePackage extends Service {
    
    private int totalHours;
    private int usedHours;
    private String instrument;
    private boolean unlimited;
    private int maxLessonsPerWeek;
    
    /**
     * Default constructor.
     */
    public CoursePackage() {
        super();
        this.totalHours = 0;
        this.usedHours = 0;
        this.unlimited = false;
        this.maxLessonsPerWeek = 0;
    }
    
    /**
     * Parameterized constructor for standard packages.
     */
    public CoursePackage(String name, BigDecimal price, String studentId,
                         int totalHours, String instrument, LocalDate startDate, LocalDate endDate) {
        super(name, ServiceType.MUSIC_PACKAGE, price, studentId);
        this.totalHours = totalHours;
        this.usedHours = 0;
        this.instrument = instrument;
        this.unlimited = false;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxLessonsPerWeek = 0;
    }
    
    /**
     * Parameterized constructor for unlimited packages.
     */
    public CoursePackage(String name, BigDecimal price, String studentId,
                         String instrument, LocalDate startDate, LocalDate endDate,
                         int maxLessonsPerWeek) {
        super(name, ServiceType.UNLIMITED_PACKAGE, price, studentId);
        this.totalHours = Integer.MAX_VALUE;
        this.usedHours = 0;
        this.instrument = instrument;
        this.unlimited = true;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxLessonsPerWeek = maxLessonsPerWeek;
    }
    
    /**
     * Copy constructor - creates a deep copy.
     * @param other the package to copy
     */
    public CoursePackage(CoursePackage other) {
        super(other);
        this.totalHours = other.totalHours;
        this.usedHours = other.usedHours;
        this.instrument = other.instrument;
        this.unlimited = other.unlimited;
        this.maxLessonsPerWeek = other.maxLessonsPerWeek;
    }
    
    @Override
    protected String getIdPrefix() {
        return "PKG";
    }
    
    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("          COURSE PACKAGE INFO          \n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("ID:          %s\n", id));
        sb.append(String.format("Name:        %s\n", name));
        sb.append(String.format("Type:        %s\n", serviceType));
        sb.append(String.format("Instrument:  %s\n", instrument));
        sb.append(String.format("Price:       €%.2f\n", price));
        sb.append(String.format("Student ID:  %s\n", studentId));
        sb.append(String.format("Period:      %s to %s\n", startDate, endDate));
        if (unlimited) {
            sb.append(String.format("Type:        Unlimited (max %d/week)\n", maxLessonsPerWeek));
        } else {
            sb.append(String.format("Hours:       %d total, %d used, %d remaining\n", 
                totalHours, usedHours, getRemainingHours()));
        }
        sb.append(String.format("Status:      %s\n", getStatusDescription()));
        sb.append(String.format("Paid:        %s\n", paid ? "Yes" : "No"));
        sb.append("═══════════════════════════════════════\n");
        return sb.toString();
    }
    
    @Override
    public Service copy() {
        return new CoursePackage(this);
    }
    
    @Override
    public boolean isValid() {
        return active && !isExpired() && (unlimited || getRemainingHours() > 0);
    }
    
    /**
     * Get remaining hours in the package.
     */
    public int getRemainingHours() {
        if (unlimited) return Integer.MAX_VALUE;
        return Math.max(0, totalHours - usedHours);
    }
    
    /**
     * Use hours from the package.
     * @param hours the hours to consume
     * @return true if hours were consumed successfully
     */
    public boolean useHours(int hours) {
        if (!isValid()) {
            return false;
        }
        if (!unlimited && getRemainingHours() < hours) {
            return false;
        }
        usedHours += hours;
        return true;
    }
    
    /**
     * Refund hours back to the package.
     * @param hours the hours to refund
     */
    public void refundHours(int hours) {
        usedHours = Math.max(0, usedHours - hours);
    }
    
    /**
     * Get the usage percentage.
     */
    public double getUsagePercentage() {
        if (unlimited || totalHours == 0) return 0;
        return (usedHours * 100.0) / totalHours;
    }
    
    /**
     * Get a status description.
     */
    public String getStatusDescription() {
        if (!active) return "Inactive";
        if (isExpired()) return "Expired";
        if (!unlimited && getRemainingHours() == 0) return "Exhausted";
        return "Active";
    }
    
    // Getters and Setters
    
    public int getTotalHours() {
        return totalHours;
    }
    
    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }
    
    public int getUsedHours() {
        return usedHours;
    }
    
    public void setUsedHours(int usedHours) {
        this.usedHours = usedHours;
    }
    
    public String getInstrument() {
        return instrument;
    }
    
    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }
    
    public boolean isUnlimited() {
        return unlimited;
    }
    
    public void setUnlimited(boolean unlimited) {
        this.unlimited = unlimited;
    }
    
    public int getMaxLessonsPerWeek() {
        return maxLessonsPerWeek;
    }
    
    public void setMaxLessonsPerWeek(int maxLessonsPerWeek) {
        this.maxLessonsPerWeek = maxLessonsPerWeek;
    }
}

