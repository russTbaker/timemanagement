package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

/**
 * Created by russbaker on 2/22/16.
 */
@RestController
@RequestMapping("/hydrated/contract/")
public class ContractController {
    private final ContractService contractService;

    @Autowired
    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createContract(@RequestBody Contract contract) {
        contract = contractService.saveContract(contract);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(contract.getId()).toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{contractId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateContract(@PathVariable(value = "contractId") Integer contractId,
                                            @RequestBody Contract contract) {

        contract.setId(contractId);
        contractService.saveContract(contract);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(contract.getId()).toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }
}
