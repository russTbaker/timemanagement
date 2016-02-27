package com.rbc.timemanagmentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;

/**
 * Created by rbaker on 2/6/16.
 */
@Data
@ToString(exclude = "user")
@EqualsAndHashCode(exclude = {"phone","user"})
@Entity
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String phone;

    @ManyToOne
    @JsonIgnore
    @RestResource(exported = false)
    private User user;
}
