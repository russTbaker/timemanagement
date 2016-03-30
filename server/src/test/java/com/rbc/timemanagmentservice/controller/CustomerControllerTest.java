package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Customer;
import com.rbc.timemanagmentservice.service.CustomerService;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by russbaker on 2/18/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TimemanagementServiceApplication.class)
@WebAppConfiguration
@Profile({"default", "test"})
@Transactional
public class CustomerControllerTest extends UserControllerTests<Customer> {

    @Autowired
    private WebApplicationContext webApplicationContext;


    @Autowired
    private StartupUtility startupUtility;

    @Resource
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private CustomerService customerService;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).addFilter(this.springSecurityFilterChain).build();
        startupUtility.init();
        user = startupUtility.getCustomerObject();
    }

    @After
    public void tearDown(){
//        if(user != null){
//            customerService.deleteUser(user.getId());
//        }
    }
}