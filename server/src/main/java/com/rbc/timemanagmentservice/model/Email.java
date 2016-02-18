package com.rbc.timemanagmentservice.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by rbaker on 2/6/16.
 */
@Data
@Entity
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @org.hibernate.validator.constraints.Email
    private String email;

//    @ManyToOne(fetch=FetchType.LAZY)
////    @JoinColumn(name="OWNER_ID")
//    private User user;
}
