package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.testutils.ContractTestUtil;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;


/**
 * Created by russbaker on 3/2/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)

public class InvoiceServiceTest {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private StartupUtility startupUtility;

    @Autowired
    private ContractTestUtil contractTestUtil;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JobService jobService;

    @Test
    @Rollback
    public void whenCallingCreateInvoice_expectInvoiceCreated() throws Exception {
        // Assemble/Act
        Invoice result = createInvoice();


        // Asert
        assertNotNull("Invoice not created", result.getId());
    }


    @Test
    public void whenCallingGetInvoices_expectAllInvoicesReturned() throws Exception {
        // Assemble
        createInvoice();
        createInvoice();

        // Assert
        List<Invoice> invoices = invoiceService.getInvoices();
        assertFalse("No invoices returned", CollectionUtils.isEmpty(invoices));
        assertTrue("Wrong number of invoices returned",  invoices.size() > 2);
    }

    @Test
    public void whenUpdatingInvoice_expectInvoiceUpdated() throws Exception {
        // Assemble
        Invoice invoice = createInvoice();
        ContractTestUtil.JobCreator jobCreator = contractTestUtil.getJobCreator().invoke();

        // Act
        Invoice result = invoiceService.addJobToInvoice(invoice.getId(), jobCreator.getJobCreated());
        assertEquals("No job added to invoice", 2, result.getJobs().size());
    }

    @Test(expected = NotFoundException.class)
    public void whenCallingGetInvoice_expectNotFoundException() throws Exception {
        // Act
        invoiceService.getInvoice(0);

    }

    @Test
    public void whenCallingGetInvoice_expectInvoiceReturned() throws Exception {
        // Assemble
        Invoice invoice = createInvoice();

        // Act
        assertNotNull("No Invoice found", invoiceService.getInvoice(invoice.getId()));

    }

    @Test(expected = NotFoundException.class)
    public void whenDeletingInvoice_expectSoftDelete() throws Exception {
        // Assemble
        Invoice invoice = createInvoice();

        // Act
        invoiceService.deleteInvoice(invoice.getId());


        // Assert
        invoiceService.getInvoice(invoice.getId());

    }

    @Test
    public void whenAggregatingJobTotals_expectJobTotalsAggregated() throws Exception {
        // Assemble
//        Employee employee = startupUtility.init();
//        Timesheet timesheet = employee.getTimesheets().get(0);
//        final int hours = 20;
//        final List<TimeEntry> timeSheetEntries = timesheet.getTimeEntries();
//        timeSheetEntries
//                .stream()
//                .forEach(timeSheetEntry1 -> {
//                    timeSheetEntry1.setHours(hours);
//                });
//        employeeService.addTimeSheetEntries(new ArrayList<>(timeSheetEntries)  , employee.getId(), timesheet.getId());
//        Job job = jobService.findJob(timeSheetEntries.get(0).getJob().getId());
//
//        // Act
//        final DateTime weekStart = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();
//        Double sum = invoiceService.sumJobForTimePeriod(job.getId(),weekStart,weekStart.plusDays(9));
//
//        // Assert
//        assertTrue("Nothing returned", sum > 0);
//        assertEquals("Wrong sum",job.getRate()*hours*timeSheetEntries.size(),sum,0.0);
    }

    //-------------------- Private Methods
    private Invoice createInvoice() {
        // Assemble
        ContractTestUtil.JobCreator jobCreator = contractTestUtil.getJobCreator().invoke();
        Invoice invoice = new Invoice();
        invoice.getJobs().add((jobCreator.getJobCreated()));

        // Act
        return invoiceService.createInvoice(invoice);
    }
}