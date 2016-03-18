package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Created by rbaker on 2/6/16.
 */
@RepositoryRestResource
@CrossOrigin(origins = "http://localhost:8888")
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Integer> {

}
