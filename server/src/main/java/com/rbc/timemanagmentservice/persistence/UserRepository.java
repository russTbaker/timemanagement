package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * Created by russbaker on 2/27/16.
 */
@RepositoryRestResource
public  interface UserRepository<U extends User> extends PagingAndSortingRepository<U,Integer>{
    @PreAuthorize("#user.username == authentication.name or hasRole('ROLE_ADMINISTRATOR')")
    Optional<U> findByUsername(@Param("username") String username);
}
