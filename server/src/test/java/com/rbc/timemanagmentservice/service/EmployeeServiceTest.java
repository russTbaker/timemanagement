package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.util.StartupUtility;
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
public class EmployeeServiceTest {

    public static final int HOURS = 8;
    @Autowired
    private EmployeeService employeeService;

//    @Autowired
//    private EmployeeRepository employeeRepository;
//
//
    @Autowired
    private ContractService contractService;

    @Autowired
    private JobService jobService;


    @Autowired
    private StartupUtility startupUtility;

    @Test
    public void whenFindingAllEmployees_expectAllEmployeesFound() throws Exception {
        // Assemble
        Employee employee = createEmployee();
        assertNotNull("No employee created", employee);

        // Act
        List<Employee> results = employeeService.findAll(null, null);

        // Assert
        assertFalse("No employees returned", CollectionUtils.isEmpty(results));
        assertTrue("Employee not found", results.contains(employee));

    }

    @Test
    public void whenRequestingEmployeesTimeSheets_expectTimeSheetsReturned() throws Exception {
        // Assemble
        Employee employee = employeeService.createEmployee(startupUtility.getEmployee());
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
    public void whenCreatingNewEmployee_expectNoContractsOrTimeSheetsAssociated() throws Exception {
        // Act
        Employee employee = employeeService.createEmployee(startupUtility.getEmployee());

        // Assert
        assertNotNull("Employee is null", employee);
        assertInitialEmployeeCorrect(employee);
        assertNotNull("No id", employee.getId());
    }

    @Test
    public void whenFindingEmployeeById_expectEmployeeReturned() throws Exception {
        // Act
        Employee employee = employeeService.createEmployee(startupUtility.getEmployee());

        // Assert
        Employee result = employeeService.getEmployee(employee.getId());
        assertNotNull("No employee returned", result);
        assertEquals("Wrong employee", employee, result);

    }

    @Test(expected = NotFoundException.class)
    public void whenFindingNonExistingUser_expectNotFoundException() throws Exception {
        employeeService.getEmployee(10000);
    }

    @Test
    public void whenAddingUsernameAndPasswordToExistingEmployee_expectAdded() throws Exception {
        // Assemble
        Employee employee = createEmployee();
        employee.setUsername("otherUsername");
        employee.setPassword("otherPassword");

        // Act
        Employee result = employeeService.updateEmployee(employee);

        // Assert
        Employee updated = employeeService.getEmployee(result.getId());
        assertNotNull("Username is empty", updated.getUsername());
        assertNotNull("Password is empty", updated.getPassword());
    }

    @Test
    public void whenAddingAddressToEmployeeExpectAddressAdded() throws Exception {
        Employee employee = addAddressToEmployee();


        // Assert
        Employee result = employeeService.getEmployee(employee.getId());
        final List<Address> addresses = result.getAddress();
        assertFalse("No addresses added",CollectionUtils.isEmpty(addresses));
        for(Address addr:addresses){
            assertNotNull("No Id",addr.getId());
        }

    }




    @Test
    public void whenDeletingAddressFromEmployee_expectAddressDeleted() throws Exception {
        // Assemble
        Employee employee = addAddressToEmployee();

        // Act
        employeeService.removeAddressFromEmployee(employee.getId(),employee.getAddress().get(0).getId());

        // Assert
        assertTrue("Address not removed",CollectionUtils.isEmpty(employeeService.getEmployee(employee.getId()).getAddress()));

    }

    @Test
    public void whenAddingContractToEmployee_expectContractAdded() throws Exception {
        // Assemble
        Employee employee = employeeService.createEmployee(startupUtility.getEmployee());
        Contract contract = contractService.saveContract(new Contract());
        Job job = contractService.createJob(new Job(),contract.getId());

        // Act
        employeeService.addEmployeeToJob(employee.getId(), job);

        // Assert
        Employee result = employeeService.getEmployee(employee.getId());
        final List<Job> jobs = result.getJobs();
        assertFalse("No contract associated with employee", CollectionUtils.isEmpty(jobs));
    }

    @Test
    public void whenAddingDetatchedJobtToEmployee_expectContractAded() throws Exception {
        // Assemble
        Employee employee = employeeService.createEmployee(startupUtility.getEmployee());
        Contract contract = contractService.saveContract(new Contract());
        Job job = contractService.createJob(new Job(),contract.getId());

        // Act
        employeeService.addEmployeeToJob(employee.getId(), job);

        // Assert
        Employee result = employeeService.getEmployee(employee.getId());
        final List<Job> jobs = result.getJobs();
        assertFalse("No contract associated with employee", CollectionUtils.isEmpty(jobs));

    }

    @Test
    public void whenAddingTimeSheetToEmployee_expectTimesheetAdded() throws Exception {
        // Assemble
        Employee employee = createEmployee();
        getTimeSheet(employee);

        // Assert
        Timesheet timesheet = employeeService.getEmployee(employee.getId()).getTimesheets().get(0);
        assertNotNull("No timesheet created", timesheet);
        assertNotNull("No timesheet persisted", timesheet.getId());
        List<TimeSheetEntry> timeSheetEntries = timesheet.getTimeSheetEntries();
        assertEquals("Wrong number of timesheet entries", 7, timeSheetEntries.size());
        assertFalse("No Contract associated with employee's timesheet", CollectionUtils.isEmpty(timeSheetEntries));
    }

    @Test
    public void whenUpdatingTimeEntries_expectTimeEntriesUpdated() throws Exception {
        // Assemble
        Employee employee = createEmployee();
        Timesheet timesheet = getTimeSheet(employee);//employeeService.getEmployee(employee.getId()).getTimesheets().get(0);
        TimeSheetEntry timeSheetEntry = timesheet.getTimeSheetEntries().get(0);
        timeSheetEntry.setHours(HOURS);

        // Act
        employeeService.addTimeSheetEntry(employee.getId(), timesheet.getId(), timeSheetEntry, timeSheetEntry.getId());

        // Assert
        Employee result = employeeService.getEmployee(employee.getId());
        assertEquals("Hours not updated", HOURS, result.getTimesheets().get(0).getTimeSheetEntries().get(0).getHours(), 0);

    }

    @Test(expected = NotFoundException.class)
    public void whenDeletingEmployee_expectEmployeeDeleted() throws Exception {
        // Assemble
        Employee employee = createEmployee();

        // Act
        employeeService.deleteEmployee(employee.getId());

        // Assert
        employeeService.getEmployee(employee.getId());

    }
//--------------- Private Methods

    private void assertInitialEmployeeCorrect(Employee employee) {
        assertTrue("Jobs associated with employee", CollectionUtils.isEmpty(employee.getJobs()));
        assertTrue("TimeSheets should be empty", CollectionUtils.isEmpty(employee.getTimesheets()));
    }

    private Employee createEmployee() {
        Employee employee = new Employee();
        employee.setFirstName("Russ");
        employee.setLastName("Baker");
        employee.setUsername("username");
        employee.setPassword("password");
        employee.setRoles(User.Roles.employee);
        employee.setDba("Russ Baker");
        return employeeService.createEmployee(employee);
    }

    private Timesheet getTimeSheet(Employee employee) {
        Contract contract = contractService.saveContract(new Contract());

        Job job = contractService.createJob(new Job(),contract.getId());
        employeeService.createTimeSheet(employee.getId(), job.getId());
        return employeeService.getEmployee(employee.getId()).getTimesheets().get(0);
    }

    private Employee addAddressToEmployee() {
        // Assemble
        Employee employee = createEmployee();
        Address address = startupUtility.getAddress();

        // Act
        employeeService.addAddressToEmployee(employee.getId(),address);
        return employee;
    }
}