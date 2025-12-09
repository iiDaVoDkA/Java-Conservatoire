package com.music.school.model.person;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base class for all persons in the system.
 * Extended by Student and Teacher classes.
 * 
 * This abstract class demonstrates:
 * - Generalization of common attributes and behavior
 * - Encapsulation of personal data
 * - Copy constructor for creating copies
 * - Abstract methods for polymorphic behavior
 */
public abstract class Person {
    
    protected String id;
    protected String firstName;
    protected String lastName;
    protected String address;
    protected LocalDate dateOfBirth;
    protected String phone;
    protected String email;
    protected LocalDate registrationDate;
    protected boolean active;
    
    /**
     * Default constructor - generates a unique ID.
     */
    protected Person() {
        this.id = generateId();
        this.registrationDate = LocalDate.now();
        this.active = true;
    }
    
    /**
     * Parameterized constructor with all required fields.
     */
    protected Person(String firstName, String lastName, String address, 
                     LocalDate dateOfBirth, String phone, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
        this.email = email;
    }
    
    /**
     * Copy constructor - creates a deep copy of another Person.
     * This demonstrates the use of copy constructors for protecting internal state.
     * @param other the person to copy
     */
    protected Person(Person other) {
        this.id = other.id;
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.address = other.address;
        this.dateOfBirth = other.dateOfBirth;
        this.phone = other.phone;
        this.email = other.email;
        this.registrationDate = other.registrationDate;
        this.active = other.active;
    }
    
    /**
     * Generate a unique identifier.
     */
    private String generateId() {
        return getIdPrefix() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Abstract method to get the ID prefix for this person type.
     * @return the ID prefix (e.g., "STU" for students, "TCH" for teachers)
     */
    protected abstract String getIdPrefix();
    
    /**
     * Abstract method to get the role description.
     * @return the role description
     */
    public abstract String getRole();
    
    /**
     * Abstract method to get a detailed description.
     * @return detailed information about the person
     */
    public abstract String getDetailedInfo();
    
    /**
     * Abstract method to create a copy of this person.
     * @return a new copy of this person
     */
    public abstract Person copy();
    
    /**
     * Get the full name.
     * @return first name and last name combined
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Calculate the age based on date of birth.
     * @return the age in years
     */
    public int getAge() {
        if (dateOfBirth == null) return 0;
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
    
    /**
     * Check if this person is a minor (under 18).
     * @return true if under 18
     */
    public boolean isMinor() {
        return getAge() < 18;
    }
    
    /**
     * Get a formatted summary of the person.
     * @return summary string
     */
    public String getSummary() {
        return String.format("[%s] %s - %s", id, getFullName(), getRole());
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
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
        Person person = (Person) o;
        return Objects.equals(id, person.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return getSummary();
    }
}

