package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.*;
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
@RequestMapping("/hydrated/employees")
public class EmployeeController {

    public static final String TIMESHEETS = "timesheets";
    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @RequestMapping(produces = "application/hal+json")
//    public Resources<EmployeeResource> getEmployees() {
    public List<Employee> getEmployees() {
        List<Link> links = new LinkedList<>();
        links.add(linkTo(methodOn(EmployeeController.class).getEmployees()).withSelfRel());
        final List<Employee> allEmployees = employeeService.findAll(null, null);
//        List<EmployeeResource> resources = employeeToResource(allEmployees.toArray(new Employee[allEmployees.size()]));
//        return new Resources<>(resources, links);
        return allEmployees;
    }

    @RequestMapping(value = "/{employeeId}", produces = "application/hal+json")
    public ResponseEntity<Resources<EmployeeResource>> getEmployee(@PathVariable("employeeId") Integer employeeId) {
        Optional<Employee> employee = Optional.of(employeeService.getEmployee(employeeId));
        List<EmployeeResource> resources = employeeToResource(employee.get());
        return new ResponseEntity<>(new Resources<>(resources), HttpStatus.OK);
    }

    @RequestMapping(value = "/{employeeId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteEmployee(@PathVariable("employeeId") Integer employeeId) {
        employeeService.deleteEmployee(employeeId);
        return new ResponseEntity<Object>(null, HttpStatus.ACCEPTED);
    }

    //-------- Email

    @RequestMapping(value = "/{employeeId}/emails/{emailId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateEmail(@PathVariable(value = "employeeId") Integer employeeId,
                                         @PathVariable(value = "emailId") Integer emailId,
                                         @RequestBody Email email) {
//        return Optional.of(employeeService.getEmployee(employeeId))
//                .map(employee -> {
//                    email.setId(emailId);
//                    employee.addEmail(email);
//                    employee = employeeService.updateEmployee(employee);
//
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(email.getId()).toUri());
//
//                    return new ResponseEntity(null, httpHeaders, HttpStatus.CREATED);
//                }).get();
        email.setId(emailId);
        employeeService.addEmailToEmployee(employeeId, email);
        return new ResponseEntity(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{employeeId}/emails/{emailId}", method = RequestMethod.DELETE)
    public void deleteEmployeeEmail(@PathVariable(value = "employeeId") Integer employeeId,
                                    @PathVariable(value = "emailId") Integer emailId) {
        employeeService.removeEmailFromEmployee(employeeId, emailId);
    }

    //--------- Address

    @RequestMapping(value = "/{employeeId}/address", method = RequestMethod.POST)
    public ResponseEntity<?> addEmployeeAddress(@PathVariable(value = "employeeId") Integer employeeId,
                                                @RequestBody Address address) {
        employeeService.addAddressToEmployee(employeeId, address);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(address.getId()).toUri());
        return new ResponseEntity(null, httpHeaders, HttpStatus.CREATED);
    }


    @RequestMapping(value = "/{employeeId}/address/{addressId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateAddress(@PathVariable(value = "employeeId") Integer employeeId,
                                           @PathVariable(value = "addressId") Integer emailId,
                                           @RequestBody Address address) {
        return Optional.of(employeeService.getEmployee(employeeId))
                .map(employee -> {
                    address.setId(emailId);
                    employee.addAddress(address);
                    employee = employeeService.updateEmployee(employee);

                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setLocation(ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{id}")
                            .buildAndExpand(address.getId()).toUri());

                    return new ResponseEntity(null, httpHeaders, HttpStatus.CREATED);
                }).get();
    }

    @RequestMapping(value = "/{employeeId}/address/{addressId}", method = RequestMethod.DELETE)
    public void deleteAddress(@PathVariable(value = "employeeId") Integer employeeId,
                              @PathVariable(value = "addressId") Integer addressId) {
        employeeService.removeAddressFromEmployee(employeeId, addressId);
    }

    //---------- Phone

    @RequestMapping(value = "/{employeeId}/phones/{phoneId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePhones(@PathVariable(value = "employeeId") Integer employeeId,
                                          @PathVariable(value = "phoneId") Integer phoneId,
                                          @RequestBody Phone phone) {
        phone.setId(phoneId);
        employeeService.addPhoneToEmployee(employeeId,phone);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(phone.getId()).toUri());

        return new ResponseEntity(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value =  "/{employeeId}/phones/{phoneId}", method = RequestMethod.DELETE)
    public void deleteEmployeePhone(@PathVariable(value = "employeeId") Integer employeeId,
                                    @PathVariable(value = "phoneId") Integer phoneId) {
        employeeService.removePhoneFromEmployee(employeeId, phoneId);
    }

    //------------- Timesheet

    @RequestMapping(path = "/{employeeId}/timesheet/{timesheetId}",
            consumes = "application/hal+json")
    public Resources<TimeSheetResource> getTimesheet(@PathVariable("employeeId") Integer employeeId,
                                                     @PathVariable("timesheetId") Integer timesheetId) {
        Timesheet timesheet = employeeService.getEmployee(employeeId).getTimesheets()
                .stream()
                .filter(timeSheet -> timeSheet.getId().equals(timesheetId))
                .findFirst()
                .get();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().build().toUri());
        Link selfLink = linkTo(methodOn(EmployeeController.class).getTimesheet(employeeId, timesheetId)).withSelfRel();
        List<TimeSheetResource> resources = timeSheetToResource(timesheet);
        return new Resources<>(resources, selfLink);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/{employeeId}/timesheet/{timesheetId}/timesheetentries/{timesheetEntryId}",
            consumes = "application/hal+json")
    public ResponseEntity<?> add(@PathVariable("employeeId") Integer employeeId,
                                 @PathVariable("timesheetId") Integer timesheetId,
                                 @PathVariable("timesheetEntryId") Integer timesheetEntryId,
                                 @RequestBody TimeSheetEntry input) {
        employeeService.addTimeSheetEntry(employeeId, timesheetId, input, timesheetEntryId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().build().toUri());

        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{employeeId}/timesheet/{timesheetId}/timesheetentries/{timesheetEntryId}", produces = "application/hal+json")
    public Resources<TimeSheetEntryResource> getTimeSheetEntry(@PathVariable("employeeId") Integer employeeId,
                                                               @PathVariable("timesheetId") Integer timesheetId,
                                                               @PathVariable("timesheetEntryId") Integer timesheetEntryId) {
        TimeSheetEntry timeSheetEntry = employeeService.getEmployee(employeeId).getTimesheets()
                .stream()
                .filter(timeSheet -> timeSheet.getId().equals(timesheetId)).findFirst()
                .get().getTimeSheetEntries()
                .stream()
                .filter(timesheet -> timesheet.getId().equals(timesheetEntryId)).findFirst()
                .get();

        final List<TimeSheetEntryResource> resources = timeSheetEntryToResource(timeSheetEntry);
        return new Resources<>(resources);
    }

    @RequestMapping(path = "/{employeeId}/timesheet", produces = "application/hal+json")
    public Resources<TimeSheetResource> getLatestTimeSheet(@PathVariable("employeeId") Integer employeeId) {
        final Link link = linkTo(methodOn(EmployeeController.class).getLatestTimeSheet(employeeId)).withSelfRel();
        final Timesheet latestTimeSheet = employeeService.getLatestTimeSheet(employeeId);
        List<TimeSheetResource> resources = timeSheetToResource(latestTimeSheet);
        return new Resources<>(resources, link);

    }

    @RequestMapping(path = "/{employeeId}/timesheet/{timesheetId}/timesheetentries", produces = "application/hal+json")
    public Resources<TimeSheetEntryResource> getTimeSheetEntrys(@PathVariable("employeeId") Integer employeeId,
                                                                @PathVariable("timesheetId") Integer timesheetId) {
        Optional<List<TimeSheetEntry>> timeSheetEntry = Optional.of(employeeService.getEmployee(employeeId).getTimesheets()
                .stream()
                .filter(timeSheet -> timeSheet.getId().equals(timesheetId))
                .findFirst()
                .get()
                .getTimeSheetEntries());
        final List<TimeSheetEntryResource> resources = timeSheetEntryToResource(timeSheetEntry.get().toArray(new TimeSheetEntry[timeSheetEntry.get().size()]));
        final Link link = linkTo(methodOn(EmployeeController.class).getTimeSheetEntrys(employeeId, timesheetId)).withSelfRel();
        return new Resources<>(resources, link);
    }


    //---------- Employee

    class EmployeeResource extends ResourceSupport {
        private final Employee employee;

        public EmployeeResource(Employee employee, List<Link> links) {
            this.employee = employee;
            this.add(linkTo(methodOn(EmployeeController.class).getEmployee(employee.getId())).withSelfRel());
            this.add(linkTo(methodOn(EmployeeController.class).getLatestTimeSheet(employee.getId())).withRel(TIMESHEETS));
            this.add(links);
        }

        public Employee getEmployee() {
            return this.employee;
        }


        public List<EmployeeController.TimeSheetResource> getTimeSheets() {
            final List<Timesheet> timesheets = this.employee.getTimesheets();
            return timeSheetToResource(timesheets.toArray(new Timesheet[timesheets.size()]));
        }
    }

    @SuppressWarnings("unchecked")
    private List<EmployeeResource> employeeToResource(Employee... employees) {
        List<EmployeeResource> resources = new ArrayList<>(employees.length);
        for (Employee employee : employees) {
            List<Link> links = new ArrayList<>();
            for (Job job : employee.getJobs()) {

                links.add(linkTo(methodOn(CustomerController.class).getContract(employee.getId(), job.getId())).withRel("contracts"));
            }
            resources.add(new EmployeeResource(employee, links));
        }
        return resources;
    }

    //--------- Timesheet

    class TimeSheetResource extends ResourceSupport {
        private final Timesheet timeSheet;

        public TimeSheetResource(Timesheet timeSheet) {
            this.timeSheet = timeSheet;
            this.add(linkTo(methodOn(EmployeeController.class).getTimesheet(timeSheet.getEmployee().getId(), timeSheet.getId())).withSelfRel());
            this.add(linkTo(methodOn(EmployeeController.class).getLatestTimeSheet(timeSheet.getEmployee().getId())).withRel(TIMESHEETS));
        }

        public Timesheet getTimesheet() {
            return this.timeSheet;
        }

        public List<TimeSheetEntryResource> getTimeSheetEntries() {
            final List<TimeSheetEntry> timeSheetEntries = this.timeSheet.getTimeSheetEntries();
            return timeSheetEntryToResource(timeSheetEntries.toArray(new TimeSheetEntry[timeSheetEntries.size()]));
        }
    }

    @SuppressWarnings("unchecked")
    List<TimeSheetResource> timeSheetToResource(Timesheet... timeSheets) {
        List<TimeSheetResource> resources = new ArrayList<>(timeSheets.length);
        for (Timesheet timesheet : timeSheets) {
            resources.add(new TimeSheetResource(timesheet));
        }
        return resources;
    }

    //--------- TimeSheetEntry

    class TimeSheetEntryResource extends ResourceSupport {
        private final TimeSheetEntry timeSheetEntry;

        public TimeSheetEntryResource(TimeSheetEntry timeSheetEntry) {
            this.timeSheetEntry = timeSheetEntry;
            // /{employeeId}/timesheet/{timesheetId}/timesheetentries/{timesheetEntryId}
//            this.add(linkTo(methodOn(EmployeeController.class)
//                    .getTimeSheetEntry(timeSheetEntry.getTimeSheet().getEmployee().getId(),
//                            timeSheetEntry.getTimeSheet().getId(),timeSheetEntry.getId())).withSelfRel());
//            this.add(linkTo(methodOn(EmployeeController.class).getLatestTimeSheet(timeSheetEntry.getTimeSheet().getEmployee().getId())).withRel(TIMESHEETS));
        }

        public TimeSheetEntry getTimeSheetEntry() {
            return timeSheetEntry;
        }
    }


    @SuppressWarnings("unchecked")
    private List<TimeSheetEntryResource> timeSheetEntryToResource(TimeSheetEntry... timeSheetEntries) {
        List<TimeSheetEntryResource> resources = new ArrayList<>(timeSheetEntries.length);
        for (TimeSheetEntry timesheetEntry : timeSheetEntries) {
            resources.add(new TimeSheetEntryResource(timesheetEntry));
        }
        return resources;
    }


}
