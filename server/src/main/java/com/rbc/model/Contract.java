package com.rbc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Created by rbaker on 2/6/16.
 */
@Data
@Entity
public class Contract {
    public enum Terms{
        net15,
        net30,
        net45
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(targetEntity = Customer.class)
    @JoinColumn(name="OWNER_ID")
    private Customer customer;
    private Double value;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd,HH:00", timezone="MST")
    private DateTime startDate;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd,HH:00", timezone="MST")
    private DateTime endDate;
    private Double rate;

    @Enumerated(value = EnumType.STRING)
    private Terms terms;

}
