package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.TestCase.*;


/**
 * Created by rbaker on 2/6/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
@Transactional(propagation = Propagation.REQUIRED)
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private StartupUtility startupUtility;


    @Test
    public void whenCreatingEmployee_expectEmployeeFound() throws Exception {
        Employee employee = getEmployee();

        Employee result = employeeRepository.findOne(employee.getId());
        // Assert
        assertNotNull("No employee returnred", employee);
        assertEquals("Wrong employee", employee, result);
    }

//    @Test
//    public void whenGettingEmployee_expectFullyHydratedEmployee() throws Exception {
//
//        final Job job = new Job();
//        DateTime weekStart = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();
//        for(int i=0;i<7;i++) {
//            TimeEntry timeEntry = new TimeEntry();
//            timeEntry.setDate(new DateTime());
//            timeEntry.setHours(8);
//            timeEntry.setDate(weekStart.plusDays(i));
//            job.addTimeEntry(timeEntry);
//        }
//
//        final Employee employee = new Employee();
//        employee.addJob(job);
//        Employee saved = employeeRepository.save(employee);
//        saved.addEmail(new Email());
//
//        Employee result = employeeRepository.findOne(saved.getId());
//
//        // Assert
//        final List<TimeEntry> timeEntries = employee.getJobs().get(0).getTimeEntries();
//        assertFalse("Timesheet entries list is empty",CollectionUtils.isEmpty(timeEntries));
//        assertEquals("Wrong number of time entries",7,timeEntries.size());
//        assertFalse("Email list is empty",CollectionUtils.isEmpty(result.getEmails()));
//    }


    private Employee getEmployee() {
        // Assemble
        Employee employee = new Employee();
        employee.setFirstName("Russ");
        employee.setLastName("Baker");
        employee.setUsername("admin");
        employee.setPassword("password");
//        employee.addEmail(new Email());

        // Act
        return employeeRepository.save(employee);
    }


}