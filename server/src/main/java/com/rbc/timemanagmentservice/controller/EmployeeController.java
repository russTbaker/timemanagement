package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.TimeSheet;
import com.rbc.timemanagmentservice.model.TimeSheetEntry;
import com.rbc.timemanagmentservice.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


/**
 * Created by russbaker on 2/12/16.
 */
@RestController
@RequestMapping("/hydrated/employee/")
public class EmployeeController {

    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @RequestMapping(produces = "application/hal+json")
    public Resources<EmployeeResource> getEmployees() {
        List<Link> links = new LinkedList<>();
        links.add(linkTo(methodOn(EmployeeController.class).getEmployees()).withSelfRel());
        final List<Employee> allEmployees = employeeService.findAll(null, null);
        List<EmployeeResource> resources = employeeToResource(allEmployees.toArray(new Employee[allEmployees.size()]));
        return new Resources<>(resources, links);
    }

    @RequestMapping(value = "/{employeeId}", produces = "application/hal+json")
    public ResponseEntity<Resources<EmployeeResource>> getEmployee(@PathVariable("employeeId") Integer employeeId) {
        Optional<Employee> employee = Optional.of(employeeService.findEmployee(employeeId));
        List<EmployeeResource> resources = employeeToResource(employee.get());
        return new ResponseEntity<Resources<EmployeeResource>>(new Resources<>(resources),HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/{employeeId}/timesheet/{timesheetId}/timesheetentries/{timesheetEntryId}",
    consumes = "application/hal+json")
    public ResponseEntity<?> add(@PathVariable("employeeId") Integer employeeId,
                                 @PathVariable("timesheetId") Integer timesheetId,
                                 @PathVariable("timesheetEntryId") Integer timesheetEntryId,
                                 @RequestBody TimeSheetEntry input) {
        employeeService.addTimeSheetEntry(employeeId, timesheetId,input, timesheetEntryId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().build().toUri());

        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/{employeeId}/timesheet", produces = "application/hal+json")
    public Resources<TimeSheetResource> getLatestTimeSheet(@PathVariable("employeeId") Integer employeeId) {
        final Link link = linkTo(methodOn(EmployeeController.class).getLatestTimeSheet(employeeId)).withSelfRel();
        final TimeSheet latestTimeSheet = employeeService.getLatestTimeSheet(employeeId);
        List<TimeSheetResource> resources = timeSheetToResource(latestTimeSheet);
        return new Resources<>(resources, link);

    }

    @RequestMapping(path = "/{employeeId}/timesheet/{timesheetId}/timesheetentries", produces = "application/hal+json")
    public Resources<TimeSheetEntryResource> getTimeSheetEntrys(@PathVariable("employeeId") Integer employeeId,
                                                                @PathVariable("timesheetId") Integer timesheetId) {
        Optional<List<TimeSheetEntry>> timeSheetEntry = Optional.of(employeeService.getLatestTimeSheet(employeeId).getTimeSheetEntries());
        final List<TimeSheetEntryResource> resources = timeSheetEntryToResource(timeSheetEntry.get().toArray(new TimeSheetEntry[timeSheetEntry.get().size()]));
        final Link link = linkTo(methodOn(EmployeeController.class).getTimeSheetEntrys(employeeId, timesheetId)).withSelfRel();
        return new Resources<>(resources, link);
    }




//    class EmailResource extends ResourceSupport {
//        private final Email email;
//
//        public Email getEmail() {
//            return email;
//        }
//
//        public EmailResource(Email email) {
//            this.email = email;
//        }
//    }

    class TimeSheetResource extends ResourceSupport {
        private final TimeSheet timeSheet;

        public TimeSheetResource(TimeSheet timeSheet) {
            this.timeSheet = timeSheet;
            this.add(linkTo(methodOn(EmployeeController.class).getLatestTimeSheet(timeSheet.getEmployee().getId())).withSelfRel());
            this.add(linkTo(EmployeeController.class).withRel("timeSheetEntries"));
        }

        public TimeSheet getTimesheet() {
            return this.timeSheet;
        }

        public List<TimeSheetEntryResource> getTimeSheetEntries() {
            final List<TimeSheetEntry> timeSheetEntries = this.timeSheet.getTimeSheetEntries();
            return timeSheetEntryToResource(timeSheetEntries.toArray(new TimeSheetEntry[timeSheetEntries.size()]));
        }
    }

    class TimeSheetEntryResource extends ResourceSupport {
        private final TimeSheetEntry timeSheetEntry;

        public TimeSheetEntryResource(TimeSheetEntry timeSheetEntry) {
            this.timeSheetEntry = timeSheetEntry;
            this.add(linkTo(methodOn(EmployeeController.class).getTimeSheetEntrys(timeSheetEntry.getTimeSheet().getEmployee().getId(),
                    timeSheetEntry.getTimeSheet().getId())).withSelfRel());
            this.add(linkTo(methodOn(EmployeeController.class).getLatestTimeSheet(timeSheetEntry.getTimeSheet().getEmployee().getId())).withRel("timesheets"));
        }

        public TimeSheetEntry getTimeSheetEntry() {
            return timeSheetEntry;
        }
    }

    @SuppressWarnings("unchecked")
    private List<EmployeeResource> employeeToResource(Employee... employees) {
        List<EmployeeResource> resources = new ArrayList<>(employees.length);
        for (Employee employee : employees) {
//            Link selfLink = linkTo(methodOn(EmployeeController.class).getEmployee(employee.getId())).withSelfRel();
            resources.add(new EmployeeResource(employee));
        }
        return resources;
    }

    @SuppressWarnings("unchecked")
    List<TimeSheetResource> timeSheetToResource(TimeSheet... timeSheets) {
        List<TimeSheetResource> resources = new ArrayList<>(timeSheets.length);
        for (TimeSheet timesheet : timeSheets) {
            resources.add(new TimeSheetResource(timesheet));
        }
        return resources;
    }

    @SuppressWarnings("unchecked")
    private List<TimeSheetEntryResource> timeSheetEntryToResource(TimeSheetEntry... timeSheetEntries) {
        List<TimeSheetEntryResource> resources = new ArrayList<>(timeSheetEntries.length);
        for (TimeSheetEntry timesheetEntry : timeSheetEntries) {
            resources.add(new TimeSheetEntryResource(timesheetEntry));
        }
        return resources;
    }

    class EmployeeResource extends ResourceSupport {
        private final Employee employee;

        public EmployeeResource(Employee employee) {
            this.employee = employee;
            this.add(linkTo(methodOn(EmployeeController.class).getEmployee(employee.getId())).withSelfRel());
            this.add(linkTo(EmployeeController.class).withRel("timesheets"));
        }

        public Employee getEmployee() {
            return this.employee;
        }


        public List<EmployeeController.TimeSheetResource> getTimeSheets() {
            final List<TimeSheet> timesheets = this.employee.getTimesheets();
            return timeSheetToResource(timesheets.toArray(new TimeSheet[timesheets.size()]));
        }
    }

//    @SuppressWarnings("unchecked")
//    List<EmailResource> emailToResource(Email... emails) {
//        List<EmailResource> resources = new ArrayList<>(emails.length);
//        for (Email email : emails) {
//            resources.add(new EmailResource(email));
//        }
//        return resources;
//    }


}
