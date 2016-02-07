package com.rbc.persistence;

import com.rbc.TimemanagementServiceApplication;
import com.rbc.model.Email;
import com.rbc.model.Employee;
import com.rbc.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by rbaker on 2/6/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimemanagementServiceApplication.class)
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void whenCreatingEmployee_expectEmployeeFound() throws Exception {
        // Assemble
        Employee employee = new Employee();
        employee.setFirstName("Russ");
        employee.setLastName("Baker");
        employee.setUsername("admin");
        employee.setPassword("password");
        employee.setEmails(Arrays.asList(new Email(),new Email()));
        employee.setRoles(User.Roles.employee);

        // Act
        Employee result = employeeRepository.save(employee);

        // Assert
        assertNotNull("No employee returnred",employee);
        assertEquals("Wrong employee",employee,result);
    }
}