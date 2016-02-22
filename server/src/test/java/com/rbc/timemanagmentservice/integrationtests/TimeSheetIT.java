package com.rbc.timemanagmentservice.integrationtests;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import com.rbc.timemanagmentservice.persistence.CustomerRepository;
import com.rbc.timemanagmentservice.persistence.EmployeeRepository;
import com.rbc.timemanagmentservice.persistence.TimeSheetRepository;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.*;

/**
 * Created by russbaker on 2/13/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
@Transactional
public class TimeSheetIT {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TimeSheetRepository timeSheetRepository;

    @Test
    public void whenCreatingContract_expectContractHydrated() throws Exception {
        // Assemble
        Contract initial = assembleContract();

        // Act
        Contract result = contractRepository.findOne(initial.getId());

        // Assert
        assertNotNull("No contract returned",result);
        assertEquals("Wrong contract returned",initial,result);

    }

    @Test
    public void whenAddingEmployeeToContract_expectEmployeeAdded() throws Exception {
        // Assemble
        Contract contract = assembleContract();
        Employee employee = getEmployee();

        // Act
        addEmployeeToContract(employee,contract);

        // Assert
        Contract result = contractRepository.findOne(contract.getId());
        Employee employeeResult = employeeRepository.findOne(employee.getId());
        assertTrue("Employee not added to contract",result.getUsers().contains(employee));
        assertTrue("Contract not associated to employee",employeeResult.getContracts().contains(contract));

    }

    @Test
    public void whenAddingTimeSheetToEmployee_expectFullTimesheet() throws Exception{
        // Assemble
        Contract contract = assembleContract();
        Employee employee = getEmployee();

        // Act
        addEmployeeToContract(employee,contract);
        TimeSheet timeSheet = getTimesheet(employee,contract);

        // Assert
        final List<TimeSheetEntry> timeSheetEntries = timeSheet.getTimeSheetEntries();
        assertFalse("No timesheet entries returned", CollectionUtils.isEmpty(timeSheetEntries));
        assertEquals("Wrong number of timesheet entries",7, timeSheetEntries.size());
        for(TimeSheetEntry timeSheetEntry: timeSheetEntries){
            assertEquals("Wrong contract",contract,timeSheetEntry.getContract());
            assertEquals("Wrong timesheet",timeSheet,timeSheetEntry.getTimeSheet());
        }

    }

    //--- Private Methods

    private Contract assembleContract() {
        Contract contract = new Contract();
        contract.setTerms(Contract.Terms.net30);
        contract.getUsers().add(getCustomer());
        contract.setRate(87.5);
        contract.setValue(90000d);
        contract.setStartDate(new DateTime().minusDays(2));
        contract.setEndDate(new DateTime().plusMonths(6));

        return contractRepository.save(contract);
    }

    private Customer getCustomer(){
        Customer customer = new Customer();
        customer.setEmails(getEmails());
        customer.setFirstName("Jonathan");
        customer.setLastName("Bein");
        customer.setName("Z2m4");
        return customerRepository.save(customer);
    }

    private List<Email> getEmails() {
        List<Email> retVal = new ArrayList<>();
        Email contactEmail = new Email();
        contactEmail.setEmail("jonathan@z2m4.net");
        retVal.add(contactEmail);
        return retVal;
    }

    private Employee getEmployee(){
        Employee employee = new Employee();
        employee.setFirstName("Russ");
        employee.setLastName("Baker");
        employee.setRoles(User.Roles.employee);
        employee.setUsername("username");
        employee.setPassword("password");
        Email email = new Email();
        email.setEmail("russabaker@yahoo.com");
        employee.setEmails(Arrays.asList(email));
        return employeeRepository.save(employee);
    }

    private void addEmployeeToContract(Employee employee, Contract contract){
        employee.addContract(contract);
        contract.getUsers().add(employee);
        contractRepository.save(contract);
    }

    private TimeSheet getTimesheet(Employee employee,Contract contract){
        TimeSheet timeSheet = new TimeSheet(employee);
        timeSheet.getTimeSheetEntries().addAll(getWeekOfTimeSheetEntries(contract, timeSheet));
        return timeSheetRepository.save(timeSheet);
    }

    private List<TimeSheetEntry> getWeekOfTimeSheetEntries(Contract contract, TimeSheet timeSheet) {
        List<TimeSheetEntry> timeSheetEntries = new ArrayList<>();
        for(int i=0;i<7;i++) {
            TimeSheetEntry timeSheetEntry = new TimeSheetEntry(timeSheet, contract);
            timeSheetEntry.setHours(0);
            timeSheetEntry.setDate(new DateTime().plusDays(i));
            timeSheetEntries.add(timeSheetEntry);
        }
        return timeSheetEntries;
    }


}
