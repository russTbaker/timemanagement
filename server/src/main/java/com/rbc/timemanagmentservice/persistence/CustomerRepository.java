package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@RepositoryRestResource
@CrossOrigin(origins = "http://localhost:8888")
public interface CustomerRepository extends PagingAndSortingRepository<Customer,Integer> {

    List<Customer> findByContactName(@Param("contactName") String contactName);
}
