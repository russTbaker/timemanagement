package com.rbc.timemanagmentservice.util;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.service.*;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by russbaker on 3/2/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
public class VelocityHelperTest {
    public static final Contract.Terms NET_30 = Contract.Terms.net30;
    public static final String JOB_NAME = "Second Phase";
    public static final int NUM_TIME_ENTRIES = 7;

    @Autowired
    private VelocityHelper velocityHelper;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private JobService jobService;

    private List<String> dateResults = new ArrayList<>();
    DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MMM d, yyyy");

    @Autowired
    private StartupUtility startupUtility;


    @Test
//    @Ignore
    public void whenCreatingDocument_expectDocumentCreated() throws Exception {

        // Assemble
        Employee employee = createUser();
        Customer customer = getCustomer();
        Email email = new Email();
        email.setEmail("russabaker@gmail.com");
        email.setEmailType(Email.EmailTypes.billing);
        customer.addEmail(email);
        Address address = new Address();
        address.setStreet1("Streeet 1");
        address.setCity("Boulder");
        address.setState("CO");
        address.setZip("80430");
        customer.addAddress(address);
        customer = customerService.updateUser(customer);
        Contract contract = getContract();
        Job job = createPersistentJob(contract);


        customerService.addContractToUser(customer.getId(), contract.getId());
        employeeService.addContractToUser(employee.getId(), contract.getId());

        contractService.addJobToContract(job.getId(), contract.getId());
        employeeService.addEmployeeToJob(employee.getId(), job.getId());


        employeeService.getLatestTimeEntriesForEmployeeJobs(employee.getId(), job.getId());
        DateTime weekStart = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();
        Interval interval = new Interval(weekStart, weekStart.plusDays(7));


        List<TimeEntry> timeEntries = assembleTimeEntries(employee.getId(), job.getId());
        timeEntries
                .stream()
                .forEachOrdered(timeEntry -> {
                    timeEntry.setHours(20);
                    dateResults.add(fmt.print(timeEntry.getDate()));
                });
        employeeService.addTimeSheetEntries(new ArrayList<>(timeEntries), timeEntries.get(0).getJob().getId());
        Invoice invoice = invoiceService.createInvoiceForJobAndTimePeriod(job.getId(), interval);


        Map<String, Object> mapEntries = new HashMap<>();
        mapEntries.put("invoice", invoice);
        mapEntries.put("message", "Thank you for your business, your prompt payment is greatly appreciated!");
        mapEntries.put("contract", customerService.getUser(customer.getId()).getContracts().get(0));


        // Act
        String result = velocityHelper.convertTemplateToString("/templates/invoice.vm", mapEntries);

        // Assert
        assertTrue("No body returned", StringUtils.isNotBlank(result));
        assertThat(result, containsString(JOB_NAME));
        for (int i = 0; i < NUM_TIME_ENTRIES; i++) {
            assertThat(result, containsString(dateResults.get(i)));
        }
        assertThat(result, containsString("$" + invoice.getAmount()));
        assertThat(result, containsString(String.valueOf(invoice.getHours())));


    }

    private TimeEntry getTimesheetEntry(Integer index, Job job) {
        TimeEntry timeSheetEntry = new TimeEntry();
        timeSheetEntry.setDate(new DateTime().plusDays(index));
        dateResults.add(fmt.print(timeSheetEntry.getDate()));
        timeSheetEntry.setHours(8);
        job.addTimeEntry(timeSheetEntry);
        return timeSheetEntry;
    }

    private Customer getCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("firstName");
        customer.setLastName("lastName");
        customer.setDba("dba");
        Email email = new Email();
        email.setEmail("russabaker@gmail.com");
        email.setEmailType(Email.EmailTypes.billing);
        customer.addEmail(email);
        Roles customerRole = new Roles();
        customerRole.setRole(Roles.Role.customer);
        customer.getRoles().add(customerRole);
        return customerService.createUser(customer);
    }

    public Employee createUser() {
        Employee employee = new Employee();
        employee.setFirstName("Russ");
        employee.setLastName("Baker");
        employee.setUsername("username" + System.currentTimeMillis());
        employee.setPassword("password");
        Roles employeeRole = new Roles();
        employeeRole.setRole(Roles.Role.employee);
        employee.getRoles().add(employeeRole);
        employee.setDba("Russ Baker");
        return employeeService.createUser(employee);
    }

    private Contract getContract() {
        final Contract contract = new Contract();
        contract.setTerms(NET_30);
        return contractService.saveContract(contract);
    }

    private List<TimeEntry> assembleTimeEntries(Integer employeeId, Integer jobId) {
        return employeeService.getTimeEntriesForEmployeeJobs(employeeId, jobId);
    }

    private Job createPersistentJob(Contract contract) {
        Job job = new Job();
        job.setRate(90.0);
        job.setName(JOB_NAME);
        return contractService.addJobToContract(contractService.createJob(job).getId(), contract.getId());
    }


}