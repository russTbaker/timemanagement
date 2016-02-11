package com.rbc.timemanagmentservice.model;

import lombok.Data;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

import javax.persistence.*;
import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@Data
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
public abstract class User {
    public enum Roles {
        administrator,
        employee,
        customer,
        guest
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    @Enumerated(value = EnumType.STRING)
    private Roles roles;
    private String firstName;
    private String lastName;
    @OneToMany(mappedBy = "user")
    private List<Email> emails;
}
