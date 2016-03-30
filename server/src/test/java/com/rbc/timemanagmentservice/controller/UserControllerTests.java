package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.service.UserService;
import com.rbc.timemanagmentservice.testutils.ContractTestUtil;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by russbaker on 2/22/16.
 */
@Transactional
@Ignore("Parent class")
public class UserControllerTests<U extends User> extends ControllerTests{

    public static final String ROOT_URI_EMPLOYEES = "/hydrated/employees/";
    protected static final DateTimeFormatter FMT = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final String PHONES = "phones";
    protected User user;
    protected MediaType contentType = new MediaType(MediaTypes.HAL_JSON.getType(),
            MediaTypes.HAL_JSON.getSubtype());

    @Autowired
    private UserService<U> userService;



    @Autowired
    private StartupUtility startupUtility;

    @Autowired
    private ContractTestUtil contractTestUtil;

    //================== TESTS



    //-- Contracts

    @Test
    public void whenAddingContractsToUsers_expectContractsAdded() throws Exception {
        // Assemble
        Contract contract = contractTestUtil.getContract();

        // Act
        mockMvc.perform(put(ROOT_URI_EMPLOYEES + user.getId() + "/contracts/" + contract.getId())
                .with(csrf())
                .session(createMockHttpSessionForPutPost())
        ).andExpect(status().isAccepted());

        //Assert
        List<Contract> contracts = userService.getUser(user.getId()).getContracts();
        assertFalse("No contracts associated with user", CollectionUtils.isEmpty(contracts));
        assertTrue("Wrong contract",contracts.contains(contract));
    }


    //--------------- Private Methods
    private void createAddress(Address address) throws Exception {
        this.mockMvc.perform(
                post(UserControllerTests.ROOT_URI_EMPLOYEES + user.getId() + "/address/")
                        .with(csrf())
                        .session(createMockHttpSessionForPutPost())
                        .contentType(contentType)
                        .accept(contentType)
                        .content(json(address)))
                .andDo(print())
                .andExpect(header().string("Location",
                        containsString("/api/addresses")))
                .andExpect(status().isCreated());
    }


}
