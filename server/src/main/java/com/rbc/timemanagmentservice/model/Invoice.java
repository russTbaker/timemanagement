package com.rbc.timemanagmentservice.model;

import lombok.Data;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@Data
@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToMany(targetEntity = Contract.class)
    private List<Contract> contracts;
    @OneToMany(targetEntity = Timesheet.class)
    private List<Timesheet> timeSheets;
    private DateTime invoiceDate;
    private DateTime dueDate;
    private boolean paid;
    private Double amount;


}
