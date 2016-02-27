package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.Timesheet;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by russbaker on 2/24/16.
 */
@RepositoryRestResource
public interface TimeSheetRepository extends PagingAndSortingRepository<Timesheet,Integer> {
}
