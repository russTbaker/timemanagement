package com.rbc.timemanagement.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by rbaker on 2/6/16.
 */
@SpringBootApplication
public class TimeManagementClientApplication {
    public static void main(String[] args){
        SpringApplication.run(TimeManagementClientApplication.class,args);
    }


}
