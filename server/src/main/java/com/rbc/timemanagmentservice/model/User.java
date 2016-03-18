package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.joda.time.DateTime;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
//@DiscriminatorColumn(name="USER_TYPE")
@ObjectTypeConverter(
        name = "roleEnumFromStringConversion",
        objectType = Roles.Role.class,
        dataType = String.class,
        conversionValues = {
                @ConversionValue(objectValue = "administrator", dataValue = "administrator"),
                @ConversionValue(objectValue = "employee", dataValue = "employee"),
                @ConversionValue(objectValue = "guest", dataValue = "guest")
        }
)
@Table(name = "USER",  uniqueConstraints = @UniqueConstraint(columnNames = {"username"}))
public abstract class User {
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "USER_CONTRACT",
    joinColumns = @JoinColumn(name="CONTRACT_ID",referencedColumnName = "id"))
    @JsonIgnore
    protected List<Contract> contracts = new ArrayList<>();
    protected String username;
    protected String password;

    @OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
    private List<Address> address = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Email> emails = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Phone> phones = new ArrayList<>();

    @JsonIgnore
    public void addContract(Contract contract){
        if(!this.contracts.contains(contract)){
            this.contracts.add(contract);
            if(!contract.getUsers().contains(this)){
                contract.getUsers().add(this);
            }
        } else {
            this.contracts.remove(contract);
            this.contracts.add(contract);
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Integer id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "USER_ROLES", joinColumns = @JoinColumn(name = "id"),
        inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @RestResource(rel = "roles")
    protected List<Roles> roles = new ArrayList<>();

    private String firstName;
    private String lastName;
    private String dba;
    private DateTime deleteDate;

    public Integer getId() {
        return id;
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Roles> getRoles() {
        return roles;
    }

    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDba() {
        return dba;
    }

    public void setDba(String dba) {
        this.dba = dba;
    }





    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address){
        for(Address address1:address){
            addAddress(address1);
        }
    }

    public List<Email> getEmails() {
        return emails;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public DateTime getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(DateTime deleteDate) {
        this.deleteDate = deleteDate;
    }

    @JsonIgnore
    public void addAddress(Address address){
        if (!this.address.contains(address)) {
            this.address.add(address);
            address.setUser(this);
        } else {
            this.address.remove(address);
            this.address.add(address);
        }
    }

    @JsonIgnore
    public void addEmail(Email email){
        if (!this.emails.contains(email)) {
            this.emails.add(email);
            email.setUser(this);
        } else {
            this.emails.remove(email);
            this.emails.add(email);
        }
    }

    @JsonIgnore
    public void addPhone(Phone phone){
        if (!this.phones.contains(phone)) {
            this.phones.add(phone);
            phone.setUser(this);
        } else {
            this.phones.remove(phone);
            this.phones.add(phone);
        }
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
        return lastName != null ? lastName.equals(user.lastName) : user.lastName == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

}
