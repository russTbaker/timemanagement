package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.Job;
import com.rbc.timemanagmentservice.model.TimeSheetEntry;
import com.rbc.timemanagmentservice.model.Timesheet;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import com.rbc.timemanagmentservice.persistence.JobRepository;
import com.rbc.timemanagmentservice.persistence.UserRepository;
import org.joda.time.DateTime;
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

    @Autowired
    public EmployeeService(UserRepository userRepository, JobRepository jobRepository,
                           ContractRepository contractRepository) {
        super(userRepository, contractRepository);
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }


    //------------- Job

    @Transactional(propagation = Propagation.REQUIRED)
    public void addEmployeeToJob(final Integer employeeId, Job job) {
        final Employee employee = (Employee) userRepository.findOne(employeeId);
        job = job.getId() != null ? jobRepository.findOne(job.getId()) :
                jobRepository.save(job);
        employee.addJob(job);
    }


    //--------- Timesheeets

    @Transactional(propagation = Propagation.REQUIRED)
    public void createTimeSheet(final Integer employeeId, final Integer jobId) {
        Employee employee = (Employee) userRepository.findOne(employeeId);
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
        userRepository.save(employee);
        Timesheet latestTimeSheet = getLatestTimeSheet(employeeId);
        latestTimeSheet.getTimeSheetEntries().stream().forEach(tse -> {
            tse.setTimesheetId(latestTimeSheet.getId());
            job.addTimeSheetEntry(tse);
        });
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

    public Timesheet getLatestTimeSheet(Integer employeeId) {
        final Employee employee = (Employee) userRepository.findOne(employeeId);
        if (employee != null) {
            final List<Timesheet> timesheets = employee.getTimesheets();
            timesheets.sort((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
            return timesheets.get(0);
        }
        throw new NotFoundException("No timesheets found for employee: " + employeeId);
    }


}
