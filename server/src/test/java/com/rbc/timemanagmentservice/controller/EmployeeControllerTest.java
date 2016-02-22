package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.TimeSheet;
import com.rbc.timemanagmentservice.model.TimeSheetEntry;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


/**
 * Created by russbaker on 2/16/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TimemanagementServiceApplication.class)
@WebAppConfiguration
@Profile({"default", "test"})
public class EmployeeControllerTest {
    public static final String ROOT_URI = "/hydrated/employee/";
    private MediaType contentType = new MediaType(MediaTypes.HAL_JSON.getType(),
            MediaTypes.HAL_JSON.getSubtype());

    private MockMvc mockMvc;
    private String realContentType = "application/hal+json";
    final String employeeResourceRoot = "$._embedded.employeeResources[0]";
    final String timesheetResourceRoot = "$._embedded.timeSheetResources[0]";
    final String timesheetEntryResourceRoot = "$._embedded.timeSheetEntryResources[0]";
    final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");


    private HttpMessageConverter mappingJackson2HttpMessageConverter;


    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StartupUtility startupUtility;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    private Employee employee;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        employee = startupUtility.init();
    }

    @Test
    public void whenGettingAllEmployees_expectAllEmployeesReturned() throws Exception {
        // Assemble


        // Act/Assert
        mockMvc.perform(get(ROOT_URI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
//                .andExpect(jsonPath("$._embedded.employeeResources", hasSize(1)))
                .andExpect(jsonPath(employeeResourceRoot + "employee.roles", is(employee.getRoles().name())))
                .andExpect(jsonPath(employeeResourceRoot + "employee.emails[0].email", is(employee.getEmails().get(0).getEmail())))
                .andExpect(jsonPath(employeeResourceRoot + "employee.emails[0].emailType", is(employee.getEmails().get(0).getEmailType().name())))
                .andExpect(jsonPath(employeeResourceRoot + ".timeSheets[0].timeSheetEntries[0].timeSheetEntry.hours", is(employee.getTimesheets()
                        .get(0).getTimeSheetEntries().get(0).getHours())));;

    }

    @Test
    public void whenCallingGetEmployee_expectEmployeeReturned() throws Exception {
        // Assemble
        Integer employeeId = employee.getId();

        // Act
        mockMvc.perform(get(ROOT_URI + "/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(realContentType))
                .andExpect(jsonPath(employeeResourceRoot + ".employee.roles", is(employee.getRoles().name())))
                .andExpect(jsonPath(employeeResourceRoot + ".employee.emails[0].email", is(employee.getEmails().get(0).getEmail())))
                .andExpect(jsonPath(employeeResourceRoot + "employee.emails[0].emailType", is(employee.getEmails().get(0).getEmailType().name())))
                .andExpect(jsonPath(employeeResourceRoot + ".timeSheets[0].timeSheetEntries[0].timeSheetEntry.hours", is(employee.getTimesheets()
                        .get(0).getTimeSheetEntries().get(0).getHours())));

    }


    @Test
    public void whenCallingGetEmployee_expectNotFound() throws Exception {
        mockMvc.perform(get(ROOT_URI + "/" + 0))
                .andExpect(status().isNotFound());

    }

    @Test
    public void whenGettingEmployeesRecentTimeSheet_expectMostRecentReturned() throws Exception {

        // Act/Assert
        mockMvc.perform(get(ROOT_URI + employee.getId() + "/timesheet" ))
                .andExpect(status().isOk())
                .andExpect(jsonPath(timesheetResourceRoot + ".timeSheetEntries[0].timeSheetEntry.date",
                        is(fmt.print(employee.getTimesheets().get(0).getTimeSheetEntries().get(0).getDate()))))
                .andExpect(jsonPath(timesheetResourceRoot + ".timeSheetEntries[0].timeSheetEntry.hours",
                        is(employee.getTimesheets().get(0).getTimeSheetEntries().get(0).getHours())))
                .andExpect(jsonPath(timesheetResourceRoot + ".timeSheetEntries[0].timeSheetEntry.contract",
                        is(employee.getTimesheets().get(0).getTimeSheetEntries().get(0).getContractId())));

    }

    @Test
    public void whenPuttingTimeSheetEntry_expectEntryAdded() throws Exception {
        // Assemble
        final TimeSheet timeSheet = employee.getTimesheets().get(0);
        TimeSheetEntry firstTimeSheetEntry = timeSheet.getTimeSheetEntries().get(0);
        firstTimeSheetEntry.setHours(12);
        final String url = ROOT_URI + employee.getId() + "/timesheet/" + timeSheet.getId()
                + "/timesheetentries/" + firstTimeSheetEntry.getId();
        String timesheetEntryJson = json(firstTimeSheetEntry);

        // Act/Assert
        mockMvc.perform(put(url)
                .contentType(contentType)
                .content(timesheetEntryJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost/hydrated/employee/"+employee.getId()
                        +"/timesheet/"+timeSheet.getId()+"/timesheetentries/" + firstTimeSheetEntry.getId()));

    }

    @Test
    public void whenGettingAllTimeEntries_expectEntrisReturned() throws Exception {
        // Act/Assert
        mockMvc.perform(get(ROOT_URI + employee.getId() + "/timesheet/" + employee.getTimesheets().get(0).getId()
        + "/timesheetentries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(timesheetEntryResourceRoot + ".timeSheetEntry.contract",
                        is(employee.getTimesheets().get(0).getTimeSheetEntries().get(0).getContractId())))
                .andExpect(jsonPath(timesheetEntryResourceRoot + ".timeSheetEntry.hours",
                        is(employee.getTimesheets().get(0).getTimeSheetEntries().get(0).getHours())))
                .andExpect(jsonPath(timesheetEntryResourceRoot + ".timeSheetEntry.date",
                        is(fmt.print(employee.getTimesheets().get(0).getTimeSheetEntries().get(0).getDate()))));
    }



    //------- Private Methods



    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}