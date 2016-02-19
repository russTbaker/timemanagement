package com.rbc.timemanagmentservice.util;

import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.persistence.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
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
        final Contract contract = getContract();
        employee.getTimesheets().add(getTimeSheet(employee, contract));
        employee.getContracts().add(contract);
        employeeRepository.save(employee);
        return employee;
    }

    public static Contract getContract() {
        Contract contract = new Contract();
        contract.setStartDate(new DateTime());
        contract.setEndDate(new DateTime().plusMonths(6));
        contract.setRate(87.5);
        contract.setTerms(Contract.Terms.net15);
        contract.setValue(87999D);

        final Customer customer = getCustomer();
        contract.setCustomer(customer);

        customer.getContracts().add(contract);//contractRepository.save(contract));
        customerRepository.save(customer);
        return customer.getContracts().get(0);
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


    public static Customer getCustomer() {
        Customer customer = new Customer();
        customer.setName("TEST");
        customer.setFirstName("Jonathan");
        customer.setLastName("Bein");
        customer.setName("Z2M4");
        customer.setContactName(CONTACT_NAME);
        customer.setRoles(User.Roles.customer);
        customer.setEmails(Arrays.asList(getEmail(),getEmail()));
        return customerRepository.save(customer);
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
        timeSheet.setStartDate(new DateTime());
        timeSheet.setEndDate(timeSheet.getStartDate().plusDays(7));
        timeSheetRepository.save(timeSheet);
        contractRepository.save(contract);
        for(int i=0;i<7;i++){
            TimeSheetEntry timeSheetEntry = new TimeSheetEntry(timeSheet, contract);
            timeSheetEntry.setHours(8);
            timeSheetEntry.setDate(new DateTime().plusDays(i));
            timeSheetEntryRepository.save(timeSheetEntry);
            timeSheet.getTimeSheetEntries().add(timeSheetEntry);
            contract.getTimeSheetEntries().add(timeSheetEntry);
        }

        return timeSheet;
    }
}
