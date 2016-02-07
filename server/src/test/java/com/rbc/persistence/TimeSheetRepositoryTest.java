package com.rbc.persistence;

import com.rbc.TimemanagementServiceApplication;
import com.rbc.model.*;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by rbaker on 2/6/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
@Transactional
public class TimeSheetRepositoryTest {
    public static final String CONTACT_NAME = "Jonathan Bein";

    @Autowired
    private TimeSheetRepository timeSheetRepository;

@Autowired
private TimeSheetEntryRepository timeSheetEntryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Test
    public void whenInsertingTimeSheet_expectTimesheetCreated() throws Exception {
        // Assemble
        TimeSheet timeSheet = new TimeSheet();
        timeSheet.setEmployee(getEmployee());
        timeSheet.setBilled(false);

        TimeSheetEntry timeSheetEntry = new TimeSheetEntry();
        timeSheetEntry.setContract(getContract());
        timeSheetEntry.setHours(8);
        timeSheetEntry.setDate(new DateTime());

        final TimeSheetEntry savedEntry = timeSheetEntryRepository.save(timeSheetEntry);
        timeSheet.setTimeSheetEntry(Arrays.asList(savedEntry));

        // Act
        assertNotNull("No timesheet returned.",timeSheetRepository.save(timeSheet));
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

    public Employee getEmployee() {
        Employee employee = new Employee();
        employee.setFirstName("Russ");
        employee.setLastName("Baker");
        employee.setUsername("admin");
        employee.setPassword("password");
        employee.setEmails(Arrays.asList(new Email(), new Email()));
        employee.setRoles(User.Roles.employee);
        return employeeRepository.save(employee);
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
}