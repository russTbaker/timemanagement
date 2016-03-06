package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.testutils.ContractTestUtil;
import com.rbc.timemanagmentservice.util.VelocityEmailHelper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.when;


/**
 * Created by russbaker on 3/2/16.
 */
//@RunWith(PowerMockRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
public class InvoiceServiceTest {

    public static final Contract.Terms NET_30 = Contract.Terms.net30;
    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractTestUtil contractTestUtil;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CustomerService customerService;

    @InjectMocks
    private InvoiceService mockedInvoiceService;

    @Mock
    private VelocityEmailHelper mockEmailer;

    @Test
    @Rollback
    public void whenCallingCreateInvoice_expectInvoiceCreated() throws Exception {
        // Assemble/Act
        Invoice result = createInvoice();


        // Asert
        assertNotNull("Invoice not created", result.getId());
    }


    @Test
    public void whenCallingGetInvoices_expectAllInvoicesReturned() throws Exception {
        // Assemble
        createInvoice();
        createInvoice();

        // Assert
        List<Invoice> invoices = invoiceService.getInvoices();
        assertFalse("No invoices returned", CollectionUtils.isEmpty(invoices));
        assertTrue("Wrong number of invoices returned", invoices.size() > 2);
    }

    @Test
    public void whenUpdatingInvoice_expectInvoiceUpdated() throws Exception {
        // Assemble
        Invoice invoice = createInvoice();
        ContractTestUtil.JobCreator jobCreator = contractTestUtil.getJobCreator().invoke();

        // Act
        Invoice result = invoiceService.addJobToInvoice(invoice.getId(), jobCreator.getJobCreated());
        assertEquals("No job added to invoice", 2, result.getJobs().size());
    }

    @Test(expected = NotFoundException.class)
    public void whenCallingGetInvoice_expectNotFoundException() throws Exception {
        // Act
        invoiceService.getInvoice(0);

    }

    @Test
    public void whenCallingGetInvoice_expectInvoiceReturned() throws Exception {
        // Assemble
        Invoice invoice = createInvoice();

        // Act
        assertNotNull("No Invoice found", invoiceService.getInvoice(invoice.getId()));

    }

    @Test(expected = NotFoundException.class)
    public void whenDeletingInvoice_expectSoftDelete() throws Exception {
        // Assemble
        Invoice invoice = createInvoice();

        // Act
        invoiceService.deleteInvoice(invoice.getId());


        // Assert
        invoiceService.getInvoice(invoice.getId());

    }

    @Test
    public void whenAggregatingJobTotals_expectJobTotalsAggregated() throws Exception {
        // Assemble
        final int updatedHours = 20;
        List<TimeEntry> timeEntries = assembleTimeEntries(updatedHours);
        final DateTime weekStart = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();

        // Act
        Double sum = invoiceService.sumJobForTimePeriod(timeEntries.get(0).getJob().getId(), weekStart, weekStart.plusDays(7));

        // Assert
        assertTrue("Nothing returned", sum > 0);
        assertEquals("Wrong sum", timeEntries.get(0).getJob().getRate() * updatedHours * timeEntries.size(), sum, 0.0);
    }


    @Test
    public void whenRequestingInvoice_expectFullyPopulatedInvoice() throws Exception {
        // Assemble
        final int updatedHours = 20;
        List<TimeEntry> timeEntries = assembleTimeEntries(updatedHours);

        // Act
        DateTime weekStart = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();
        Interval interval = new Interval(weekStart, weekStart.plusDays(7));
        Invoice invoice = invoiceService.createInvoiceForJobAndTimePeriod(timeEntries.get(0).getJob().getId(), interval);

        // Assert
        assertNotNull("No Invoice returned", invoice);
        assertNotNull("Inoice date null", invoice.getInvoiceDate());
        assertNotNull("Due date is null", invoice.getDueDate());
        assertEquals("Wrong due date", new DateTime().withTimeAtStartOfDay().plusDays(NET_30.getTerm()), invoice.getDueDate());
        final List<Job> jobs = invoice.getJobs();
        assertFalse("Job is null", CollectionUtils.isEmpty(jobs));
        assertTrue("Wrong jobs", jobs.contains(timeEntries.get(0).getJob()));
        assertEquals("Wrong customer", timeEntries.get(0).getJob().getContract().getUsers().stream().filter(user -> user instanceof Customer).findFirst().get(),
                invoice.getCustomer());
        assertNotNull("No invoice total returned", invoice.getAmount());
        final Integer hours = invoice.getHours();
        assertNotNull("Hours not returned", hours);
        assertEquals("Wrong number of hours", updatedHours * timeEntries.size(), hours, 0.0);
        assertNull("Delete date is not null", invoice.getDeleteDate());
    }

    @Test
    @Ignore("Needs a real mail server")
    public void whenSendingOffInvoice_expectInvoiceSent() throws Exception {
        // Assemble
        final int updatedHours = 20;
        List<TimeEntry> timeEntries = assembleTimeEntries(updatedHours);
        DateTime weekStart = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();
        Interval interval = new Interval(weekStart, weekStart.plusDays(7));
        Invoice invoice = invoiceService.createInvoiceForJobAndTimePeriod(timeEntries.get(0).getJob().getId(), interval);

        // Act
        invoiceService.sendInvoiceAndBillEntries(invoice);

        // Assert
        Invoice result = invoiceService.getInvoice(invoice.getId());
        result.getJobs().get(0).getTimeEntries()
                .stream()
                .forEachOrdered(inv -> assertTrue(inv.getBilled()));

    }

    //-------------------- Private Methods
    private Invoice createInvoice() {
        // Assemble
        ContractTestUtil.JobCreator jobCreator = contractTestUtil.getJobCreator().invoke();
        Invoice invoice = new Invoice();
        invoice.getJobs().add((jobCreator.getJobCreated()));

        // Act
        return invoiceService.createInvoice(invoice);
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


    private List<TimeEntry> assembleTimeEntries(int updatedHours) {
        Employee employee = createUser();
        Customer customer = getCustomer();
        Contract contract = getContract();
        Job job = createPersistentJob(contract);

        customerService.addContractToUser(customer.getId(), contract.getId());
        employeeService.addContractToUser(employee.getId(), contract.getId());

        contractService.addJobToContract(job.getId(), contract.getId());
        employeeService.addEmployeeToJob(employee.getId(), job.getId());


        List<TimeEntry> timeEntries = assembleTimeEntries(employee.getId(), job.getId());
        timeEntries
                .stream()
                .forEachOrdered(timeEntry -> timeEntry.setHours(updatedHours));

        employeeService.addTimeSheetEntries(new ArrayList<>(timeEntries), timeEntries.get(0).getJob().getId());
        return timeEntries;
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
        return contractService.addJobToContract(contractService.createJob(job).getId(), contract.getId());
    }
}