package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.Address;
import com.rbc.timemanagmentservice.model.Email;
import com.rbc.timemanagmentservice.model.Phone;
import com.rbc.timemanagmentservice.model.User;
import com.rbc.timemanagmentservice.service.UserService;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Arrays;

import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by russbaker on 2/22/16.
 */
@Transactional
public class ControllerTests<U extends User> {

    public static final String ROOT_URI = "/hydrated/employees/";
    protected static final DateTimeFormatter FMT = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final String PHONES = "phones";
    protected HttpMessageConverter mappingJackson2HttpMessageConverter;
    protected User user;
    protected MediaType contentType = new MediaType(MediaTypes.HAL_JSON.getType(),
            MediaTypes.HAL_JSON.getSubtype());

    @Autowired
    @Qualifier("userDetailsService")
    protected UserDetailsService userDetailsService;

    @Autowired
    private UserService<U> userService;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Autowired
    private StartupUtility startupUtility;

    protected MockMvc mockMvc;

    protected UsernamePasswordAuthenticationToken getPrincipal(String username) {

        UserDetails user = this.userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        user.getPassword(),
                        user.getAuthorities());

        return authentication;
    }

    protected MockHttpSession createMockHttpSessionForPutPost() {
        UsernamePasswordAuthenticationToken principal =
                this.getPrincipal("admin");

        SecurityContextHolder.getContext().setAuthentication(principal);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
        return session;
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    //================== TESTS

    //--------- Email

    @Test
    public void whenAddingUserEmail_expectEmailAdded() throws Exception {
        // Assemble
        user = userService.getUser(user.getId());

        Email email = new Email();
        email.setEmail("email@email.com");
        email.setEmailType(Email.EmailTypes.both);

        // Act
        this.mockMvc.perform(put(ROOT_URI + user.getId() + "/emails/", email)
                .session(createMockHttpSessionForPutPost())
                .contentType(contentType)
                .content(json(email)))
                .andDo(print())
                .andExpect(header().string("Location",
                        containsString("http://localhost/api/emails/")))
                .andExpect(status().isAccepted());
    }

    @Test
    public void whenUpdatingCustomersEmails_expectEmailsUpdated() throws Exception {
        // Assemble
        user = userService.getUser(user.getId());
        final Email email = user.getEmails().get(0);
        final String newValue = "bubba@gump.com";
        email.setEmail(newValue);
        user.addEmail(email);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put(ROOT_URI + user.getId() + "/emails/" + email.getId())
                        .session(createMockHttpSessionForPutPost())
                        .contentType(contentType)
                        .accept(contentType)
                        .content(json(email)))
                .andDo(print())
                .andExpect(header().string("Location",
                        is("http://localhost/api/emails/" + email.getId())))
                .andExpect(status().isCreated());

        user = userService.getUser(user.getId());
        assertEquals("Wrong email", email.getEmail(), user.getEmails().get(0).getEmail());

    }

    @Test
    public void whenDeleteingUserEmailsExpectEmailsDeleted() throws Exception {
        // Assemble
        user = userService.getUser(user.getId());
        final Email email = user.getEmails().get(0);

        // Act
        this.mockMvc.perform(delete(ROOT_URI + user.getId() + "/emails/" + email.getId()))
                .andExpect(status().isOk());

        // Assert
        assertTrue("No emails removed",CollectionUtils.isEmpty(userService.getUser(user.getId()).getEmails()));
    }

    //--------- Address


    @Test
    public void whenCreatingUsersAddress_expectUsersAddressCreated() throws Exception {
        // Assemble
        user = userService.getUser(user.getId());
        Address address = startupUtility.getAddress();

        createAddress(address);
    }


    @Test
    public void whenUpdatingUsersAddress_expectAddressUpdated() throws Exception {
        // Assemble
        user = userService.getUser(user.getId());
        final Address address = user.getAddress().get(0);
        final String newValue = "a new value";
        address.setStreet1(newValue);
        user.addAddress(address);

        this.mockMvc.perform(
                put(ControllerTests.ROOT_URI + user.getId() + "/address/" + address.getId())
                        .session(createMockHttpSessionForPutPost())
                        .contentType(contentType)
                        .accept(contentType)
                        .content(json(address)))
                .andDo(print())
                .andExpect(header().string("Location",
                        containsString("/api/addresses")))
                .andExpect(status().isCreated());

        user = userService.getUser(user.getId());
        assertEquals("Wrong address", address.getStreet1(), user.getAddress().get(0).getStreet1());

    }

    @Test
    public void whenDeletingUsersAddress_expectUsersAddressDeleted() throws Exception {
        // Assemble
        createAddress(startupUtility.getAddress());
        user = userService.getUser(user.getId());

        this.mockMvc.perform(
                delete(ControllerTests.ROOT_URI + user.getId() + "/address/" + user.getAddress().get(0).getId()))
                .andDo(print())
                .andExpect(status().isOk());


    }

    //--------- Phone


    @Test
    public void whenCreatingPhoneForUser_expectPhoneCreated() throws Exception {
        // Assemble
        user = userService.getUser(user.getId());
        Phone phone = new Phone();
        phone.setPhone("3035551212");

        // Act
        this.mockMvc.perform(post(ROOT_URI + "/" + user.getId() + "/" + PHONES, phone)
                .session(createMockHttpSessionForPutPost())
                .contentType(contentType)
                .content(json(phone)))
                .andDo(print())
                .andExpect(header().string("Location",
                        containsString("/api/" + PHONES))
                ).andExpect(status().isCreated());
    }

    @Test
    public void whenUpdatingUserPhone_expectPhonesUpdated() throws Exception {
        // Assemble
        user = userService.getUser(user.getId());
        final Phone phone = user.getPhones().get(0);
        final String newValue = "a new value";
        phone.setPhone(newValue);
        user.addPhone(phone);

        this.mockMvc.perform(
                put(ControllerTests.ROOT_URI + user.getId() + "/phones/" + phone.getId())
                        .session(createMockHttpSessionForPutPost())
                        .contentType(contentType)
                        .accept(contentType)
                        .content(json(phone)))
                .andDo(print())
                .andExpect(status().isCreated());

        user = userService.getUser(user.getId());
        assertEquals("Wrong phone", phone.getPhone(), user.getPhones().get(0).getPhone());
    }

    @Test
    public void whenDeletingUsersPhone_expectPhoneDeleted() throws Exception {
        // Assemble
        user = userService.getUser(user.getId());

        // Act
        this.mockMvc.perform(delete(ROOT_URI + user.getId() + "/phones/" +user.getPhones().get(0).getId()))
        .andExpect(status().isOk());

        // Assert
        assertTrue("Phones not deleted", CollectionUtils.isEmpty(userService.getUser(user.getId()).getPhones()));

    }

    //--------------- Private Methods
    private void createAddress(Address address) throws Exception {
        this.mockMvc.perform(
                post(ControllerTests.ROOT_URI + user.getId() + "/address/")
                        .session(createMockHttpSessionForPutPost())
                        .contentType(contentType)
                        .accept(contentType)
                        .content(json(address)))
                .andDo(print())
                .andExpect(header().string("Location",
                        containsString("/api/addresses")))
                .andExpect(status().isCreated());
    }
}
