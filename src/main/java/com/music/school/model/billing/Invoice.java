package com.music.school.model.billing;

import com.music.school.enums.PaymentStatus;
import com.music.school.interfaces.Billable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents an invoice for services rendered.
 * 
 * Demonstrates:
 * - Copy constructor
 * - Polymorphic use of Billable interface
 * - Invoice management logic
 */
public class Invoice {
    
    private String id;
    private String studentId;
    private LocalDateTime issueDate;
    private LocalDate dueDate;
    private PaymentStatus status;
    private List<InvoiceItem> items;
    private BigDecimal subtotal;
    private BigDecimal taxRate; // Percentage
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private String notes;
    
    /**
     * Default constructor.
     */
    public Invoice() {
        this.id = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.issueDate = LocalDateTime.now();
        this.dueDate = LocalDate.now().plusDays(30);
        this.status = PaymentStatus.PENDING;
        this.items = new ArrayList<>();
        this.subtotal = BigDecimal.ZERO;
        this.taxRate = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.paidAmount = BigDecimal.ZERO;
    }
    
    /**
     * Parameterized constructor.
     */
    public Invoice(String studentId) {
        this();
        this.studentId = studentId;
    }
    
    /**
     * Copy constructor - creates a deep copy.
     * @param other the invoice to copy
     */
    public Invoice(Invoice other) {
        this.id = other.id;
        this.studentId = other.studentId;
        this.issueDate = other.issueDate;
        this.dueDate = other.dueDate;
        this.status = other.status;
        this.items = new ArrayList<>();
        for (InvoiceItem item : other.items) {
            this.items.add(new InvoiceItem(item));
        }
        this.subtotal = other.subtotal;
        this.taxRate = other.taxRate;
        this.taxAmount = other.taxAmount;
        this.totalAmount = other.totalAmount;
        this.paidAmount = other.paidAmount;
        this.notes = other.notes;
    }
    
    /**
     * Create a copy of this invoice.
     */
    public Invoice copy() {
        return new Invoice(this);
    }
    
    /**
     * Add a billable item to the invoice.
     * Demonstrates polymorphic use of Billable interface.
     */
    public void addBillableItem(Billable billable) {
        InvoiceItem item = new InvoiceItem(
            billable.getBillingId(),
            billable.getBillingDescription(),
            1,
            billable.calculateAmount()
        );
        addItem(item);
    }
    
    /**
     * Add an item to the invoice.
     */
    public void addItem(InvoiceItem item) {
        items.add(item);
        recalculateTotals();
    }
    
    /**
     * Remove an item from the invoice.
     */
    public boolean removeItem(String itemId) {
        boolean removed = items.removeIf(item -> item.getId().equals(itemId));
        if (removed) {
            recalculateTotals();
        }
        return removed;
    }
    
    /**
     * Recalculate all totals.
     */
    private void recalculateTotals() {
        this.subtotal = items.stream()
            .map(InvoiceItem::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.taxAmount = subtotal.multiply(taxRate).divide(BigDecimal.valueOf(100));
        this.totalAmount = subtotal.add(taxAmount);
        
        updateStatus();
    }
    
    /**
     * Update the payment status.
     */
    private void updateStatus() {
        if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            if (LocalDate.now().isAfter(dueDate)) {
                status = PaymentStatus.OVERDUE;
            } else {
                status = PaymentStatus.PENDING;
            }
        } else if (paidAmount.compareTo(totalAmount) >= 0) {
            status = PaymentStatus.PAID;
        } else {
            status = PaymentStatus.PARTIAL;
        }
    }
    
    /**
     * Record a payment against this invoice.
     */
    public void recordPayment(BigDecimal amount) {
        this.paidAmount = this.paidAmount.add(amount);
        updateStatus();
    }
    
    /**
     * Get the outstanding balance.
     */
    public BigDecimal getOutstandingBalance() {
        return totalAmount.subtract(paidAmount).max(BigDecimal.ZERO);
    }
    
    /**
     * Check if this invoice is overdue.
     */
    public boolean isOverdue() {
        return status == PaymentStatus.OVERDUE || 
               (status != PaymentStatus.PAID && LocalDate.now().isAfter(dueDate));
    }
    
    /**
     * Get detailed invoice information.
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("                    INVOICE                        \n");
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append(String.format("Invoice #:    %s\n", id));
        sb.append(String.format("Student ID:   %s\n", studentId));
        sb.append(String.format("Issue Date:   %s\n", issueDate.toLocalDate()));
        sb.append(String.format("Due Date:     %s\n", dueDate));
        sb.append(String.format("Status:       %s\n", status));
        sb.append("───────────────────────────────────────────────────\n");
        sb.append("Items:\n");
        for (InvoiceItem item : items) {
            sb.append(String.format("  - %s\n", item));
        }
        sb.append("───────────────────────────────────────────────────\n");
        sb.append(String.format("Subtotal:     €%.2f\n", subtotal));
        if (taxRate.compareTo(BigDecimal.ZERO) > 0) {
            sb.append(String.format("Tax (%.1f%%):  €%.2f\n", taxRate, taxAmount));
        }
        sb.append(String.format("Total:        €%.2f\n", totalAmount));
        sb.append(String.format("Paid:         €%.2f\n", paidAmount));
        sb.append(String.format("Balance:      €%.2f\n", getOutstandingBalance()));
        sb.append("═══════════════════════════════════════════════════\n");
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
    
    public LocalDateTime getIssueDate() {
        return issueDate;
    }
    
    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
    
    public List<InvoiceItem> getItems() {
        return new ArrayList<>(items);
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public BigDecimal getTaxRate() {
        return taxRate;
    }
    
    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
        recalculateTotals();
    }
    
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public BigDecimal getPaidAmount() {
        return paidAmount;
    }
    
    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
        updateStatus();
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
        Invoice invoice = (Invoice) o;
        return Objects.equals(id, invoice.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("[%s] €%.2f - %s (Balance: €%.2f)", 
            id, totalAmount, status, getOutstandingBalance());
    }
    
    /**
     * Inner class for invoice line items.
     */
    public static class InvoiceItem {
        private String id;
        private String referenceId;
        private String description;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal total;
        
        public InvoiceItem() {
            this.id = "ITM-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            this.quantity = 1;
        }
        
        public InvoiceItem(String referenceId, String description, int quantity, BigDecimal unitPrice) {
            this();
            this.referenceId = referenceId;
            this.description = description;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.total = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        
        /**
         * Copy constructor.
         */
        public InvoiceItem(InvoiceItem other) {
            this.id = other.id;
            this.referenceId = other.referenceId;
            this.description = other.description;
            this.quantity = other.quantity;
            this.unitPrice = other.unitPrice;
            this.total = other.total;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getReferenceId() { return referenceId; }
        public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { 
            this.quantity = quantity;
            if (unitPrice != null) {
                this.total = unitPrice.multiply(BigDecimal.valueOf(quantity));
            }
        }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { 
            this.unitPrice = unitPrice;
            this.total = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        public BigDecimal getTotal() { return total; }
        
        @Override
        public String toString() {
            return String.format("%s (x%d) - €%.2f", description, quantity, total);
        }
    }
}

