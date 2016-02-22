package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Address;
import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.model.Email;
import com.rbc.timemanagmentservice.service.CustomerService;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Autowired
    private StartupUtility startupUtility;

    @Resource
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private MockServletContext servletContext;

    @Autowired
    @Qualifier("userDetailsService")
    protected UserDetailsService userDetailsService;


    private Customer customer;

    protected UsernamePasswordAuthenticationToken getPrincipal(String username) {

        UserDetails user = this.userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        user.getPassword(),
                        user.getAuthorities());

        return authentication;
    }



    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).addFilter(this.springSecurityFilterChain).build();
        startupUtility.init();
        customer = startupUtility.getCustomerObject();
        customer = customerService.getCustomer(customer.getId());
    }



    @Test
    public void whenGettingAllCustomers_expectAllCustomersReturned() throws Exception {

        // Act/Assert
        assertCustomerCorrect();
    }

    @Test
    public void whenUpdatingCustomersEmails_expectEmailsUpdated() throws Exception {
        // Assemble
        customer = customerService.getCustomer(customer.getId());
        final Email email = customer.getEmails().get(0);
        final String newValue = "a new value";
        email.setEmail(newValue);
        customer.addEmail(email);




        this.mockMvc.perform(
                put(ROOT_URI +customer.getId() + "/email/" + email.getId())
                        .session(createMockHttpSessionForPutPost())
                .contentType(contentType)
                .accept(contentType)
                .content(json(email)))
                .andDo(print())
                .andExpect(status().isCreated());

        customer = customerService.getCustomer(customer.getId());
        assertEquals("Wrong email",email.getEmail(),customer.getEmails().get(0).getEmail());

    }




    @Test
    public void whenUpdatingCustomersAddress_expectAddressUpdated() throws Exception {
        // Assemble
        customer = customerService.getCustomer(customer.getId());
        final Address address = customer.getAddress().get(0);
        final String street1 = "a new value";
        address.setStreet1(street1);
        customer.addAddress(address);
//        customerService.updateCustomer(customer);

        mockMvc.perform(put(ROOT_URI + "/" +customer.getId() + "/address/" + address.getId())
                .session(createMockHttpSessionForPutPost())
                .contentType(contentType)
                .accept(contentType)
                .content(json(address)))
                .andDo(print())
                .andExpect(status().isCreated());

        // Act/Assert
        assertCustomerCorrect();

    }

    private void assertCustomerCorrect() throws Exception {
        mockMvc.perform(get(CustomerControllerTest.ROOT_URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath(customerResourceRoot + "customer.roles", is(customer.getRoles().name())))
                .andExpect(jsonPath(customerResourceRoot + "customer.name", is(customer.getName())))
                .andExpect(jsonPath(customerResourceRoot + "customer.firstName", is(customer.getFirstName())))
                .andExpect(jsonPath(customerResourceRoot + "customer.lastName", is(customer.getLastName())))
                .andExpect(jsonPath(customerResourceRoot + "customer.contactName", is(customer.getContactName())))
                .andExpect(jsonPath(customerResourceRoot + "customer.contracts[0].value", is(customer.getContracts().get(0).getValue())))
                .andExpect(jsonPath(customerResourceRoot + "customer.contracts[0].startDate", is(fmt.print(customer.getContracts().get(0).getStartDate()))))
                .andExpect(jsonPath(customerResourceRoot + "customer.contracts[0].endDate", is(fmt.print(customer.getContracts().get(0).getEndDate()))))
                .andExpect(jsonPath(customerResourceRoot + "customer.contracts[0].rate", is(customer.getContracts().get(0).getRate())))
                .andExpect(jsonPath(customerResourceRoot + "customer.contracts[0].terms", is(customer.getContracts().get(0).getTerms().name())))
                .andExpect(jsonPath(customerResourceRoot + "customer.emails[0].email.", is(customer.getEmails().get(0).getEmail())))
                .andExpect(jsonPath(customerResourceRoot + "customer.emails[0].emailType.", is(customer.getEmails().get(0).getEmailType().name())))
                .andExpect(jsonPath(customerResourceRoot + "customer.address[0].street1", is(customer.getAddress().get(0).getStreet1())))
                .andExpect(jsonPath(customerResourceRoot + "customer.address[0].city", is(customer.getAddress().get(0).getCity())))
                .andExpect(jsonPath(customerResourceRoot + "customer.address[0].state", is(customer.getAddress().get(0).getState())))
                .andExpect(jsonPath(customerResourceRoot + "customer.address[0].zip", is(customer.getAddress().get(0).getZip())));
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    //------------ Private Methods
    private MockHttpSession createMockHttpSessionForPutPost() {
        UsernamePasswordAuthenticationToken principal =
                this.getPrincipal("admin");

        SecurityContextHolder.getContext().setAuthentication(principal);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
        return session;
    }
}