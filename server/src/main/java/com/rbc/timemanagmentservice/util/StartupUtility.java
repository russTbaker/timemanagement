package com.rbc.timemanagmentservice.util;

import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.persistence.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Created by russbaker on 2/12/16.
 */
@Component
public class StartupUtility {

    private static EmployeeRepository employeeRepository;

    private static ContractRepository contractRepository;

    private static CustomerRepository customerRepository;

    private static TimeSheetRepository timeSheetRepository;

    private static TimeSheetEntryRepository timeSheetEntryRepository;

    @Autowired
    public StartupUtility(EmployeeRepository employeeRepository, ContractRepository contractRepository,
                          CustomerRepository customerRepository, TimeSheetRepository timeSheetRepository,
                          TimeSheetEntryRepository timeSheetEntryRepository) {
        this.employeeRepository = employeeRepository;
        this.contractRepository = contractRepository;
        this.customerRepository = customerRepository;
        this.timeSheetRepository = timeSheetRepository;
        this.timeSheetEntryRepository = timeSheetEntryRepository;
    }

    public static final String CONTACT_NAME = "Jonathan Bein";

    public Employee init(){
        final Employee employee = getEmployee();
        employee.getTimesheets().add(getTimeSheet(employee,getContract()));
        employeeRepository.save(employee);
        return employee;
    }

    public static Contract getContract() {
        Contract contract = new Contract();
        contract.setStartDate(new DateTime());
        contract.setEndDate(new DateTime().plusMonths(6));
        contract.setRate(87.5);
        contract.setTerms(Contract.Terms.net15);
        contract.setCustomer(getCustomer());
        contract.setValue(87999D);
        return contractRepository.save(contract);
    }

    public static Employee getEmployee() {
        Employee employee = new Employee();
        employee.setFirstName("Russ");
        employee.setLastName("Baker");
        employee.setUsername("admin");
        employee.setPassword("password");
        employee.setEmails(Arrays.asList(getEmail(), getEmail()));
        employee.setRoles(User.Roles.employee);
        return employeeRepository.save(employee);
    }


    private static Customer getCustomer() {
        Customer customer = new Customer();
        customer.setName("TEST");
        customer.setFirstName("Jonathan");
        customer.setLastName("Bein");
        customer.setName("Z2M4");
        customer.setContactName(CONTACT_NAME);
        customer.setRoles(User.Roles.customer);
        Customer savedCustomer = customerRepository.save(customer);
        savedCustomer.setEmails(Arrays.asList(getEmail(),getEmail()));
        return savedCustomer;
    }

    private static Email getEmail(){
        Email email = new Email();
        email.setEmail("jonathan@z2m4.com");
        return email;
    }



    public static TimeSheet getTimeSheet( Employee employee,
                                   Contract contract){
        TimeSheet timeSheet = new TimeSheet(employee);
        timeSheet.setBilled(false);
        timeSheetRepository.save(timeSheet);

        TimeSheetEntry timeSheetEntry = new TimeSheetEntry(timeSheet,contract);
        timeSheetEntry.setHours(8);
        timeSheetEntry.setDate(new DateTime());
        timeSheetEntryRepository.save(timeSheetEntry);

        timeSheet.getTimeSheetEntries().add(timeSheetEntry);
        contract.getTimeSheetEntries().add(timeSheetEntry);
        return timeSheet;
    }
}
