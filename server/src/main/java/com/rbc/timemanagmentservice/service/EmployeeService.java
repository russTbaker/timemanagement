package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.TimeSheet;
import com.rbc.timemanagmentservice.model.TimeSheetEntry;
import com.rbc.timemanagmentservice.persistence.EmployeeRepository;
import org.joda.time.DateTime;
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
    private EmployeeRepository employeeRepository;


    @Autowired
    public void setEmployeeRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Employee createEmployee() {
        final Employee employee = new Employee();
        return employeeRepository.save(employee);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Employee updateEmployee(final Employee employee) {
        return employeeRepository.save(employee);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addEmployeeToContract(final Employee employee, final Contract contract) {
        employee.getContracts().add(contract);
        contract.getEmployees().add(employee);
        employeeRepository.save(employee);
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
    public List<TimeSheet> createTimeSheet(final Employee employee, final Contract contract) {
        final TimeSheet timeSheet = new TimeSheet(employee);
        final List<TimeSheetEntry> timeSheetEntryList = new ArrayList<>(DAYS_PER_WEEK);
        for (int i = 0; i < DAYS_PER_WEEK; i++) {
            final TimeSheetEntry timeSheetEntry = new TimeSheetEntry(timeSheet, contract);
            timeSheetEntry.setDate(new DateTime().plusDays(i));
            timeSheetEntryList.add(timeSheetEntry);
        }
        timeSheet.getTimeSheetEntries().addAll(timeSheetEntryList);
        employee.getTimesheets().add(timeSheet);
        return employeeRepository.save(employee).getTimesheets();
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
    public void addTimeSheet(Integer employeeId, TimeSheet timeSheet) {
        final Employee employee = employeeRepository.findOne(employeeId);
        employee.getTimesheets()
                .stream()
                .filter(timeSheet1 -> timeSheet1.getId().equals(timeSheet.getId()))
                .forEach(timeSheet1 -> timeSheet1.getTimeSheetEntries().addAll(timeSheet.getTimeSheetEntries()));
        employeeRepository.save(employee);
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
