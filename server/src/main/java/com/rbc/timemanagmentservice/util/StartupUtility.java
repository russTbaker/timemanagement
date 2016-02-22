package com.rbc.timemanagmentservice.util;

import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import com.rbc.timemanagmentservice.service.CustomerService;
import com.rbc.timemanagmentservice.service.EmployeeService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * Created by russbaker on 2/12/16.
 */
@Component
public class StartupUtility {


    private final EmployeeService employeeService;
    private final CustomerService customerService;
    private final ContractRepository contractRepository;


    @Autowired
    public StartupUtility(EmployeeService employeeService, CustomerService customerService, ContractRepository contractRepository) {
        this.employeeService = employeeService;
        this.customerService = customerService;
        this.contractRepository = contractRepository;
    }

    public static final String CONTACT_NAME = "Jonathan Bein";

    @Transactional(propagation = Propagation.REQUIRED)
    public  Employee init(){
        // Set up business relationship
        Customer customer = customerService.createCustomer(getCustomer());
        Contract contract = getContractForCustomer(customer);

        // Set up employee with job
        Employee employee = employeeService.createEmployee(getEmployee());
        employeeService.createTimeSheet(employee.getId(),contract.getId());
        return employeeService.findEmployee(employee.getId());
    }

    public  Contract getContractForCustomer(User customer) {
        Contract contract = new Contract();
        contract.setStartDate(new DateTime());
        contract.setEndDate(new DateTime().plusMonths(6));
        contract.setRate(87.5);
        contract.setTerms(Contract.Terms.net15);
        contract.setValue(87999D);
        contract = contractRepository.save(contract);
        customerService.addContractToCustomer(customer.getId(),contract.getId());
        return contract;
    }

    public  Contract getContractForEmployee(User employee) {
        Contract contract = new Contract();
        contract.setStartDate(new DateTime());
        contract.setEndDate(new DateTime().plusMonths(6));
        contract.setRate(87.5);
        contract.setTerms(Contract.Terms.net15);
        contract.setValue(87999D);
        contract = contractRepository.save(contract);
        employeeService.addEmployeeToContract(employee.getId(),contract);
        return contract;
    }


    public  Employee getEmployee() {
        Employee employee = new Employee();
        employee.setFirstName("Russ");
        employee.setLastName("Baker");
        employee.setUsername("admin");
        employee.setPassword("password");
        employee.setDba("RussBaker");
        employee.setRoles(User.Roles.employee);
        employee.setEmails(Arrays.asList(getEmail(employee)));
        employee.setPhones(Arrays.asList(getPhone(employee)));
        employee.setAddress(Arrays.asList(getAddress(employee)));
        return employee;
    }


    public  Customer getCustomer() {
        Customer customer = new Customer();
        customer.setName("TEST");
        customer.setFirstName("Jonathan");
        customer.setLastName("Bein");
        customer.setDba("Z2M4");
        customer.setContactName(CONTACT_NAME);
        customer.setRoles(User.Roles.customer);
        customer.setEmails(Arrays.asList(getEmail(customer)));
        customer.setAddress(Arrays.asList(getAddress(customer)));
        customer.setPhones(Arrays.asList(getPhone(customer)));
        return customer;
//        return customerRepository.save(customer);
    }

    private Email getEmail(User user){
        Email email = new Email();
        email.setEmail(String.format("%s@company.com",user.getDba()));
        email.setEmailType(Email.EmailTypes.both);
        email.setUser(user);
        return email;
    }

    public   Address getAddress(User user){
        Address address = new Address();
        address.setUser(user);
        address.setStreet1("73 Linden Cyn.");
        address.setCity("Boulder");
        address.setZip("80304");
        address.setState("CO");
        return address;
    }

    private  Phone getPhone(User user){
        Phone phone = new Phone();
        phone.setUser(user);
        phone.setPhone("3035551212");
        return phone;
    }



//    public  TimeSheet getTimeSheet( Employee employee,
//                                   Contract contract){
//        TimeSheet timeSheet = new TimeSheet(employee);
//        timeSheet.setBilled(false);
//        timeSheet.setStartDate(new DateTime());
//        timeSheet.setEndDate(timeSheet.getStartDate().plusDays(7));
//        timeSheetRepository.save(timeSheet);
//        for(int i=0;i<7;i++){
//            TimeSheetEntry timeSheetEntry = new TimeSheetEntry(timeSheet, contract);
//            timeSheetEntry.setHours(8);
//            timeSheetEntry.setDate(new DateTime().plusDays(i));
//            timeSheetEntryRepository.save(timeSheetEntry);
//            timeSheet.getTimeSheetEntries().add(timeSheetEntry);
//            contract.getTimeSheetEntries().add(timeSheetEntry);
//        }
//
//        return timeSheet;
//    }
}
