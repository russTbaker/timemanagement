package com.rbc.timemanagmentservice.util;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.model.Invoice;
import com.rbc.timemanagmentservice.model.Job;
import com.rbc.timemanagmentservice.model.TimeEntry;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by russbaker on 3/2/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
public class VelocityHelperTest {

    public static final String JOB_NAME = "Second Phase";
    public static final int NUM_TIME_ENTRIES = 7;

    @Autowired
    private VelocityHelper velocityHelper;

    private List<String> dateResults = new ArrayList<>();
    DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MMM d, yyyy");

    @Autowired
    private StartupUtility startupUtility;


    @Test
    @Ignore
    public void whenCreatingDocument_expectDocumentCreated() throws Exception {

        // Assemble
        Job job = new Job();
        job.setName(JOB_NAME);
        job.setRate(87.5);
        List<TimeEntry> timeSheetEntries = new ArrayList<>();
        for(int i = 0; i<= NUM_TIME_ENTRIES; i++){
            timeSheetEntries.add(getTimesheetEntry(i, job));
        }


        Invoice invoice = new Invoice();
        invoice.setId(1);
        invoice.setHours(40);
        invoice.setAmount(10000d);
        invoice.setDueDate(new DateTime().plusDays(30));
        invoice.getJobs().add(job);
        startupUtility.init();
        Customer customer = startupUtility.getCustomerObject();
        customer.addInvoiceToCustomer(invoice);


        Map<String,Object> mapEntries = new HashMap<>();
        mapEntries.put("invoice",invoice);
        mapEntries.put("message","Thank you for your business, your prompt payment is greatly appreciated!");
        mapEntries.put("contract",customer.getContracts().get(0));


        // Act
        String result = velocityHelper.convertTemplateToString("/templates/invoice.vm",mapEntries);

        // Assert
        assertTrue("No body returned", StringUtils.isNotBlank(result));
        assertThat(result,containsString(JOB_NAME));
        for(int i=0;i<NUM_TIME_ENTRIES;i++){
            assertThat(result,containsString(dateResults.get(i)));
        }
        assertThat(result,containsString("$"+invoice.getAmount()));
        assertThat(result,containsString(String.valueOf(invoice.getHours())));


    }

    private TimeEntry getTimesheetEntry(Integer index, Job job) {
        TimeEntry timeSheetEntry = new TimeEntry();
        timeSheetEntry.setDate(new DateTime().plusDays(index));
        dateResults.add(fmt.print(timeSheetEntry.getDate()));
        timeSheetEntry.setHours(8);
        job.addTimeEntry(timeSheetEntry);
        return timeSheetEntry;
    }
}