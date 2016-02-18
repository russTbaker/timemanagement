package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.TimeSheet;
import com.rbc.timemanagmentservice.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


/**
 * Created by russbaker on 2/12/16.
 */
@RestController
@RequestMapping("/hydrated/employee/")
public class EmployeeController {
    public EmployeeController() {
    }

    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @RequestMapping("/{employeeId}")
    @Transactional(readOnly = true)
    public ResponseEntity<Employee> getEmployee(@PathVariable Integer employeeId) {
        final Employee employee = this.employeeService.findEmployee(employeeId);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @RequestMapping()
    @Transactional(readOnly = true)
    public ResponseEntity<List<Employee>> findAll(@QueryParam(value = "start") Integer start,
                                                          @QueryParam(value = "end") Integer end) {
        final List<Employee> employees = employeeService.findAll(start, end);
        final HttpStatus status = CollectionUtils.isEmpty(employees) ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        return new ResponseEntity<>(employees, status);
    }

    @RequestMapping(method = RequestMethod.POST, path = "timesheet/{employeeId}")
    public ResponseEntity<?> add(@PathVariable Integer employeeId, @RequestBody TimeSheet input,
                                 @Context UriInfo uriInfo) {
        employeeService.addTimeSheet(employeeId,input);
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setLocation(URI.create(uriInfo.getPath() + "/" + input.getId()));

        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/{employeeId}/timesheet")
    public ResponseEntity<TimeSheet> getTimeSheet(@PathVariable("employeeId") Integer employeeId){
        final TimeSheet latestTimeSheet = employeeService.getLatestTimeSheet(employeeId);
        final HttpStatus status = latestTimeSheet == null ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        return new ResponseEntity<>(latestTimeSheet,status);
    }





}
