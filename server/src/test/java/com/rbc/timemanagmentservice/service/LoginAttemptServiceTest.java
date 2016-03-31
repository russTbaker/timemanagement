package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Administrator;
import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.User;
import com.rbc.timemanagmentservice.persistence.AdministratorRepository;
import com.rbc.timemanagmentservice.persistence.EmployeeRepository;
import com.rbc.timemanagmentservice.persistence.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Created by russbaker on 3/30/16.
 */
@RunWith(PowerMockRunner.class)
public class LoginAttemptServiceTest {
     Map<String,String> usernames = new HashMap<>();

    public static final String USERNAME = "Username";

    @InjectMocks
    private LoginAttemptServiceJPAImpl loginAttemptService = new LoginAttemptServiceJPAImpl();

    @Mock
    private EmployeeRepository mockEmployeeRepository;

    @Mock
    private AdministratorRepository mockAdministratorRepository;

    @Mock
    private Employee mockEmployee;

    @Mock
    private Administrator mockAdministrator;

    @Mock
    private Optional mockOptional;


    @Before
    public void setUp() {
        String employee = "employee";
        usernames.put(employee,"Bubba");
        String administrator = "administrator";
        usernames.put(administrator,"Forest");
        assembleUser(mockEmployee,mockEmployeeRepository, employee, mockAdministratorRepository);
        assembleUser(mockAdministrator,mockAdministratorRepository, administrator, mockEmployeeRepository);
        loginAttemptService.loginAttemptsLimit = 3;
    }
    
    //--- Employee

    @Test
    public void whenIncrementingEmployeeLoginFailures_expectLoginFailuresIncremented() throws Exception {
        // Assemble
        failLogin(mockEmployee, 1);
    }


    @Test
    public void whenLoginSuccessEmployee_expectLoginAttemtpsZero() throws Exception {
        sucessLogin(mockEmployee);
    }



    @Test
    public void whenEmployeeExceedsLoginAttempts_expectUserLockedOut() throws Exception {
        lockout(mockEmployee);


    }



    //---------- Administrator


    @Test
    public void whenIncrementingAdministratorLoginFailures_expectLoginFailuresIncremented() throws Exception {
        // Assemble
        failLogin(mockAdministrator, 1);
    }


    @Test
    public void whenLoginSuccessAdministrator_expectLoginAttemtpsZero() throws Exception {
        // Assemble
        sucessLogin(mockAdministrator);
    }

    @Test
    public void whenAdministratorExceedsLoginAttempts_expectUserLockedOut() throws Exception {
        // Assemble
        lockout(mockAdministrator);

    }

    //--------- Private Methods

    private void sucessLogin(User mockUser) {
        // Assemble
        failLogin(mockUser, 1);
        failLogin(mockUser, 2);
        verify(mockUser,atLeastOnce()).setLoginAttempts(any());
        // Act
        loginAttemptService.success(mockEmployee.getUsername());

        // Assert
        assertEquals("Login attempts not cleared", 0, mockEmployee.getLoginAttempts(), 0.0);
        verifySaveCalled(times(3));
    }

    private void lockout(User mockUser) {
        // Assemble
        failLogin(mockUser, 1);
        failLogin(mockUser, 2);
        failLogin(mockUser, 3);


        // Act
        loginAttemptService.incrementLoginFailure(mockUser.getUsername());

        // Assert
        verify(mockUser, times(1)).setAccountLockedDateTime(any());
        verifySaveCalled(times(1));
    }

    private <U extends User> void failLogin(U mockUser, int expected) {
        // Act
        loginAttemptService.incrementLoginFailure(mockUser.getUsername());

        // Assert
        verify(mockUser,atLeastOnce()).setLoginAttempts(anyInt());
    }

    private <R extends UserRepository> void assembleUser(User mockUser, R mockRepository, String type, R unusedRepository) {
        when(mockUser.getUsername()).thenReturn(usernames.get(type));
        when(mockRepository.findByUsername(usernames.get(type))).thenReturn(Optional.of(mockUser));

        when(mockOptional.isPresent()).thenReturn(false);
        when(unusedRepository.findByUsername(usernames.get(type))).thenReturn(mockOptional);

        when(mockUser.getLoginAttempts()).thenCallRealMethod();
        doCallRealMethod().when(mockUser).setLoginAttempts(anyInt());
        mockUser.setLoginAttempts(0);
    }

    private void verifySaveCalled(VerificationMode times) {
        verify(mockEmployeeRepository, times);
    }


}