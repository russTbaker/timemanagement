package com.rbc.model;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by rbaker on 2/6/16.
 */
@Data
@Entity
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @org.hibernate.validator.constraints.Email
    private String email;

    @ManyToOne(targetEntity = Customer.class)
    private Customer customer;
}
