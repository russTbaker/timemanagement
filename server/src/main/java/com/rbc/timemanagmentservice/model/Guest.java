package com.rbc.timemanagmentservice.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * Created by russbaker on 3/7/16.
 */
@Entity
@Table(name = "GUEST")
@PrimaryKeyJoinColumn(name = "GUEST_ID")
public class Guest extends User{
}
