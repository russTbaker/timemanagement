package com.rbc.timemanagmentservice.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by russbaker on 2/24/16.
 */
@Entity
@Table(name = "JOB")
public class Job implements EntityMarkerInterface{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    private String description;

    private Double rate;

    @ManyToOne//(cascade = CascadeType.ALL)
    private Contract contract;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "EMPLOYEE_JOB", joinColumns = @JoinColumn(name="EMPLOYEE_ID"), inverseJoinColumns = @JoinColumn(name = "JOB_ID"))
    private List<Employee> employees = new ArrayList<>();


    @OneToMany(cascade = CascadeType.ALL,mappedBy = "job", fetch = FetchType.EAGER)
    private List<TimeEntry> timeEntries = new ArrayList<>();

    public void addTimeEntry(TimeEntry timeSheetEntry) {
        if (!this.timeEntries.contains(timeSheetEntry)) {
            this.timeEntries.add(timeSheetEntry);
        } else {
            this.timeEntries.remove(timeSheetEntry);
            this.timeEntries.add(timeSheetEntry);
        }
        timeSheetEntry.setJob(this);
    }

    public List<TimeEntry> getTimeEntries() {
        return timeEntries;
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

    public List<Employee> getEmployees() {
        return employees;
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
