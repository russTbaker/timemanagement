package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.testutils.ContractTestUtil;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

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
        Job job = createPersistentJob();

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
        Job job = createPersistentJob();

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
    public void whenRequestingEmployeesTimeEntriess_expectTimeEntriesReturned() throws Exception {
        // Assemble
        Employee employee = employeeService.createUser(startupUtility.getEmployee());

        // Act
        List<TimeEntry> employeeTimeEntries = getTimeEntries(employee);
        employee = employeeService.getUser(employee.getId());

        // Assert
        assertNotNull("No timesheets returned", employee);
        // TODO: Fix
        for(TimeEntry timeSheetEntry:employeeTimeEntries){
            assertNotNull("No Id",timeSheetEntry.getId());
            assertEquals("Hours populated",0,timeSheetEntry.getHours(),0.0);
            assertNotNull("No date",timeSheetEntry.getDate());
        }
    }

    @Test
    public void whenGettingEmployeeLatestTimeEntries_expectLatestReturned() throws Exception {
        DateTimeFormatter dateTimeFormatter  = DateTimeFormat.forPattern("yyyy-MM-dd");
        // Assemble
//        Job job = createPersistentJob();
        Employee employee = employeeService.createUser(startupUtility.getEmployee());
        getTimeEntries(employee);
        employee = employeeService.getUser(employee.getId());

        // Act
        List<TimeEntry> results = employeeService.getLatestTimeEntriesForEmployeeJobs(employee.getId(),employee.getJobs().get(0).getId());
        assertFalse("Results are empty",CollectionUtils.isEmpty(results));
        assertEquals("Wrong number of time entries",7,results.size());
        DateTime weekStart = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();
        Interval interval = new Interval(weekStart,weekStart.plusDays(7));
        for(TimeEntry timeEntry:results){
            assertTrue("Wrong date for time entry " + dateTimeFormatter.print(timeEntry.getDate()),interval.contains(timeEntry.getDate()));
        }

    }

    @Test
    public void whenUpdatingTimeEntries_expectEntriesUpdated() throws Exception {
        // Assemble
        Employee employee = startupUtility.init();
        final List<TimeEntry> timeEntries = employee.getJobs().get(0).getTimeEntries();
        TimeEntry timeEntry = timeEntries.get(0);
        final int updatedHours = 20;
        timeEntry.setHours(updatedHours);

        // Act
        employeeService.addTimeSheetEntries(new ArrayList<>(timeEntries)  , employee.getJobs().get(0).getId());

        // Assert
        Employee result = employeeService.getUser(employee.getId());
        final List<TimeEntry> timeEntryResults = result.getJobs().get(0).getTimeEntries();
        assertEquals("Wrong number of timesheet entries",7,timeEntryResults.size());
        assertEquals("Wrong hours",updatedHours,timeEntryResults
                .stream()
                .filter(te -> te.getDate().equals(timeEntry.getDate()))
                .findFirst()
                .get().getHours(),0.0);


        // Make sure the job also has the updated entries
        final List<TimeEntry> jobTimesheetEntries = jobService.findJob(employee.getJobs().get(0).getId()).getTimeEntries();
        assertEquals("Wrong number of job timesheet entries",7,jobTimesheetEntries.size());
        assertTrue("Job doesn't have updates", jobTimesheetEntries.containsAll(timeEntries));

//        final List<TimeEntry> timesheetTimesheetEntries = timesheetRepository.findOne(timesheet.getId()).getTimeEntries();
//        assertEquals("Wrong number of job timesheet entries",7,timesheetTimesheetEntries.size());
//        assertTrue("Job doesn't have updates", timesheetTimesheetEntries.containsAll(timeEntries));

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

    private List<TimeEntry> getTimeEntries(Employee employee) {
        Job job = createPersistentJob();
        return employeeService.getTimeEntriesForEmployeeJobs(employee.getId(), job.getId());
    }

    private Job createPersistentJob() {
        Contract contract = contractService.saveContract(new Contract());
        return contractService.addJobToContract(contractService.createJob(new Job()).getId(),contract.getId());
    }

}