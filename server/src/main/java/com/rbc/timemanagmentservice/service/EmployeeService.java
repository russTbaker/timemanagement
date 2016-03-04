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
    private final TimesheetEntryRepository timeSheetEntryRepository;

    @Autowired
    public EmployeeService(UserRepository userRepository, JobRepository jobRepository,
                           ContractRepository contractRepository, EmployeeRepository employeeRepository, TimesheetRepository timesheetRepository, TimesheetEntryRepository timeSheetEntryRepository) {
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
        final List<TimesheetEntry> timeSheetEntryList = new ArrayList<>(DAYS_PER_WEEK);
        final DateTime weekStart = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();
        for (int i = 0; i < DAYS_PER_WEEK; i++) {
            final TimesheetEntry timeSheetEntry = new TimesheetEntry();
            timeSheetEntry.setDate(weekStart.plusDays(i));
            timeSheetEntryList.add(timeSheetEntry);
            timeSheetEntryRepository.save(timeSheetEntry);
            timeSheet.addTimesheetEntry(timeSheetEntry);
        }
        timeSheet.setStartDate(getFirstDayOfWeek());
        timeSheet.setEndDate(getLastDayOfWeek());

        timeSheet.setBilled(false);
        employee.addTimeSheet(timesheetRepository.save(timeSheet));
        userRepository.save(employee);
        Timesheet latestTimeSheet = getLatestTimeSheet(employeeId);
        latestTimeSheet.getTimeSheetEntries().stream().forEach(tse -> {
            job.addTimesheetEntry(tse);
        });
        jobRepository.save(job);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void addTimesheetEntry(Integer employeeId, Integer timeSheetId, TimesheetEntry timeSheetEntry,
                                  Integer timeSheetEntryId) {
        final Employee employee = (Employee) userRepository.findOne(employeeId);
        final Timesheet timeSheet = employee.getTimesheets()
                .stream()
                .filter(timeSheet1 -> timeSheet1.getId().equals(timeSheetId))
                .findFirst().get();
        final TimesheetEntry existingTimesheetEntry = timeSheet
                .getTimeSheetEntries()
                .stream()
                .filter(timeSheetEntry1 -> timeSheetEntry1.getId().equals(timeSheetEntryId))
                .findFirst().get();
        BeanUtils.copyProperties(timeSheetEntry, existingTimesheetEntry, "id");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addTimeSheetEntries(List<TimesheetEntry> newTimeSheetEntries, Integer employeeId, Integer timesheetId) {
        final Employee employee = employeeRepository.findOne(employeeId);
        final Timesheet employeeTimesheet = employee.getTimesheets()
                .stream()
                .filter(timesheet -> timesheet.getId().equals(timesheetId))
                .findFirst()
                .get();
        for(TimesheetEntry newTimesheetEntry :newTimeSheetEntries){
            for(TimesheetEntry existingTimesheetEntry: employeeTimesheet.getTimeSheetEntries()){
                if(existingTimesheetEntry.equals(newTimesheetEntry)){
                    BeanUtils.copyProperties(newTimesheetEntry, existingTimesheetEntry, "id","timesheet","job");
                }
            }
        }
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
