package com.rbc.persistence;

import com.rbc.model.Employee;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by rbaker on 2/6/16.
 */
@RepositoryRestResource
public interface EmployeeRepository extends PagingAndSortingRepository<Employee,Integer>{
}
