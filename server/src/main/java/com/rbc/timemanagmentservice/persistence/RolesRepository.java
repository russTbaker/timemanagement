package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.Roles;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by russbaker on 3/6/16.
 */
@RepositoryRestResource(exported = false)
public interface RolesRepository extends PagingAndSortingRepository<Roles,Integer>{
}
