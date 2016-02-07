package com.rbc;

import com.rbc.model.*;
import com.rbc.persistence.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
public class TimemanagementServiceApplication {
    private static final Logger LOG = LoggerFactory.getLogger(TimemanagementServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TimemanagementServiceApplication.class, args);
    }
    public static final String CONTACT_NAME = "Jonathan Bein";

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/customers").allowedOrigins("http://localhost:8888");
            }
        };
    }


    @Bean
    @Transactional(propagation = Propagation.REQUIRED)
    public CommandLineRunner demo(TimeSheetRepository timeSheetRepository, CustomerRepository customerRepository,TimeSheetEntryRepository timeSheetEntryRepository, EmployeeRepository employeeRepository,
                                  ContractRepository contractRepository) {
        return (args) -> {
            TimeSheet timeSheet = new TimeSheet();
            timeSheet.setEmployee(getEmployee(employeeRepository));
            timeSheet.setBilled(false);

            TimeSheetEntry timeSheetEntry = new TimeSheetEntry();
            timeSheetEntry.setContract(getContract(contractRepository, customerRepository));
            timeSheetEntry.setHours(8);
            timeSheetEntry.setDate(new DateTime());


            final TimeSheetEntry entry = timeSheetEntryRepository.save(timeSheetEntry);
            timeSheet.setTimeSheetEntry(Collections.singletonList(entry));
            timeSheetRepository.save(timeSheet);
        };
    }

    private Contract getContract(ContractRepository contractRepository, CustomerRepository customerRepository) {
        Contract contract = new Contract();
        contract.setStartDate(new DateTime());
        contract.setEndDate(new DateTime().plusMonths(6));
        contract.setRate(87.5);
        contract.setTerms(Contract.Terms.net15);
        contract.setCustomer(getCustomer(customerRepository));
        contract.setValue(87999D);
        return contractRepository.save(contract);
    }

    public Employee getEmployee(EmployeeRepository employeeRepository) {
        Employee employee = new Employee();
        employee.setFirstName("Russ");
        employee.setLastName("Baker");
        employee.setUsername("admin");
        employee.setPassword("password");
        employee.setEmails(Arrays.asList(new Email(), new Email()));
        employee.setRoles(User.Roles.employee);
        return employeeRepository.save(employee);
    }


    private Customer getCustomer(CustomerRepository customerRepository) {
        Customer customer = new Customer();
        customer.setName("TEST");
        customer.setLastName("CUSTOMER");
        customer.setName("Z2M4");
        customer.setContactName(CONTACT_NAME);
        customer.setRoles(User.Roles.customer);
        return customerRepository.save(customer);
    }



}

