package com.rbc.timemanagmentservice.model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
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
        this.setRoles(Roles.customer);
    }


    @OneToMany(mappedBy = "customer")
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
