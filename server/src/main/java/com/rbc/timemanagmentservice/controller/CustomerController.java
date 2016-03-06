package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by russbaker on 2/18/16.
 */
@RestController
@RequestMapping("/hydrated/customers")
public class CustomerController extends UserController<Customer>{



    @Autowired
    public CustomerController(CustomerService customerService) {
        super(customerService);
    }

}
