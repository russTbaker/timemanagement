package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by russbaker on 2/18/16.
 */
@RestController
@RequestMapping("/hydrated/customer/")
public class CustomerController {


    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @RequestMapping(produces = "application/hal+json")
    public Resources<CustomerResource> getCustomers(){
        List<Link> links = new LinkedList<>();
        links.add(linkTo(methodOn(CustomerController.class).getCustomers()).withSelfRel());
        final List<Customer> customers = customerService.findAll();
        List<CustomerResource> customerResources = customerToResource(customers.toArray(new Customer[customers.size()]));
        return new Resources<>(customerResources,links);
    }

    private List<CustomerResource> customerToResource(Customer... customers) {
        List<CustomerResource> resources = new ArrayList<>(customers.length);
        for (Customer customer : customers) {
            resources.add(new CustomerResource(customer));
        }
        return resources;
    }

    class CustomerResource extends ResourceSupport {
        private final Customer employee;

        public CustomerResource(Customer employee) {
            this.employee = employee;
            this.add(linkTo(methodOn(CustomerController.class).getCustomers()).withSelfRel());
            this.add(linkTo(CustomerController.class).withRel("timesheets"));
        }

        public Customer getCustomer() {
            return this.employee;
        }


//        public List<TimeSheetResource> getTimeSheets() {
//            final List<TimeSheet> timesheets = this.employee.getTimesheets();
//            return timeSheetToResource(timesheets.toArray(new TimeSheet[timesheets.size()]));
//        }
//
//        public List<EmailResource> getEmails() {
//            final List<Email> emails = this.employee.getEmails();
//            return emailToResource(emails.toArray(new Email[emails.size()]));
//        }
    }
}
