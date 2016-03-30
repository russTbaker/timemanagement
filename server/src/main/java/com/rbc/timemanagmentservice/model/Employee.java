package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@Entity
@Table(name = "EMPLOYEE")
@PrimaryKeyJoinColumn(name = "EMPLOYEE_ID")
public class Employee extends User{


    @ManyToMany(mappedBy = "employees",fetch = FetchType.EAGER)
    private List<Job> jobs = new ArrayList<>();

    public Employee() {
        super();
        setRole(Role.employee);
    }




    public List<Job> getJobs() {
        return jobs;
    }


    @JsonIgnore
    public void addJob(Job job) {
        if (!this.jobs.contains(job)) {
            this.jobs.add(job);
            if(!job.getEmployees().contains(this)){
                job.getEmployees().add(this);
            }
        } else {
            this.jobs.remove(job);
            this.jobs.add(job);
        }

    }

    public void removeJob(Job job){
        this.jobs.remove(job);
        job.getEmployees().remove(this);
    }

    @Override
    public Role getRole() {
        return Role.employee;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode(){
        return super.hashCode();
    }

}
