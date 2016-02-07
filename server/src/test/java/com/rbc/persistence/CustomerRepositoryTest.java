package com.rbc.persistence;

import com.rbc.TimemanagementServiceApplication;
import com.rbc.model.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Created by rbaker on 2/6/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
public class CustomerRepositoryTest {

    public static final String CONTACT_NAME = "Jonathan Bein";
    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void whenAddingCustomer_expectCustomerCreated() throws Exception {
        assertNotNull("No Customer saved", getCustomer());
    }

    @Test
    public void whenFindingCustomerByContactName_expectCustomerFound() throws Exception {
        // Assemble
        getCustomer();

        // Assert
        assertNotNull("Customer not found", customerRepository.findByContactName(CONTACT_NAME));
    }

    //---- Private Methods

    private Customer getCustomer() {
        Customer customer = new Customer();
        customer.setName("TEST");
        customer.setLastName("CUSTOMER");
        customer.setName("Z2M4");
        customer.setContactName(CONTACT_NAME);
        return customerRepository.save(customer);
    }
}