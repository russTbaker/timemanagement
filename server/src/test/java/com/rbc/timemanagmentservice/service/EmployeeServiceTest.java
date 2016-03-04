package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.persistence.TimesheetRepository;
import com.rbc.timemanagmentservice.testutils.ContractTestUtil;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;


/**
 * Created by russbaker on 2/16/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
public class EmployeeServiceTest extends UserServiceTest<Employee>{

    public static final int HOURS = 8;
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractTestUtil contractTestUtil;


    @Autowired
    private StartupUtility startupUtility;

    @Autowired
    private JobService jobService;

    @Autowired
    private TimesheetRepository timesheetRepository;


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
        Job job = contractService.addJobToContract(contractService.createJob(new Job()).getId(),contract.getId());

        // Act
        employeeService.addEmployeeToJob(employee.getId(), job.getId());

        // Assert
        Employee result = employeeService.getUser(employee.getId());
        final List<Job> jobs = result.getJobs();
        assertFalse("No contract associated with employee", CollectionUtils.isEmpty(jobs));
    }

    @Test
    public void whenAddingDetatchedJobToEmployee_expectContractAded() throws Exception {
        // Assemble
        Employee employee = employeeService.createUser(startupUtility.getEmployee());
        Contract contract = contractService.saveContract(new Contract());
        Job job = contractService.addJobToContract(contractService.createJob(new Job()).getId(),contract.getId());

        // Act
        employeeService.addEmployeeToJob(employee.getId(), job.getId());

        // Assert
        Employee result = employeeService.getUser(employee.getId());
        final List<Job> jobs = result.getJobs();
        assertFalse("No contract associated with employee", CollectionUtils.isEmpty(jobs));

    }

    @Test
    public void whenGettingEmployeesAvailableJobs_expectJobsReturned() throws Exception {
        // Assemble
        Employee employee = employeeService.createUser(startupUtility.getEmployee());
        Contract contract = contractService.saveContract(new Contract());
        employeeService.addContractToUser(employee.getId(),contract.getId());
        Job job = contractService.addJobToContract(contractService.createJob(new Job()).getId(),contract.getId());

        // Act
        List<Job> result = employeeService.getEmployeesAvailableJobs(employee.getId());

        // Assert
        assertFalse("No jobs returned",CollectionUtils.isEmpty(result));
        assertTrue("Wrong job",result.contains(job));
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
        for(TimesheetEntry timeSheetEntry:result.getTimeSheetEntries()){
            assertNotNull("No Id",timeSheetEntry.getId());
            assertEquals("Hours populated",0,timeSheetEntry.getHours(),0.0);
            assertNotNull("No date",timeSheetEntry.getDate());
        }
    }

    @Test(expected = org.springframework.dao.InvalidDataAccessApiUsageException.class)
    public void whenCallingGetNewTimesheetWithExistingCurrentTimesheet_expectIllegalArgumentException() throws Exception {
        // Assemble
        Employee employee = employeeService.createUser(startupUtility.getEmployee());
        ContractTestUtil.JobCreator jobCreator = contractTestUtil.getJobCreator().invoke();

        // Act
        employeeService.createTimeSheet(employee.getId(),jobCreator.getJobCreated().getId());
        employeeService.createTimeSheet(employee.getId(),jobCreator.getJobCreated().getId());

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
        List<TimesheetEntry> timeSheetEntries = timesheet.getTimeSheetEntries();
        assertEquals("Wrong number of timesheet entries", 7, timeSheetEntries.size());
        assertFalse("No Contract associated with employee's timesheet", CollectionUtils.isEmpty(timeSheetEntries));
    }

    @Test
    public void whenUpdatingTimeEntry_expectTimeEntryUpdated() throws Exception {
        // Assemble
        Employee employee = createUser();
        Timesheet timesheet = getTimeSheet(employee);
        TimesheetEntry timeSheetEntry = timesheet.getTimeSheetEntries().get(0);
        timeSheetEntry.setHours(HOURS);

        // Act
        employeeService.addTimesheetEntry(employee.getId(), timesheet.getId(), timeSheetEntry, timeSheetEntry.getId());

        // Assert
        Employee result = employeeService.getUser(employee.getId());
        assertEquals("Hours not updated", HOURS, result.getTimesheets().get(0).getTimeSheetEntries().get(0).getHours(), 0);

    }

    @Test
    public void whenUpdatingTimeSheetEntries_expectEntriesUpdated() throws Exception {
        // Assemble
        Employee employee = startupUtility.init();
        Timesheet timesheet = employee.getTimesheets().get(0);
        final List<TimesheetEntry> timeSheetEntries = timesheet.getTimeSheetEntries();
        TimesheetEntry timeSheetEntry = timeSheetEntries.get(0);
        timeSheetEntry.setHours(20);

        // Act
        employeeService.addTimeSheetEntries(new ArrayList<>(timeSheetEntries)  , employee.getId(), timesheet.getId());

        // Assert
        employee = employeeService.getUser(employee.getId());
        final List<TimesheetEntry> timeEntryResults = employee.getTimesheets().get(0).getTimeSheetEntries();
        assertEquals("Wrong number of timesheet entries",7,timeEntryResults.size());
        assertTrue("Wrong timesheet updated.", timeEntryResults.contains(timeSheetEntry));
        for(TimesheetEntry tse: employee.getTimesheets().get(0).getTimeSheetEntries()){

        }

        // Make sure the job also has the updated entries
        final List<TimesheetEntry> jobTimesheetEntries = jobService.findJob(employee.getJobs().get(0).getId()).getTimeSheetEntries();
        assertEquals("Wrong number of job timesheet entries",7,jobTimesheetEntries.size());
        assertTrue("Job doesn't have updates", jobTimesheetEntries.containsAll(timeSheetEntries));

        final List<TimesheetEntry> timesheetTimesheetEntries = timesheetRepository.findOne(timesheet.getId()).getTimeSheetEntries();
        assertEquals("Wrong number of job timesheet entries",7,timesheetTimesheetEntries.size());
        assertTrue("Job doesn't have updates", timesheetTimesheetEntries.containsAll(timeSheetEntries));

    }
//----------- Contracts

    @Test
    public void whenGettingEmployeesContracts_expectNoneFound() throws Exception {
        // Assemble
        Employee employee = createUser();
        contractTestUtil.getJobCreator().invoke();
        Contract contract = contractTestUtil.getContract();
        employeeService.addContractToUser(employee.getId(),contract.getId());

        // Act
        List<Contract> contracts = employeeService.getUserContracts(employee.getId());
        assertFalse("No contracts returned",CollectionUtils.isEmpty(contracts));

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

        Job job = contractService.addJobToContract(contractService.createJob(new Job()).getId(),contract.getId());
        employeeService.createTimeSheet(employee.getId(), job.getId());
        return employeeService.getUser(employee.getId()).getTimesheets().get(0);
    }

}