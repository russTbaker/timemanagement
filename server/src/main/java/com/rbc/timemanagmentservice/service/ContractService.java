package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.model.Job;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import com.rbc.timemanagmentservice.persistence.JobRepository;
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
    private final JobRepository jobRepository;

    @Autowired
    public ContractService(ContractRepository contractRepository, JobRepository jobRepository) {
        this.contractRepository = contractRepository;
        this.jobRepository = jobRepository;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public Contract saveContract(final Contract contract){
        return contractRepository.save(contract);
    }

    public Contract getContract(Integer id){
        return contractRepository.findOne(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Job createJob(Job job, Integer contractId){
        final Job realJob = jobRepository.save(job);
        final Contract contract = contractRepository.findOne(contractId);
        contract.addJob(realJob);
        contractRepository.save(contract);
        return realJob;
    }
}
