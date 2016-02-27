package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

/**
 * Created by rbaker on 2/6/16.
 */
@RepositoryRestResource(itemResourceRel = "employee", collectionResourceRel = "employees")
public interface EmployeeRepository extends JpaRepository<Employee,Integer> {
    Optional<Employee> findByUsername(String username);
}
