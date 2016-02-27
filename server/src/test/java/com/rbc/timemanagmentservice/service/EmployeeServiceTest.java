package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.NotFoundException;
import java.util.List;

import static junit.framework.TestCase.*;


/**
 * Created by russbaker on 2/16/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
@Transactional
public class EmployeeServiceTest extends UserServiceTest<Employee>{

    public static final int HOURS = 8;
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ContractService contractService;


    @Autowired
    private StartupUtility startupUtility;

    @Before
    public void setUp(){
        super.setUserService(employeeService);
        super.setUp();
    }

    @Test
    public void whenCreatingNewEmployee_expectNoContractsOrTimeSheetsAssociated() throws Exception {
        // Act
        Employee employee = employeeService.createUser(startupUtility.getEmployee());

        // Assert
        assertNotNull("Employee is null", employee);
        assertInitialEmployeeCorrect(employee);
        assertNotNull("No id", employee.getId());
    }


    @Test
    public void whenAddingUsernameAndPasswordToExistingEmployee_expectAdded() throws Exception {
        // Assemble
        Employee employee = createUser();
        employee.setUsername("otherUsername");
        employee.setPassword("otherPassword");

        // Act
        Employee result = employeeService.updateUser(employee);

        // Assert
        Employee updated = employeeService.getUser(result.getId());
        assertNotNull("Username is empty", updated.getUsername());
        assertNotNull("Password is empty", updated.getPassword());
    }



    //------------- Jobs

    @Test
    public void whenAddingJobToEmployee_expectContractAdded() throws Exception {
        // Assemble
        Employee employee = employeeService.createUser(startupUtility.getEmployee());
        Contract contract = contractService.saveContract(new Contract());
        Job job = contractService.createJob(new Job(),contract.getId());

        // Act
        employeeService.addEmployeeToJob(employee.getId(), job);

        // Assert
        Employee result = employeeService.getUser(employee.getId());
        final List<Job> jobs = result.getJobs();
        assertFalse("No contract associated with employee", CollectionUtils.isEmpty(jobs));
    }

    @Test
    public void whenAddingDetatchedJobtToEmployee_expectContractAded() throws Exception {
        // Assemble
        Employee employee = employeeService.createUser(startupUtility.getEmployee());
        Contract contract = contractService.saveContract(new Contract());
        Job job = contractService.createJob(new Job(),contract.getId());

        // Act
        employeeService.addEmployeeToJob(employee.getId(), job);

        // Assert
        Employee result = employeeService.getUser(employee.getId());
        final List<Job> jobs = result.getJobs();
        assertFalse("No contract associated with employee", CollectionUtils.isEmpty(jobs));

    }

    //------------ Timesheets

    @Test
    public void whenRequestingEmployeesTimeSheets_expectTimeSheetsReturned() throws Exception {
        // Assemble
        Employee employee = employeeService.createUser(startupUtility.getEmployee());
        Timesheet timesheet = getTimeSheet(employee);

        // Act
        Timesheet result = employeeService.getLatestTimeSheet(employee.getId());

        // Assert
        assertNotNull("No timesheets returned", result);
        assertTrue("Wrong timesheet", timesheet.equals(result));
        for(TimeSheetEntry timeSheetEntry:result.getTimeSheetEntries()){
            assertNotNull("No Id",timeSheetEntry.getId());
            assertNull("Hours populated",timeSheetEntry.getHours());
            assertNotNull("No date",timeSheetEntry.getDate());
            assertEquals("No timesheet",result.getId(),timeSheetEntry.getTimesheetId());
        }

    }



    @Test(expected = NotFoundException.class)
    public void whenNoTimeSheetsFound_expectNotFoundExcption() throws Exception {
        // Act
        employeeService.getLatestTimeSheet(0);
    }

    @Test
    public void whenAddingTimeSheetToEmployee_expectTimesheetAdded() throws Exception {
        // Assemble
        Employee employee = createUser();
        getTimeSheet(employee);

        // Assert
        Timesheet timesheet = employeeService.getUser(employee.getId()).getTimesheets().get(0);
        assertNotNull("No timesheet created", timesheet);
        assertNotNull("No timesheet persisted", timesheet.getId());
        List<TimeSheetEntry> timeSheetEntries = timesheet.getTimeSheetEntries();
        assertEquals("Wrong number of timesheet entries", 7, timeSheetEntries.size());
        assertFalse("No Contract associated with employee's timesheet", CollectionUtils.isEmpty(timeSheetEntries));
    }

    @Test
    public void whenUpdatingTimeEntries_expectTimeEntriesUpdated() throws Exception {
        // Assemble
        Employee employee = createUser();
        Timesheet timesheet = getTimeSheet(employee);//employeeService.getEmployee(employee.getId()).getTimesheets().get(0);
        TimeSheetEntry timeSheetEntry = timesheet.getTimeSheetEntries().get(0);
        timeSheetEntry.setHours(HOURS);

        // Act
        employeeService.addTimeSheetEntry(employee.getId(), timesheet.getId(), timeSheetEntry, timeSheetEntry.getId());

        // Assert
        Employee result = employeeService.getUser(employee.getId());
        assertEquals("Hours not updated", HOURS, result.getTimesheets().get(0).getTimeSheetEntries().get(0).getHours(), 0);

    }


//--------------- Private Methods

    private void assertInitialEmployeeCorrect(Employee employee) {
        assertTrue("Jobs associated with employee", CollectionUtils.isEmpty(employee.getJobs()));
        assertTrue("TimeSheets should be empty", CollectionUtils.isEmpty(employee.getTimesheets()));
    }

    public Employee createUser() {
        Employee employee = new Employee();
        employee.setFirstName("Russ");
        employee.setLastName("Baker");
        employee.setUsername("username");
        employee.setPassword("password");
        employee.setRoles(User.Roles.employee);
        employee.setDba("Russ Baker");
        return employeeService.createUser(employee);
    }

    private Timesheet getTimeSheet(Employee employee) {
        Contract contract = contractService.saveContract(new Contract());

        Job job = contractService.createJob(new Job(),contract.getId());
        employeeService.createTimeSheet(employee.getId(), job.getId());
        return employeeService.getUser(employee.getId()).getTimesheets().get(0);
    }

}