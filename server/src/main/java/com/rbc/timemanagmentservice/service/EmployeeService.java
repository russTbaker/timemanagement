package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.TimeSheet;
import com.rbc.timemanagmentservice.model.TimeSheetEntry;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import com.rbc.timemanagmentservice.persistence.EmployeeRepository;
import com.rbc.timemanagmentservice.persistence.TimeSheetRepository;
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
    private final TimeSheetRepository timeSheetRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, ContractRepository contractRepository, TimeSheetRepository timeSheetRepository) {
        this.employeeRepository = employeeRepository;
        this.contractRepository = contractRepository;
        this.timeSheetRepository = timeSheetRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Employee updateEmployee(final Employee employee) {
        final Employee emp = employeeRepository.findOne(employee.getId());
        BeanUtils.copyProperties(employee,emp,"id");
        return emp;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addEmployeeToContract(final Integer employeeId, final Integer contractId) {
        final Employee employee = employeeRepository.findOne(employeeId);
        final Contract contract = contractRepository.findOne(contractId);
        contract.addUser(employee);
    }

    //    @Transactional(readOnly = true)
    public Employee findEmployee(Integer id) {
        final Employee employee = employeeRepository.findOne(id);
        if (employee == null) {
            throw new NotFoundException("Employee with id: " + id + " not found.");
        }
        return employee;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void createTimeSheet(final Integer employeeId, final Integer contractId) {
        final Employee employee = employeeRepository.findOne(employeeId);
        final Contract contract = contractRepository.findOne(contractId);
        final TimeSheet timeSheet = new TimeSheet(employee);
        final List<TimeSheetEntry> timeSheetEntryList = new ArrayList<>(DAYS_PER_WEEK);
        for (int i = 0; i < DAYS_PER_WEEK; i++) {
            final TimeSheetEntry timeSheetEntry = new TimeSheetEntry(timeSheet, contract);
            timeSheetEntry.setDate(new DateTime().plusDays(i));
            timeSheetEntryList.add(timeSheetEntry);
        }
        timeSheet.setStartDate(getFirstDayOfWeek());
        timeSheet.setEndDate(getLastDayOfWeek());
        timeSheet.getTimeSheetEntries().addAll(timeSheetEntryList);
        timeSheet.setBilled(false);
        timeSheetRepository.save(timeSheet);
//        employee.getTimesheets().add(timeSheet);
//        final List<TimeSheet> timesheets = employee.getTimesheets();
//        timesheets.sort((ts1,ts2)-> ts1.getStartDate().compareTo(ts2.getStartDate()));
//        return timesheets.stream().findFirst().get();
    }

    private DateTime getLastDayOfWeek() {
        return new DateTime().withDayOfWeek(DateTimeConstants.SUNDAY);
    }

    private DateTime getFirstDayOfWeek() {
        return new DateTime().withDayOfWeek(DateTimeConstants.MONDAY);
    }

    @SuppressWarnings("unchecked")
    public List<Employee> findAll(Integer start, Integer end) {
        final List<Employee> employeeList = start != null && end != null ?
                employeeRepository.findAll(new PageRequest(start, end)).getContent()
                : (List<Employee>) employeeRepository.findAll();
        if (CollectionUtils.isEmpty(employeeList)) {
            throw new NotFoundException("List of employees is empty");
        }
        return employeeList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addTimeSheetEntry(Integer employeeId, Integer timeSheetId, TimeSheetEntry timeSheetEntry,
                                  Integer timeSheetEntryId) {
        final Employee employee = employeeRepository.findOne(employeeId);
        final TimeSheet timeSheet = employee.getTimesheets()
                .stream()
                .filter(timeSheet1 -> timeSheet1.getId().equals(timeSheetId))
                .findFirst().get();
        final TimeSheetEntry existingTimeSheet = timeSheet
                .getTimeSheetEntries()
                .stream()
                .filter(timeSheetEntry1 -> timeSheetEntry1.getId().equals(timeSheetEntryId))
                .findFirst().get();
        BeanUtils.copyProperties(timeSheetEntry,existingTimeSheet,"id");
        timeSheetRepository.save(timeSheet);
    }

    public TimeSheet getLatestTimeSheet(Integer employeeId) {
        final Employee employee = employeeRepository.findOne(employeeId);
        if(employee != null) {
            final List<TimeSheet> timesheets = employee.getTimesheets();
            timesheets.sort((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
            return timesheets.get(0);
        }
        throw new NotFoundException("No timesheets found for employee: " + employeeId);
    }
}
