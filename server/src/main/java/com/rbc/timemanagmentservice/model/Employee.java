package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonProperty(value = "timesheets")
    private List<TimeSheet> timesheets = new ArrayList<>();

    @ManyToMany
    @JsonIgnore
    private List<Contract> contracts = new ArrayList<>();

    public Employee(Contract contract) {
        this.contracts.add(contract);
    }

    public Employee() {
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public List<TimeSheet> getTimesheets() {
        return timesheets;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        if (!super.equals(o)) return false;

        Employee employee = (Employee) o;

        if (username != null ? !username.equals(employee.username) : employee.username != null) return false;
        if (password != null ? !password.equals(employee.password) : employee.password != null) return false;
        if (timesheets != null ? !timesheets.equals(employee.timesheets) : employee.timesheets != null) return false;
        return contracts != null ? contracts.equals(employee.contracts) : employee.contracts == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (timesheets != null ? timesheets.hashCode() : 0);
        result = 31 * result + (contracts != null ? contracts.hashCode() : 0);
        return result;
    }
}
