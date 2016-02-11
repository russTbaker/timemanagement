package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.TimeSheet;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by rbaker on 2/6/16.
 */
@RepositoryRestResource
public interface TimeSheetRepository extends PagingAndSortingRepository<TimeSheet,Integer>{
}
