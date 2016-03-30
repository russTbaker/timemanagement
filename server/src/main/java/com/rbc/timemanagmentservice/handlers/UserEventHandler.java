package com.rbc.timemanagmentservice.handlers;

import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkSave;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Created by russbaker on 3/21/16.
 */
@Component
@RepositoryEventHandler(User.class)
public class UserEventHandler {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserEventHandler(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @HandleBeforeCreate
    public void handleCreateUser(final User user){
        if(!(user instanceof Customer)){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
    }


    @HandleBeforeSave
    public void handleSaveUser(final User user){
        if(!(user instanceof Customer)){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
    }
}
