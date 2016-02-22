package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * Created by rbaker on 2/6/16.
 */
@RepositoryRestResource
public interface UserRepository extends PagingAndSortingRepository<User,Integer> {

}
