package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.exception.NotFoundException;
import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.Job;
import com.rbc.timemanagmentservice.model.TimeEntry;
import com.rbc.timemanagmentservice.persistence.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by russbaker on 2/16/16.
 */
@Repository
public class EmployeeService extends UserService<Employee> {

    public static final int DAYS_PER_WEEK = 7;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final EmployeeRepository employeeRepository;
    private final ContractRepository contractRepository;
    private final TimesheetEntryRepository timeSheetEntryRepository;

    @Autowired
    public EmployeeService(UserRepository userRepository, JobRepository jobRepository,
                           ContractRepository contractRepository, EmployeeRepository employeeRepository, TimesheetEntryRepository timeSheetEntryRepository) {
        super(userRepository, contractRepository);
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.employeeRepository = employeeRepository;
        this.contractRepository = contractRepository;
        this.timeSheetEntryRepository = timeSheetEntryRepository;
    }

//    @Transactional(readOnly = true)
//    public Employee findByUsername(final String username) {
//        return employeeRepository.findByUsername(username).get();
//    }

//    @Transactional(propagation = Propagation.REQUIRED)
//    public void deleteUser(final Integer employeeId) {
//        final Employee employee = employeeRepository.findOne(employeeId);
//        employee.getJobs()
//                .stream()
//                .forEach(job -> job.getEmployees().remove(employee));
//        super.deleteUser(employeeId);
//    }


    //------------- Job

    @Transactional(propagation = Propagation.REQUIRED)
    public void addEmployeeToJob(final Integer employeeId, final Integer jobId) {
        final Employee employee = employeeRepository.findOne(employeeId);
        final Job job = jobRepository.findOne(jobId);
        employee.addJob(job);
        employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public List<Job> getEmployeeJobs(final Integer employeeId) {
        final Employee employee = employeeRepository.findOne(employeeId);
        if (employee == null) {
            throw new NotFoundException("Cannot find employee with id: " + employeeId);
        }
        return employee.getJobs();
    }


    //--------- Time entries

    @Transactional(propagation = Propagation.REQUIRED)
    public List<TimeEntry> getTimeEntriesForEmployeeJobs(final Integer employeeId, final Integer jobId) {
        final Employee employee = getEmployeeWithTimeEntries(employeeId, jobId,
                new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay());
        return saveEmployeeReturnTimeEntries(jobId, employee);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<TimeEntry> getTimeEntriesForEmployeeJobs(Integer employeeId, Integer jobId, DateTime startDate) {
        final Employee employee = getEmployeeWithTimeEntries(employeeId, jobId, startDate);
        return saveEmployeeReturnTimeEntries(jobId, employee);
    }


    @Transactional(readOnly = true)
    public List<TimeEntry> getLatestTimeEntriesForEmployeeJobs(final Integer employeeId, final Integer jobId) {
        final DateTime weekStart = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();
        final Interval interval = new Interval(weekStart, weekStart.plusDays(7));
        final Job job = jobRepository.findOne(jobId);
        List<TimeEntry> timeEntries = job.getTimeEntries()
                .stream()
                .filter(timeEntry -> interval.contains(timeEntry.getDate()))
                .collect(Collectors.toList());
        return timeEntries;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void addTimeSheetEntries(final List<TimeEntry> newTimeSheetEntries, final Integer jobId) {
        final Job job = jobRepository.findOne(jobId);
        newTimeSheetEntries.parallelStream()
                .forEachOrdered(timeEntry -> {
                            BeanUtils.copyProperties(timeEntry, job.getTimeEntries().stream()
                                    .filter(timeEntry1 -> timeEntry1.equals(timeEntry))
                                    .findFirst()
                                    .get(),"id","job");
                        }
                );
    }

    //---------- Private Methods

    private Employee getEmployeeWithTimeEntries(final Integer employeeId, final Integer jobId, final DateTime startDate) {
        final Employee employee = (Employee) userRepository.findOne(employeeId);
        final Job job = jobRepository.findOne(jobId);
        addTimeSheetEntries(job, startDate);
        employee.addJob(job);
        return employee;
    }

    private void addTimeSheetEntries(Job job, DateTime weekStart) {

        for (int i = 0; i < DAYS_PER_WEEK; i++) {
            final TimeEntry timeSheetEntry = new TimeEntry();
            timeSheetEntry.setDate(weekStart.plusDays(i));
            timeSheetEntry.setHours(0);
            timeSheetEntryRepository.save(timeSheetEntry);
            job.addTimeEntry(timeSheetEntry);
        }
    }

    private List<TimeEntry> saveEmployeeReturnTimeEntries(final Integer jobId, final Employee employee) {
        return ((Employee) userRepository.save(employee)).getJobs()
                .stream()
                .filter(job1 -> job1.getId().equals(jobId))
                .findFirst()
                .get().getTimeEntries();
    }

}
