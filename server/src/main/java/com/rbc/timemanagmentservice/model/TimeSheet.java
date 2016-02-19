package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rbc.timemanagmentservice.model.serializer.JodaTimeDateSerializer;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@Entity
public class TimeSheet {
    public TimeSheet() {
    }

    public TimeSheet(Employee employee) {
        this.employee = employee;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<TimeSheetEntry> timeSheetEntries = new ArrayList<>();
    private Boolean billed;

    @ManyToOne
    @JsonIgnore
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

    public List<TimeSheetEntry> getTimeSheetEntries() {
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
        if (!(o instanceof TimeSheet)) return false;

        TimeSheet timeSheet = (TimeSheet) o;

        if (id != null ? !id.equals(timeSheet.id) : timeSheet.id != null) return false;
        if (timeSheetEntries != null ? !timeSheetEntries.equals(timeSheet.timeSheetEntries) : timeSheet.timeSheetEntries != null)
            return false;
        if (billed != null ? !billed.equals(timeSheet.billed) : timeSheet.billed != null) return false;
        return employee != null ? employee.equals(timeSheet.employee) : timeSheet.employee == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (timeSheetEntries != null ? timeSheetEntries.hashCode() : 0);
        result = 31 * result + (billed != null ? billed.hashCode() : 0);
        result = 31 * result + (employee != null ? employee.hashCode() : 0);
        return result;
    }
}
