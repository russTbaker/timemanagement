package com.rbc.timemanagmentservice.security;

import com.rbc.timemanagmentservice.filter.CorsFilter;
import com.rbc.timemanagmentservice.filter.CsrfHeaderFilter;
import com.rbc.timemanagmentservice.service.TimeManagementUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

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
        // csrf().disable()
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
                .formLogin()
                .and()
                .httpBasic().and()
                .csrf().csrfTokenRepository(csrfTokenRepository());
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
