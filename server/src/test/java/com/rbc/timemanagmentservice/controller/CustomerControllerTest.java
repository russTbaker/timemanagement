package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.service.ContractService;
import com.rbc.timemanagmentservice.service.CustomerService;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by russbaker on 2/18/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TimemanagementServiceApplication.class)
@WebAppConfiguration
@Profile({"default", "test"})
@Transactional
public class CustomerControllerTest {
    public static final String ROOT_URI = "/hydrated/customer/";
    private MediaType contentType = new MediaType(MediaTypes.HAL_JSON.getType(),
            MediaTypes.HAL_JSON.getSubtype());

    private MockMvc mockMvc;
    final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
    final String customerResourceRoot = "$._embedded.customerResources[0]";

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ContractService contractService;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Autowired
    private StartupUtility startupUtility;

    private Customer customer;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
//        customer = assembleCustomer();
        startupUtility.init();
        customer = new Customer();
        customer.setId(1);
        customer = customerService.getCustomer(1);
    }

    @Test
    public void whenGettingAllCustomers_expectAllCustomersReturned() throws Exception {

        // Act/Assert
        mockMvc.perform(get("/hydrated/customer/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(customerResourceRoot + "customer.roles", is(customer.getRoles().name())))
                .andExpect(jsonPath(customerResourceRoot + "customer.name", is(customer.getName())))
                .andExpect(jsonPath(customerResourceRoot + "customer.firstName", is(customer.getFirstName())))
                .andExpect(jsonPath(customerResourceRoot + "customer.lastName", is(customer.getLastName())))
                .andExpect(jsonPath(customerResourceRoot + "customer.contactName", is(customer.getContactName())))
                .andExpect(jsonPath(customerResourceRoot + "contracts[0].contract.value", is(customer.getContracts().get(0).getValue())))
                .andExpect(jsonPath(customerResourceRoot + "contracts[0].contract.startDate", is(fmt.print(customer.getContracts().get(0).getStartDate()))))
                .andExpect(jsonPath(customerResourceRoot + "contracts[0].contract.endDate", is(fmt.print(customer.getContracts().get(0).getEndDate()))))
                .andExpect(jsonPath(customerResourceRoot + "contracts[0].contract.rate", is(customer.getContracts().get(0).getRate())))
                .andExpect(jsonPath(customerResourceRoot + "contracts[0].contract.terms", is(customer.getContracts().get(0).getTerms().name())))
                .andExpect(jsonPath(customerResourceRoot + "customer.emails[0].email.", is(customer.getEmails().get(0).getEmail())))
                .andExpect(jsonPath(customerResourceRoot + "customer.emails[0].emailType.", is(customer.getEmails().get(0).getEmailType().name())))
                .andExpect(jsonPath(customerResourceRoot + "address[0].address.street1", is(customer.getAddress().get(0).getStreet1())))
                .andExpect(jsonPath(customerResourceRoot + "address[0].address.city", is(customer.getAddress().get(0).getCity())))
                .andExpect(jsonPath(customerResourceRoot + "address[0].address.state", is(customer.getAddress().get(0).getState())))
                .andExpect(jsonPath(customerResourceRoot + "address[0].address.zip", is(customer.getAddress().get(0).getZip())));
        // value=87999.0, startDate=2016-02-19, endDate=2016-08-19, rate=87.5, terms=net15

    }

}