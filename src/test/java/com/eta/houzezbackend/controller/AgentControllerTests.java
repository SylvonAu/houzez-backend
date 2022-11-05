package com.eta.houzezbackend.controller;

import com.eta.houzezbackend.dto.AgentSignUpDto;
import com.eta.houzezbackend.dto.PropertyCreateDto;
import com.eta.houzezbackend.mapper.AgentMapper;
import com.eta.houzezbackend.model.Agent;
import com.eta.houzezbackend.repository.AgentRepository;
import com.eta.houzezbackend.service.JwtService;
import com.eta.houzezbackend.util.PropertyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class AgentControllerTests extends ControllerIntTest {
    @Autowired
    private AgentController agentController;

    @Autowired
    private JwtService jwtService;


    @Autowired
    private AgentRepository agentRepository;
    @Autowired
    private AgentMapper agentMapper;

    private PropertyCreateDto mockPropertyCreateDto;
    private Agent mockAgent;
    private long mockAgentId;
    private long mockUserId;

    private String mockUserEmail;

    private String mockJwt;

    private String mockFakeJwt;

    @BeforeEach
    void signUp() {

        agentRepository.deleteAll();
        agentRepository.flush();
        mockUserId = agentController.signUp(AgentSignUpDto.builder().email("test3@gmail.com")
                .password("123qqqqq.").build()).getId();
        mockUserEmail = agentController.signUp(AgentSignUpDto.builder().email("agent002@gmail.com")
                .password("agent002@gmail.comA.").build()).getEmail();
        String mockUserName = agentController.getAgent(mockUserId).getName();
        mockJwt = jwtService.createJWT(String.valueOf(mockUserId), mockUserName, 80000);
        mockFakeJwt = jwtService.createJWT(String.valueOf(mockUserId), mockUserName, -80000);

        mockAgent = agentMapper.agentGetDtoToAgent(agentController.signUp(AgentSignUpDto.builder()
                .email("test2@gmail.com")
                .password("123qqqqq.")
                .build()));
        mockAgent.setActivated(true);
        mockAgentId = mockAgent.getId();
        mockPropertyCreateDto = PropertyCreateDto.builder()
                .garage(1).
                propertyType(PropertyType.HOUSE)
                .description("Mount house")
                .title("HOUSE with sea view")
                .propertyIsNew(true)
                .price(800000)
                .livingRoom(2)
                .bedroom(4).bathroom(3)
                .landSize(200).state("Tas")
                .suburb("Kingston").postcode(7010).build();

    }

    @Test
    void shouldReturn201AndAgentDtoWhenAgentIsCreated() throws Exception {
        AgentSignUpDto agentSignUpDto = AgentSignUpDto.builder().email("test4@gmail.com").password("123qqqqq.").build();

        mockMvc.perform(post("/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(agentSignUpDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test4@gmail.com"));
    }

    @Test
    void shouldReturn200AndAgentDtoWhenGetAgentDto() throws Exception {
        mockMvc.perform(get("/agents/" + mockUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockUserId));
    }

    @Test
    void shouldReturn200WhenFindByEmail() throws Exception {
        mockMvc.perform(head("/agents?email=" + mockUserEmail))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenCannotFindByEmail() throws Exception {
        mockMvc.perform(head("/agents?email=t" + mockUserEmail))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn200AndAgentWhenSetAgentToActive() throws Exception {
        mockMvc.perform(patch("/agents/" + mockUserId + "?token=" + mockJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activated").value(true));
    }

    @Test
    void shouldReturn201AndPropertyCreateDtoWhenPropertyIsCreated() throws Exception {
        mockMvc.perform(post("/agents/" + mockAgentId + "/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockPropertyCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("HOUSE with sea view"));
    }

    @Test
    void shouldReturn401WhenSetAgentToActive() throws Exception {
        mockMvc.perform(patch("/agents/" + mockUserId + "?token=" + mockFakeJwt))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void shouldReturn200AndForgetPasswordEmailSent() throws Exception {
        mockMvc.perform(post("/agents/forget-password")
                        .content("{\"email\":\"agent002@gmail.com\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400WhenForgetPasswordEmailSentFailed() throws Exception {
        mockMvc.perform(post("/agents/forget-password")
                        .content("{\"email\":\"hagent002@gmail.com\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}