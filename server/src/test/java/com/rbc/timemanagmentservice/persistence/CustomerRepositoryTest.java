package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

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


    //---- Private Methods

    private Customer getCustomer() {
        Customer customer = new Customer();
        customer.setLastName("CUSTOMER");
        customer.setDba("Z2M4");
        return customerRepository.save(customer);
    }
}