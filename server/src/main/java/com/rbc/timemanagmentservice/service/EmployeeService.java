package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.persistence.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;

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
    private final TimesheetRepository timesheetRepository;
    private final TimeSheetEntryRepository timeSheetEntryRepository;

    @Autowired
    public EmployeeService(UserRepository userRepository, JobRepository jobRepository,
                           ContractRepository contractRepository, EmployeeRepository employeeRepository, TimesheetRepository timesheetRepository, TimeSheetEntryRepository timeSheetEntryRepository) {
        super(userRepository, contractRepository);
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.employeeRepository = employeeRepository;
        this.contractRepository = contractRepository;
        this.timesheetRepository = timesheetRepository;
        this.timeSheetEntryRepository = timeSheetEntryRepository;
    }


    //------------- Job

    @Transactional(propagation = Propagation.REQUIRED)
    public void addEmployeeToJob(final Integer employeeId, Integer jobId) {
        final Employee employee = (Employee) employeeRepository.findOne(employeeId);
        final Job job = jobRepository.findOne(jobId);
        employee.addJob(job);
        employeeRepository.save(employee);
    }


    //--------- Timesheeets

    @Transactional(propagation = Propagation.REQUIRED)
    public void createTimeSheet(final Integer employeeId, final Integer jobId) {
        Employee employee = (Employee) userRepository.findOne(employeeId);
        final Job job = jobRepository.findOne(jobId);
        final Timesheet timeSheet = new Timesheet();
        final List<TimeSheetEntry> timeSheetEntryList = new ArrayList<>(DAYS_PER_WEEK);
        final DateTime weekStart = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();
        for (int i = 0; i < DAYS_PER_WEEK; i++) {
            final TimeSheetEntry timeSheetEntry = new TimeSheetEntry();
            timeSheetEntry.setDate(weekStart.plusDays(i));
            timeSheetEntryList.add(timeSheetEntry);
        }
        timeSheet.setStartDate(getFirstDayOfWeek());
        timeSheet.setEndDate(getLastDayOfWeek());
        timeSheet.getTimeSheetEntries().addAll(timeSheetEntryList);
        timeSheet.setBilled(false);

        employee.addTimeSheet(timeSheet);
        userRepository.save(employee);
        Timesheet latestTimeSheet = getLatestTimeSheet(employeeId);
        latestTimeSheet.getTimeSheetEntries().stream().forEach(tse -> {
            tse.setTimesheetId(latestTimeSheet.getId());
            job.addTimeSheetEntry(tse);
        });
        jobRepository.save(job);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void addTimeSheetEntry(Integer employeeId, Integer timeSheetId, TimeSheetEntry timeSheetEntry,
                                  Integer timeSheetEntryId) {
        final Employee employee = (Employee) userRepository.findOne(employeeId);
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

    @Transactional(propagation = Propagation.REQUIRED)
    public void addTimeSheetEntries(List<TimeSheetEntry> timeSheetEntries, Integer employeeId, Integer timesheetId) {
        final Timesheet timesheet = timesheetRepository.findOne(timesheetId);
        final Employee employee = employeeRepository.findOne(employeeId);
        timesheet.getTimeSheetEntries().clear();
        timesheet.getTimeSheetEntries().addAll(timeSheetEntries);
        // Add to Jobs
        timeSheetEntries
                .stream()
                .forEach(timeSheetEntry -> {
                    timeSheetEntryRepository.save(timeSheetEntry);
                    final Job job = jobRepository.findOne(timeSheetEntry.getJobId());
                    job.addTimeSheetEntry(timeSheetEntry);
                });
        final Timesheet savedTimesheet = timesheetRepository.save(timesheet);

        employee.getTimesheets().remove(savedTimesheet);
        employee.getTimesheets().add(savedTimesheet);
        employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public Timesheet getLatestTimeSheet(final Integer employeeId) {
        final Employee employee = (Employee) userRepository.findOne(employeeId);
        if (employee != null) {
            final List<Timesheet> timesheets = employee.getTimesheets();
            timesheets.sort((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
            return timesheets.get(0);
        }
        throw new NotFoundException("No timesheets found for employee: " + employeeId);
    }

    @Transactional(readOnly = true)
    public List<Job> getEmployeesAvailableJobs(Integer employeeId) {
        final Employee employee = employeeRepository.findOne(employeeId);
        if (employee == null) {
            throw new NotFoundException("Cannot find employee with id: " + employeeId);
        }
        final List<Contract> employeeContracts = contractRepository.findByUsersDba(employee.getDba());
        List<Job> retVal = new ArrayList<>();
        employeeContracts
                .parallelStream()
                .flatMap(contract -> contract.getJobs().stream())
                .forEach(job -> retVal.add(job));
        return retVal;
    }

}
