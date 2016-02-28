package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import com.rbc.timemanagmentservice.persistence.CustomerRepository;
import com.rbc.timemanagmentservice.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by russbaker on 2/18/16.
 */
@Repository
public class CustomerService extends UserService<Customer>{

    private final CustomerRepository customerRepository;


    @Autowired
    public CustomerService(CustomerRepository customerRepository, ContractRepository contractRepository,
                           UserRepository<Customer> userRepository) {
        super(userRepository, contractRepository);
        this.customerRepository = customerRepository;
    }
}
