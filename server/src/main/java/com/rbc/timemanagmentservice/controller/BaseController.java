package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.EntityMarkerInterface;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Created by russbaker on 2/29/16.
 */
public abstract class BaseController {
    protected HttpHeaders getHttpHeadersForEntity(final EntityMarkerInterface entity, final String resourceUri) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/"+resourceUri+"/{id}")
                .buildAndExpand(entity.getId()).toUri());
        return httpHeaders;
    }
}
