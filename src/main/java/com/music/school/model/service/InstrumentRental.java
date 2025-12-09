package com.music.school.model.service;

import com.music.school.enums.ServiceType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents an instrument rental service.
 * Extends Service abstract class.
 * 
 * Demonstrates:
 * - Inheritance from abstract class
 * - Copy constructor
 * - Rental-specific logic
 */
public class InstrumentRental extends Service {
    
    private String instrumentId;
    private String instrumentName;
    private BigDecimal dailyRate;
    private BigDecimal depositAmount;
    private boolean depositPaid;
    private boolean instrumentReturned;
    private LocalDate returnDate;
    
    /**
     * Default constructor.
     */
    public InstrumentRental() {
        super();
        this.depositPaid = false;
        this.instrumentReturned = false;
    }
    
    /**
     * Parameterized constructor.
     */
    public InstrumentRental(String studentId, String instrumentId, String instrumentName,
                            BigDecimal dailyRate, BigDecimal depositAmount,
                            LocalDate startDate, LocalDate endDate) {
        super("Rental: " + instrumentName, ServiceType.INSTRUMENT_RENTAL, 
              calculateTotalRent(dailyRate, startDate, endDate), studentId);
        this.instrumentId = instrumentId;
        this.instrumentName = instrumentName;
        this.dailyRate = dailyRate;
        this.depositAmount = depositAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.depositPaid = false;
        this.instrumentReturned = false;
    }
    
    /**
     * Copy constructor - creates a deep copy.
     * @param other the rental to copy
     */
    public InstrumentRental(InstrumentRental other) {
        super(other);
        this.instrumentId = other.instrumentId;
        this.instrumentName = other.instrumentName;
        this.dailyRate = other.dailyRate;
        this.depositAmount = other.depositAmount;
        this.depositPaid = other.depositPaid;
        this.instrumentReturned = other.instrumentReturned;
        this.returnDate = other.returnDate;
    }
    
    private static BigDecimal calculateTotalRent(BigDecimal dailyRate, 
                                                  LocalDate startDate, LocalDate endDate) {
        if (dailyRate == null || startDate == null || endDate == null) {
            return BigDecimal.ZERO;
        }
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        return dailyRate.multiply(BigDecimal.valueOf(Math.max(1, days)));
    }
    
    @Override
    protected String getIdPrefix() {
        return "RNT";
    }
    
    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("       INSTRUMENT RENTAL INFO          \n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("ID:            %s\n", id));
        sb.append(String.format("Instrument:    %s (%s)\n", instrumentName, instrumentId));
        sb.append(String.format("Student ID:    %s\n", studentId));
        sb.append(String.format("Period:        %s to %s\n", startDate, endDate));
        sb.append(String.format("Daily Rate:    €%.2f\n", dailyRate));
        sb.append(String.format("Total Rent:    €%.2f\n", price));
        sb.append(String.format("Deposit:       €%.2f (%s)\n", depositAmount, 
            depositPaid ? "Paid" : "Not paid"));
        sb.append(String.format("Rental Paid:   %s\n", paid ? "Yes" : "No"));
        sb.append(String.format("Returned:      %s\n", 
            instrumentReturned ? "Yes (" + returnDate + ")" : "No"));
        sb.append("═══════════════════════════════════════\n");
        return sb.toString();
    }
    
    @Override
    public Service copy() {
        return new InstrumentRental(this);
    }
    
    @Override
    public boolean isValid() {
        return active && !instrumentReturned && !isExpired();
    }
    
    @Override
    public BigDecimal calculateAmount() {
        // Include deposit if not paid
        BigDecimal total = price != null ? price : BigDecimal.ZERO;
        if (!depositPaid && depositAmount != null) {
            total = total.add(depositAmount);
        }
        return total;
    }
    
    /**
     * Mark the instrument as returned.
     */
    public void markAsReturned() {
        this.instrumentReturned = true;
        this.returnDate = LocalDate.now();
    }
    
    /**
     * Mark the deposit as paid.
     */
    public void markDepositPaid() {
        this.depositPaid = true;
    }
    
    /**
     * Calculate late fees if returned after end date.
     */
    public BigDecimal calculateLateFees() {
        if (returnDate == null || endDate == null) {
            if (!instrumentReturned && LocalDate.now().isAfter(endDate)) {
                long lateDays = java.time.temporal.ChronoUnit.DAYS.between(endDate, LocalDate.now());
                return dailyRate.multiply(BigDecimal.valueOf(lateDays * 1.5)); // 150% daily rate
            }
            return BigDecimal.ZERO;
        }
        
        if (returnDate.isAfter(endDate)) {
            long lateDays = java.time.temporal.ChronoUnit.DAYS.between(endDate, returnDate);
            return dailyRate.multiply(BigDecimal.valueOf(lateDays * 1.5));
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Check if deposit should be refunded (instrument returned without damage).
     */
    public boolean isDepositRefundable() {
        return instrumentReturned && depositPaid;
    }
    
    // Getters and Setters
    
    public String getInstrumentId() {
        return instrumentId;
    }
    
    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }
    
    public String getInstrumentName() {
        return instrumentName;
    }
    
    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }
    
    public BigDecimal getDailyRate() {
        return dailyRate;
    }
    
    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }
    
    public BigDecimal getDepositAmount() {
        return depositAmount;
    }
    
    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }
    
    public boolean isDepositPaid() {
        return depositPaid;
    }
    
    public void setDepositPaid(boolean depositPaid) {
        this.depositPaid = depositPaid;
    }
    
    public boolean isInstrumentReturned() {
        return instrumentReturned;
    }
    
    public void setInstrumentReturned(boolean instrumentReturned) {
        this.instrumentReturned = instrumentReturned;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}

