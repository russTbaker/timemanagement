package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@Entity
@Table(name = "EMPLOYEE",  uniqueConstraints = @UniqueConstraint(columnNames = {"username"}))
@PrimaryKeyJoinColumn(name = "EMPLOYEE_ID")
public class Employee extends User{
    private String username;

    @JsonIgnore
    private String password;


    @ManyToMany(mappedBy = "employees",fetch = FetchType.EAGER)
    private List<Job> jobs = new ArrayList<>();

    public Employee() {
        super();
        final Roles roles = new Roles();
        roles.setRole(Roles.Role.employee);
        this.roles.add(roles);
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
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
