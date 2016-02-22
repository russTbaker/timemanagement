package com.rbc.timemanagmentservice.controller;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

/**
 * Created by russbaker on 2/22/16.
 */
public class ControllerTests {

    protected static final DateTimeFormatter FMT = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Autowired
    @Qualifier("userDetailsService")
    protected UserDetailsService userDetailsService;

    protected UsernamePasswordAuthenticationToken getPrincipal(String username) {

        UserDetails user = this.userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        user.getPassword(),
                        user.getAuthorities());

        return authentication;
    }
    protected MockHttpSession createMockHttpSessionForPutPost() {
        UsernamePasswordAuthenticationToken principal =
                this.getPrincipal("admin");

        SecurityContextHolder.getContext().setAuthentication(principal);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
        return session;
    }
}
