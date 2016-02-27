package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

/**
 * Created by russbaker on 2/27/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
@Transactional
@SuppressWarnings("unchecked")
public abstract class UserServiceTest<U extends User> {
    @Autowired
    private StartupUtility startupUtility;

    private  UserService<U> userService;

    public void setUserService(UserService<U> userService) {
        this.userService = userService;
    }

    public abstract U createUser();
    //---------- Address

    @Test
    public void whenAddingAddressToEmployeeExpectAddressAdded() throws Exception {
        User user = addAddressToUser();


        // Assert
        User result = userService.getUser(user.getId());
        final List<Address> addresses = result.getAddress();
        assertFalse("No addresses added", CollectionUtils.isEmpty(addresses));
        for(Address addr:addresses){
            assertNotNull("No Id",addr.getId());
        }

    }

    @Test
    public void whenDeletingAddressFromUser_expectAddressDeleted() throws Exception {
        // Assemble
        User user = addAddressToUser();

        // Act
        userService.removeAddressFromUser(user.getId(),user.getAddress().get(0).getId());

        // Assert
        assertTrue("Address not removed",CollectionUtils.isEmpty(userService.getUser(user.getId()).getAddress()));

    }

    //---------- Phones

    @Test
    public void whenAddingPhoneToUser_expectPhoneAdded() throws Exception {
        // Assemble
        User user = addPhoneToUser();

        // Assert
        assertFalse("No phones added",CollectionUtils.isEmpty(userService.getUser(user.getId()).getPhones()));

    }

    @Test
    public void whenDeletingPhonesFromUser_expectPhoneDeleted() throws Exception {
        // Assemble
        User user = addPhoneToUser();

        // Act
        userService.removePhoneFromUser(user.getId(),user.getPhones().get(0).getId());

        // Assert
        assertTrue("Phone not removed",CollectionUtils.isEmpty(userService.getUser(user.getId()).getPhones()));

    }

    //-------------- Emails

    @Test
    public void whenAddingEmailToUser_expectEmailAdded() throws Exception {
        // Assemble
        User user = addEmailToUser();

        // Assert
        assertFalse("No emails added",CollectionUtils.isEmpty(userService.getUser(user.getId()).getEmails()));

    }

    @Test
    public void whenDeletingEmailFromUser_expectEmailDeleted() throws Exception {
        // Assemble
        User user = addEmailToUser();

        // Act
        userService.removeEmailFromUser(user.getId(),user.getEmails().get(0).getId());

        // Assert
        assertTrue("Email not removed",CollectionUtils.isEmpty(userService.getUser(user.getId()).getPhones()));

    }


    protected U addAddressToUser() {
        // Assemble
        User user = createUser();
        Address address = startupUtility.getAddress();

        // Act
        userService.addAddressToUser(user.getId(),address);
        return (U) user;
    }

    protected U addPhoneToUser() {
        User user = createUser();
        Phone phone = startupUtility.getPhone();

        // Act
        userService.addPhoneToUser(user.getId(),phone);
        return (U) user;
    }

    protected U addEmailToUser() {
        User user = createUser();
        Email email = startupUtility.getEmail("username");

        // Act
        userService.addEmailToUser(user.getId(),email);
        return (U) user;
    }

}