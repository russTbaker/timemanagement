package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.model.Email;
import com.rbc.timemanagmentservice.model.Invoice;
import com.rbc.timemanagmentservice.model.Job;
import com.rbc.timemanagmentservice.persistence.InvoiceRepository;
import com.rbc.timemanagmentservice.persistence.JobRepository;
import com.rbc.timemanagmentservice.util.VelocityEmailHelper;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.NotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by russbaker on 3/2/16.
 */

@Repository
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final JobRepository jobRepository;
    private final VelocityEmailHelper emailHelper;

    @Value("${invoice.from.address}")
    private String fromAddress;

    @Value("${invoice.template.path}")
    private String invoiceTemplatePath;

    @Value("${invoice.accountant.send}")
    private Boolean sendToAccountant;

    @Value("${invoice.accountant.email}")
    private String accountantEmail;

    @Value("${invoice.messsage}")
    private String invoiceMessage;

    @Value("${invoice.subject}")
    private String invoiceSubject;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, JobRepository jobRepository, VelocityEmailHelper emailHelper) {
        this.invoiceRepository = invoiceRepository;
        this.jobRepository = jobRepository;
        this.emailHelper = emailHelper;
    }

    @RestResource(path = "/test/invoices")
    public List<Invoice> getInvoices() {
        return (List<Invoice>) invoiceRepository.findAll();
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
        if (invoice == null) {
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
        final Interval interval = new Interval(startTime, endTime);
        final Job job = jobRepository.findOne(jobId);
        return job.getTimeEntries()
                .stream()
                .filter(timeSheetEntry -> (interval.contains(timeSheetEntry.getDate())))
                .mapToDouble(value -> value.getHours() * job.getRate())
                .sum();
    }

    @Transactional(readOnly = true)
    public Invoice createInvoiceForJobAndTimePeriod(Integer jobId, Interval interval) {
        final Invoice invoice = new Invoice();
        final Job job = jobRepository.findOne(jobId);
        invoice.setInvoiceDate(new DateTime());
        invoice.setDueDate(new DateTime().withTimeAtStartOfDay().plusDays(job.getContract().getTerms().getTerm()));
        invoice.getJobs().add(job);
        invoice.setCustomer((Customer) job.getContract().getUsers()
                .stream()
                .filter(user -> user instanceof Customer)
                .findFirst().get());
        invoice.setAmount(sumJobForTimePeriod(jobId,interval.getStart(),interval.getEnd()));
        invoice.setHours((int) (invoice.getAmount()/job.getRate()));
        return invoice;
    }

    public void sendInvoiceAndBillEntries(Invoice invoice) {

        invoice.getJobs()
                .stream()
                .forEach(job -> job.getTimeEntries()
                .stream()
                .forEach(timeEntry -> timeEntry.setBilled(true)));
        final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(invoice.getCustomer().getEmails()
        .stream()
        .filter(email -> email.getEmailType().equals(Email.EmailTypes.billing))
        .findFirst()
        .get()
        .getEmail());
        simpleMailMessage.setFrom(fromAddress);
        // TODO: add property
        simpleMailMessage.setSubject( invoiceSubject);

        if(sendToAccountant){
            simpleMailMessage.setCc(accountantEmail);
        }
        Map<String,Object> mapEntries = new HashMap<>();
        mapEntries.put("invoice",invoice);
        mapEntries.put("message",invoiceMessage);
        mapEntries.put("contract",invoice.getJobs().get(0).getContract());

        emailHelper.send(simpleMailMessage,mapEntries,invoiceTemplatePath);
    }
}
