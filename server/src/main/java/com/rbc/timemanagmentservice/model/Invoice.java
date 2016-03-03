package com.rbc.timemanagmentservice.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@Entity
@Table(name = "INVOICE")
public class Invoice {
    @Transient
    private DateTimeFormatter FMT = DateTimeFormat.forPattern("EEE MMM d, yyyy");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Job> jobs = new ArrayList<>();

    @ManyToOne
    private Customer customer;
    private DateTime invoiceDate;
    private DateTime dueDate;
    private boolean paid;
    private Double amount;
    private Integer hours;
    private DateTime deleteDate;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(DateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public DateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(DateTime dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public DateTime getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(DateTime deleteDate) {
        this.deleteDate = deleteDate;
    }

    public String getInvoiceDateString() {
        return FMT.print(invoiceDate);
    }

    public String getInvoiceDueDateString(){
        return FMT.print(dueDate);
    }
}
