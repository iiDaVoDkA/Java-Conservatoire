package com.music.school.interfaces;

import com.music.school.enums.ServiceType;
import java.math.BigDecimal;

/**
 * Interface for entities that can be billed.
 * Implemented by services, lessons, rentals, and other chargeable items.
 * 
 * This interface demonstrates the use of interfaces in OOP to define
 * billing capabilities across different service types.
 */
public interface Billable {
    
    /**
     * Get the unique identifier for billing purposes.
     * @return the billing ID
     */
    String getBillingId();
    
    /**
     * Get the type of service for billing categorization.
     * @return the service type
     */
    ServiceType getServiceType();
    
    /**
     * Calculate the total amount for this billable item.
     * @return the total amount
     */
    BigDecimal calculateAmount();
    
    /**
     * Get a description of the billable item for invoices.
     * @return the billing description
     */
    String getBillingDescription();
    
    /**
     * Check if this item has been paid.
     * @return true if paid in full
     */
    boolean isPaid();
    
    /**
     * Mark this item as paid.
     */
    void markAsPaid();
    
    /**
     * Get the outstanding balance for this item.
     * @return the outstanding balance
     */
    default BigDecimal getOutstandingBalance() {
        return isPaid() ? BigDecimal.ZERO : calculateAmount();
    }
    
    /**
     * Apply a discount percentage to this billable.
     * @param discountPercent the discount percentage (0-100)
     * @return the discounted amount
     */
    default BigDecimal calculateDiscountedAmount(double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        BigDecimal multiplier = BigDecimal.valueOf(1 - (discountPercent / 100));
        return calculateAmount().multiply(multiplier);
    }
    
    /**
     * Check if this billable requires immediate payment.
     * @return true if payment is required upfront
     */
    default boolean requiresImmediatePayment() {
        ServiceType type = getServiceType();
        return type == ServiceType.SINGLE_LESSON || 
               type == ServiceType.INSTRUMENT_RENTAL;
    }
}

