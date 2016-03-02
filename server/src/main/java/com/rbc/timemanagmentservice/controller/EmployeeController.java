package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.Job;
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
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


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

    @RequestMapping(method = RequestMethod.PUT, path = "/{employeeId}/timesheets/{timesheetId}/timesheetentries/{timesheetEntryId}",
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

    @RequestMapping(method = RequestMethod.PUT, path = "/{employeeId}/timesheets/{timesheetId}/timesheetentries")
    public ResponseEntity<?> updateTimesheet(@PathVariable("employeeId") Integer employeeId,
                                             @PathVariable("timesheetId") Integer timesheetId,
                                             @RequestBody List<TimeSheetEntry> timesheetEntries){
        employeeService.addTimeSheetEntries(timesheetEntries,employeeId,timesheetId);
        return new ResponseEntity<>(null, getHttpHeadersForEntity(() -> timesheetId,"timesheets"),HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "/{employeeId}/jobs/{jobId}", method = RequestMethod.POST)
    public ResponseEntity<?> addJobToEmployee(@PathVariable("employeeId") Integer employeeId,
                                              @PathVariable("jobId") Integer jobId) {
        employeeService.addEmployeeToJob(employeeId,jobId);
        return new ResponseEntity<>(null,getHttpHeadersForEntity(()->jobId,"jobs"),HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "/{employeeId}/jobs", produces = "application/hal+json")
    public Resources<JobsResource> getEmployeeJobs(@PathVariable("employeeId") Integer employeeId){
        final List<Job> employeesAvailableJobs = employeeService.getEmployeesAvailableJobs(employeeId);
        List<JobsResource> jobsResources = jobToResource(employeesAvailableJobs.toArray(new Job[employeesAvailableJobs.size()]));
        final Link link = linkTo(methodOn(EmployeeController.class).getEmployeeJobs(employeeId)).withSelfRel();
        return new Resources<>(jobsResources,link);
    }

    class JobsResource extends ResourceSupport {
        private final Job job;

        public JobsResource(Job job) {
            this.job = job;
            this.add(linkTo(methodOn(EmployeeController.class).getEmployeeJobs(job.getId())).withSelfRel());
        }

        public Job getJob(){
            return job;
        }

        public Integer getJobId(){
            return job.getId();
        }
    }

    @SuppressWarnings("unchecked")
    List<JobsResource> jobToResource(Job... jobs) {
        List<JobsResource> resources = new ArrayList<>(jobs.length);
        for (Job job : jobs) {

            resources.add(new JobsResource(job));
        }
        return resources;
    }

}
