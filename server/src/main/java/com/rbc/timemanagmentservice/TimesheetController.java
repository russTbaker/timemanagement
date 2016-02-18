package com.rbc.timemanagmentservice;

import com.rbc.timemanagmentservice.model.TimeSheetEntry;
import com.rbc.timemanagmentservice.persistence.TimeSheetEntryRepository;
import com.rbc.timemanagmentservice.persistence.TimeSheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Created by russbaker on 2/13/16.
 */
@RestController
@RequestMapping("/api/timesheets/")
public class TimesheetController {

    private final TimeSheetRepository timeSheetRepository;
    private final TimeSheetEntryRepository timeSheetEntryRepository;

    @Autowired
    public TimesheetController(TimeSheetRepository timeSheetRepository, TimeSheetEntryRepository timeSheetEntryRepository) {
        this.timeSheetRepository = timeSheetRepository;
        this.timeSheetEntryRepository = timeSheetEntryRepository;
    }


    @RequestMapping(value = "/{timesheetId}/timesheetentries", method = RequestMethod.POST)
    ResponseEntity<?> add(@PathVariable Integer timesheetId, @RequestBody TimeSheetEntry input) {
        return this.timeSheetRepository
                .findById(timesheetId)
                .map(timesheet -> {
                    TimeSheetEntry result = this.timeSheetEntryRepository.save(input);
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setLocation(ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{id}")
                            .buildAndExpand(result.getId()).toUri());
                    return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
                }).get();

    }

}
