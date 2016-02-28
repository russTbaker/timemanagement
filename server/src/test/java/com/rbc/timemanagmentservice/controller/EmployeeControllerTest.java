package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.TimeSheetEntry;
import com.rbc.timemanagmentservice.model.Timesheet;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


/**
 * Created by russbaker on 2/16/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TimemanagementServiceApplication.class)
@WebAppConfiguration
@Profile({"default", "test"})
@Transactional
public class EmployeeControllerTest extends UserControllerTests<Employee> {


    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StartupUtility startupUtility;




    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        user = startupUtility.init();
    }


    @Test
    public void whenPuttingTimeSheetEntry_expectEntryAdded() throws Exception {
        // Assemble
        final Timesheet timeSheet = ((Employee)user).getTimesheets().get(0);
        TimeSheetEntry firstTimeSheetEntry = timeSheet.getTimeSheetEntries().get(0);
        firstTimeSheetEntry.setHours(12);
        final String url = ROOT_URI_EMPLOYEES + user.getId() + "/timesheet/" + timeSheet.getId()
                + "/timesheetentries/" + firstTimeSheetEntry.getId();
        String timesheetEntryJson = json(firstTimeSheetEntry);

        // Act/Assert
        mockMvc.perform(put(url)
                .contentType(contentType)
                .content(timesheetEntryJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost/hydrated/employees/"+user.getId()
                        +"/timesheet/"+timeSheet.getId()+"/timesheetentries/" + firstTimeSheetEntry.getId()));

    }

    //----------- Contracts

}