package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.TimeSheet;
import com.rbc.timemanagmentservice.model.TimeSheetEntry;
import com.rbc.timemanagmentservice.persistence.EmployeeRepository;
import com.rbc.timemanagmentservice.persistence.TimeSheetEntryRepository;
import com.rbc.timemanagmentservice.persistence.TimeSheetRepository;
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

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TimeSheetRepository timeSheetRepository;

    @Autowired
    private TimeSheetEntryRepository timeSheetEntryRepository;

    @Test
    public void whenCreatingNewEmployee_expectNoContractsOrTimeSheetsAssociated() throws Exception {
        // Act
        Employee employee = employeeService.createEmployee();

        // Assert
        assertNotNull("Employee is null", employee);
        assertInitialEmployeeCorrect(employee);
        assertNotNull("No id", employee.getId());
    }

    @Test
    public void whenFindingEmployeeById_expectEmployeeReturned() throws Exception {
        // Act
        Employee employee = employeeService.createEmployee();

        // Assert
        Employee result = employeeService.findEmployee(employee.getId());
        assertNotNull("No employee returned", result);
        assertEquals("Wrong employee",employee,result);

    }

    @Test(expected = NotFoundException.class)
    public void whenFindingNonExistingUser_expectNotFoundException() throws Exception{
        employeeService.findEmployee(10000);
    }

    @Test
    public void whenAddingUsernameAndPasswordToExistingEmployee_expectAdded() throws Exception {
        // Assemble
        Employee employee = createEmployee();

        // Act
        Employee result = employeeService.updateEmployee(employee);
        assertNotNull("Username is empty", result.getUsername());
        assertNotNull("Password is empty", result.getPassword());
    }


    @Test
    public void whenAddingContractToEmployee_expectContractAdded() throws Exception {
        // Assemble
        Employee employee = createEmployee();
        Contract contract = StartupUtility.getContract();

        // Act
        employeeService.addEmployeeToContract(employee,contract);

        // Assert
        Employee result = employeeService.findEmployee(employee.getId());
        final List<Contract> contracts = result.getContracts();
        assertFalse("No contract associated with employee",CollectionUtils.isEmpty(contracts));
        assertTrue("Wrong contract",contracts.contains(contract));
    }

    @Test
    public void whenAddingTimeSheetToEmployee_expectTimesheetAdded() throws Exception {
        // Assemble
        Employee employee = createEmployee();
        Contract contract = StartupUtility.getContract();
        employeeService.addEmployeeToContract(employee,contract);

        // Act
        List<TimeSheet> timeSheets = employeeService.createTimeSheet(employee,contract);

        // Assert
        assertFalse("No timesheet returned",CollectionUtils.isEmpty(timeSheets));
        for(TimeSheet timesheet : timeSheets) {
            assertNotNull("No timesheet created", timesheet);
            assertNotNull("No timesheet persisted", timesheet.getId());
            List<TimeSheetEntry> timeSheetEntries = timesheet.getTimeSheetEntries();
            assertEquals("Wrong number of timesheet entries",7,timeSheetEntries.size());
            assertFalse("No Contract associated with employee's timesheet", CollectionUtils.isEmpty(timeSheetEntries));
            for (TimeSheetEntry timeSheetEntry : timeSheetEntries) {
                assertEquals("No employee associated with timesheet", contract, timeSheetEntry.getContract());
            }
        }
    }

    @Test
    public void whenUpdatingTimeEntries_expectTimeEntriesUpdated() throws Exception {
        // Assemble
        Employee employee = createEmployee();
        Contract contract = StartupUtility.getContract();
        employeeService.addEmployeeToContract(employee,contract);
        List<TimeSheet> timeSheets = employeeService.createTimeSheet(employee,contract);
        TimeSheetEntry timeSheetEntry = timeSheets.get(0).getTimeSheetEntries().get(0);
        timeSheetEntry.setHours(HOURS);

        // Act
        employeeService.addTimeSheetEntry(employee.getId(),timeSheets.get(0).getId(), timeSheetEntry, timeSheetEntry.getId());

        // Assert
        Employee result = employeeService.findEmployee(employee.getId());
        assertEquals("Hours not updated",HOURS,result.getTimesheets().get(0).getTimeSheetEntries().get(0).getHours(),0);

    }

    @Test(expected = NotFoundException.class)
    public void whenFindingAllEmployeesAndNoneExist_expectNotFoundException() throws Exception {
        // Assemble
        employeeRepository.deleteAll();
        timeSheetRepository.deleteAll();
        timeSheetEntryRepository.deleteAll();

        // Act
        employeeService.findAll(1,20);

    }

    @Test
    public void whenFindingAllEmployees_expectAllEmployeesFound() throws Exception {
        // Assemble
        Employee employee = createEmployee();
        assertNotNull("No employee created", employee);

        // Act
        List<Employee> results = employeeService.findAll(null,null);

        // Assert
        assertFalse("No employees returned",CollectionUtils.isEmpty(results));
        assertTrue("Employee not found",results.contains(employee));

    }

    @Test
    public void whenRequestingEmployeesTimeSheets_expectTimeSheetsReturned() throws Exception {
        // Assemble
        Employee employee = createEmployee();
        TimeSheet timeSheet = StartupUtility.getTimeSheet(employee,StartupUtility.getContract());
        employee.getTimesheets().add(timeSheet);
        employeeRepository.save(employee);

        // Act
        TimeSheet result = employeeService.getLatestTimeSheet(employee.getId());

        // Assert
        assertNotNull("No timesheets returned",result);
        assertEquals("Wrong timesheet",timeSheet,result);
    }

    @Test(expected = NotFoundException.class)
    public void whenNoTimeSheetsFound_expectNotFoundExcption() throws Exception {
        // Act
       employeeService.getLatestTimeSheet(0);
    }

    //--------------- Private Methods

    private void assertInitialEmployeeCorrect(Employee employee) {
        assertTrue("Contract associated with employee", CollectionUtils.isEmpty(employee.getContracts()));
        assertTrue("TimeSheets should be empty", CollectionUtils.isEmpty(employee.getTimesheets()));
    }

    private Employee createEmployee() {
        Employee employee = employeeService.createEmployee();
        employee.setUsername("username");
        employee.setPassword("password");
        return employee;
    }
}