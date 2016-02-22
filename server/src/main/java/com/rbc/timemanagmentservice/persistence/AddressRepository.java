package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.Address;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by russbaker on 2/20/16.
 */
@Repository
public interface AddressRepository extends PagingAndSortingRepository<Address,Integer>{
}
