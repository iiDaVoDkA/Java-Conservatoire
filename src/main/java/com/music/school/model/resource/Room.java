package com.music.school.model.resource;

import com.music.school.interfaces.Bookable;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a practice/lesson room in the school.
 * Implements Bookable interface for scheduling.
 * 
 * Demonstrates:
 * - Implementation of interface
 * - Copy constructor
 * - Resource management
 */
public class Room implements Bookable {
    
    private String id;
    private String name;
    private int capacity;
    private List<String> equipment;
    private List<String> suitableInstruments;
    private boolean available;
    private boolean underMaintenance;
    private List<TimeSlot> bookedSlots;
    
    /**
     * Default constructor.
     */
    public Room() {
        this.id = "ROOM-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.equipment = new ArrayList<>();
        this.suitableInstruments = new ArrayList<>();
        this.available = true;
        this.underMaintenance = false;
        this.bookedSlots = new ArrayList<>();
    }
    
    /**
     * Parameterized constructor.
     */
    public Room(String name, int capacity, List<String> suitableInstruments) {
        this();
        this.name = name;
        this.capacity = capacity;
        this.suitableInstruments = new ArrayList<>(suitableInstruments);
    }
    
    /**
     * Copy constructor - creates a deep copy.
     * @param other the room to copy
     */
    public Room(Room other) {
        this.id = other.id;
        this.name = other.name;
        this.capacity = other.capacity;
        this.equipment = new ArrayList<>(other.equipment);
        this.suitableInstruments = new ArrayList<>(other.suitableInstruments);
        this.available = other.available;
        this.underMaintenance = other.underMaintenance;
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
        return name;
    }
    
    @Override
    public boolean isAvailableAt(LocalDateTime dateTime, int durationMinutes) {
        if (!available || underMaintenance) {
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
    
    @Override
    public boolean isUnderMaintenance() {
        return underMaintenance;
    }
    
    /**
     * Create a copy of this room.
     */
    public Room copy() {
        return new Room(this);
    }
    
    /**
     * Check if this room is suitable for a specific instrument.
     */
    public boolean isSuitableFor(String instrument) {
        return suitableInstruments.stream()
            .anyMatch(i -> i.equalsIgnoreCase(instrument));
    }
    
    /**
     * Add equipment to the room.
     */
    public void addEquipment(String item) {
        if (!equipment.contains(item)) {
            equipment.add(item);
        }
    }
    
    /**
     * Get detailed information about the room.
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("            ROOM INFORMATION            \n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("ID:          %s\n", id));
        sb.append(String.format("Name:        %s\n", name));
        sb.append(String.format("Capacity:    %d persons\n", capacity));
        sb.append(String.format("Equipment:   %s\n", 
            equipment.isEmpty() ? "None" : String.join(", ", equipment)));
        sb.append(String.format("Instruments: %s\n", 
            suitableInstruments.isEmpty() ? "All" : String.join(", ", suitableInstruments)));
        sb.append(String.format("Available:   %s\n", available ? "Yes" : "No"));
        sb.append(String.format("Maintenance: %s\n", underMaintenance ? "Yes" : "No"));
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
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public List<String> getEquipment() {
        return new ArrayList<>(equipment);
    }
    
    public void setEquipment(List<String> equipment) {
        this.equipment = new ArrayList<>(equipment);
    }
    
    public List<String> getSuitableInstruments() {
        return new ArrayList<>(suitableInstruments);
    }
    
    public void setSuitableInstruments(List<String> suitableInstruments) {
        this.suitableInstruments = new ArrayList<>(suitableInstruments);
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s (Capacity: %d)", id, name, capacity);
    }
}

