package com.rbc.timemanagmentservice.util;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Invoice;
import com.rbc.timemanagmentservice.model.Job;
import com.rbc.timemanagmentservice.model.TimesheetEntry;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by russbaker on 3/2/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
public class VelocityEmailHelperIT {

    @Autowired
    private VelocityEmailHelper velocityEmailHelper;



    public static final String JOB_NAME = "Second Phase";
    public static final int NUM_TIME_ENTRIES = 7;

    private List<String> dateResults = new ArrayList<>();
    DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MMM d, yyyy");

    @Test
    public void whenSendingInvoice_expectInvoiceReceived() throws Exception {
        // Assemble
        List<TimesheetEntry> timeSheetEntries = new ArrayList<>();
        for(int i = 0; i<= NUM_TIME_ENTRIES; i++){
            timeSheetEntries.add(getTimesheetEntry(i));
        }

        Job job = new Job();
        job.setName(JOB_NAME);
        job.getTimeSheetEntries().addAll(timeSheetEntries);
        Invoice invoice = new Invoice();
        invoice.setId(1);
        invoice.setHours(40);
        invoice.setAmount(10000d);
        invoice.setDueDate(new DateTime().plusDays(30));
        invoice.getJobs().add(job);

        Map<String,Object> mapEntries = new HashMap<>();
        mapEntries.put("invoice",invoice);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo("russabaker@gmail.com");
        simpleMailMessage.setFrom("russabaker@gmail.com");
        simpleMailMessage.setSubject("Invoice");

        velocityEmailHelper.send(simpleMailMessage,mapEntries,"templates/invoice.vm" );

    }

    private TimesheetEntry getTimesheetEntry(Integer index) {
        TimesheetEntry timeSheetEntry = new TimesheetEntry();
        timeSheetEntry.setDate(new DateTime().plusDays(index));
        dateResults.add(fmt.print(timeSheetEntry.getDate()));
        timeSheetEntry.setHours(8);
        return timeSheetEntry;
    }
}