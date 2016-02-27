package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Address;
import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import com.rbc.timemanagmentservice.persistence.CustomerRepository;
import com.rbc.timemanagmentservice.persistence.UserRepository;
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
public class CustomerService extends UserService{

    private final CustomerRepository customerRepository;
    private final ContractRepository contractRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, ContractRepository contractRepository,
                           UserRepository<Customer> userRepository) {
        super(userRepository);
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
    
    // TODO: Delete customer
    

    @Transactional(propagation = Propagation.REQUIRED)
    public void addAddressToCustomer(final Integer customerId, final Address address){
        final Customer employee = customerRepository.findOne(customerId);
        employee.addAddress(address);
        customerRepository.save(employee);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void removeAddressFromCustomer(Integer customerId, Integer addressId) {
        final Customer employee = customerRepository.findOne(customerId);
        final List<Address> addresses = employee.getAddress();
        addresses.remove(addresses.stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .get());
        customerRepository.save(employee);
    }
    
    //--------- Contracts

    @Transactional(propagation = Propagation.REQUIRED)
    public void addContractToCustomer(Integer customerId, Integer contractId) {
        final Customer customer = customerRepository.findOne(customerId);
        Contract contract = contractRepository.findOne(contractId);
        if(contract == null){
            contract = contractRepository.save(contract);
        }
        customer.addContract(contract);
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
