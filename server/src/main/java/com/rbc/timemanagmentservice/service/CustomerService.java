package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.persistence.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.NotFoundException;
import java.util.List;

/**
 * Created by russbaker on 2/18/16.
 */
@Repository
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() {
        return (List<Customer>) customerRepository.findAll();
    }

    public Customer getCustomer(Integer customerId) {
        Customer customer = customerRepository.findOne(customerId);
        if (customer == null) {
            throw new NotFoundException("Cannot find customer with Id: " + customerId);
        }
        return customer;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public Customer addContractToCustomer(Customer customer, Contract contract) {
        final Customer cust = getCustomer(customer.getId());
        contract.setCustomer(cust);
        cust.getContracts().add(contract);
        return customerRepository.save(cust);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Customer updateCustomer(final Customer customer) {
        return customerRepository.save(customer);
    }
}
