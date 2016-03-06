package com.rbc.timemanagmentservice.util;

import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.service.ContractService;
import com.rbc.timemanagmentservice.service.CustomerService;
import com.rbc.timemanagmentservice.service.EmployeeService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

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

    public static final String DBA = "Z2M4";

    public  Employee init(){
        // Set up business relationship
        customer = customerService.createUser(getCustomer("Jonathan", "Bein", DBA));
        customer.addEmail(getEmail(customer.getDba()));
        customer.addAddress(getAddress());
        customer.addPhone(getPhone());
        customer = customerService.updateUser(customer);

        Customer rbc = customerService.createUser(getCustomer("Russ","Baker","Russ Baker"));
        rbc.addEmail(getEmail(rbc.getFirstName()));
        rbc.addAddress(getAddress());
        rbc.addPhone(getPhone());
        rbc = customerService.updateUser(rbc);


        // CONTRACTS
        // Associate the customer with the contract
        Contract z2M4Contract = getContractForCustomer(customer,"HDS Social Innovation" ,"HDS Social Innovation - Second Phase" );
        Contract rbcContract = getContractForCustomer(rbc,"GA" , "General Overhead");


        // JOB
        Job softwareEngineering = getJob("BT");
        Job gA = getJob("GA");

        Integer softwareEngineeringJobId = contractService.createJob(softwareEngineering).getId();
        Integer gaJobId = contractService.createJob(gA).getId();
        softwareEngineering = contractService.addJobToContract(softwareEngineeringJobId, z2M4Contract.getId());
        gA = contractService.addJobToContract(gaJobId,rbcContract.getId());





        // EMPLOYEE
        // Set up employee with softwareEngineering
        Employee russ = employeeService.createUser(getEmployee());
        russ.addEmail(getEmail(russ.getUsername()));
        russ.addAddress(getAddress());
        russ.addPhone(getPhone());
        russ = employeeService.updateUser(russ);

        // Add the employee to the contract
        employeeService.addContractToUser(russ.getId(),z2M4Contract.getId());
        employeeService.addContractToUser(russ.getId(),rbcContract.getId());

        // Now add the employee jobs
        employeeService.addEmployeeToJob(russ.getId(),
                softwareEngineeringJobId);
        employeeService.addEmployeeToJob(russ.getId(),gaJobId);

        // Set up some time entries for each job
        employeeService.getTimeEntriesForEmployeeJobs(russ.getId(),softwareEngineeringJobId);
        employeeService.getTimeEntriesForEmployeeJobs(russ.getId(),gaJobId);

        return employeeService.getUser(russ.getId());
    }



    public Customer getCustomerObject(){
        return this.customer;
    }

    public  Contract getContractForCustomer(User customer, String contractName, String contractDescription) {
        Contract contract = new Contract();
        contract.setStartDate(new DateTime());
        contract.setEndDate(new DateTime().plusMonths(6));
        contract.setTerms(Contract.Terms.net15);
        contract.setValue(87999D);
        contract.setName(contractName);
        contract.setDescription(contractDescription);
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
        Roles adminRole = new Roles();
        adminRole.setRole(Roles.Role.administrator);

        Roles employeeRole = new Roles();
        employeeRole.setRole(Roles.Role.employee);
        employee.getRoles().addAll(Arrays.asList(adminRole,employeeRole));
        return employee;
    }


    public  Customer getCustomer(String firstName, String lastName, String dba) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setDba(dba);
        Roles customerRole = new Roles();
        customerRole.setRole(Roles.Role.customer);
        customer.getRoles().add(customerRole);
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

    public Job getJob(String name) {
        final Job job = new Job();
        job.setRate(RATE);
        job.setName(name);
        job.setDescription("Second Phase");
        return job;
    }
}
