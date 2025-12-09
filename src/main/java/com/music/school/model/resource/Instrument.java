package com.music.school.model.resource;

import com.music.school.interfaces.Bookable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a musical instrument owned by the school.
 * Implements Bookable interface for rental management.
 * 
 * Demonstrates:
 * - Implementation of interface
 * - Copy constructor
 * - Resource management for rentals
 */
public class Instrument implements Bookable {
    
    private String id;
    private String name;
    private String type; // Piano, Violin, Guitar, etc.
    private String brand;
    private String serialNumber;
    private BigDecimal dailyRentalRate;
    private BigDecimal depositRequired;
    private boolean available;
    private boolean underMaintenance;
    private String condition; // Excellent, Good, Fair, Poor
    private String currentRenterId;
    private List<TimeSlot> bookedSlots;
    
    /**
     * Default constructor.
     */
    public Instrument() {
        this.id = "INS-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.available = true;
        this.underMaintenance = false;
        this.condition = "Good";
        this.bookedSlots = new ArrayList<>();
    }
    
    /**
     * Parameterized constructor.
     */
    public Instrument(String name, String type, String brand, 
                      BigDecimal dailyRentalRate, BigDecimal depositRequired) {
        this();
        this.name = name;
        this.type = type;
        this.brand = brand;
        this.dailyRentalRate = dailyRentalRate;
        this.depositRequired = depositRequired;
    }
    
    /**
     * Copy constructor - creates a deep copy.
     * @param other the instrument to copy
     */
    public Instrument(Instrument other) {
        this.id = other.id;
        this.name = other.name;
        this.type = other.type;
        this.brand = other.brand;
        this.serialNumber = other.serialNumber;
        this.dailyRentalRate = other.dailyRentalRate;
        this.depositRequired = other.depositRequired;
        this.available = other.available;
        this.underMaintenance = other.underMaintenance;
        this.condition = other.condition;
        this.currentRenterId = other.currentRenterId;
        this.bookedSlots = new ArrayList<>();
        for (TimeSlot slot : other.bookedSlots) {
            this.bookedSlots.add(new TimeSlot(slot.start(), slot.end(), slot.bookingReference()));
        }
    }
    
    // Implementation of Bookable interface
    
    @Override
    public String getResourceId() {
        return id;
    }
    
    @Override
    public String getResourceName() {
        return name + " (" + type + ")";
    }
    
    @Override
    public boolean isAvailableAt(LocalDateTime dateTime, int durationMinutes) {
        if (!available || underMaintenance || currentRenterId != null) {
            return false;
        }
        
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
        if (underMaintenance) {
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
    
    @Override
    public boolean isUnderMaintenance() {
        return underMaintenance;
    }
    
    /**
     * Create a copy of this instrument.
     */
    public Instrument copy() {
        return new Instrument(this);
    }
    
    /**
     * Rent this instrument to a student.
     */
    public boolean rentTo(String studentId) {
        if (!available || underMaintenance || currentRenterId != null) {
            return false;
        }
        this.currentRenterId = studentId;
        this.available = false;
        return true;
    }
    
    /**
     * Return this instrument from rental.
     */
    public void returnFromRental() {
        this.currentRenterId = null;
        this.available = true;
    }
    
    /**
     * Check if this instrument is currently rented.
     */
    public boolean isRented() {
        return currentRenterId != null;
    }
    
    /**
     * Get detailed information about the instrument.
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("        INSTRUMENT INFORMATION         \n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("ID:           %s\n", id));
        sb.append(String.format("Name:         %s\n", name));
        sb.append(String.format("Type:         %s\n", type));
        sb.append(String.format("Brand:        %s\n", brand != null ? brand : "N/A"));
        sb.append(String.format("Serial #:     %s\n", serialNumber != null ? serialNumber : "N/A"));
        sb.append(String.format("Condition:    %s\n", condition));
        sb.append(String.format("Daily Rate:   €%.2f\n", dailyRentalRate));
        sb.append(String.format("Deposit:      €%.2f\n", depositRequired));
        sb.append(String.format("Available:    %s\n", available ? "Yes" : "No"));
        sb.append(String.format("Maintenance:  %s\n", underMaintenance ? "Yes" : "No"));
        if (currentRenterId != null) {
            sb.append(String.format("Rented by:    %s\n", currentRenterId));
        }
        sb.append("═══════════════════════════════════════\n");
        return sb.toString();
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public BigDecimal getDailyRentalRate() {
        return dailyRentalRate;
    }
    
    public void setDailyRentalRate(BigDecimal dailyRentalRate) {
        this.dailyRentalRate = dailyRentalRate;
    }
    
    public BigDecimal getDepositRequired() {
        return depositRequired;
    }
    
    public void setDepositRequired(BigDecimal depositRequired) {
        this.depositRequired = depositRequired;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public void setUnderMaintenance(boolean underMaintenance) {
        this.underMaintenance = underMaintenance;
    }
    
    public String getCondition() {
        return condition;
    }
    
    public void setCondition(String condition) {
        this.condition = condition;
    }
    
    public String getCurrentRenterId() {
        return currentRenterId;
    }
    
    public void setCurrentRenterId(String currentRenterId) {
        this.currentRenterId = currentRenterId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instrument that = (Instrument) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s %s (%s) - €%.2f/day", 
            id, brand != null ? brand : "", name, type, dailyRentalRate);
    }
}

