package com.rbc.timemanagmentservice.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by russbaker on 2/24/16.
 */
@Entity
public class Job implements EntityMarkerInterface{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    private String description;

    private Double rate;

    @ManyToOne(cascade = CascadeType.ALL)
    private Contract contract;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Employee employee;

    @OneToMany(mappedBy = "job", fetch = FetchType.EAGER)
    private List<TimesheetEntry> timeSheetEntries = new ArrayList<>();

    public void addTimesheetEntry(TimesheetEntry timeSheetEntry) {
        if (!this.timeSheetEntries.contains(timeSheetEntry)) {
            this.timeSheetEntries.add(timeSheetEntry);
        } else {
            this.timeSheetEntries.remove(timeSheetEntry);
            this.timeSheetEntries.add(timeSheetEntry);
        }
        timeSheetEntry.setJob(this);
    }

    public List<TimesheetEntry> getTimeSheetEntries() {
        return timeSheetEntries;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }


    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Job)) return false;

        Job job = (Job) o;

        return id != null ? id.equals(job.id) : job.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
