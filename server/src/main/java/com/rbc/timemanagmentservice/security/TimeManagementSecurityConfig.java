package com.rbc.timemanagmentservice.security;

import com.rbc.timemanagmentservice.filter.CsrfHeaderFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;

/**
 * Created by russbaker on 2/22/16.
 */
//@Configuration
@EnableWebSecurity
public class TimeManagementSecurityConfig extends WebSecurityConfigurerAdapter{

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().disable().authorizeRequests()
                .antMatchers("/*/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser("admin").password("admin").roles("USER");
    }

}
