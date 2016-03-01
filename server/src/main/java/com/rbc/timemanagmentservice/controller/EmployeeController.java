package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.TimeSheetEntry;
import com.rbc.timemanagmentservice.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


/**
 * Created by russbaker on 2/12/16.
 */
@RestController
@RequestMapping("/hydrated/employees")
public class EmployeeController extends UserController<Employee>{

    public static final String TIMESHEETS = "timesheets";
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        super(employeeService);
        this.employeeService = employeeService;
    }


    //------------- Timesheet

    @RequestMapping(method = RequestMethod.PUT, path = "/{employeeId}/timesheet/{timesheetId}/timesheetentries/{timesheetEntryId}",
            consumes = "application/hal+json")
    public ResponseEntity<?> addTimesheet(@PathVariable("employeeId") Integer employeeId,
                                          @PathVariable("timesheetId") Integer timesheetId,
                                          @PathVariable("timesheetEntryId") Integer timesheetEntryId,
                                          @RequestBody TimeSheetEntry input) {
        employeeService.addTimeSheetEntry(employeeId, timesheetId, input, timesheetEntryId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().build().toUri());

        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/{employeeId}/jobs/{jobId}", method = RequestMethod.POST)
    public ResponseEntity<?> addJobToEmployee(@PathVariable("employeeId") Integer employeeId,
                                              @PathVariable("jobId") Integer jobId) {
        employeeService.addEmployeeToJob(employeeId,jobId);
        return new ResponseEntity<>(null,getHttpHeadersForEntity(()->jobId,"jobs"),HttpStatus.ACCEPTED);
    }

}
