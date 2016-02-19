package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rbc.timemanagmentservice.model.serializer.JodaTimeDateSerializer;
import lombok.ToString;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Created by rbaker on 2/6/16.
 */
@Entity
@ToString(exclude = "contract")
public class TimeSheetEntry {
    public TimeSheetEntry() {
    }
    public TimeSheetEntry(TimeSheet timeSheet, Contract contract) {
        this.timeSheet = timeSheet;
        this.contract = contract;
    }



    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @JsonIgnore
    @ManyToOne
    private TimeSheet timeSheet;

    @ManyToOne
    @JsonIgnore
    private Contract contract;

    @JsonProperty(value = "contract")
    public Integer getContractId(){
        return contract.getId();
    }

//    @JsonProperty(value = "contract")
//    public void setContractId(Integer contractId){
//        this.contract.setId(contractId);
//    }

    @JsonSerialize(using = JodaTimeDateSerializer.class)
    private DateTime date;
    private Integer hours;




    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Contract getContract() {
        return contract;
    }

    public TimeSheet getTimeSheet() {
        return timeSheet;
    }
}
