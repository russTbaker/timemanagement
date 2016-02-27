package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.joda.time.DateTime;
import org.junit.Before;
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
 * Created by russbaker on 2/18/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
@Transactional
public class CustomerServiceTest extends UserServiceTest<Customer>{
    @Autowired
    private CustomerService customerService;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private StartupUtility startupUtility;

    private Customer customer;

    @Before
    public void setUp() {
        super.setUserService(customerService);
        customer = createUser();
    }

    @Test
    public void whenCreatingCustomer_expectCustomerCreated() throws Exception {
        // Assemble

        assertNotNull("No result returned", customerService.getCustomer(customer.getId()));

    }


    @Test
    public void whenGettingAllCustomers_expectAllCustomersReturned() throws Exception {

        // Act
        List<Customer> result = customerService.findAll();

        // Assert
        assertFalse("No customers returned", CollectionUtils.isEmpty(result));
    }

    @Test(expected = NotFoundException.class)
    public void whenFindingCustomerById_expectNotFoundException() throws Exception {
        customerService.getCustomer(0);
    }

    @Test

    public void whenCallingGetCustomer_expectCustomerReturned() throws Exception {

        // Act
        Customer result = customerService.getCustomer(customer.getId());
        assertNotNull("No customer returned", result);
        assertEquals("Wrong customer returned", customer, result);

    }

    @Test
    public void whenUpdatingCustomer_expectCustomerUpdated() throws Exception {
        // Assemble
        Customer customer = startupUtility.getCustomer();
        customer.setName("new Name");

        // Act
        Customer result = customerService.updateCustomer(customer);
        assertEquals("Customer not updated", customer.getName(), result.getName());
    }


    //-- Contracts

    @Test
    public void whenAddingNewContractToCustomer_expectContractAdded() throws Exception {
        // Assemble
        Contract contract = new Contract();
        contract.setStartDate(new DateTime());
        contract.setEndDate(new DateTime().plusMonths(6));
        contract.setValue(9000000d);
        contract.setRate(87.5);
        contract.setTerms(Contract.Terms.net45);
        contract = contractRepository.save(contract);

        // Act
        customerService.addContractToCustomer(customer.getId(), contract.getId());
        Customer result = customerService.getCustomer(customer.getId());
        assertNotNull("No result", result);
        assertFalse("No contracts added to customer", CollectionUtils.isEmpty(result.getContracts()));
    }



    // Private Methods

    public Customer createUser() {
        Customer customer = new Customer();
        customer.setName("TEST");
        customer.setFirstName("Jonathan");
        customer.setLastName("Bein");
        customer.setName("Z2M4");
        customer.setContactName("Jon");
        customer.setRoles(User.Roles.customer);
        return customerService.createCustomer(customer);
    }

}