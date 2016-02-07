package com.rbc.persistence;

import com.rbc.model.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@RepositoryRestResource
public interface CustomerRepository extends PagingAndSortingRepository<Customer,Integer> {

    List<Customer> findByContactName(@Param("contactName") String contactName);
}
