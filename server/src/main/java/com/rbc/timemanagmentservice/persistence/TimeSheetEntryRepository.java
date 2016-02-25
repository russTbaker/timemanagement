package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.TimeSheetEntry;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by russbaker on 2/24/16.
 */
@RepositoryRestResource
public interface TimeSheetEntryRepository extends PagingAndSortingRepository<TimeSheetEntry,Integer> {
}
