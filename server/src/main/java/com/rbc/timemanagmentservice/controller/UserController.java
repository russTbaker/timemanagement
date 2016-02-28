package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

/**
 * Created by russbaker on 2/28/16.
 */
@SuppressWarnings("unchecked")
public class UserController<U extends User> {
    public static final String PHONES = "phones";
    public static final String EMAILS = "emails";
    public static final String ADDRESSES = "addresses";
    private final  UserService<U> userService;

    public UserController(UserService<U> userService) {
        this.userService = userService;
    }

    //--------- Address

    @RequestMapping(value = "/{userId}/address", method = RequestMethod.POST)
    public ResponseEntity<?> addUserAddress(@PathVariable(value = "userId") Integer userId,
                                            @RequestBody Address address) {
        address = userService.addAddressToUser(userId, address);
        HttpHeaders httpHeaders = getHttpHeadersForEntity(address, ADDRESSES);
        return new ResponseEntity(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{userId}/address/{addressId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateAddress(@PathVariable(value = "userId") Integer userId,
                                           @PathVariable(value = "addressId") Integer emailId,
                                           @RequestBody Address address) {
        return Optional.of(userService.getUser(userId))
                .map(employee -> {
                    address.setId(emailId);
                    employee.addAddress(address);
                    employee = userService.updateUser(employee);
                    return new ResponseEntity(null, getHttpHeadersForEntity(address, ADDRESSES), HttpStatus.CREATED);
                }).get();
    }


    @RequestMapping(value = "/{userId}/address/{addressId}", method = RequestMethod.DELETE)
    public void deleteAddress(@PathVariable(value = "userId") Integer userId,
                              @PathVariable(value = "addressId") Integer addressId) {
        userService.removeAddressFromUser(userId, addressId);
    }

    //------------- Phone

    @RequestMapping(value = "/{userId}/phones", method = RequestMethod.POST)
    public ResponseEntity<?> addUserPhone(@PathVariable(value = "userId") Integer userId,
                                          @RequestBody Phone phone) {
        return new ResponseEntity(null, getHttpHeadersForEntity(userService.addPhoneToUser(userId, phone),PHONES),
                HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{userId}/phones/{phoneId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePhones(@PathVariable(value = "userId") Integer userId,
                                          @PathVariable(value = "phoneId") Integer phoneId,
                                          @RequestBody Phone phone) {
        phone.setId(phoneId);
        userService.addPhoneToUser(userId,phone);
        return new ResponseEntity(null, getHttpHeadersForEntity(() ->  phoneId, PHONES), HttpStatus.CREATED);
    }

    @RequestMapping(value =  "/{userId}/phones/{phoneId}", method = RequestMethod.DELETE)
    public void deleteUserPhone(@PathVariable(value = "userId") Integer userId,
                                @PathVariable(value = "phoneId") Integer phoneId) {
        userService.removePhoneFromUser(userId, phoneId);
    }

    //-------- Email
    @RequestMapping(value = "/{userId}/emails", method = RequestMethod.PUT)
    public ResponseEntity<?> addEmail(@PathVariable(value = "userId") Integer userId,
                                      @RequestBody Email email){
        email.setId(userService.addEmailToUser(userId,email).getId());
        HttpHeaders httpHeaders = getHttpHeadersForEntity(email, EMAILS);
        return new ResponseEntity<>(null,httpHeaders,HttpStatus.ACCEPTED);
    }



    @RequestMapping(value = "/{userId}/emails/{emailId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateEmail(@PathVariable(value = "userId") Integer userId,
                                         @PathVariable(value = "emailId") Integer emailId,
                                         @RequestBody Email email) {
        email.setId(emailId);
        userService.addEmailToUser(userId, email);
        HttpHeaders httpHeaders = getHttpHeadersForEntity(email, EMAILS);
        return new ResponseEntity(null, httpHeaders, HttpStatus.CREATED);
    }



    @RequestMapping(value = "/{userId}/emails/{emailId}", method = RequestMethod.DELETE)
    public void deleteUserEmail(@PathVariable(value = "userId") Integer userId,
                                @PathVariable(value = "emailId") Integer emailId) {
        userService.removeEmailFromUser(userId, emailId);
    }

    //-------------- Contracts
    
    @RequestMapping(value = "/{userId}/contracts/{contractId}", method = RequestMethod.PUT)
    public ResponseEntity<?> addContractsToUser(@PathVariable(value = "userId") Integer userId,
                                                @PathVariable(value = "contractId") Integer contractId) {
        userService.addContractToUser(userId, contractId);
        HttpHeaders httpHeaders = getHttpHeadersForEntity(() ->  contractId, "contracts");
        return new ResponseEntity(null, httpHeaders, HttpStatus.ACCEPTED);
    }

    //--------- Private Methods
    private HttpHeaders getHttpHeadersForEntity(EntityMarkerInterface entity, String resourceUri) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/"+resourceUri+"/{id}")
                .buildAndExpand(entity.getId()).toUri());
        return httpHeaders;
    }


}
