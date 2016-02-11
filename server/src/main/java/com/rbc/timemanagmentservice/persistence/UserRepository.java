package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by rbaker on 2/6/16.
 */
@RepositoryRestResource
public interface UserRepository extends PagingAndSortingRepository<User,Integer> {
}
