package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.exception.NotFoundException;
import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static junit.framework.TestCase.*;

/**
 * Created by russbaker on 2/27/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
@SuppressWarnings("unchecked")
public abstract class UserServiceTest<U extends User> {
    public static final String EMPLOYEE_PASSWORD = "password";
    protected U user;

    @Autowired
    private StartupUtility startupUtility;

    private  UserService<U> userService;


    @Autowired
    protected PasswordEncoder passwordEncoder;

    public void setUserService(UserService<U> userService) {
        this.userService = userService;
    }


    public void setUp(){
        user = createUser();
    }

    @After
    public void tearDown(){
//        userService.deleteUser(user.getId());
    }

    // Create
    @Test
    public void whenCreatingCustomer_expectUserCreated() throws Exception {
        // Assemble

        assertNotNull("No result returned", userService.getUser(user.getId()));

    }

    // Read
    @Test
    public void whenCallingGetUser_expectUserReturned() throws Exception {

        // Act
        U result = userService.getUser(user.getId());

        // Assert
        assertNotNull("No user returned", result);
        assertEquals("Wrong user returned", user, result);
    }

    @Test(expected = NotFoundException.class)
    public void whenFindingUserById_expectNotFoundException() throws Exception {
        userService.getUser(0);
    }


    // Read All
    @Test
    public void whenGettingAllUsers_expectAllUsersReturned() throws Exception {

        // Act
        List<U> result = userService.findAll(null,null);

        // Assert
        assertFalse("No users returned", CollectionUtils.isEmpty(result));
    }

    // Update

    @Test
    public void whenUpdatingUser_expectUserUpdated() throws Exception {
        // Assemble
        user.setDba("new Name");

        // Act
        U result = userService.updateUser(user);
        assertEquals("Customer not updated", user.getDba(), result.getDba());
        if( !(result instanceof Customer)){
            assertTrue("Passwords don't match",passwordEncoder.matches(EMPLOYEE_PASSWORD,result.getPassword()));
        }
    }


//    @Test(expected = NotFoundException.class)
//    public void whenDeletingUser_expectUserDeleted() throws Exception {
//
////        // Act
//        userService.deleteUser(user.getId());
//
//        // Assert
//        userService.getUser(user.getId());
//
//    }

    protected abstract U createUser();






    protected U addAddressToUser() {
        // Assemble
        Address address = startupUtility.getAddress();

        // Act
//        assertNotNull("Address not added.",userService.addAddressToUser(user.getId(),address));
        return  userService.getUser(user.getId());
    }

    protected U addPhoneToUser() {
        Phone phone = startupUtility.getPhone();

        // Act
//        userService.addPhoneToUser(user.getId(),phone);
        return  userService.getUser(user.getId());
    }

    protected U addEmailToUser() {
        final List<U> allUsers = userService.findAll(null, null);
        Email email = startupUtility.getEmail("username");

        // Act
//        userService.addEmailToUser(user.getId(),email);
        return  userService.getUser(user.getId());
    }

}