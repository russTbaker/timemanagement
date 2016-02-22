package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by russbaker on 2/21/16.
 */
@Repository
public class ContractService {
    private final ContractRepository contractRepository;

    @Autowired
    public ContractService(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public Contract saveContract(final Contract contract){
        return contractRepository.save(contract);
    }
}
