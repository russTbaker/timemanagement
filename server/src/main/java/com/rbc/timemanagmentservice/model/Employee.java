package com.rbc.timemanagmentservice.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@Data
@Entity
@DiscriminatorValue(value = "EMPLOYEE")
public class Employee extends User{
    private String username;
    private String password;

    @OneToMany
    private List<TimeSheet> timesheets;
}
