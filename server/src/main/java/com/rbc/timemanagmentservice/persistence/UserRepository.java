package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by russbaker on 2/27/16.
 */
@RepositoryRestResource//(exported = false)
public  interface UserRepository<U extends User> extends PagingAndSortingRepository<U,Integer>{

}
