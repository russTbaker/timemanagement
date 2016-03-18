package com.rbc.timemanagmentservice.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * Created by russbaker on 3/7/16.
 */
@Entity
@Table(name = "ADMINISTRATOR")
@PrimaryKeyJoinColumn(name = "ADMINISTRATOR_ID")
public class Administrator extends User{
    public Administrator() {
        super();
        final Roles roles = new Roles();
        roles.setRole(Roles.Role.administrator);
        this.roles.add(roles);
    }
}
