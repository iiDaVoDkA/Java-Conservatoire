package com.music.school.model.billing;

import com.music.school.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a payment made by a student.
 * 
 * Demonstrates:
 * - Copy constructor
 * - Encapsulation of payment logic
 */
public class Payment {
    
    private String id;
    private String studentId;
    private String invoiceId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String paymentMethod; // Cash, Card, Transfer, etc.
    private PaymentStatus status;
    private String reference;
    private String notes;
    
    /**
     * Default constructor.
     */
    public Payment() {
        this.id = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.paymentDate = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }
    
    /**
     * Parameterized constructor.
     */
    public Payment(String studentId, BigDecimal amount, String paymentMethod) {
        this();
        this.studentId = studentId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }
    
    /**
     * Constructor with invoice reference.
     */
    public Payment(String studentId, String invoiceId, BigDecimal amount, String paymentMethod) {
        this(studentId, amount, paymentMethod);
        this.invoiceId = invoiceId;
    }
    
    /**
     * Copy constructor - creates a deep copy.
     * @param other the payment to copy
     */
    public Payment(Payment other) {
        this.id = other.id;
        this.studentId = other.studentId;
        this.invoiceId = other.invoiceId;
        this.amount = other.amount;
        this.paymentDate = other.paymentDate;
        this.paymentMethod = other.paymentMethod;
        this.status = other.status;
        this.reference = other.reference;
        this.notes = other.notes;
    }
    
    /**
     * Create a copy of this payment.
     */
    public Payment copy() {
        return new Payment(this);
    }
    
    /**
     * Process this payment.
     */
    public void process() {
        this.status = PaymentStatus.PAID;
        this.paymentDate = LocalDateTime.now();
    }
    
    /**
     * Refund this payment.
     */
    public void refund() {
        this.status = PaymentStatus.REFUNDED;
    }
    
    /**
     * Cancel this payment.
     */
    public void cancel() {
        this.status = PaymentStatus.CANCELLED;
    }
    
    /**
     * Get detailed payment information.
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("         PAYMENT INFORMATION           \n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("ID:           %s\n", id));
        sb.append(String.format("Student ID:   %s\n", studentId));
        if (invoiceId != null) {
            sb.append(String.format("Invoice ID:   %s\n", invoiceId));
        }
        sb.append(String.format("Amount:       €%.2f\n", amount));
        sb.append(String.format("Method:       %s\n", paymentMethod));
        sb.append(String.format("Date:         %s\n", paymentDate));
        sb.append(String.format("Status:       %s\n", status));
        if (reference != null) {
            sb.append(String.format("Reference:    %s\n", reference));
        }
        if (notes != null) {
            sb.append(String.format("Notes:        %s\n", notes));
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
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getInvoiceId() {
        return invoiceId;
    }
    
    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
    
    public String getReference() {
        return reference;
    }
    
    public void setReference(String reference) {
        this.reference = reference;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("[%s] €%.2f - %s (%s)", id, amount, paymentMethod, status);
    }
}

