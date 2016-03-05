package com.rbc.timemanagmentservice.testutils;

import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.Job;
import com.rbc.timemanagmentservice.service.ContractService;
import com.rbc.timemanagmentservice.service.CustomerService;
import com.rbc.timemanagmentservice.service.EmployeeService;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by russbaker on 2/27/16.
 */
@Component
public class ContractTestUtil {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private StartupUtility startupUtility;

    public static final Contract.Terms ORIGINAL_TERMS = Contract.Terms.net15;
    public static final String TEST_JOB_DESCRIPTION = "Test job description";

    private Contract createContract() {
        Contract contract = new Contract();
        contract.setStartDate(new DateTime());
        contract.setEndDate(new DateTime().plusMonths(6));
        contract.setTerms(ORIGINAL_TERMS);
        contract.setValue(87999D);
        return contract;
    }

    public class JobCreator {
        private Contract contract;
        private Job jobCreated;

        public Contract getThisContract() {
            return this.contract;
        }

        public Job getJobCreated() {
            return jobCreated;
        }

        public JobCreator invoke() {
            contract = getContract();
            Customer customer = customerService.createUser(startupUtility.getCustomer());
            final Employee employee1 = startupUtility.getEmployee();
            employee1.setUsername(employee1.getUsername() + System.currentTimeMillis());
            Employee employee = employeeService.createUser(employee1);
            customerService.addContractToUser(customer.getId(), contract.getId());
            employeeService.addContractToUser(employee.getId(), contract.getId());
            Job job = new Job();
            job.setName("Job name");
            job.setDescription(TEST_JOB_DESCRIPTION);
            job = contractService.createJob(job);
            // Act
            jobCreated = contractService.addJobToContract(job.getId(), contract.getId());
            return this;
        }
    }

    public JobCreator getJobCreator(){
        return new JobCreator();
    }

    public Contract getContract() {
        Contract contract = createContract();

        contractService.createContract(contract);
        return contract;
    }
}
