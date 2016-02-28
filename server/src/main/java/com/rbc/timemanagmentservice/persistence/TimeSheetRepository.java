package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.Timesheet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by russbaker on 2/24/16.
 */
@RepositoryRestResource
public interface TimeSheetRepository extends PagingAndSortingRepository<Timesheet,Integer> {
    @Query("from Timesheet t where t.employee.id =?1 and t.startDate = (select max(startDate) from Timesheet)")
    Timesheet getLatestTimesheet(@Param("employeeId") Integer employeeId);
}
