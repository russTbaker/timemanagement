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
        customer = customerService.createUser(getCustomer());
        customer.addEmail(getEmail(customer.getName()));
        final Address address = getAddress();
        customer.addAddress(address);
        final Phone phone = getPhone();
        customer.addPhone(phone);
        customer = customerService.updateUser(customer);


        Contract customerContract = getContractForCustomer(customer);
        final Job job = new Job();
        job.setRate(87.5);


        // Set up employee with job
        Employee employee = employeeService.createUser(getEmployee());
        employee.addEmail(getEmail(employee.getUsername()));
        employee.addAddress(getAddress());
        employee.addPhone(getPhone());
        employee = employeeService.updateUser(employee);
        employeeService.addEmployeeToJob(employee.getId(), contractService.createJob(job,customerContract.getId()));
        employeeService.createTimeSheet(employee.getId(),employeeService.getUser(employee.getId()).getJobs().get(0).getId());
        return employeeService.getUser(employee.getId());
    }

    public Customer getCustomerObject(){
        return this.customer;
    }

    public  Contract getContractForCustomer(User customer) {
        Contract contract = new Contract();
        contract.setStartDate(new DateTime());
        contract.setEndDate(new DateTime().plusMonths(6));
        contract.setTerms(Contract.Terms.net15);
        contract.setValue(87999D);
        contract.setName("HDS Social Innovation");
        contract.setDescription("Second Phase of original contract");
        contract = contractService.saveContract(contract);
        customerService.addContractToUser(customer.getId(),contract.getId());
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

    public Email getEmail(String username){
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

    public  Phone getPhone(){
        Phone phone = new Phone();
        phone.setPhone("3035551212");
        return phone;
    }
}
