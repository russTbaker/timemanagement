package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rbc.timemanagmentservice.model.serializer.JodaTimeDateDeserializer;
import com.rbc.timemanagmentservice.model.serializer.JodaTimeDateSerializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;

/**
 * Created by rbaker on 2/6/16.
 */
@Entity
@Table(name = "TIME_ENTRY", uniqueConstraints = @UniqueConstraint(columnNames = {"job_id","date"}))
public class TimeEntry {
    public TimeEntry() {
    }
    @Transient
    private DateTimeFormatter FMT = DateTimeFormat.forPattern("EEE MMM d, yyyy");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Transient
    private Integer jobId;

    @ManyToOne
    @JsonIgnore
    private Job job;

    @JsonSerialize(using = JodaTimeDateSerializer.class)
    @JsonDeserialize(using = JodaTimeDateDeserializer.class)
    private DateTime date;
    private Integer hours;
    private Boolean billed;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getJobId() {
        return job != null ? job.getId(): jobId;
    }

    public void setJobId(Integer job) {
        this.jobId = job;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
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

    public String getEntryDate(){
        return FMT.print(date);
    }
    public Boolean getBilled() {
        return billed;
    }

    public void setBilled(Boolean billed) {
        this.billed = billed;
    }

    @RestResource(exported = false)
    @JsonIgnore
    public Double getAmount(){
        return job.getRate() * hours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeEntry)) return false;

        TimeEntry that = (TimeEntry) o;

        return date != null ? date.withTimeAtStartOfDay().equals(that.date.withTimeAtStartOfDay()) : that.date == null;

    }

    @Override
    public int hashCode() {
        return date != null ? date.hashCode() : 0;
    }
}
