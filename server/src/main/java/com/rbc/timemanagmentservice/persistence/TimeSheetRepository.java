package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.TimeSheet;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by rbaker on 2/6/16.
 */
//@RepositoryRestResource
    @Repository
public interface TimeSheetRepository extends PagingAndSortingRepository<TimeSheet,Integer>{
    Optional<TimeSheet> findById(Integer timesheetId);
}
