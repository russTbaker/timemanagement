package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.TimesheetEntry;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by russbaker on 2/24/16.
 */
@RepositoryRestResource
public interface TimesheetEntryRepository extends PagingAndSortingRepository<TimesheetEntry,Integer> {
}
