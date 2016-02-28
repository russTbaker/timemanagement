package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.model.Email;
import com.rbc.timemanagmentservice.service.CustomerService;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
//@Ignore("Angular spring data rest is making this obsolete")
public class CustomerControllerTest extends ControllerTests<Customer>{
    public static final String ROOT_URI = "/hydrated/customer/";
    public static final String CONTRACT_RESOURCE_ROOT = "$._embedded.contractResources[0]";
//    private MediaType contentType = new MediaType(MediaTypes.HAL_JSON.getType(),
//            MediaTypes.HAL_JSON.getSubtype());

//    private MockMvc mockMvc;
    final String customerResourceRoot = "$._embedded.customerResources[0]";


    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CustomerService customerService;



    @Autowired
    private StartupUtility startupUtility;

    @Resource
    private FilterChainProxy springSecurityFilterChain;


//    private Customer customer;


    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).addFilter(this.springSecurityFilterChain).build();
        startupUtility.init();
        user = startupUtility.getCustomerObject();
//        user = customerService.getUser(customer.getId());
    }



//    @Test
//    public void whenGettingAllCustomers_expectAllCustomersReturned() throws Exception {
//
//        // Act/Assert
//        assertCustomersCorrect();
//    }

//    @Test
//    public void whenGettingCustomer_expectCustomerReturned() throws Exception {
//        // Act/Assert
//        assertCustomerCorrect();
//
//    }

    @Test
    public void whenUpdatingCustomersEmails_expectEmailsUpdated() throws Exception {
        // Assemble
        user = customerService.getUser(user.getId());
        final Email email = user.getEmails().get(0);
        final String newValue = "a new value";
        email.setEmail(newValue);
        user.addEmail(email);

        this.mockMvc.perform(
                put(ROOT_URI +user.getId() + "/email/" + email.getId())
                        .session(createMockHttpSessionForPutPost())
                .contentType(contentType)
                .accept(contentType)
                .content(json(email)))
                .andDo(print())
                .andExpect(status().isCreated());

        user = customerService.getUser(user.getId());
        assertEquals("Wrong email",email.getEmail(),user.getEmails().get(0).getEmail());

    }


//    @Test
//    public void whenUpdatingCustomersAddress_expectAddressUpdated() throws Exception {
//        // Assemble
//        user = customerService.getUser(user.getId());
//        final Address address = user.getAddress().get(0);
//        final String street1 = "a new value";
//        address.setStreet1(street1);
//        user.addAddress(address);
//
//        mockMvc.perform(put(ROOT_URI + "/" +user.getId() + "/address/" + address.getId())
//                .session(createMockHttpSessionForPutPost())
//                .contentType(contentType)
//                .accept(contentType)
//                .content(json(address)))
//                .andDo(print())
//                .andExpect(status().isCreated());
//
//        // Act/Assert
//        assertCustomersCorrect();
//
//    }

//    @Test
//    public void whenUpdatingCustomersPhone_expectPhoneUpdated() throws Exception {
//        // Assemble
//        user = customerService.getUser(user.getId());
//        final Phone phone = user.getPhones().get(0);
//        final String street1 = "a new value";
//        phone.setPhone(street1);
//        user.addPhone(phone);
//
//        mockMvc.perform(put(ROOT_URI  +user.getId() + "/phones/" + phone.getId())
//                .session(createMockHttpSessionForPutPost())
//                .contentType(contentType)
//                .accept(contentType)
//                .content(json(phone)))
//                .andDo(print())
//                .andExpect(status().isCreated());
//
//        // Act/Assert
//        assertCustomersCorrect();
//
//    }

    @Test
    public void whenFindingContract_expectContractReturned() throws Exception {
        // Assert
        user = customerService.getUser(user.getId());

        // Act/Assert
        mockMvc.perform(get(ROOT_URI + +user.getId() + "/contract/" + user.getContracts().get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(CONTRACT_RESOURCE_ROOT + ".contract.value",is(user.getContracts().get(0).getValue())))
                .andExpect(jsonPath(CONTRACT_RESOURCE_ROOT + ".contract.startDate", is(FMT.print(user.getContracts().get(0).getStartDate()))))
                .andExpect(jsonPath(CONTRACT_RESOURCE_ROOT + ".contract.endDate", is(FMT.print(user.getContracts().get(0).getEndDate()))))
                .andExpect(jsonPath(CONTRACT_RESOURCE_ROOT + ".contract.terms", is(user.getContracts().get(0).getTerms().name())))
                .andDo(print());

    }
//----------- Private Methods

//    private void assertCustomerCorrect() throws Exception {
//        mockMvc.perform(get(CustomerControllerTest.ROOT_URI + user.getId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath(customerResourceRoot + "user.roles", is(user.getRoles().name())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.name", is(customer.getName())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.firstName", is(customer.getFirstName())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.lastName", is(customer.getLastName())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.contactName", is(customer.getContactName())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.contracts[0].value", is(customer.getContracts().get(0).getValue())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.contracts[0].startDate", is(FMT.print(customer.getContracts().get(0).getStartDate()))))
//                .andExpect(jsonPath(customerResourceRoot + "customer.contracts[0].endDate", is(FMT.print(customer.getContracts().get(0).getEndDate()))))
//                .andExpect(jsonPath(customerResourceRoot + "customer.contracts[0].terms", is(customer.getContracts().get(0).getTerms().name())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.emails[0].email.", is(customer.getEmails().get(0).getEmail())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.emails[0].emailType.", is(customer.getEmails().get(0).getEmailType().name())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.address[0].street1", is(customer.getAddress().get(0).getStreet1())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.address[0].city", is(customer.getAddress().get(0).getCity())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.address[0].state", is(customer.getAddress().get(0).getState())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.address[0].zip", is(customer.getAddress().get(0).getZip())));
//    }
//
//    private void assertCustomersCorrect() throws Exception {
//        mockMvc.perform(get(CustomerControllerTest.ROOT_URI))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath(customerResourceRoot + "customer.roles", is(customer.getRoles().name())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.name", is(customer.getName())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.firstName", is(customer.getFirstName())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.lastName", is(customer.getLastName())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.contactName", is(customer.getContactName())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.contracts[0].value", is(customer.getContracts().get(0).getValue())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.contracts[0].startDate", is(FMT.print(customer.getContracts().get(0).getStartDate()))))
//                .andExpect(jsonPath(customerResourceRoot + "customer.contracts[0].endDate", is(FMT.print(customer.getContracts().get(0).getEndDate()))))
//                .andExpect(jsonPath(customerResourceRoot + "customer.contracts[0].terms", is(customer.getContracts().get(0).getTerms().name())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.emails[0].email.", is(customer.getEmails().get(0).getEmail())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.emails[0].emailType.", is(customer.getEmails().get(0).getEmailType().name())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.address[0].street1", is(customer.getAddress().get(0).getStreet1())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.address[0].city", is(customer.getAddress().get(0).getCity())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.address[0].state", is(customer.getAddress().get(0).getState())))
//                .andExpect(jsonPath(customerResourceRoot + "customer.address[0].zip", is(customer.getAddress().get(0).getZip())));
//    }
}