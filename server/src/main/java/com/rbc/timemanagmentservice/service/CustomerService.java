package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
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
    private final ContractRepository contractRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, ContractRepository contractRepository) {
        this.customerRepository = customerRepository;
        this.contractRepository = contractRepository;
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
    public void addContractToCustomer(Integer customerId, Integer contractId) {
//        final Customer cust = getCustomer(customer.getId());
//        contract.getUsers().add(cust);
//        cust.addContract(contract);
//        contractRepository.save(contract);
//        return customerRepository.save(cust);
        final Customer customer = customerRepository.findOne(customerId);
        final Contract contract = contractRepository.findOne(contractId);
                contract.addUser(customer);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Customer updateCustomer(final Customer customer) {
        return customerRepository.save(customer);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
}
