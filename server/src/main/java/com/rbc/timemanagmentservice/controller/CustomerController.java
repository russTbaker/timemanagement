package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Resources<CustomerResource> getCustomers(){
        List<Link> links = new LinkedList<>();
        links.add(linkTo(methodOn(CustomerController.class).getCustomers()).withSelfRel());
        final List<Customer> customers = customerService.findAll();
        List<CustomerResource> customerResources = customerToResource(customers.toArray(new Customer[customers.size()]));
        return new Resources<>(customerResources,links);
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

//    @RequestMapping(value = "/{customerId}/email/{emailId}", produces = "application/hal+json")
//    public Resources<EmailResource> getEmail(@PathVariable(value = "customerId") Integer customerId,
//                                             @PathVariable(value = "emailId") Integer emailId) {
//        final Optional<Customer> customerOptional = Optional.of(customerService.getCustomer(customerId));
//        final Link link = linkTo(methodOn(CustomerController.class).getEmail(customerId,emailId)).withSelfRel();
//        return new Resources<>(emailToResource(customerOptional.get().getMailContainers()
//                .stream()
//                .filter(email -> email.getId().equals(emailId))
//                .findFirst()
//                .get()),link);
//    }

    @RequestMapping(value = "/{customerId}/address/{addressId}", produces = "application/hal+json")
    public Resources<AddressResource> getAddress(@PathVariable(value = "customerId") Integer customerId,
                                             @PathVariable(value = "addressId") Integer addressId) {
        final Optional<Customer> customerOptional = Optional.of(customerService.getCustomer(customerId));
        final Link link = linkTo(methodOn(CustomerController.class).getAddress(customerId,addressId)).withSelfRel();
        return new Resources<>(addressToResource(customerOptional.get().getAddress()
                .stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .get()),link);
    }

    //-- Private Methods


    private List<CustomerResource> customerToResource(Customer... customers) {
        List<CustomerResource> resources = new ArrayList<>(customers.length);
        for (Customer customer : customers) {
            Link selfLink = linkTo(methodOn(CustomerController.class).getCustomer(customer.getId())).withSelfRel();
            resources.add(new CustomerResource(customer, selfLink));
        }
        return resources;
    }





    class CustomerResource extends ResourceSupport {
        private final Customer customer;

        public CustomerResource(Customer customer, Link selfLink) {
            this.customer = customer;
            this.add(selfLink);
        }

        public Customer getCustomer() {
            return this.customer;
        }


        public List<ContractResource> getContracts() {
            final List<Contract> contracts = this.customer.getContracts();
            return contractToResource(contracts.toArray(new Contract[contracts.size()]));
        }

//        public List<EmailResource> getMailContainers() {
//            final List<MailContainer> emails = this.customer.getMailContainers();
//            return emailToResource(emails.toArray(new MailContainer[emails.size()]));
//        }
//
        public List<AddressResource> getAddress() {
            final List<Address> address = this.customer.getAddress();
            return addressToResource(address.toArray(new Address[address.size()]));
        }
    }

    class ContractResource extends ResourceSupport{
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
                    .getId(),contract.getId())).withSelfRel();
            resources.add(new ContractResource(contract,selfLink ));
        }
        return resources;
    }
    
//    class TransportResource extends ResourceSupport {
//        private final Transport email;
//
//        public TransportResource(Transport email, Link selfLink) {
//            this.email = email;
//            this.add(selfLink);
//        }
//
//        public Transport getTransport() {
//            return email;
//        }
//    }

//    private List<TransportResource> emailToResource(Transport... emails) {
//        List<TransportResource> resources = new ArrayList<>(emails.length);
//        for (Transport email : emails) {
//            Link selfLink = linkTo(methodOn(CustomerController.class).getEmail(email.getUser().getId(), email.getId())).withSelfRel();
//            resources.add(new TransportResource(email,selfLink ));
//        }
//        return resources;
//    }

    class AddressResource extends ResourceSupport{
        private final Address address;

        public AddressResource(Address address, Link selfLink) {
            this.address = address;
            this.add(selfLink);
        }

        public Address getAddress() {
            return address;
        }
    }

    private List<AddressResource> addressToResource(Address... addresses) {
        List<AddressResource> resources = new ArrayList<>(addresses.length);
        for (Address address : addresses) {
            Link selfLink = linkTo(methodOn(CustomerController.class).getAddress(address.getUser().getId(), address.getId())).withSelfRel();
            resources.add(new AddressResource(address,selfLink ));
        }
        return resources;
    }

}
