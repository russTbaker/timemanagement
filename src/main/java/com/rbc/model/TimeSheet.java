package com.rbc.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@Data
@Entity
public class TimeSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToMany
    private List<TimeSheetEntry> timeSheetEntry;
    private Boolean billed;
}
