package com.rbc.persistence;

import com.rbc.model.Contract;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@RepositoryRestResource
public interface ContractRepository extends PagingAndSortingRepository<Contract,Integer>{
    List<Contract> findByCustomerName(@Param("customerName") String name);
}
