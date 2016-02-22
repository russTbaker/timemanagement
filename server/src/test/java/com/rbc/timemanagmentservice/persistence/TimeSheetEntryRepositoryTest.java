package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Contract;
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
import org.springframework.util.CollectionUtils;

import static junit.framework.TestCase.*;


/**
 * Created by russbaker on 2/13/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
@Transactional
public class TimeSheetEntryRepositoryTest {

    @Autowired
    private TimeSheetRepository timeSheetRepository;

    @Test
    public void whenAddingTimeSheetEntry_expectEntryAdded() throws Exception {
        TimeSheet timeSheet = getTimeSheet();


        // Act
        timeSheetRepository.save(timeSheet);

        // Assert
        TimeSheet result = timeSheetRepository.findOne(timeSheet.getId());
        assertNotNull("Timesheet is null",result);
        assertFalse("No entries found", CollectionUtils.isEmpty(result.getTimeSheetEntries()));
        assertEquals("Timesheets not equal",timeSheet,result);
    }

    @Test
    public void whenAddingTimeSheetEntryToExistingList_expectEntryAdded() throws Exception {
        TimeSheet timeSheet = getTimeSheet();
        timeSheet.getTimeSheetEntries().add(getTimeSheetEntry(timeSheet));

        // Act
        timeSheetRepository.save(timeSheet);

        // Assert
        TimeSheet result = timeSheetRepository.findOne(timeSheet.getId());
        assertEquals("Wrong number of entries",2,result.getTimeSheetEntries().size());
    }

    private TimeSheet getTimeSheet() {
        // Assemble
        TimeSheet timeSheet = new TimeSheet(getEmployee());
        TimeSheetEntry timeSheetEntry = getTimeSheetEntry(timeSheet);
        timeSheet.getTimeSheetEntries().add(timeSheetEntry);
        return timeSheet;
    }

    private TimeSheetEntry getTimeSheetEntry(TimeSheet timeSheet) {
        TimeSheetEntry timeSheetEntry = new TimeSheetEntry(timeSheet,getContract());
        timeSheetEntry.setDate(new DateTime());
        timeSheetEntry.setHours(8);
        return timeSheetEntry;
    }

    public Employee getEmployee() {
        final Employee employee = new Employee();
        employee.addContract(getContract());
        return employee;
    }

    private Contract getContract() {
        return new Contract();
    }
}