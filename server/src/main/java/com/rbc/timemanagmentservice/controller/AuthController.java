package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.persistence.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.*;

/**
 * Created by russbaker on 2/22/16.
 */
@RestController
public class AuthController {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public AuthController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    @RequestMapping("/user")
    public Map<String, Object> user(final Principal user) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", user.getName());
        map.put("roles", AuthorityUtils.authorityListToSet(((Authentication) user)
                .getAuthorities()));
        return map;
    }

    @RequestMapping("/token")
    @ResponseBody
    public Map<String,String> token(HttpSession session) {
        return Collections.singletonMap("token", session.getId());
    }

    @RequestMapping(value = "/employee", produces = "application/hal+json")
    public Resources<EmployeeResource> getEmployee(){
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String username = auth.getName();
        List<EmployeeResource> employeeResources = employeeToResource(employeeRepository.findByUsername(username).get());
        return new Resources<>(employeeResources);
    }

    class EmployeeResource extends ResourceSupport{
        private final Employee employee;

        public EmployeeResource(Employee employee, Link link) {
            this.employee = employee;
            this.add(link);
        }

        public Employee getEmployee() {
            return employee;
        }
    }

    List<EmployeeResource> employeeToResource(Employee... employees){
        List<EmployeeResource> employeeResources = new ArrayList<>(employees.length);
        for (Employee employee : employees) {
            Link link = new Link(ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/api/jobs/{id}")
                    .buildAndExpand(employee.getId()).toUri().getPath());
            employeeResources.add(new EmployeeResource(employee,link));
        }
        return employeeResources;
    }
}
