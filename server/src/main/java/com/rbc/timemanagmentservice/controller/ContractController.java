package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.model.EntityMarkerInterface;
import com.rbc.timemanagmentservice.model.Job;
import com.rbc.timemanagmentservice.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Created by russbaker on 2/22/16.
 */
@RestController
@RequestMapping("/hydrated/contracts")
public class ContractController extends BaseController{
    public static final String API_URI = "contracts";
    private final ContractService contractService;

    @Autowired
    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createContract(@RequestBody Contract contract) {
        contract = contractService.saveContract(contract);
        return new ResponseEntity<>(null, getHttpHeadersForEntity(contract, API_URI), HttpStatus.CREATED);
    }

    @RequestMapping(value = "{contractId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateContract(@PathVariable(value = "contractId") Integer contractId,
                                            @RequestBody Contract contract) {

        contract.setId(contractId);
        contractService.saveContract(contract);
        return new ResponseEntity<>(null, getHttpHeadersForEntity(contract,API_URI), HttpStatus.CREATED);
    }

    @RequestMapping(value = "{contractId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteContract(@PathVariable("contractId") Integer contractId){
        contractService.deleteContract(contractId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "{contractId}/jobs/{jobId}", method = RequestMethod.POST)
    public ResponseEntity<?> addJobToContract(@PathVariable("contractId") Integer contractId,
                                              @PathVariable("jobId") Integer jobId){
        return new ResponseEntity<>(null,getHttpHeadersForEntity(
                contractService.addJobToContract(jobId,contractId),"jobs"),HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "{contractId}/jobs/{jobId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateContractJob(@PathVariable("contractId") Integer contractId,
                                             @RequestBody Job job){
        return new ResponseEntity<>(null,getHttpHeadersForEntity(
                contractService.updateJob(contractId,job),"jobs"),HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "{contractId}/jobs/{jobId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteJobFromContract(@PathVariable("contractId") Integer contractId,
                                                   @PathVariable("jobId") Integer jobId){
        contractService.removeJobFromContract(contractId,jobId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
