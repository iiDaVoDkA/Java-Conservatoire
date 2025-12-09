package com.music.school.enums;

/**
 * Enumeration representing the status of payments and invoices.
 */
public enum PaymentStatus {
    PENDING("Pending", "Payment is awaiting processing"),
    PARTIAL("Partial", "Payment has been partially received"),
    PAID("Paid", "Payment has been received in full"),
    OVERDUE("Overdue", "Payment is past due date"),
    REFUNDED("Refunded", "Payment has been refunded"),
    CANCELLED("Cancelled", "Payment has been cancelled");

    private final String displayName;
    private final String description;

    PaymentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if this status indicates an outstanding balance.
     */
    public boolean hasOutstandingBalance() {
        return this == PENDING || this == PARTIAL || this == OVERDUE;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

