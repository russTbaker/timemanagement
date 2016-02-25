package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Job;
import com.rbc.timemanagmentservice.persistence.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by russbaker on 2/24/16.
 */
@Repository
public class JobService {
    private final JobRepository jobRepository;

    @Autowired
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Job findJob(Integer jobId){
        return jobRepository.findOne(jobId);
    }
}
