package com.rbc.timemanagmentservice.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@Entity
@Table(name = "CUSTOMER")
@PrimaryKeyJoinColumn(name = "CUSTOMER_ID")
public class Customer extends User{
    public Customer() {
        super();
        Roles roles = new Roles();
        roles.setRole(Roles.Role.customer);
        this.roles.add(roles);
    }


    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Invoice> invoices = new ArrayList<>();




    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void addInvoiceToCustomer(final Invoice invoice){
        if(!invoices.contains(invoice)){
            invoices.add(invoice);
            invoice.setCustomer(this);
        } else {
            invoices.remove(invoice);
            invoices.add(invoice);
        }
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
