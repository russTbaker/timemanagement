package com.rbc.timemanagmentservice.security;

import com.rbc.timemanagmentservice.filter.CsrfHeaderFilter;
import com.rbc.timemanagmentservice.service.TimeManagementUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/**
 * Created by russbaker on 2/22/16.
 */
//@Configuration
@EnableWebSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class TimeManagementSecurityConfig extends WebSecurityConfigurerAdapter {

    private TimeManagementUserDetailsService timeManagementUserDetailsService;

    @Autowired
    public void setTimeManagementUserDetailsService(TimeManagementUserDetailsService timeManagementUserDetailsService) {
        this.timeManagementUserDetailsService = timeManagementUserDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/index.html","/home.html","/login.html","/").permitAll()
                .antMatchers("/user/**").hasRole("ADMIN")
                .antMatchers("/employee/addEmployee.html").hasRole("ADMIN")
                .antMatchers("/employee/employee.html").hasRole("EMPLOYEE")
                .antMatchers("/timesheet/**").hasRole("EMPLOYEE")
                .and()
                .csrf().csrfTokenRepository(csrfTokenRepository())
                .and()
                .addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(timeManagementUserDetailsService);
    }

    @Bean
    private static CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }



}
