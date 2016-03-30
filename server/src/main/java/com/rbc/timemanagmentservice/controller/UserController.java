package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.User;
import com.rbc.timemanagmentservice.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by russbaker on 2/28/16.
 */
@SuppressWarnings("unchecked")
public class UserController<U extends User> extends BaseController{
    public static final String PHONES = "phones";
    public static final String EMAILS = "emails";
    public static final String ADDRESSES = "addresses";
    private final  UserService<U> userService;

    public UserController(UserService<U> userService) {
        this.userService = userService;
    }


    //-------------- Contracts
    
    @RequestMapping(value = "/{userId}/contracts/{contractId}", method = RequestMethod.PUT)
    public ResponseEntity<?> addContractsToUser(@PathVariable(value = "userId") Integer userId,
                                                @PathVariable(value = "contractId") Integer contractId) {
        userService.addContractToUser(userId, contractId);
        HttpHeaders httpHeaders = getHttpHeadersForEntity(() ->  contractId, "contracts");
        return new ResponseEntity(null, httpHeaders, HttpStatus.ACCEPTED);
    }


}
