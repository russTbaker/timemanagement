package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.model.Roles;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;

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

    @Before
    public void setUp() {
        super.setUserService(customerService);
        super.setUp();
    }


    //-- Contracts

    @Test
    public void whenAddingNewContractToCustomer_expectContractAdded() throws Exception {
        // Assemble
        Contract contract = new Contract();
        contract.setStartDate(new DateTime());
        contract.setEndDate(new DateTime().plusMonths(6));
        contract.setValue(9000000d);
        contract.setTerms(Contract.Terms.net45);
        contract = contractRepository.save(contract);

        // Act
        customerService.addContractToUser(user.getId(), contract.getId());
        Customer result = customerService.getUser(user.getId());
        assertNotNull("No result", result);
        assertFalse("No contracts added to user", CollectionUtils.isEmpty(result.getContracts()));
    }

    @Test
    public void whenUpdatingContractForCustomer_expectContractUpdated() throws Exception {


    }
// Private Methods

    public Customer createUser() {
        Customer customer = new Customer();
        customer.setFirstName("Jonathan");
        customer.setLastName("Bein");
        customer.setDba("Z2M4");
        Roles customerRole = new Roles();
        customerRole.setRole(Roles.Role.customer);
        customer.getRoles().add(customerRole);
        return customerService.createUser(customer);
    }

}