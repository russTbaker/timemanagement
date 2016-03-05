package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Invoice;
import com.rbc.timemanagmentservice.model.Job;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import com.rbc.timemanagmentservice.persistence.InvoiceRepository;
import com.rbc.timemanagmentservice.persistence.JobRepository;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.NotFoundException;
import java.util.List;

/**
 * Created by russbaker on 3/2/16.
 */

@Repository
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;
    private final JobRepository jobRepository;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, ContractRepository timeSheetEntryRepository, JobRepository jobRepository) {
        this.invoiceRepository = invoiceRepository;
        this.contractRepository = timeSheetEntryRepository;
        this.jobRepository = jobRepository;
    }

    @RestResource(path = "/test/invoices")
    public List<Invoice> getInvoices(){
        return (List<Invoice>)invoiceRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Invoice createInvoice(final Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Invoice addJobToInvoice(Integer invoiceId, final Job job) {
        final Invoice invoice = invoiceRepository.findOne(invoiceId);
        invoice.getJobs().add(job);
        return invoiceRepository.save(invoice);
    }

    @Transactional(readOnly = true)
    public Invoice getInvoice(Integer invoiceId) {
        final Invoice invoice = invoiceRepository.findOne(invoiceId);
        if(invoice == null){
            throw new NotFoundException("Cannot find invoice with ID: " + invoiceId);
        }
        return invoice;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteInvoice(Integer invoiceId) {
        final Invoice invoice = invoiceRepository.findOne(invoiceId);
        invoice.setDeleteDate(new DateTime());
        invoiceRepository.save(invoice);
    }

    @Transactional(readOnly = true)
    public Double sumJobForTimePeriod(Integer jobId, DateTime startTime, DateTime endTime) {
        final Interval interval = new Interval(startTime,endTime);
        final Job job = jobRepository.findOne(jobId);
        return job.getTimeEntries()
                .stream()
                .filter(timeSheetEntry -> (interval.contains(timeSheetEntry.getDate())))
                .mapToDouble(value -> value.getHours()*job.getRate())
                .sum();
    }
}
