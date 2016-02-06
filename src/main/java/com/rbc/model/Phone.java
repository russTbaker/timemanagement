package com.rbc.model;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by rbaker on 2/6/16.
 */
@Data
@Entity
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String phone;

    @ManyToOne(targetEntity = Customer.class)
    private Customer customer;
}
