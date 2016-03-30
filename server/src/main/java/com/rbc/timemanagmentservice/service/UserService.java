package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.exception.NotFoundException;
import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import com.rbc.timemanagmentservice.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by russbaker on 2/22/16.
 */
@SuppressWarnings("unchecked")
public class UserService<U extends User> {

    protected final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, ContractRepository contractRepository) {
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    //-- User CRUD

    @Transactional(propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public U createUser(U user) {
        if(!(user instanceof Customer)){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return (U) userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("#user.username == authentication.name or hasRole('ROLE_ADMINISTRATOR')")
    public U getUser(final Integer userId){
        final U user = (U)userRepository.findOne(userId);
        if(user == null){
            throw new NotFoundException("Cannot find user with ID: " + userId);
        }
        return user;
    }

    @Transactional(readOnly = true)
    public U findByUsername(final String username) {
        return (U) userRepository.findByUsername(username).get();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @PreAuthorize("#user.username == authentication.name or hasRole('ROLE_ADMINISTRATOR')")
    public U updateUser(final U user) {
        return (U) userRepository.save(user);
    }


    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<U> findAll(Integer start, Integer end) {
        final List<Employee> employeeList = start != null && end != null ?
                (List<Employee>) userRepository.findAll()
                : (List<Employee>) userRepository.findAll();
        if (CollectionUtils.isEmpty(employeeList)) {
            throw new NotFoundException("List of employees is empty");
        }
        return (List<U>) employeeList;
    }


    //--------- Contracts

    @Transactional(propagation = Propagation.REQUIRED)
    public void addContractToUser(Integer customerId, Integer contractId) {
        final U user = (U) userRepository.findOne(customerId);
        Contract contract = contractRepository.findOne(contractId);
        if(contract == null){
            contract = contractRepository.save(contract);
        }
        user.getContracts().add(contract);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Contract> getUserContracts(Integer userId) {
        final U user = (U) userRepository.findOne(userId);
        if(user == null){
            throw new NotFoundException("Cannot find user with Id: " + userId);
        }
        return user.getContracts();
    }


}
