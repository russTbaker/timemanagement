package com.rbc.timemanagmentservice.util;

import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.service.ContractService;
import com.rbc.timemanagmentservice.service.CustomerService;
import com.rbc.timemanagmentservice.service.EmployeeService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by russbaker on 2/12/16.
 */
@Component
@Transactional
public class StartupUtility {


    public static final double RATE = 87.5;
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
        customer.addEmail(getEmail(customer.getDba()));
        final Address address = getAddress();
        customer.addAddress(address);
        final Phone phone = getPhone();
        customer.addPhone(phone);
        customer = customerService.updateUser(customer);


        Contract customerContract = getContractForCustomer(customer);
        final Job job = getJob();


        // Set up employee with job
        Employee employee = employeeService.createUser(getEmployee());
        employee.addEmail(getEmail(employee.getUsername()));
        employee.addAddress(getAddress());
        employee.addPhone(getPhone());
        employee = employeeService.updateUser(employee);

        // Add the employee to the contract
        employeeService.addContractToUser(employee.getId(),customerContract.getId());

        // Now add the employee to the job
        final Integer jobId = contractService.createJob(job).getId();
        employeeService.addEmployeeToJob(employee.getId(), contractService.addJobToContract(
                jobId,customerContract.getId()).getId());
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
        contract.setDescription("HDS Social Innovation - Second Phase");
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
        employee.setRoles(User.Roles.administrator);
        return employee;
    }


    public  Customer getCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("Jonathan");
        customer.setLastName("Bein");
        customer.setDba("Z2M4");
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

    public Job getJob() {
        final Job job = new Job();
        job.setRate(RATE);
        job.setName("BT");
        job.setDescription("Second Phase");
        return job;
    }
}
