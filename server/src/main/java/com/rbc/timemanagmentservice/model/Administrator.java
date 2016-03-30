package com.rbc.timemanagmentservice.model;

import javax.persistence.Entity;
import javax.persistence.PostLoad;
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
        this.setRole(Role.administrator);
    }


    @Override
    public Role getRole() {
        return Role.administrator;
    }
}
