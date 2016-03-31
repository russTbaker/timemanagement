package com.rbc.timemanagmentservice.service;


/**
 * Created by russbaker on 3/30/16.
 */
public interface LoginAttemptService {
    void incrementLoginFailure(String userName);
    void success(String userName);
}
