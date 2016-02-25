package com.rbc.timemanagmentservice.controller;

import com.rbc.timemanagmentservice.TimemanagementServiceApplication;
import com.rbc.timemanagmentservice.model.Contract;
import com.rbc.timemanagmentservice.model.Employee;
import com.rbc.timemanagmentservice.util.StartupUtility;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by russbaker on 2/22/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TimemanagementServiceApplication.class)
@WebAppConfiguration
@Profile({"default", "test"})
@Transactional
public class ContractControllerTest extends ControllerTests{
    public static final String ROOT_URI = "/hydrated/contract/";
    private MediaType contentType = new MediaType(MediaTypes.HAL_JSON.getType(),
            MediaTypes.HAL_JSON.getSubtype());

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StartupUtility startupUtility;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void whenCreatingContract_expectContractCreated() throws Exception {
        // Assemble
        Contract contract = new Contract();
        contract.setStartDate(new DateTime());
        contract.setEndDate(new DateTime().plusMonths(6));
        contract.setRate(87.5);
        contract.setTerms(Contract.Terms.net15);
        contract.setValue(87999D);

        // Act
        mockMvc.perform(post(ROOT_URI )
        .content(json(contract))
        .contentType(contentType)
        .header("Location",is("http://localhost/hydrated/contract/1")))
                .andExpect(status().isCreated());

    }
// TODO: implement
//    @Test
//    public void whenUpdatingContract_expectContractUpdated() throws Exception {
//        // Assemble
//        Employee employee = startupUtility.init();
//        Contract existingContract = employee.getJob().get(0);
//        existingContract.setEndDate(existingContract.getEndDate().plusMonths(4));
//
//        // Act
//        mockMvc.perform(put(ROOT_URI + existingContract.getId())
//                .session(createMockHttpSessionForPutPost())
//                .content(json(existingContract))
//                .contentType(contentType)
//                .header("Location",is("http://localhost/hydrated/contract/1")))
//                .andDo(print())
//                .andExpect(status().isCreated());
//
//    }
}