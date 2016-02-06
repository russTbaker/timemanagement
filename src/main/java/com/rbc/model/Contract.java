package com.rbc.model;

import lombok.Data;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Created by rbaker on 2/6/16.
 */
@Data
@Entity
public class Contract {
    public enum Terms{
        net15,
        net30,
        net45
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(targetEntity = Customer.class)
    private Customer customer;
    private Double value;
    private DateTime startDate;
    private DateTime endDate;
    private Double rate;

    @Enumerated(value = EnumType.STRING)
    private Terms terms;

}
