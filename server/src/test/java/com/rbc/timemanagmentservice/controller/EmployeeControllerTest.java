package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.persistence.EmployeeRepository;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;


    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

//    @Test
//    public void whenCallingGetAllEmployees_expectNoneFound() throws Exception {
//        mockMvc.perform(get(ROOT_URI)
//                .contentType(contentType))
//                .andExpect(status().isNotFound());
//    }

    @Test
    public void whenCallingGetEmployee_expectEmployeeReturned() throws Exception {
        // Assemble
        Employee employee = getEmployeeWithTimeSheet();

        // Act
        Integer employeeId = employee.getId();
        mockMvc.perform(get(ROOT_URI + "/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))//"application/hal+json"))
                .andExpect(jsonPath("$.id", is(employeeId.intValue())))
                .andExpect(jsonPath("$.emails[0].email", is(employee.getEmails().get(0).getEmail())))
                .andExpect(jsonPath("$.timesheets[0].timeSheetEntries[0].hours", is(employee.getTimesheets()
                        .get(0).getTimeSheetEntries().get(0).getHours())));

    }



    @Test
    public void whenCallingGetEmployee_expectNotFound() throws Exception {
        mockMvc.perform(get(ROOT_URI + "/" + 0))
                .andExpect(status().isNotFound());

    }

    @Test
    public void whenGettingEmployeesRecentTimeSheet_expectMostRecentReturned() throws Exception {
        // Assemble
        Employee employee = getEmployeeWithTimeSheet();

        mockMvc.perform(get(ROOT_URI + employee.getId() + "/timesheet" ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timeSheetEntries[0].hours", is(employee.getTimesheets()
                        .get(0).getTimeSheetEntries().get(0).getHours())));;

    }


    private Employee getEmployeeWithTimeSheet() {
        Employee employee = StartupUtility.getEmployee();
        employee.getTimesheets().add(StartupUtility.getTimeSheet(employee,StartupUtility.getContract()));
        employeeRepository.save(employee);
        return employee;
    }
}