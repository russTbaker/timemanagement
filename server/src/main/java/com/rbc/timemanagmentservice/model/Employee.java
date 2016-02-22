package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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

    @OneToMany
    @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "USER_ID")
    @JsonProperty(value = "timesheets")
    private List<TimeSheet> timesheets = new ArrayList<>();

    public Employee() {
        super();
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
        return timesheets != null ? timesheets.equals(employee.timesheets) : employee.timesheets == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (timesheets != null ? timesheets.hashCode() : 0);
        return result;
    }
}
