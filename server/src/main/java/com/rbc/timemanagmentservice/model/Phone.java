package com.rbc.timemanagmentservice.model;

import javax.persistence.*;

/**
 * Created by rbaker on 2/6/16.
 */
@Entity
public class Phone implements EntityMarkerInterface{


    public enum PhoneTypes{
        fax,
        mobile,
        office;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String phone;

    @Enumerated(value = EnumType.STRING)
    private PhoneTypes phoneType;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public PhoneTypes getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(PhoneTypes phoneType) {
        this.phoneType = phoneType;
    }
}
