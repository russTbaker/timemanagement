package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.Invoice;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by rbaker on 2/6/16.
 */
@RepositoryRestResource(exported = false)
public interface InvoiceRepository extends PagingAndSortingRepository<Invoice,Integer>{
}
