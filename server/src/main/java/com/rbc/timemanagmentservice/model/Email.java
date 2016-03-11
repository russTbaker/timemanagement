package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;

/**
 * Created by russbaker on 2/19/16.
 */
@Entity
public class Email implements EntityMarkerInterface{
    public enum EmailTypes{
        billing,
        business,
        both;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @org.hibernate.validator.constraints.Email
    private String email;

    @ManyToOne//(fetch = FetchType.LAZY)
    @JsonIgnore
    @RestResource(exported = false)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;

        Email email = (Email) o;

        return id != null ? id.equals(email.id) : email.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
