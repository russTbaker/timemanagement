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
public class
Contract {


    public void addJob(Job job) {
        if (!this.jobs.contains(job)) {
            this.jobs.add(job);
            job.setContract(this);
        } else {
            this.jobs.remove(job);
            this.jobs.add(job);
        }
    }

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

    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Job> jobs = new ArrayList<>();

    @ManyToOne
//    @JoinTable(name = "CUSTOMER_CONTRACT",
//    joinColumns=@JoinColumn(name = "USER_ID"),
//            inverseJoinColumns=@JoinColumn(name="CONTRACT_ID"))
    @RestResource(exported = false)
    private Customer customer;



    public List<Job> getJobs() {
        return jobs;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
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

        return id != null ? id.equals(contract.id) : contract.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
