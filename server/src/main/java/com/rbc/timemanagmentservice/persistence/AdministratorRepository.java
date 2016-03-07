package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.Administrator;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by russbaker on 3/7/16.
 */
@RepositoryRestResource
public interface AdministratorRepository extends PagingAndSortingRepository<Administrator,Integer>{
}
