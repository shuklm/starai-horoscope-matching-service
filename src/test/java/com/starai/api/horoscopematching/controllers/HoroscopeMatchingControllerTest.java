package com.starai.api.horoscopematching.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starai.api.horoscopematching.entities.HoroscopeMatching;
import com.starai.api.horoscopematching.services.HoroscopeMatchingService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

@WebMvcTest(controllers = HoroscopeMatchingController.class)
@ActiveProfiles("test")
class HoroscopeMatchingControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private HoroscopeMatchingService horoscopeMatchingService;

    @Autowired private ObjectMapper objectMapper;

    private List<HoroscopeMatching> horoscopeMatchingList;

    @BeforeEach
    void setUp() {
        this.horoscopeMatchingList = new ArrayList<>();
        this.horoscopeMatchingList.add(new HoroscopeMatching(1L, "text 1"));
        this.horoscopeMatchingList.add(new HoroscopeMatching(2L, "text 2"));
        this.horoscopeMatchingList.add(new HoroscopeMatching(3L, "text 3"));

        objectMapper.registerModule(new ProblemModule());
        objectMapper.registerModule(new ConstraintViolationProblemModule());
    }

    @Test
    void shouldFetchAllHoroscopeMatchings() throws Exception {
        given(horoscopeMatchingService.findAllHoroscopeMatchings()).willReturn(this.horoscopeMatchingList);

        this.mockMvc
                .perform(get("/api/horoscopematching"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(horoscopeMatchingList.size())));
    }

    @Test
    void shouldFindHoroscopeMatchingById() throws Exception {
        Long horoscopeMatchingId = 1L;
        HoroscopeMatching horoscopeMatching = new HoroscopeMatching(horoscopeMatchingId, "text 1");
        given(horoscopeMatchingService.findHoroscopeMatchingById(horoscopeMatchingId)).willReturn(Optional.of(horoscopeMatching));

        this.mockMvc
                .perform(get("/api/horoscopematching/{id}", horoscopeMatchingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(horoscopeMatching.getText())));
    }

    @Test
    void shouldReturn404WhenFetchingNonExistingHoroscopeMatching() throws Exception {
        Long horoscopeMatchingId = 1L;
        given(horoscopeMatchingService.findHoroscopeMatchingById(horoscopeMatchingId)).willReturn(Optional.empty());

        this.mockMvc
                .perform(get("/api/horoscopematching/{id}", horoscopeMatchingId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewHoroscopeMatching() throws Exception {
        given(horoscopeMatchingService.saveHoroscopeMatching(any(HoroscopeMatching.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        HoroscopeMatching horoscopeMatching = new HoroscopeMatching(1L, "some text");
        this.mockMvc
                .perform(
                        post("/api/horoscopematching")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(horoscopeMatching)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.text", is(horoscopeMatching.getText())));
    }

    @Test
    void shouldReturn400WhenCreateNewHoroscopeMatchingWithoutText() throws Exception {
        HoroscopeMatching horoscopeMatching = new HoroscopeMatching(null, null);

        this.mockMvc
                .perform(
                        post("/api/horoscopematching")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(horoscopeMatching)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(
                        jsonPath(
                                "$.type",
                                is("https://zalando.github.io/problem/constraint-violation")))
                .andExpect(jsonPath("$.title", is("Constraint Violation")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.violations", hasSize(1)))
                .andExpect(jsonPath("$.violations[0].field", is("text")))
                .andExpect(jsonPath("$.violations[0].message", is("Text cannot be empty")))
                .andReturn();
    }

    @Test
    void shouldUpdateHoroscopeMatching() throws Exception {
        Long horoscopeMatchingId = 1L;
        HoroscopeMatching horoscopeMatching = new HoroscopeMatching(horoscopeMatchingId, "Updated text");
        given(horoscopeMatchingService.findHoroscopeMatchingById(horoscopeMatchingId)).willReturn(Optional.of(horoscopeMatching));
        given(horoscopeMatchingService.saveHoroscopeMatching(any(HoroscopeMatching.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        this.mockMvc
                .perform(
                        put("/api/horoscopematching/{id}", horoscopeMatching.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(horoscopeMatching)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(horoscopeMatching.getText())));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingHoroscopeMatching() throws Exception {
        Long horoscopeMatchingId = 1L;
        given(horoscopeMatchingService.findHoroscopeMatchingById(horoscopeMatchingId)).willReturn(Optional.empty());
        HoroscopeMatching horoscopeMatching = new HoroscopeMatching(horoscopeMatchingId, "Updated text");

        this.mockMvc
                .perform(
                        put("/api/horoscopematching/{id}", horoscopeMatchingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(horoscopeMatching)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteHoroscopeMatching() throws Exception {
        Long horoscopeMatchingId = 1L;
        HoroscopeMatching horoscopeMatching = new HoroscopeMatching(horoscopeMatchingId, "Some text");
        given(horoscopeMatchingService.findHoroscopeMatchingById(horoscopeMatchingId)).willReturn(Optional.of(horoscopeMatching));
        doNothing().when(horoscopeMatchingService).deleteHoroscopeMatchingById(horoscopeMatching.getId());

        this.mockMvc
                .perform(delete("/api/horoscopematching/{id}", horoscopeMatching.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(horoscopeMatching.getText())));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingHoroscopeMatching() throws Exception {
        Long horoscopeMatchingId = 1L;
        given(horoscopeMatchingService.findHoroscopeMatchingById(horoscopeMatchingId)).willReturn(Optional.empty());

        this.mockMvc
                .perform(delete("/api/horoscopematching/{id}", horoscopeMatchingId))
                .andExpect(status().isNotFound());
    }
}
