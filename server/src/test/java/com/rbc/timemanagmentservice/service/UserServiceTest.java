package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Address;
import com.rbc.timemanagmentservice.model.Email;
import com.rbc.timemanagmentservice.model.Phone;
import com.rbc.timemanagmentservice.model.User;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.NotFoundException;
import java.util.List;

import static junit.framework.TestCase.*;

/**
 * Created by russbaker on 2/27/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
@SuppressWarnings("unchecked")
public abstract class UserServiceTest<U extends User> {
    protected U user;

    @Autowired
    private StartupUtility startupUtility;

    private  UserService<U> userService;

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
    }


    @Test(expected = NotFoundException.class)
    public void whenDeletingUser_expectUserDeleted() throws Exception {

//        // Act
        userService.deleteUser(user.getId());

        // Assert
        userService.getUser(user.getId());

    }

    protected abstract U createUser();



    //---------- Address

    @Test
    public void whenAddingAddressToUserExpectAddressAdded() throws Exception {
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
        assertTrue("Email not removed",CollectionUtils.isEmpty(userService.getUser(user.getId()).getEmails()));

    }


    protected U addAddressToUser() {
        // Assemble
        Address address = startupUtility.getAddress();

        // Act
        assertNotNull("Address not added.",userService.addAddressToUser(user.getId(),address));
        return  userService.getUser(user.getId());
    }

    protected U addPhoneToUser() {
        Phone phone = startupUtility.getPhone();

        // Act
        userService.addPhoneToUser(user.getId(),phone);
        return  userService.getUser(user.getId());
    }

    protected U addEmailToUser() {
        final List<U> allUsers = userService.findAll(null, null);
        Email email = startupUtility.getEmail("username");

        // Act
        userService.addEmailToUser(user.getId(),email);
        return  userService.getUser(user.getId());
    }

}