package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rbc.timemanagmentservice.model.serializer.JodaTimeDateSerializer;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Created by rbaker on 2/6/16.
 */
@Entity
public class TimeSheetEntry {
    public TimeSheetEntry() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    @Column(name = "TIMESHEET_ID")
    private Integer timesheetId;

    @Column(name = "JOB_ID")
    private Integer jobId;

    @JsonSerialize(using = JodaTimeDateSerializer.class)
    private DateTime date;
    private Integer hours;




    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(Integer timesheetId) {
        this.timesheetId = timesheetId;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }
}
