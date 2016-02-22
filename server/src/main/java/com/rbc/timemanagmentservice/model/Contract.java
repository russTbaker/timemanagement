package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rbc.timemanagmentservice.model.serializer.JodaTimeDateSerializer;
import lombok.ToString;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@Entity
public class Contract {
    public enum Terms{
        net15,
        net30,
        net45
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CONTRACT_ID")
    private Integer id;

    private Double value;

    @JsonSerialize(using = JodaTimeDateSerializer.class)
    private DateTime startDate;

    @JsonSerialize(using = JodaTimeDateSerializer.class)
    private DateTime endDate;

    private Double rate;

    @Enumerated(value = EnumType.STRING)
    private Terms terms;

    @OneToMany
    private List<TimeSheetEntry> timeSheetEntries = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "USER_CONTRACT",
    joinColumns  =@JoinColumn(name = "USER_ID"),
            inverseJoinColumns=@JoinColumn(name="CONTRACT_ID"))
    @JsonIgnore
    private List<User> users = new ArrayList<>();

    public void addUser(User user){
        if(!getUsers().contains(user)){
            getUsers().add(user);
        }
//        if(!user.getContracts().contains(this)){
//            user.getContracts().add(this);
//        }
    }


    public List<TimeSheetEntry> getTimeSheetEntries() {
        return timeSheetEntries;
    }

    public List<User> getUsers() {
        return users;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
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

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Terms getTerms() {
        return terms;
    }

    public void setTerms(Terms terms) {
        this.terms = terms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contract)) return false;

        Contract contract = (Contract) o;

        if (id != null ? !id.equals(contract.id) : contract.id != null) return false;
        if (value != null ? !value.equals(contract.value) : contract.value != null) return false;
        if (startDate != null ? !startDate.equals(contract.startDate) : contract.startDate != null) return false;
        if (endDate != null ? !endDate.equals(contract.endDate) : contract.endDate != null) return false;
        if (rate != null ? !rate.equals(contract.rate) : contract.rate != null) return false;
        if (terms != contract.terms) return false;
        if (timeSheetEntries != null ? !timeSheetEntries.equals(contract.timeSheetEntries) : contract.timeSheetEntries != null)
            return false;
        return users != null ? users.equals(contract.users) : contract.users == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (rate != null ? rate.hashCode() : 0);
        result = 31 * result + (terms != null ? terms.hashCode() : 0);
        result = 31 * result + (timeSheetEntries != null ? timeSheetEntries.hashCode() : 0);
        result = 31 * result + (users != null ? users.hashCode() : 0);
        return result;
    }
}
