package com.rbc.timemanagmentservice.controller;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.ws.rs.NotFoundException;

/**
 * Created by russbaker on 2/16/16.
 */
@ControllerAdvice
public class TimeSheetManagementControllerAdvice {
    public TimeSheetManagementControllerAdvice() {
        super();
    }

    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    VndErrors notFoundExceptionHandler(NotFoundException ex) {
        return new VndErrors("error", ex.getMessage());
    }

}
