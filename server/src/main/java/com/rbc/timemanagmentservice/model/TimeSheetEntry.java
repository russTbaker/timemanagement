package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rbc.timemanagmentservice.model.serializer.JodaTimeDateSerializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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

    public void setJobId(Integer job) {
        this.jobId = job;
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

    @Override
    public boolean equals(Object o) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        if (this == o) return true;
        if (!(o instanceof TimeSheetEntry)) return false;

        TimeSheetEntry that = (TimeSheetEntry) o;

        if (timesheetId != null ? !timesheetId.equals(that.timesheetId) : that.timesheetId != null) return false;
        if (jobId != null ? !jobId.equals(that.jobId) : that.jobId != null) return false;
        return date != null ? fmt.print(date).equals(fmt.print(that.date)) : that.date == null;

    }

    @Override
    public int hashCode() {
        int result = timesheetId != null ? timesheetId.hashCode() : 0;
        result = 31 * result + (jobId != null ? jobId.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
