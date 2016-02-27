package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@Entity
@DiscriminatorValue(value = "EMPLOYEE")
public class Employee extends User{
    private String username;
    private String password;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Timesheet> timesheets = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Job> jobs = new ArrayList<>();

    public Employee() {
        super();
    }


    public List<Timesheet> getTimesheets() {
        return timesheets;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @JsonIgnore
    public void addTimeSheet(Timesheet timeSheet) {
        if (!this.timesheets.contains(timeSheet)) {
            this.timesheets.add(timeSheet);
            timeSheet.setEmployee(this);
        } else {
            this.timesheets.remove(timeSheet);
            this.timesheets.add(timeSheet);
        }
    }

    @JsonIgnore
    public void addJob(Job job) {
        if (!this.jobs.contains(job)) {
            this.jobs.add(job);
        } else {
            this.jobs.remove(job);
            this.jobs.add(job);
        }
    }




}
