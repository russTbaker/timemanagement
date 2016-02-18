package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.TimeSheet;
import com.rbc.timemanagmentservice.model.TimeSheetEntry;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by rbaker on 2/6/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
@Transactional
public class TimeSheetRepositoryTest {
    public static final String CONTACT_NAME = "Jonathan Bein";

    @Autowired
    private TimeSheetRepository timeSheetRepository;

    @Autowired
    private TimeSheetEntryRepository timeSheetEntryRepository;


    @Test
    public void whenInsertingTimeSheet_expectTimesheetCreated() throws Exception {
        // Assemble
        Employee employee = new Employee();
        TimeSheet timeSheet = new TimeSheet(employee);
        timeSheet.setBilled(false);

        TimeSheetEntry timeSheetEntry = new TimeSheetEntry();
        timeSheetEntry.setHours(8);
        timeSheetEntry.setDate(new DateTime());

        final TimeSheetEntry savedEntry = timeSheetEntryRepository.save(timeSheetEntry);
        timeSheet.getTimeSheetEntries().add(timeSheetEntry);

        // Act
        TimeSheet savedTimesheet = timeSheetRepository.save(timeSheet);
        assertNotNull("No timesheet returned.", savedTimesheet);

        // Assert
        assertEquals("Wrong timesheet entry", timeSheetEntry, timeSheetRepository.findOne(savedTimesheet.getId())
                .getTimeSheetEntries().get(0));
    }

    @Test
    public void whenCreatingTimeSheetWithEntries_expectEntriesSaved() throws Exception {


    }

}