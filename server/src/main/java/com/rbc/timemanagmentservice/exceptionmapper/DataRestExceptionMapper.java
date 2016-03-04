package com.rbc.timemanagmentservice.exceptionmapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created by russbaker on 3/4/16.
 */
public class DataRestExceptionMapper implements ExceptionMapper<RuntimeException>{
    @Override
    public Response toResponse(RuntimeException e) {
        Response retVal = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        return retVal;
    }
}
