package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.Job;
import com.rbc.timemanagmentservice.model.TimeEntry;
import com.rbc.timemanagmentservice.model.User;
import com.rbc.timemanagmentservice.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;


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

    @RequestMapping(value = "/{userId}",method = RequestMethod.GET)
    public User getUser(@PathVariable("userId") Integer userId){
        return employeeService.getUser(userId);
    }

    //------------- Timesheet
    @RequestMapping(method = RequestMethod.PUT, path = "/{employeeId}/timesheets/{jobId}")
    public ResponseEntity<?> createNewEmployeeTimesheet(@PathVariable("jobId") Integer jobId){
        final Integer employeeId = getCurrentEmployee().getId();
        List<TimeEntry> timeEntries = employeeService.getTimeEntriesForEmployeeJobs(employeeId,jobId);
        return new ResponseEntity<>(null,getHttpHeadersForEntity(()->employeeId,"timeentries"),HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.PUT, path = "/timesheets/{jobId}/timesheetentries")
    public ResponseEntity<?> updateTimesheet(@PathVariable("jobId") Integer jobId,
                                             @RequestBody List<TimeEntry> timesheetEntries){
        employeeService.addTimeSheetEntries(timesheetEntries, jobId);
        return new ResponseEntity<>(null, getHttpHeadersForEntity(() -> jobId,"timesheets"),HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "/{employeeId}/jobs/{jobId}", method = RequestMethod.POST)
    public ResponseEntity<?> addJobToEmployee(@PathVariable("employeeId") Integer employeeId,
                                              @PathVariable("jobId") Integer jobId) {
//        final Integer employeeId = getCurrentEmployee().getId();
        employeeService.addEmployeeToJob(employeeId,jobId);
        return new ResponseEntity<>(null,getHttpHeadersForEntity(()->jobId,"jobs"),HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "/jobs", produces = "application/hal+json")
    public Resources<JobsResource> getEmployeeJobs(){
        final Integer employeeId = getCurrentEmployee().getId();
        final List<Job> employeesAvailableJobs = employeeService.getEmployeeJobs(employeeId);
        List<JobsResource> jobsResources = jobToResource(employeesAvailableJobs.toArray(new Job[employeesAvailableJobs.size()]));
        return new Resources<>(jobsResources);
    }

    class JobsResource extends ResourceSupport {
        private final Job job;

        public JobsResource(Job job) {
            this.job = job;
            this.add(new Link("/employees/jobs/"+job.getId(),"self"));
        }

        public JobsResource(Job job, Link link){
            this.job = job;
            this.add(link);
        }

        public Job getJob(){
            return job;
        }

        public Integer getJobId(){
            return job.getId();
        }

        public List<TimeEntry> getTimeEntries(){
            return job.getTimeEntries();
        }
    }

    @SuppressWarnings("unchecked")
    List<JobsResource> jobToResource(Job... jobs) {
        List<JobsResource> resources = new ArrayList<>(jobs.length);
        for (Job job : jobs) {
            Link link = new Link(ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/api/jobs/{id}")
                    .buildAndExpand(job.getId()).toUri().getPath());
            resources.add(new JobsResource(job,link));
        }
        return resources;
    }

    private Employee getCurrentEmployee(){
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String username = auth.getName();
        return employeeService.findByUsername(username);
    }

}
