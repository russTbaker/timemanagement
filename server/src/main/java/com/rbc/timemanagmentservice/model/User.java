package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@ObjectTypeConverter(
        name = "roleEnumFromStringConversion",
        objectType = User.Role.class,
        dataType = String.class,
        conversionValues = {
                @ConversionValue(objectValue = "administrator", dataValue = "administrator"),
                @ConversionValue(objectValue = "employee", dataValue = "employee"),
                @ConversionValue(objectValue = "guest", dataValue = "guest")
        }
)
@Table(name = "USER",  uniqueConstraints = @UniqueConstraint(columnNames = {"username"}))
public abstract class User {

    public enum Role {
        administrator,
        employee,
        customer,
        guest
    }


    @OneToMany
    @JsonIgnore
    private List<Address> addresses = new ArrayList<>();

    @OneToMany
    private List<Email> emails= new ArrayList<>();

    @OneToMany
    private List<Phone> phones= new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "USER_CONTRACT",
            joinColumns = @JoinColumn(name="CONTRACT_ID",referencedColumnName = "id"))
    @JsonIgnore
    protected List<Contract> contracts = new ArrayList<>();
    
    protected String username;
    protected String password;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Integer id;

    @Enumerated(value = EnumType.STRING)
    protected Role role;// = new ArrayList<>();

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

    public abstract Role getRole();

    public void setRole(Role role) {
        this.role = role;
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





    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public DateTime getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(DateTime deleteDate) {
        this.deleteDate = deleteDate;
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
