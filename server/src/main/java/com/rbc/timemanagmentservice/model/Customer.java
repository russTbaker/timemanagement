package com.rbc.timemanagmentservice.model;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by rbaker on 2/6/16.
 */
@Data
@Entity
@DiscriminatorValue(value = "CUSTOMER")
public class Customer extends User{
    private String name;
    private String contactName;
}
