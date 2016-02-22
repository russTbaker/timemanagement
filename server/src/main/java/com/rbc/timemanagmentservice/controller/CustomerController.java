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
@RequestMapping("/hydrated/customer/")
public class CustomerController {


    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @RequestMapping(produces = "application/hal+json")
    public Resources<CustomerResource> getCustomers() {
        List<Link> links = new LinkedList<>();
        links.add(linkTo(methodOn(CustomerController.class).getCustomers()).withSelfRel());
        final List<Customer> customers = customerService.findAll();
        List<CustomerResource> customerResources = customerToResource(customers.toArray(new Customer[customers.size()]));
        return new Resources<>(customerResources, links);
    }

    @RequestMapping(value = "/{customerId}", produces = "application/hal+json")
    public Resources<CustomerResource> getCustomer(@PathVariable("customerId") Integer customerId) {
        final Link link = linkTo(methodOn(CustomerController.class).getCustomer(customerId)).withSelfRel();
        Optional<Customer> customer = Optional.of(customerService.getCustomer(customerId));
        List<CustomerResource> resources = customerToResource(customer.get());
        return new Resources<>(resources, link);
    }

    @RequestMapping(value = "/{customerId}/contract/{contractId}", produces = "application/hal+json")
    public Resources<ContractResource> getContract(@PathVariable(value = "customerId") Integer customerId,
                                                   @PathVariable(value = "contractId") Integer contractId) {
        Optional<List<Contract>> contracts = Optional.of(customerService.getCustomer(customerId).getContracts());
        final List<ContractResource> resources = contractToResource(contracts.get().toArray(new Contract[contracts.get().size()]));
        final Link link = linkTo(methodOn(CustomerController.class).getContract(customerId, contractId)).withSelfRel();
        return new Resources<>(resources, link);
    }

    @RequestMapping(value = "/{customerId}/email/{emailId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateEmail(@PathVariable(value = "customerId") Integer customerId,
                                         @PathVariable(value = "emailId") Integer emailId,
                                         @RequestBody Email email) {
        return Optional.of(customerService.getCustomer(customerId))
                .map(customer -> {
                    email.setId(emailId);
                    customer.addEmail(email);
                    customer = customerService.updateCustomer(customer);

                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setLocation(ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{id}")
                            .buildAndExpand(email.getId()).toUri());

                    return new ResponseEntity(null, httpHeaders, HttpStatus.CREATED);
                }).get();
    }

    @RequestMapping(value = "/{customerId}/address/{addressId}", method = RequestMethod.PUT)
    public ResponseEntity<?> getAddress(@PathVariable(value = "customerId") Integer customerId,
                                        @PathVariable(value = "addressId") Integer addressId,
                                        @RequestBody Address address) {
        return Optional.of(customerService.getCustomer(customerId))
                .map(customer -> {
                    address.setId(addressId);
                    customer.addAddress(address);
                    customer = customerService.updateCustomer(customer);

                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setLocation(ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{id}")
                            .buildAndExpand(address.getId()).toUri());

                    return new ResponseEntity(null, httpHeaders, HttpStatus.CREATED);
                }).get();
    }



    @RequestMapping(value = "/{customerId}/phones/{phoneId}", method = RequestMethod.PUT)
    public ResponseEntity<?> getPhones(@PathVariable(value = "customerId") Integer customerId,
                                        @PathVariable(value = "phoneId") Integer phoneId,
                                        @RequestBody Phone phone) {
        return Optional.of(customerService.getCustomer(customerId))
                .map(customer -> {
                    phone.setId(phoneId);
                    customer.addPhone(phone);
                    customer = customerService.updateCustomer(customer);

                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setLocation(ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{id}")
                            .buildAndExpand(phone.getId()).toUri());

                    return new ResponseEntity(null, httpHeaders, HttpStatus.CREATED);
                }).get();
    }

    //-- Private Methods


    private List<CustomerResource> customerToResource(Customer... customers) {
        List<CustomerResource> resources = new ArrayList<>(customers.length);
        for (Customer customer : customers) {
            List<Link> links = new ArrayList<>();
            links.add(linkTo(methodOn(CustomerController.class).getCustomer(customer.getId())).withSelfRel());
            for (Contract contract : customer.getContracts()) {

                links.add(linkTo(methodOn(CustomerController.class).getContract(customer.getId(), contract.getId())).withRel("contracts"));
            }
            resources.add(new CustomerResource(customer, links));
        }
        return resources;
    }


    class CustomerResource extends ResourceSupport {
        private final Customer customer;

        public CustomerResource(Customer customer, List<Link> links) {
            this.customer = customer;
            this.add(links);
        }

        public Customer getCustomer() {
            return this.customer;
        }

    }

    class ContractResource extends ResourceSupport {
        private final Contract contract;

        public ContractResource(Contract contract, Link selfLink) {
            this.contract = contract;
            this.add(selfLink);
        }

        public Contract getContract() {
            return contract;
        }
    }

    private List<ContractResource> contractToResource(Contract... contracts) {
        List<ContractResource> resources = new ArrayList<>(contracts.length);
        for (Contract contract : contracts) {

            Link selfLink = linkTo(methodOn(CustomerController.class).getContract(contract.getUsers()
                    .stream()
                    .filter(user -> user.getRoles() == User.Roles.customer)
                    .findFirst()
                    .get()
                    .getId(), contract.getId())).withSelfRel();
            resources.add(new ContractResource(contract, selfLink));
        }
        return resources;
    }

    class EmailResource extends ResourceSupport {
        private final Email email;

        public EmailResource(Email email, Link selfLink) {
            this.email = email;
            this.add(selfLink);
        }

        public Email getTransport() {
            return email;
        }
    }

//    private List<EmailResource> emailToResource(Email... emails) {
//        List<EmailResource> resources = new ArrayList<>(emails.length);
//        for (Email email : emails) {
//            Link selfLink = linkTo(methodOn(CustomerController.class).getEmail(email.getUser().getId(), email.getId())).withSelfRel();
//            resources.add(new EmailResource(email, selfLink));
//        }
//        return resources;
//    }

    class AddressResource extends ResourceSupport {
        private final Address address;

        public AddressResource(Address address, Link selfLink) {
            this.address = address;
            this.add(selfLink);
        }

        public Address getAddress() {
            return address;
        }
    }

//    private List<AddressResource> addressToResource(Address... addresses) {
//        List<AddressResource> resources = new ArrayList<>(addresses.length);
//        for (Address address : addresses) {
//            Link selfLink = linkTo(methodOn(CustomerController.class).getAddress(address.getUser().getId(), address.getId())).withSelfRel();
//            resources.add(new AddressResource(address,selfLink ));
//        }
//        return resources;
//    }

}
