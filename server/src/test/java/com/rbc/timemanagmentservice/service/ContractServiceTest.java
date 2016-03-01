package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.Job;
import com.rbc.timemanagmentservice.testutils.ContractTestUtil;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.NotFoundException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by russbaker on 2/27/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
@Transactional
public class ContractServiceTest {


    @Autowired
    private ContractService contractService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ContractTestUtil contractTestUtil;


    @Autowired
    private StartupUtility startupUtility;

    // Create
    @Test
    public void whenCreatingNewContract_expectContractCreated() throws Exception {
        // Assemble
        Contract contract = contractTestUtil.getContract();
        assertNotNull("Contract not created", contract);
        assertEquals("Contracts are different", contract, contract);
    }


    @Test(expected = NotFoundException.class)
    public void whenFindingContract_expectContractNotFound() throws Exception {
        // Act
        contractService.getContract(0);

    }

    // Read
    @Test
    public void whenFindingContract_expectContractReturned() throws Exception {
        // Assemble
        Contract contract = contractTestUtil.getContract();

        // Act
        Contract result = contractService.getContract(contract.getId());
        assertNotNull("No Contract returned", result);
        assertEquals("Wrong Contract returned", contract, result);

    }


    @Test
    public void whenUpdatingContract_expectContractUpdated() throws Exception {
        // Assemble
        Contract contract = contractTestUtil.getContract();
        Contract.Terms expectedTerms = Contract.Terms.net45;
        contract.setTerms(expectedTerms);

        // Act
        contractService.updateContract(contract);

        // Assert
        Contract result = contractService.getContract(contract.getId());
        assertTrue("Contract not updated", expectedTerms==result.getTerms());
    }

    // Delete

    @Test(expected = NotFoundException.class)
    public void whenDeletingContract_expectContractDeleted() throws Exception {
        // Assemble
        Contract contract = contractTestUtil.getContract();
        Customer customer = customerService.createUser(startupUtility.getCustomer());
        Employee employee = employeeService.createUser(startupUtility.getEmployee());
        customerService.addContractToUser(customer.getId(), contract.getId());
        employeeService.addContractToUser(employee.getId(), contract.getId());

        // Act
        contractService.deleteContract(contract.getId());

        // Assert
        assertTrue("Contracts not removed from employee.", CollectionUtils.isEmpty(employeeService.getUser(employee.getId()).getContracts()));
        assertTrue("Contracts not removed from customer.", CollectionUtils.isEmpty(customerService.getUser(customer.getId()).getContracts()));
        contractService.getContract(contract.getId());

    }

    //----------- Jobs

    @Test
    public void whenAddingJobToContract_expectJobAdded() throws Exception {
        // Assemble
        ContractTestUtil.JobCreator jobCreator = contractTestUtil.getJobCreator().invoke();
        Contract contract = jobCreator.getThisContract();
        Job jobCreated = jobCreator.getJobCreated();

        // Assert
        contract = contractService.getContract(contract.getId());
        List<Job> jobs = contract.getJobs();
        assertFalse("No job added",CollectionUtils.isEmpty(jobs));
        assertTrue("Wrong job returned",jobs.contains(jobCreated));
    }

    @Test
    public void whenUpdatingJob_expectJobUpdated() throws Exception {
        // Assemble
        ContractTestUtil.JobCreator jobCreator = contractTestUtil.getJobCreator().invoke();
        Contract contract = jobCreator.getThisContract();

        contract = contractService.getContract(contract.getId());
        String description = "new description";
        Job job = contract.getJobs().get(0);
        job.setDescription(description);

        // Act
        contractService.updateJob(contract.getId(),job);

        // Assert
        assertEquals("Contract not updated",description,contractService.getContract(contract.getId()).getJobs().get(0).getDescription());
    }

    @Test
    public void whenRemovingJobFromContract_expectJobRemoved() throws Exception {
        // Assemble
        ContractTestUtil.JobCreator jobCreator = contractTestUtil.getJobCreator().invoke();
        Contract contract = jobCreator.getThisContract();

        // Act
        contractService.removeJobFromContract(contract.getId(),jobCreator.getJobCreated().getId());

        // Assert
        assertTrue("Job not removed",CollectionUtils.isEmpty(contractService.getContract(contract.getId()).getJobs()));

    }

}