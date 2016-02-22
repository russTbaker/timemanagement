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
import org.springframework.util.CollectionUtils;

import java.util.Arrays;

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

    @Test
    public void whenGettingEmployee_expectFullyHydratedEmployee() throws Exception {

        final Contract contract = new Contract();
        final Employee employee = new Employee();
        employee.addContract(contract);
        Employee saved = employeeRepository.save(employee);
        final TimeSheet timesheet = new TimeSheet(saved);
        timesheet.getTimeSheetEntries().add(new TimeSheetEntry(timesheet,contract));
        saved.getTimesheets().add(timesheet);
        saved.setEmails(Arrays.asList(new Email()));

        Employee result = employeeRepository.findOne(saved.getId());

        // Assert
        assertFalse("Timesheet list is empty",CollectionUtils.isEmpty(result.getTimesheets()));
        for(TimeSheet timeSheet: result.getTimesheets()) {
            assertFalse("TimeSheet entries list is empty",CollectionUtils.isEmpty(timeSheet.getTimeSheetEntries()));
        }
        assertFalse("Email list is empty",CollectionUtils.isEmpty(result.getEmails()));
    }


    private Employee getEmployee() {
        // Assemble
        Employee employee = new Employee();
        employee.setFirstName("Russ");
        employee.setLastName("Baker");
        employee.setUsername("admin");
        employee.setPassword("password");
        employee.setEmails(Arrays.asList(new Email(), new Email()));
        employee.setRoles(User.Roles.employee);

        // Act
        return employeeRepository.save(employee);
    }


}