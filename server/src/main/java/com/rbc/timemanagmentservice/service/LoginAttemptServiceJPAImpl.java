package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Administrator;
import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.User;
import com.rbc.timemanagmentservice.persistence.AdministratorRepository;
import com.rbc.timemanagmentservice.persistence.EmployeeRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by russbaker on 3/30/16.
 */
@Service
public class LoginAttemptServiceJPAImpl implements LoginAttemptService {

    private EmployeeRepository employeeRepository;

    private AdministratorRepository administratorRepository;

    @Value("${login.attempts}")
    protected Integer loginAttemptsLimit;

    @Autowired
    public void setEmployeeRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Autowired
    public void setAdministratorRepository(AdministratorRepository administratorRepository) {
        this.administratorRepository = administratorRepository;
    }

    @Override
    public void incrementLoginFailure(String userName) {
        User user = getUser(userName);
        if(user.getLoginAttempts() >= loginAttemptsLimit){
            lockUser(userName);
        } else {
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            saveUser(user);
        }


    }



    @Override
    public void success(String userName) {
        final User user = getUser(userName);
        user.setLoginAttempts(0);
        user.setAccountLockedDateTime(null);
        saveUser(user);
    }

    //--------- Private Methods

    private void lockUser(String userName) {
        final User user = getUser(userName);
        user.setAccountLockedDateTime(new DateTime());
        saveUser(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void saveUser(User user) {
        if (user instanceof Employee) {
            employeeRepository.save((Employee) user);
        } else {
            administratorRepository.save((Administrator) user);
        }
    }

    @Transactional(readOnly = true)
    private User getUser(String userName) {
        Optional<?> retVal = employeeRepository.findByUsername(userName);
        if(retVal.isPresent()){
            return (User) retVal.get();
        } else {
            return administratorRepository.findByUsername(userName).get();
        }
    }

}
