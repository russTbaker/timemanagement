package com.rbc.timemanagmentservice.security;

import com.rbc.timemanagmentservice.filter.CsrfHeaderFilter;
import com.rbc.timemanagmentservice.service.TimeManagementUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
public class TimeManagementSecurityConfig extends WebSecurityConfigurerAdapter{

    private  TimeManagementUserDetailsService timeManagementUserDetailsService;

    @Autowired
    public void setTimeManagementUserDetailsService(TimeManagementUserDetailsService timeManagementUserDetailsService) {
        this.timeManagementUserDetailsService = timeManagementUserDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().disable().authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
                .formLogin();
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
