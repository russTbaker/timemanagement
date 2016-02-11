package com.rbc.timemanagmentservice.model;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by rbaker on 2/6/16.
 */
@Data
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(targetEntity = Customer.class)
    private Customer customer;
    private String street1;
    private String street2;
    private String city;
    private String state;
    private String zip;
}
