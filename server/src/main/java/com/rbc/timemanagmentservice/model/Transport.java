package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rbc.timemanagmentservice.model.User;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

/**
 * Created by russbaker on 2/19/16.
 */
@Entity
public class Transport {
    public enum EmailTypes{
        billing,
        business,
        both;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @org.hibernate.validator.constraints.Email
    @JsonProperty(value = "emailAddress")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;

    @Enumerated(value = EnumType.STRING)
    private EmailTypes emailType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EmailTypes getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailTypes emailType) {
        this.emailType = emailType;
    }
}
