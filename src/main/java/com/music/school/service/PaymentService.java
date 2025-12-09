package com.music.school.service;

import com.music.school.enums.PaymentStatus;
import com.music.school.interfaces.Billable;
import com.music.school.model.billing.Invoice;
import com.music.school.model.billing.Payment;
import com.music.school.model.person.Student;
import com.music.school.model.service.Service;
import com.music.school.repository.DataRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing payments and billing operations.
 * 
 * Demonstrates:
 * - Use of Billable interface for polymorphic billing
 * - Financial calculations and reporting
 */
public class PaymentService {
    
    private final DataRepository repository;
    
    public PaymentService() {
        this.repository = DataRepository.getInstance();
    }
    
    /**
     * Create an invoice for a student.
     */
    public Invoice createInvoice(String studentId) {
        Student student = repository.getStudent(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }
        
        Invoice invoice = new Invoice(studentId);
        repository.addInvoice(invoice);
        return invoice;
    }
    
    /**
     * Add a billable item to an invoice.
     * Demonstrates polymorphic use of Billable interface.
     */
    public void addBillableToInvoice(String invoiceId, Billable billable) {
        Invoice invoice = repository.getInvoice(invoiceId);
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice not found: " + invoiceId);
        }
        
        invoice.addBillableItem(billable);
    }
    
    /**
     * Record a payment.
     */
    public Payment recordPayment(String studentId, BigDecimal amount, String paymentMethod) {
        Student student = repository.getStudent(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }
        
        Payment payment = new Payment(studentId, amount, paymentMethod);
        payment.process();
        repository.addPayment(payment);
        
        return payment;
    }
    
    /**
     * Record a payment against a specific invoice.
     */
    public Payment recordInvoicePayment(String invoiceId, BigDecimal amount, String paymentMethod) {
        Invoice invoice = repository.getInvoice(invoiceId);
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice not found: " + invoiceId);
        }
        
        Payment payment = new Payment(invoice.getStudentId(), invoiceId, amount, paymentMethod);
        payment.process();
        repository.addPayment(payment);
        
        // Update the invoice
        invoice.recordPayment(amount);
        
        return payment;
    }
    
    /**
     * Get total amount spent by a student.
     */
    public BigDecimal getTotalSpentByStudent(String studentId) {
        return repository.getStudentPayments(studentId).stream()
            .filter(p -> p.getStatus() == PaymentStatus.PAID)
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Get outstanding balance for a student.
     */
    public BigDecimal getStudentOutstandingBalance(String studentId) {
        return repository.getStudentInvoices(studentId).stream()
            .map(Invoice::getOutstandingBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Get monthly revenue summary.
     */
    public Map<YearMonth, BigDecimal> getMonthlyRevenue() {
        Map<YearMonth, BigDecimal> revenue = new TreeMap<>();
        
        for (Payment payment : repository.getAllPayments()) {
            if (payment.getStatus() == PaymentStatus.PAID) {
                YearMonth month = YearMonth.from(payment.getPaymentDate());
                revenue.merge(month, payment.getAmount(), BigDecimal::add);
            }
        }
        
        return revenue;
    }
    
    /**
     * Get revenue for a specific month.
     */
    public BigDecimal getMonthRevenue(YearMonth month) {
        return repository.getAllPayments().stream()
            .filter(p -> p.getStatus() == PaymentStatus.PAID)
            .filter(p -> YearMonth.from(p.getPaymentDate()).equals(month))
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Get payment summary for a student.
     */
    public String getStudentPaymentSummary(String studentId) {
        Student student = repository.getStudent(studentId);
        if (student == null) {
            return "Student not found";
        }
        
        List<Invoice> invoices = repository.getStudentInvoices(studentId);
        List<Payment> payments = repository.getStudentPayments(studentId);
        
        BigDecimal totalInvoiced = invoices.stream()
            .map(Invoice::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalPaid = getTotalSpentByStudent(studentId);
        BigDecimal outstanding = getStudentOutstandingBalance(studentId);
        
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("      STUDENT PAYMENT SUMMARY          \n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("Student:        %s\n", student.getFullName()));
        sb.append(String.format("Student ID:     %s\n", studentId));
        sb.append("───────────────────────────────────────\n");
        sb.append(String.format("Total Invoiced: €%.2f\n", totalInvoiced));
        sb.append(String.format("Total Paid:     €%.2f\n", totalPaid));
        sb.append(String.format("Outstanding:    €%.2f\n", outstanding));
        sb.append("───────────────────────────────────────\n");
        sb.append(String.format("Invoices:       %d\n", invoices.size()));
        sb.append(String.format("Payments:       %d\n", payments.size()));
        sb.append("═══════════════════════════════════════\n");
        
        return sb.toString();
    }
    
    /**
     * Get all unpaid invoices.
     */
    public List<Invoice> getUnpaidInvoices() {
        return repository.getUnpaidInvoices();
    }
    
    /**
     * Get overdue invoices.
     */
    public List<Invoice> getOverdueInvoices() {
        return repository.getAllInvoices().stream()
            .filter(Invoice::isOverdue)
            .collect(Collectors.toList());
    }
    
    /**
     * Mark a service as paid.
     * Demonstrates polymorphic use of Service as Billable.
     */
    public void markServiceAsPaid(String serviceId) {
        Service service = repository.getService(serviceId);
        if (service == null) {
            throw new IllegalArgumentException("Service not found: " + serviceId);
        }
        service.markAsPaid();
    }
    
    /**
     * Generate a monthly financial report.
     */
    public String generateMonthlyReport(YearMonth month) {
        BigDecimal revenue = getMonthRevenue(month);
        
        List<Payment> monthPayments = repository.getAllPayments().stream()
            .filter(p -> p.getStatus() == PaymentStatus.PAID)
            .filter(p -> YearMonth.from(p.getPaymentDate()).equals(month))
            .collect(Collectors.toList());
        
        Map<String, Long> paymentMethods = monthPayments.stream()
            .collect(Collectors.groupingBy(Payment::getPaymentMethod, Collectors.counting()));
        
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("        MONTHLY FINANCIAL REPORT       \n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("Month:          %s\n", month));
        sb.append("───────────────────────────────────────\n");
        sb.append(String.format("Total Revenue:  €%.2f\n", revenue));
        sb.append(String.format("Transactions:   %d\n", monthPayments.size()));
        sb.append("───────────────────────────────────────\n");
        sb.append("Payment Methods:\n");
        for (Map.Entry<String, Long> entry : paymentMethods.entrySet()) {
            sb.append(String.format("  %-12s: %d\n", entry.getKey(), entry.getValue()));
        }
        sb.append("═══════════════════════════════════════\n");
        
        return sb.toString();
    }
    
    /**
     * Refund a payment.
     */
    public void refundPayment(String paymentId) {
        Payment payment = repository.getPayment(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found: " + paymentId);
        }
        
        payment.refund();
        
        // If linked to an invoice, update the invoice
        if (payment.getInvoiceId() != null) {
            Invoice invoice = repository.getInvoice(payment.getInvoiceId());
            if (invoice != null) {
                invoice.setPaidAmount(invoice.getPaidAmount().subtract(payment.getAmount()));
            }
        }
    }
    
    /**
     * Get revenue breakdown by service type.
     */
    public Map<String, BigDecimal> getRevenueByServiceType() {
        Map<String, BigDecimal> breakdown = new HashMap<>();
        
        for (Service service : repository.getAllServices()) {
            if (service.isPaid()) {
                String type = service.getServiceType().getDisplayName();
                breakdown.merge(type, service.calculateAmount(), BigDecimal::add);
            }
        }
        
        return breakdown;
    }
}

