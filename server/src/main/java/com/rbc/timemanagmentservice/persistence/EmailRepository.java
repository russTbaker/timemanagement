package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.Email;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by russbaker on 2/11/16.
 */
@RepositoryRestResource
public interface EmailRepository extends PagingAndSortingRepository<Email,Integer> {
}
