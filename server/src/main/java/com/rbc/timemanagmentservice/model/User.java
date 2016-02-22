package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="USER_TYPE")
@ObjectTypeConverter(
        name = "roleEnumFromStringConversion",
        objectType = User.Roles.class,
        dataType = String.class,
        conversionValues = {
                @ConversionValue(objectValue = "administrator", dataValue = "administrator"),
                @ConversionValue(objectValue = "employee", dataValue = "employee"),
                @ConversionValue(objectValue = "guest", dataValue = "guest")
        }
)
@Data
public abstract class User {
    public enum Roles {
        administrator,
        employee,
        customer,
        guest
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID")
    protected Integer id;


    @Enumerated(value = EnumType.STRING)
    private Roles roles;
    private String firstName;
    private String lastName;
    private String dba;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Address> address = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Transport> emails = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Phone> phones = new ArrayList<>();


    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private List<Contract> contracts = new ArrayList<>();

    @JsonIgnore
    public void addContract(Contract contract){
        this.contracts.add(contract);
        if(!contract.getUsers().contains(this)){
            contract.addUser(this);
        }
    }

    @JsonIgnore
    public void addAddress(Address address){
        if (!this.address.contains(address)) {
            this.address.add(address);
            address.setUser(this);
        }
    }


}
