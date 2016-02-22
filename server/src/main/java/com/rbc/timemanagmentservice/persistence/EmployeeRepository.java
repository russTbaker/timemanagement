package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.model.TimeSheet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@RepositoryRestResource
public interface EmployeeRepository extends PagingAndSortingRepository<Employee,Integer>{
    @Query(value = "select e.timesheets from Employee e where e.id = ?1 ")
    List<TimeSheet> findEmployeeTimesheets(Integer employeeId);

//    @Query(value = "select e.tim")
}
