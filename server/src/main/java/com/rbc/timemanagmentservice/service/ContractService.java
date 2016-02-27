package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.model.Job;
import com.rbc.timemanagmentservice.model.User;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import com.rbc.timemanagmentservice.persistence.JobRepository;
import com.rbc.timemanagmentservice.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.NotFoundException;

/**
 * Created by russbaker on 2/21/16.
 */
@Repository
public class ContractService {
    private final ContractRepository contractRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Autowired
    public ContractService(ContractRepository contractRepository, JobRepository jobRepository, UserRepository userRepository) {
        this.contractRepository = contractRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public Contract saveContract(final Contract contract){
        return contractRepository.save(contract);
    }

    public Contract getContract(Integer id){
        final Contract contract = contractRepository.findOne(id);
        if(contract == null){
            throw new NotFoundException("Contract with id: " + id + " not found.");
        }
        return contract;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Job createJob(Job job, Integer contractId){
        final Job realJob = jobRepository.save(job);
        final Contract contract = contractRepository.findOne(contractId);
        contract.addJob(realJob);
        contractRepository.save(contract);
        return realJob;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Contract createContract(Contract contract) {
        return contractRepository.save(contract);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Contract updateContract(Contract contract) {
        for(final Job job:contract.getJobs()){
            jobRepository.save(job);
        }
        return contractRepository.save(contract);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteContract(Integer contractId) {
        final Contract contract = contractRepository.findOne(contractId);
        for(User user:contract.getUsers()){
            user.getContracts().remove(contract);
            userRepository.save(user);
        }
        contractRepository.delete(contractId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeJobFromContract(final Integer contractId, final Job job) {
        final Contract contract = contractRepository.findOne(contractId);
        contract.getJobs().remove(job);
        contractRepository.save(contract);
    }
}
