package com.rbc.timemanagmentservice.util;

import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.service.ContractService;
import com.rbc.timemanagmentservice.service.CustomerService;
import com.rbc.timemanagmentservice.service.EmployeeService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by russbaker on 2/12/16.
 */
@Component
public class StartupUtility {


    private final EmployeeService employeeService;
    private final CustomerService customerService;
    private final ContractService contractService;
    private Customer customer;


    @Autowired
    public StartupUtility(EmployeeService employeeService, CustomerService customerService, ContractService contractService) {
        this.employeeService = employeeService;
        this.customerService = customerService;
        this.contractService = contractService;
    }

    public static final String CONTACT_NAME = "Jonathan Bein";

    public  Employee init(){
        // Set up business relationship
        customer = customerService.createCustomer(getCustomer());
        customer.addEmail(getEmail(customer.getName()));
        final Address address = getAddress();
        customer.addAddress(address);
        final Phone phone = getPhone();
        customer.addPhone(phone);
        customer = customerService.updateCustomer(customer);


        Contract customerContract = getContractForCustomer(customer);

        // Set up employee with job
        Employee employee = employeeService.createEmployee(getEmployee());
        employee.addEmail(getEmail(employee.getUsername()));
        employee.addAddress(getAddress());
        employee.addPhone(getPhone());
        employee = employeeService.updateEmployee(employee);
        employeeService.createTimeSheet(employee.getId(),customerContract.getId());
        employeeService.addEmployeeToContract(employee.getId(),customerContract);
        return employeeService.getEmployee(employee.getId());
    }

    public Customer getCustomerObject(){
        return this.customer;
    }

    public  Contract getContractForCustomer(User customer) {
        Contract contract = new Contract();
        contract.setStartDate(new DateTime());
        contract.setEndDate(new DateTime().plusMonths(6));
        contract.setRate(87.5);
        contract.setTerms(Contract.Terms.net15);
        contract.setValue(87999D);
        contract = contractService.saveContract(contract);
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
        contract = contractService.saveContract(contract);
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
        return customer;
    }

    private Email getEmail(String username){
        Email email = new Email();
        email.setEmail(String.format("%s@company.com",username));
        email.setEmailType(Email.EmailTypes.both);
        return email;
    }

    public   Address getAddress(){
        Address address = new Address();
        address.setStreet1("73 Linden Cyn.");
        address.setCity("Boulder");
        address.setZip("80304");
        address.setState("CO");
        return address;
    }

    private  Phone getPhone(){
        Phone phone = new Phone();
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
