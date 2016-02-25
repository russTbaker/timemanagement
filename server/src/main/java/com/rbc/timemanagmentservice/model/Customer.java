package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@Entity
@DiscriminatorValue(value = "CUSTOMER")
public class Customer extends User{
    public Customer() {
        super();
        this.setRoles(Roles.customer);
    }

    private String name;
    private String contactName;

    @OneToMany//(mappedBy = "users", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Contract> contracts = new ArrayList<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @JsonIgnore
    public void addContract(Contract contract){
//
        if(!this.contracts.contains(contract)){
            this.contracts.add(contract);
            contract.setCustomer(this);
        } else {
            this.contracts.remove(contract);
            this.contracts.add(contract);
        }
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        if (!super.equals(o)) return false;

        Customer customer = (Customer) o;

        if (name != null ? !name.equals(customer.name) : customer.name != null) return false;
        return contactName != null ? contactName.equals(customer.contactName) : customer.contactName == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (contactName != null ? contactName.hashCode() : 0);
        return result;
    }
}
