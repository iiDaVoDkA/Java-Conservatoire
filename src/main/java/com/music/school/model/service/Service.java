package com.music.school.model.service;

import com.music.school.enums.ServiceType;
import com.music.school.interfaces.Billable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base class for all services offered by the school.
 * Extended by CoursePackage, IndividualLesson, InstrumentRental, etc.
 * 
 * This abstract class demonstrates:
 * - Generalization of service attributes
 * - Implementation of Billable interface
 * - Copy constructor for creating copies
 * - Abstract methods for polymorphic behavior
 */
public abstract class Service implements Billable {
    
    protected String id;
    protected String name;
    protected String description;
    protected ServiceType serviceType;
    protected BigDecimal price;
    protected LocalDate createdDate;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected String studentId;
    protected boolean paid;
    protected boolean active;
    
    /**
     * Default constructor - generates a unique ID.
     */
    protected Service() {
        this.id = generateId();
        this.createdDate = LocalDate.now();
        this.active = true;
        this.paid = false;
    }
    
    /**
     * Parameterized constructor with essential fields.
     */
    protected Service(String name, ServiceType serviceType, BigDecimal price, String studentId) {
        this();
        this.name = name;
        this.serviceType = serviceType;
        this.price = price;
        this.studentId = studentId;
    }
    
    /**
     * Copy constructor - creates a deep copy of another Service.
     * This demonstrates the use of copy constructors for protecting internal state.
     * @param other the service to copy
     */
    protected Service(Service other) {
        this.id = other.id;
        this.name = other.name;
        this.description = other.description;
        this.serviceType = other.serviceType;
        this.price = other.price;
        this.createdDate = other.createdDate;
        this.startDate = other.startDate;
        this.endDate = other.endDate;
        this.studentId = other.studentId;
        this.paid = other.paid;
        this.active = other.active;
    }
    
    /**
     * Generate a unique identifier.
     */
    private String generateId() {
        return getIdPrefix() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Abstract method to get the ID prefix for this service type.
     * @return the ID prefix
     */
    protected abstract String getIdPrefix();
    
    /**
     * Abstract method to get detailed service information.
     * @return detailed information about the service
     */
    public abstract String getDetailedInfo();
    
    /**
     * Abstract method to create a copy of this service.
     * @return a new copy of this service
     */
    public abstract Service copy();
    
    /**
     * Abstract method to check if the service is still valid/usable.
     * @return true if the service can still be used
     */
    public abstract boolean isValid();
    
    /**
     * Check if the service has expired based on end date.
     * @return true if expired
     */
    public boolean isExpired() {
        return endDate != null && LocalDate.now().isAfter(endDate);
    }
    
    /**
     * Get the remaining days until expiration.
     * @return days remaining, or -1 if no end date
     */
    public long getRemainingDays() {
        if (endDate == null) return -1;
        long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), endDate);
        return Math.max(0, days);
    }
    
    // Implementation of Billable interface
    
    @Override
    public String getBillingId() {
        return id;
    }
    
    @Override
    public ServiceType getServiceType() {
        return serviceType;
    }
    
    @Override
    public BigDecimal calculateAmount() {
        return price != null ? price : BigDecimal.ZERO;
    }
    
    @Override
    public String getBillingDescription() {
        return String.format("%s - %s", name, serviceType.getDisplayName());
    }
    
    @Override
    public boolean isPaid() {
        return paid;
    }
    
    @Override
    public void markAsPaid() {
        this.paid = true;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public void setPaid(boolean paid) {
        this.paid = paid;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equals(id, service.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %s (€%.2f)", id, name, serviceType, price);
    }
}

