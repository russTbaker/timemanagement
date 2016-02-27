package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import com.rbc.timemanagmentservice.persistence.EmployeeRepository;
import com.rbc.timemanagmentservice.persistence.JobRepository;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by russbaker on 2/16/16.
 */
@Repository
public class EmployeeService {

    public static final int DAYS_PER_WEEK = 7;
    private final EmployeeRepository employeeRepository;
    private final ContractRepository contractRepository;
    private final JobRepository jobRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, ContractRepository contractRepository, JobRepository jobRepository) {
        this.employeeRepository = employeeRepository;
        this.contractRepository = contractRepository;
        this.jobRepository = jobRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    /**
     * Requires phone, email and address populated
     */
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Employee updateEmployee(final Employee employee) {
        return employeeRepository.save(employee);
    }



    //--------- Address

    @Transactional(propagation = Propagation.REQUIRED)
    public void addAddressToEmployee(final Integer employeeId, final Address address){
        final Employee employee = employeeRepository.findOne(employeeId);
        employee.addAddress(address);
        employeeRepository.save(employee);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void removeAddressFromEmployee(Integer employeeId, Integer addressId) {
        final Employee employee = employeeRepository.findOne(employeeId);
        final List<Address> addresses = employee.getAddress();
        addresses.remove(addresses.stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .get());
        employeeRepository.save(employee);
    }

    //----------- Phone

    @Transactional(propagation = Propagation.REQUIRED)
    public void addPhoneToEmployee(Integer employeeId, Phone phone) {
        final Employee employee = employeeRepository.findOne(employeeId);
        employee.addPhone(phone);
        employeeRepository.save(employee);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removePhoneFromEmployee(Integer employeeId, Integer phoneId) {
        final Employee employee = employeeRepository.findOne(employeeId);
        final List<Phone> phones = employee.getPhones();
        phones.remove(phones.stream()
                .filter(phone -> phone.getId().equals(phoneId))
                .findFirst()
                .get());
        employeeRepository.save(employee);
    }

    //--------- Email

    @Transactional(propagation = Propagation.REQUIRED)
    public void addEmailToEmployee(Integer employeeId, Email email) {
        final Employee employee = employeeRepository.findOne(employeeId);
        employee.addEmail(email);
        employeeRepository.save(employee);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeEmailFromEmployee(Integer employeeId, Integer emailId) {
        final Employee employee = employeeRepository.findOne(employeeId);
        final List<Email> emails = employee.getEmails();
        emails.remove(emails.stream()
                .filter(email -> email.getId().equals(emailId))
                .findFirst()
                .get());
        employeeRepository.save(employee);
    }

    //------------- Job

    @Transactional(propagation = Propagation.REQUIRED)
    public void addEmployeeToJob(final Integer employeeId, Job job) {
        final Employee employee = employeeRepository.findOne(employeeId);
        job = job.getId() != null ? jobRepository.findOne(job.getId()) :
                jobRepository.save(job);
        employee.addJob(job);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Employee getEmployee(Integer id) {
        final Employee employee = employeeRepository.findOne(id);
        if (employee == null) {
            throw new NotFoundException("Employee with id: " + id + " not found.");
        }
        return employee;
    }


    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Employee> findAll(Integer start, Integer end) {
        final List<Employee> employeeList = start != null && end != null ?
                employeeRepository.findAll(new PageRequest(start, end)).getContent()
                : employeeRepository.findAll();
        if (CollectionUtils.isEmpty(employeeList)) {
            throw new NotFoundException("List of employees is empty");
        }
        return employeeList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteEmployee(Integer employeeId) {
        employeeRepository.delete(employeeId);
    }

    //--------- Timesheeets

    //TODO: Rework for job
    @Transactional(propagation = Propagation.REQUIRED)
    public void createTimeSheet(final Integer employeeId, final Integer jobId) {
        Employee employee = employeeRepository.findOne(employeeId);
        final Job job = jobRepository.findOne(jobId);
        final Timesheet timeSheet = new Timesheet();
        final List<TimeSheetEntry> timeSheetEntryList = new ArrayList<>(DAYS_PER_WEEK);
        for (int i = 0; i < DAYS_PER_WEEK; i++) {
            final TimeSheetEntry timeSheetEntry = new TimeSheetEntry();
            timeSheetEntry.setDate(new DateTime().plusDays(i));
            timeSheetEntryList.add(timeSheetEntry);
        }
        timeSheet.setStartDate(getFirstDayOfWeek());
        timeSheet.setEndDate(getLastDayOfWeek());
        timeSheet.getTimeSheetEntries().addAll(timeSheetEntryList);
        timeSheet.setBilled(false);

        employee.addTimeSheet(timeSheet);
        employeeRepository.save(employee);
        Timesheet latestTimeSheet = getLatestTimeSheet(employeeId);
        latestTimeSheet.getTimeSheetEntries().stream().forEach(tse -> {
            tse.setTimesheetId(latestTimeSheet.getId());
            job.addTimeSheetEntry(tse);
        });
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void addTimeSheetEntry(Integer employeeId, Integer timeSheetId, TimeSheetEntry timeSheetEntry,
                                  Integer timeSheetEntryId) {
        final Employee employee = employeeRepository.findOne(employeeId);
        final Timesheet timeSheet = employee.getTimesheets()
                .stream()
                .filter(timeSheet1 -> timeSheet1.getId().equals(timeSheetId))
                .findFirst().get();
        final TimeSheetEntry existingTimeSheet = timeSheet
                .getTimeSheetEntries()
                .stream()
                .filter(timeSheetEntry1 -> timeSheetEntry1.getId().equals(timeSheetEntryId))
                .findFirst().get();
        BeanUtils.copyProperties(timeSheetEntry, existingTimeSheet, "id");
    }

    public Timesheet getLatestTimeSheet(Integer employeeId) {
        final Employee employee = employeeRepository.findOne(employeeId);
        if (employee != null) {
            final List<Timesheet> timesheets = employee.getTimesheets();
            timesheets.sort((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
            return timesheets.get(0);
        }
        throw new NotFoundException("No timesheets found for employee: " + employeeId);
    }




    //--------------- Private Methods

    private DateTime getLastDayOfWeek() {
        return new DateTime().withDayOfWeek(DateTimeConstants.SUNDAY);
    }

    private DateTime getFirstDayOfWeek() {
        return new DateTime().withDayOfWeek(DateTimeConstants.MONDAY);
    }



}
