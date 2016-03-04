package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rbc.timemanagmentservice.model.serializer.JodaTimeDateSerializer;
import org.joda.time.DateTime;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@Entity
@Table(name = "TIMESHEET")
public class Timesheet {
    public Timesheet() {
        timeSheetEntries = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToMany(mappedBy = "timesheet",fetch = FetchType.EAGER)
    @JsonIgnore
    private List<TimesheetEntry> timeSheetEntries;

    private Boolean billed;

    @ManyToOne(targetEntity = Employee.class)
    @JsonIgnore
    @RestResource(exported = false)
    private Employee employee;

    @JsonSerialize(using = JodaTimeDateSerializer.class)
    private DateTime startDate;

    @JsonSerialize(using = JodaTimeDateSerializer.class)
    private DateTime endDate;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void addTimesheetEntry(final TimesheetEntry timeSheetEntry){
//        if(timeSheetEntries == null){
//            timeSheetEntries =  new ArrayList<>();
//        }
//        if(!timeSheetEntries.contains(timeSheetEntry)){
//            timeSheetEntries.add(timeSheetEntry);
//        } else {
//            timeSheetEntries.remove(timeSheetEntry);
//            timeSheetEntries.add(timeSheetEntry);
//        }
        if (!getTimeSheetEntries().contains(timeSheetEntry)) {
            getTimeSheetEntries().add(timeSheetEntry);
//            if (timeSheetEntry.getTimesheet() != null) {
//                timeSheetEntry.getTimesheet().getTimeSheetEntries().remove(timeSheetEntry);
//            }
        } else {
            getTimeSheetEntries().remove(timeSheetEntry);
            getTimeSheetEntries().add(timeSheetEntry);
        }
        timeSheetEntry.setTimesheet(this);
    }

    public List<TimesheetEntry> getTimeSheetEntries() {
        return timeSheetEntries;
    }

    public Boolean getBilled() {
        return billed;
    }

    public void setBilled(Boolean billed) {
        this.billed = billed;
    }

    public Employee getEmployee() {
        return employee;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Timesheet)) return false;

        Timesheet timeSheet = (Timesheet) o;

        return id != null ? id.equals(timeSheet.id) : timeSheet.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
