package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.testutils.ContractTestUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
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
public class EmployeeServiceTest extends UserServiceTest<Employee> {

    public static final int HOURS = 8;
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractTestUtil contractTestUtil;

    @Autowired
    private JobService jobService;




    @Before
    public void setUp() {
        super.setUserService(employeeService);
        super.setUp();
    }

    @After
    public void tearDown() {
        super.tearDown();
        try {
            if (employeeService.getUser(user.getId()) != null) {
                employeeService.deleteUser(user.getId());
            }
        } catch (NotFoundException e) {
            // Don't care
        }
    }

    @Test
    public void whenCreatingNewEmployee_expectNoContractsOrTimeSheetsAssociated() throws Exception {
        // Act
        Employee employee = user;//employeeService.createUser(startupUtility.getEmployee());

        // Assert
        assertNotNull("Employee is null", employee);
        assertInitialEmployeeCorrect(employee);
        assertNotNull("No id", employee.getId());
    }


    @Test
    public void whenAddingUsernameAndPasswordToExistingEmployee_expectAdded() throws Exception {
        // Assemble
        user.setUsername("otherUsername");
        user.setPassword("otherPassword");

        // Act
        Employee result = employeeService.updateUser(user);

        // Assert
        Employee updated = employeeService.getUser(result.getId());
        assertNotNull("Username is empty", updated.getUsername());
        assertNotNull("Password is empty", updated.getPassword());
    }


    //------------- Jobs

    @Test
    public void whenAddingJobToEmployee_expectContractAdded() throws Exception {
        // Assemble
        Contract firstContract = getContract();
        Contract secondContract = getContract();
        Job job = createPersistentJob(firstContract);
        employeeService.addContractToUser(user.getId(), firstContract.getId());


        // Act
        employeeService.addEmployeeToJob(user.getId(), job.getId());

        // Assert
        Employee result = employeeService.getUser(user.getId());
        final List<Job> jobs = result.getJobs();
        assertEquals("Wrong number of jobs", 1, jobs.size());
        assertFalse("No contract associated with employee", CollectionUtils.isEmpty(jobs));

        // Verify Job/Employee relationship
        Job jobResult = jobService.findJob(job.getId());
        assertEquals("wrong number of employees", 1, jobResult.getEmployees().size());
    }


    @Test
    public void whenGettingEmployeesAvailableJobs_expectJobsReturned() throws Exception {
        // Assemble
        Contract contract = getContract();
        Job job = contractService.addJobToContract(contractService.createJob(new Job()).getId(), contract.getId());
        employeeService.addContractToUser(user.getId(), contract.getId());
        employeeService.addEmployeeToJob(user.getId(), job.getId());

        // Act
        List<Job> result = employeeService.getEmployeeJobs(user.getId());

        // Assert
        assertFalse("No jobs returned", CollectionUtils.isEmpty(result));
        assertEquals("Wrong job size", 1, result.size());
    }

    //------------ Timesheets

    @Test
    public void whenRequestingEmployeesTimeEntriess_expectTimeEntriesReturned() throws Exception {

        // Act
        List<TimeEntry> employeeTimeEntries = getTimeEntries(user, getContract());
        user = employeeService.getUser(user.getId());

        // Assert
        assertNotNull("No timesheets returned", user);
        // TODO: Fix
        for (TimeEntry timeSheetEntry : employeeTimeEntries) {
            assertNotNull("No Id", timeSheetEntry.getId());
            assertEquals("Hours populated", 0, timeSheetEntry.getHours(), 0.0);
            assertNotNull("No date", timeSheetEntry.getDate());
        }
    }

    @Test
    public void whenRequestingEmployeeTimesheetWithSpecificStartWeek_expectTimesheetWithCorrectStartDate() throws Exception {
        // Assemble
        DateTime lastWeek = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay().minusDays(7);

        // Act
        List<TimeEntry> employeeTimeEntries = getTimeEntries(user, getContract(), lastWeek);
        user = employeeService.getUser(user.getId());

        // Assert
        Interval interval = new Interval(lastWeek, lastWeek.plusDays(7));
        for (TimeEntry timeSheetEntry : employeeTimeEntries) {
            assertNotNull("No Id", timeSheetEntry.getId());
            assertEquals("Hours populated", 0, timeSheetEntry.getHours(), 0.0);
            assertNotNull("No date", timeSheetEntry.getDate());
            assertTrue("Date not within interval: " + interval.getStart() + " and  " + interval.getEnd(), interval.contains(timeSheetEntry.getDate()));
        }
    }

    @Test
    public void whenGettingEmployeeLatestTimeEntries_expectLatestReturned() throws Exception {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        // Assemble
        getTimeEntries(user, getContract());
        user = employeeService.getUser(user.getId());

        // Act
        List<TimeEntry> results = employeeService.getLatestTimeEntriesForEmployeeJobs(user.getId(), user.getJobs().get(0).getId());
        assertFalse("Results are empty", CollectionUtils.isEmpty(results));
        assertEquals("Wrong number of time entries", 7, results.size());
        DateTime weekStart = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();
        Interval interval = new Interval(weekStart, weekStart.plusDays(7));
        for (TimeEntry timeEntry : results) {
            assertTrue("Wrong date for time entry " + dateTimeFormatter.print(timeEntry.getDate()), interval.contains(timeEntry.getDate()));
        }
    }

    @Test
    public void whenUpdatingTimeEntries_expectEntriesUpdated() throws Exception {
        // Assemble
        List<TimeEntry> timeEntries = getTimeEntries(user, getContract());
        user = employeeService.getUser(user.getId());
        TimeEntry timeEntry = timeEntries.get(0);
        final int updatedHours = 20;
        timeEntry.setHours(updatedHours);
        List<DateTime> expectedTimes = getWeeksWorthOfTimeEntries();

        // Act
        Integer originalJobId = timeEntries.get(0).getJob().getId();
        employeeService.addTimeSheetEntries(new ArrayList<>(timeEntries), originalJobId);

        // Assert
        assertTimesheetHydrated(timeEntries, timeEntry, updatedHours, expectedTimes);

    }




    @Test
    public void whenUpdatingTimeSheetEntriesWithNoIdOrJob_expectIdsFromOriginal() throws Exception {
        // Assemble
        List<TimeEntry> timeEntries = getTimeEntries(user, getContract());
        Integer originalJobId = timeEntries.get(0).getJob().getId();

        timeEntries
                .stream()
                .forEach(timeEntry1 -> {
                    timeEntry1.setJob(null);
                    timeEntry1.setId(null);
                });

        user = employeeService.getUser(user.getId());
        TimeEntry timeEntry = timeEntries.get(0);
        final int updatedHours = 20;
        timeEntry.setHours(updatedHours);
        List<DateTime> expectedTimes = getWeeksWorthOfTimeEntries();


        // Act
        employeeService.addTimeSheetEntries(new ArrayList<>(timeEntries), originalJobId);

        // Assert
        assertTimesheetHydrated(timeEntries, timeEntry, updatedHours, expectedTimes);
    }

    //----------- Contracts

    @Test
    public void whenGettingEmployeesContracts_expectNoneFound() throws Exception {
        // Assemble
        Employee employee = user;//createUser();
        contractTestUtil.getJobCreator().invoke();
        Contract contract = contractTestUtil.getContract();
        employeeService.addContractToUser(employee.getId(), contract.getId());

        // Act
        List<Contract> contracts = employeeService.getUserContracts(employee.getId());
        assertFalse("No contracts returned", CollectionUtils.isEmpty(contracts));

    }


//--------------- Private Methods

    private void assertInitialEmployeeCorrect(Employee employee) {
        assertTrue("Jobs associated with employee", CollectionUtils.isEmpty(employee.getJobs()));
    }

    public Employee createUser() {
        Employee employee = new Employee();
        employee.setFirstName("Russ");
        employee.setLastName("Baker");
        employee.setUsername("username" + System.currentTimeMillis());
        employee.setPassword(EMPLOYEE_PASSWORD);
        Roles employeeRole = new Roles();
        employeeRole.setRole(Roles.Role.employee);
        employee.getRoles().add(employeeRole);
        employee.setDba("Russ Baker");
        return employeeService.createUser(employee);
    }

    private List<TimeEntry> getTimeEntries(Employee employee, Contract contract) {
        Job job = createPersistentJob(contract);
        return employeeService.getTimeEntriesForEmployeeJobs(employee.getId(), job.getId());
    }

    private List<TimeEntry> getTimeEntries(Employee employee, Contract contract, DateTime startDate) {
        Job job = createPersistentJob(contract);
        return employeeService.getTimeEntriesForEmployeeJobs(employee.getId(), job.getId(), startDate);
    }

    private Job createPersistentJob(Contract contract) {
        return contractService.addJobToContract(contractService.createJob(new Job()).getId(), contract.getId());
    }

    private Contract getContract() {
        return contractService.saveContract(new Contract());
    }

    private void assertTimesheetHydrated(List<TimeEntry> timeEntries, TimeEntry timeEntry, int updatedHours, List<DateTime> expectedTimes) {
        Employee employeeResult = employeeService.getUser(user.getId());
        List<TimeEntry> timeEntryResults = employeeResult.getJobs().get(0).getTimeEntries();
        assertEquals("Wrong number of timesheet entries", 7, timeEntryResults.size());
        assertEquals("Wrong hours", updatedHours, timeEntryResults
                .stream()
                .filter(te -> te.getDate().equals(timeEntry.getDate()))
                .findFirst()
                .get().getHours(), 0.0);
        timeEntryResults
                .stream()
                .forEach(timeEntry2 -> assertNotNull("No jobId", timeEntry2.getJobId()));

        // Make sure the job also has the updated entries
        final List<TimeEntry> jobTimesheetEntries = jobService.findJob(user.getJobs().get(0).getId()).getTimeEntries();
        assertEquals("Wrong number of job time entries", 7, jobTimesheetEntries.size());
        assertTrue("Job doesn't have updates", jobTimesheetEntries.containsAll(timeEntries));
        timeEntryResults
                .stream()
                .forEach(timeEntry1 -> assertTrue("Time entry not in collection", expectedTimes.contains(timeEntry1.getDate())));
    }

    private List<DateTime> getWeeksWorthOfTimeEntries() {
        List<DateTime> expectedTimes = new ArrayList<>();
        DateTime weekStart = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();
        for (int i = 0; i < 7; i++) {
            expectedTimes.add(weekStart.plusDays(i));
        }
        return expectedTimes;
    }

}