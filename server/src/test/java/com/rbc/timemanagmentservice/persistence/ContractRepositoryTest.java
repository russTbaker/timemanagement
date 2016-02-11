package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.model.User;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by rbaker on 2/6/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
public class ContractRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ContractRepository contractRepository;

    public static final String CONTACT_NAME = "Jonathan Bein";

    @Test
    public void whenInsertingContract_expectContractInserted() throws Exception {
        assertNotNull("No contract inserted",getContract());
    }

    @Test
    public void whenFindingContractByCustomerName_expectContractFound() throws Exception {
        // Assemble
        getContract();

        // Assert
        assertNotNull("No contract returned",contractRepository.findByCustomerName(CONTACT_NAME));

    }

    private Customer getCustomer() {
        Customer customer = new Customer();
        customer.setName("TEST");
        customer.setLastName("CUSTOMER");
        customer.setName("Z2M4");
        customer.setContactName(CONTACT_NAME);
        customer.setRoles(User.Roles.customer);
        return customerRepository.save(customer);
    }
    private Contract getContract() {
        Contract contract = new Contract();
        contract.setStartDate(new DateTime());
        contract.setEndDate(new DateTime().plusMonths(6));
        contract.setRate(87.5);
        contract.setTerms(Contract.Terms.net15);
        contract.setCustomer(getCustomer());
        contract.setValue(87999D);
        return contractRepository.save(contract);
    }

}