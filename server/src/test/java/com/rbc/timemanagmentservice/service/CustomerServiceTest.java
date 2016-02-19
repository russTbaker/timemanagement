package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.glassfish.jersey.internal.inject.Custom;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.NotFoundException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by russbaker on 2/18/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
public class CustomerServiceTest {
    @Autowired
    private CustomerService customerService;

    @Test
    public void whenGettingAllCustomers_expectAllCustomersReturned() throws Exception {
        // Assemble
        Customer customer = StartupUtility.getCustomer();

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
    @Transactional
    public void whenCallingGetCustomer_expectCustomerReturned() throws Exception {
        // Assemble
        Customer customer = StartupUtility.getCustomer();

        // Act
        Customer result = customerService.getCustomer(customer.getId());
        assertNotNull("No customer returned",result);
        assertEquals("Wrong customer returned",customer,result);

    }

    @Test
    public void whenAddingNewContractToCustomer_expectContractAdded() throws Exception{
        // Assemble
        Customer customer = StartupUtility.getCustomer();
        Contract contract = new Contract();
        contract.setStartDate(new DateTime());
        contract.setEndDate(new DateTime().plusMonths(6));
        contract.setValue(9000000d);
        contract.setRate(87.5);
        contract.setTerms(Contract.Terms.net45);

        // Act
        Customer result = customerService.addContractToCustomer(customer,contract);
        assertNotNull("No result",result);
        assertFalse("No contracts added to customer",CollectionUtils.isEmpty(result.getContracts()));
    }

    @Test
    public void whenUpdatingCustomer_expectCustomerUpdated() throws Exception {
        // Assemble
        Customer customer = StartupUtility.getCustomer();
        customer.setName("new Name");

        // Act
        Customer result = customerService.updateCustomer(customer);
        assertEquals("Customer not updated",customer.getName(),result.getName());
    }
}