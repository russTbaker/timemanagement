package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.service.ContractService;
import com.rbc.timemanagmentservice.service.EmployeeService;
import com.rbc.timemanagmentservice.testutils.ContractTestUtil;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


/**
 * Created by russbaker on 2/16/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TimemanagementServiceApplication.class)
@WebAppConfiguration
@Profile({"default", "test"})
public class EmployeeControllerTest extends UserControllerTests<Employee> {

    private static int counter =0;


    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StartupUtility startupUtility;

    @Autowired
    private ContractService contractService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ContractTestUtil contractTestUtil;


    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        user = startupUtility.init();
    }

    @Test
    public void whenRequestingNewEmployeeTimesheet_expectTimesheetReturned() throws Exception {
        // Assemble
        ((Employee)user).getJobs().get(0).getTimeEntries().clear();
        employeeService.updateUser((Employee) user);

        // Act
        mockMvc.perform(put(ROOT_URI_EMPLOYEES + user.getId() + "/timesheets/" + ((Employee) user).getJobs().get(0).getId())
        .session(createMockHttpSessionForPutPost()))
                .andExpect(status().isOk());

        // Assert
        user = employeeService.getUser(user.getId());
        assertFalse("No timesheet created",CollectionUtils.isEmpty(((Employee)user).getJobs().get(0).getTimeEntries()));

    }

    //-- Timesheet Entries
//    @Test
//    public void whenPuttingTimesheetEntry_expectEntryAdded() throws Exception {
//        // Assemble
//        final Timesheet timeSheet = ((Employee) user).getTimesheets().get(0);
//        TimeEntry firstTimesheetEntry = timeSheet.getTimeEntries().get(0);
//        firstTimesheetEntry.setHours(12);
//        final String url = ROOT_URI_EMPLOYEES + user.getId() + "/timesheets/" + timeSheet.getId()
//                + "/timesheetentries/" + firstTimesheetEntry.getId();
//        String timesheetEntryJson = json(firstTimesheetEntry);
//
//        // Act/Assert
//        mockMvc.perform(put(url)
//                .contentType(contentType)
//                .content(timesheetEntryJson))
//                .andDo(print())
//                .andExpect(status().isCreated())
//                .andExpect(header().string("location", "http://localhost/hydrated/employees/" + user.getId()
//                        + "/timesheets/" + timeSheet.getId() + "/timesheetentries/" + firstTimesheetEntry.getId()));
//
//    }

//    @Test
//    public void AwhenPuttingTimeSheetEntries_expectEntriesAdded() throws Exception {
//        // Assemble
//        user = startupUtility.init();
//        final Timesheet timeSheet = ((Employee) user).getTimesheets().get(0);
//        TimeEntry firstTimesheetEntry = timeSheet.getTimeEntries().get(0);
//        firstTimesheetEntry.setHours(12);
//        final String url = ROOT_URI_EMPLOYEES + user.getId() + "/timesheets/" + timeSheet.getId()
//                + "/timesheetentries";
//        timeSheet.getTimeEntries()
//                .stream()
//                .forEach(
//                        timeSheetEntry -> {
//                            timeSheetEntry.setJobId(user.getContracts().get(0).getJobs().get(0).getId());
//                        }
//                );
//
//        String timesheetEntriesJson = new ObjectMapper().writeValueAsString(timeSheet.getTimeEntries());//json(timeSheet.getTimeEntries().get(0));
//        // Act/Assert
//        mockMvc.perform(put(url)
//                .contentType(contentType)
//                .content(timesheetEntriesJson))
//                .andDo(print())
//                .andExpect(status().isAccepted())
//                .andExpect(header().string("location", "http://localhost/api/timesheets/"+timeSheet.getId() ));
//
//    }
    //----------- Jobs


    @Test
    public void whenAddingJobToEmployee_expectJobAdded() throws Exception {
        // Assemble
        Job job = new Job();
        final String test = "TEST";
        job.setName(test);
        job.setDescription(test);
        job = contractService.createJob(job);

        mockMvc.perform(post(ROOT_URI_EMPLOYEES + user.getId() + "/jobs/" + job.getId())
                .session(createMockHttpSessionForPutPost())
                .contentType(contentType)
                .content(json(job)))
                .andExpect(status().isAccepted())
                .andDo(print())
                .andExpect(header().string("Location",
                        is("http://localhost/api/jobs/" + job.getId())));

        // Assert
        List<Job> jobs = employeeService.getUser(user.getId()).getJobs();
        assertFalse("No jobs associated to employee", CollectionUtils.isEmpty(jobs));
        assertTrue("Job not part of jbs",jobs.contains(job));

    }


    @Test
    public void whenGettingAvaialbeJobs_expectJobsReturned() throws Exception {
        // Assemble
        Employee employee = startupUtility.init();

        mockMvc.perform(get(ROOT_URI_EMPLOYEES + employee.getId() + "/jobs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.jobsResources[0].job.name", is("BT")))
                .andExpect(jsonPath("$._embedded.jobsResources[0].job.description", is("Second Phase")))
                .andExpect(jsonPath("$._embedded.jobsResources[0].job.rate", is(87.5)))
                .andExpect(jsonPath("$._embedded.jobsResources[0]._links.self.href", containsString("/jobs")))
                .andExpect(jsonPath("$._links.self.href", containsString("http://localhost/hydrated/employees/"+employee.getId()+"/jobs")))
        .andDo(print());


    }
}