package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by russbaker on 2/18/16.
 */
@RestController
@RequestMapping("/hydrated/customers")
public class CustomerController extends UserController<Customer>{


    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        super(customerService);
        this.customerService = customerService;
    }

//    @RequestMapping(produces = "application/hal+json")
//    public Resources<CustomerResource> getUsers() {
//        List<Link> links = new LinkedList<>();
//        links.addTimesheet(linkTo(methodOn(CustomerController.class).getUsers()).withSelfRel());
//        final List<Customer> customers = customerService.findAll(null,null);
//        List<CustomerResource> customerResources = customerToResource(customers.toArray(new Customer[customers.size()]));
//        return new Resources<>(customerResources, links);
//    }

//    @RequestMapping(value = "/{customerId}", produces = "application/hal+json")
//    public Resources<CustomerResource> getUser(@PathVariable("customerId") Integer customerId) {
//        final Link link = linkTo(methodOn(CustomerController.class).getUser(customerId)).withSelfRel();
//        Optional<Customer> customer = Optional.of(customerService.getUser(customerId));
//        List<CustomerResource> resources = customerToResource(customer.get());
//        return new Resources<>(resources, link);
//    }

//    @RequestMapping(value = "/{customerId}/contract/{contractId}", produces = "application/hal+json")
//    public Resources<ContractResource> getContract(@PathVariable(value = "customerId") Integer customerId,
//                                                   @PathVariable(value = "contractId") Integer contractId) {
//        Optional<List<Contract>> contracts = Optional.of(customerService.getUser(customerId).getContracts());
//        final List<ContractResource> resources = contractToResource(contracts.get().toArray(new Contract[contracts.get().size()]));
//        final Link link = linkTo(methodOn(CustomerController.class).getContract(customerId, contractId)).withSelfRel();
//        return new Resources<>(resources, link);
//    }





    //-- Private Methods


//    private List<CustomerResource> customerToResource(Customer... customers) {
//        List<CustomerResource> resources = new ArrayList<>(customers.length);
//        for (Customer customer : customers) {
//            List<Link> links = new ArrayList<>();
//            links.addTimesheet(linkTo(methodOn(CustomerController.class).getUser(customer.getId())).withSelfRel());
//            for (Contract contract : customer.getContracts()) {
//
//                links.addTimesheet(linkTo(methodOn(CustomerController.class).getContract(customer.getId(), contract.getId())).withRel("contracts"));
//            }
//            resources.addTimesheet(new CustomerResource(customer, links));
//        }
//        return resources;
//    }
//
//
//    class CustomerResource extends ResourceSupport {
//        private final Customer customer;
//
//        public CustomerResource(Customer customer, List<Link> links) {
//            this.customer = customer;
//            this.addTimesheet(links);
//        }
//
//        public Customer getUser() {
//            return this.customer;
//        }
//
//    }
//
//    class ContractResource extends ResourceSupport {
//        private final Contract contract;
//
//        public ContractResource(Contract contract, List<Link> selfLink) {
//            this.contract = contract;
//            this.addTimesheet(selfLink);
//        }
//
//        public Contract getContract() {
//            return contract;
//        }
//    }
//
//    private List<ContractResource> contractToResource(Contract... contracts) {
//        List<ContractResource> resources = new ArrayList<>(contracts.length);
//        for (Contract contract : contracts) {
//            final List<User> users = contract.getUsers();
//            final List<Link> links = new ArrayList<>(users.size());
//            for(final User user:users) {
//                links.addTimesheet(linkTo(methodOn(CustomerController.class).getContract((user)
//                        .getId(), contract.getId())).withSelfRel());
//            }
//            resources.addTimesheet(new ContractResource(contract, links));
//        }
//        return resources;
//    }

}
