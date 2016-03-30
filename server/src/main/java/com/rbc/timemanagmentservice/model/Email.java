package com.rbc.timemanagmentservice.model;

import com.rbc.timemanagmentservice.model.validation.ValidEmail;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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

    @ValidEmail
    @NotNull
    @NotEmpty
    private String email;


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
