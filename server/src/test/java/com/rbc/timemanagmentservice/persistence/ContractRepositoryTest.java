package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.model.Roles;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

/**
 * Created by rbaker on 2/6/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
@Profile(value = {"default","test"})
public class ContractRepositoryTest {

    @Autowired
    private UserRepository<Customer> userRepository;

    @Autowired
    private ContractRepository contractRepository;

    public static final String CONTACT_NAME = "Jonathan Bein";

    @Test
    public void whenInsertingContract_expectContractInserted() throws Exception {
        // Assemble
        assembleContractForCustomer();


        assertNotNull("No contract inserted",getContract());
    }



    @Test
    public void whenFindingContractByCustomerName_expectContractFound() throws Exception {
        // Assemble
        assembleContractForCustomer();

        // Assert
        assertNotNull("No contract returned",contractRepository.findByUsersDba(CONTACT_NAME));

    }

    //-------------- Private Methods

    private void assembleContractForCustomer() {
        Customer customer = getCustomer();
        customer.addContract(getContract());
    }

    private Customer getCustomer() {
        Customer customer = new Customer();
        customer.setLastName("CUSTOMER");
        customer.setDba("Z2M4");
        Roles customerRole = new Roles();
        customerRole.setRole(Roles.Role.customer);
        customer.getRoles().add(customerRole);
        return userRepository.save(customer);
    }

    private Contract getContract() {
        Contract contract = new Contract();
        contract.setStartDate(new DateTime());
        contract.setEndDate(new DateTime().plusMonths(6));
        contract.setTerms(Contract.Terms.net15);
        contract.setValue(87999D);
        return contractRepository.save(contract);
    }

}