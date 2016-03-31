package com.rbc.timemanagmentservice.listeners;

import com.rbc.timemanagmentservice.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

/**
 * Created by russbaker on 3/31/16.
 */
@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private final LoginAttemptService loginAttemptService;

    @Autowired
    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }
    public void onApplicationEvent(AuthenticationSuccessEvent authenticationSuccessEvent) {
        loginAttemptService.success( ((User)((UsernamePasswordAuthenticationToken)authenticationSuccessEvent.getSource()).getPrincipal()).getUsername());
    }
}
