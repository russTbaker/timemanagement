package com.rbc.timemanagmentservice.model;

import javax.persistence.*;

/**
 * Created by russbaker on 3/5/16.
 */
@Entity
@Table(name = "ROLES")
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    public enum Role {
        administrator,
        employee,
        customer,
        guest
    }

    @Enumerated(value = EnumType.STRING)
    private Role role;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
