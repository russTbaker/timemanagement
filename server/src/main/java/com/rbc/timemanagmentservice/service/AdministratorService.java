package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Administrator;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import com.rbc.timemanagmentservice.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by russbaker on 3/17/16.
 */
@Repository
public class AdministratorService extends UserService<Administrator> {

    @Autowired
    public AdministratorService(UserRepository userRepository, ContractRepository contractRepository) {
        super(userRepository, contractRepository);
    }
}
