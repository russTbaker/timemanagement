package com.rbc.timemanagmentservice.persistence;

import com.rbc.timemanagmentservice.model.Invoice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by rbaker on 2/6/16.
 */
@RepositoryRestResource(exported = false)
public interface InvoiceRepository extends PagingAndSortingRepository<Invoice,Integer>{
    @Override
    @Query("from Invoice i where i.id = ?1 and i.deleteDate is null")
    Invoice findOne(@Param("invoiceId") Integer invoiceId);
}
