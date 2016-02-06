package com.rbc.persistence;

import com.rbc.model.Contract;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by rbaker on 2/6/16.
 */
@RepositoryRestResource
public interface ContractRepository extends PagingAndSortingRepository<Contract,Integer>{
}
