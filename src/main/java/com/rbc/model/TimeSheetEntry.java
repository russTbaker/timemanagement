package com.rbc.model;

import lombok.Data;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Created by rbaker on 2/6/16.
 */
@Data
@Entity
public class TimeSheetEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne(targetEntity = TimeSheet.class)
    private TimeSheet timeSheet;
    private DateTime date;
    private Integer hours;
    @ManyToOne(targetEntity = Contract.class)
    private Contract contract;
}
