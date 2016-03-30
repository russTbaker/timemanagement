package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.exception.NotFoundException;
import com.rbc.timemanagmentservice.model.error.ErrorPayload;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;



/**
 * Created by russbaker on 2/16/16.
 */
@ControllerAdvice
public class TimeSheetManagementControllerAdvice  {
    private static final Logger LOG = LoggerFactory.getLogger(TimeSheetManagementControllerAdvice.class);
    public TimeSheetManagementControllerAdvice() {
        super();
    }

    @ResponseBody
    @ExceptionHandler(value = {NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorPayload> notFound(NotFoundException e){
        final String message = "User not found";
        LOG.info(message,e);
        final ErrorPayload errorPayload = new ErrorPayload();
        errorPayload.getMessages().add(message);
        return new ResponseEntity(errorPayload,HttpStatus.NOT_FOUND);
    }

    @ResponseBody
    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorPayload> constraintViolation(ConstraintViolationException e){
        LOG.error("Constraint violation.",e);
        final ErrorPayload errorPayload = new ErrorPayload();
        errorPayload.getMessages().add("Username already exists");
        return new ResponseEntity(errorPayload,HttpStatus.CONFLICT);
    }

    @ResponseBody
    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<ErrorPayload> runtimeException(RuntimeException e){
        final ErrorPayload errorPayload = new ErrorPayload();
        final String message = "An error has occurred.";
        errorPayload.getMessages().add(message);
        LOG.error(message,e);
        return new ResponseEntity(errorPayload,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<ErrorPayload> dataIntegrityViolationException(DataIntegrityViolationException e){
        final ErrorPayload errorPayload = new ErrorPayload();
        errorPayload.getMessages().add("Username already exists");
        return new ResponseEntity(errorPayload,HttpStatus.CONFLICT);
    }
}
