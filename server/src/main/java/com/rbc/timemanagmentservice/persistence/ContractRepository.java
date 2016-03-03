package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.model.Job;
import com.rbc.timemanagmentservice.model.TimeSheetEntry;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by rbaker on 2/6/16.
 */
@RepositoryRestResource
public interface ContractRepository extends PagingAndSortingRepository<Contract,Integer>{
    List<Contract> findByUsersDba(@Param("name") String name);
}
