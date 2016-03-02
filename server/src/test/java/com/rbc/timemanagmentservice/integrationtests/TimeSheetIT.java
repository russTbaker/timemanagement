package com.rbc.timemanagmentservice.integrationtests;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import com.rbc.timemanagmentservice.persistence.CustomerRepository;
import com.rbc.timemanagmentservice.persistence.EmployeeRepository;
import com.rbc.timemanagmentservice.persistence.TimesheetRepository;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
    private TimesheetRepository timeSheetRepository;


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
    public void whenAddingTimeSheetToEmployee_expectFullTimesheet() throws Exception{
        // Assemble
        Contract contract = assembleContract();
        Employee employee = getEmployee();

        // Act
//        addEmployeeToContract(employee,contract);
        Timesheet timeSheet = getTimesheet(contract.getJobs().get(0));
        employee.addTimeSheet(timeSheet);
        employee = employeeRepository.save(employee);


        // Assert
        Timesheet result = timeSheetRepository.findOne(employee.getTimesheets().get(0).getId());
        final List<TimeSheetEntry> timeSheetEntries = result.getTimeSheetEntries();
        assertFalse("No timesheet entries returned", CollectionUtils.isEmpty(timeSheetEntries));
        assertEquals("Wrong number of timesheet entries",7, timeSheetEntries.size());
        for(TimeSheetEntry timeSheetEntry: timeSheetEntries){
//            assertEquals("Wrong job",contract.getJobs().get(0),timeSheetEntry.getJobId());
            assertEquals("Wrong timesheet",result.getId(),timeSheetEntry.getTimesheetId());
        }

    }

    //--- Private Methods

    private Contract assembleContract() {
        Contract contract = new Contract();
        contract.setTerms(Contract.Terms.net30);
        contract.setValue(90000d);
        contract.setStartDate(new DateTime().minusDays(2));
        contract.setEndDate(new DateTime().plusMonths(6));
        contract.addJob(new Job());
        return contractRepository.save(contract);
    }

    private Customer getCustomer(){
        Customer customer = new Customer();
        customer.addEmail(getEmail());
        customer.setFirstName("Jonathan");
        customer.setLastName("Bein");
        customer.setName("Z2m4");
        return customerRepository.save(customer);
    }

    private Email getEmail() {
        Email contactEmail = new Email();
        contactEmail.setEmail("jonathan@z2m4.net");
        return contactEmail;
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
        employee.addEmail(email);
        return employeeRepository.save(employee);
    }


    private Timesheet getTimesheet(Job contract){
        Timesheet timeSheet = new Timesheet();
        timeSheet.getTimeSheetEntries().addAll(getWeekOfTimeSheetEntries(contract, timeSheet));
        return timeSheet;
    }

    private List<TimeSheetEntry> getWeekOfTimeSheetEntries(Job contract, Timesheet timeSheet) {
        List<TimeSheetEntry> timeSheetEntries = new ArrayList<>();
        for(int i=0;i<7;i++) {
            TimeSheetEntry timeSheetEntry = new TimeSheetEntry();
            timeSheetEntry.setHours(0);
            timeSheetEntry.setDate(new DateTime().plusDays(i));
            timeSheetEntries.add(timeSheetEntry);
        }
        return timeSheetEntries;
    }

    private Job createJob(){
        Job job = new Job();
        Contract contract = new Contract();
        contract.addJob(job);
        contractRepository.save(contract);
        return job;

    }


}
