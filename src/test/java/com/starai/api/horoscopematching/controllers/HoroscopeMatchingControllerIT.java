package com.starai.api.horoscopematching.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.starai.api.horoscopematching.common.AbstractIntegrationTest;
import com.starai.api.horoscopematching.entities.HoroscopeMatching;
import com.starai.api.horoscopematching.repositories.HoroscopeMatchingRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class HoroscopeMatchingControllerIT extends AbstractIntegrationTest {

    @Autowired private HoroscopeMatchingRepository horoscopeMatchingRepository;

    private List<HoroscopeMatching> horoscopeMatchingList = null;

    @BeforeEach
    void setUp() {
        horoscopeMatchingRepository.deleteAll();

        horoscopeMatchingList = new ArrayList<>();
        horoscopeMatchingList.add(new HoroscopeMatching(1L, "First HoroscopeMatching"));
        horoscopeMatchingList.add(new HoroscopeMatching(2L, "Second HoroscopeMatching"));
        horoscopeMatchingList.add(new HoroscopeMatching(3L, "Third HoroscopeMatching"));
        horoscopeMatchingList = horoscopeMatchingRepository.saveAll(horoscopeMatchingList);
    }

    @Test
    void shouldFetchAllHoroscopeMatchings() throws Exception {
        this.mockMvc
                .perform(get("/api/horoscopematching"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(horoscopeMatchingList.size())));
    }

    @Test
    void shouldFindHoroscopeMatchingById() throws Exception {
        HoroscopeMatching horoscopeMatching = horoscopeMatchingList.get(0);
        Long horoscopeMatchingId = horoscopeMatching.getId();

        this.mockMvc
                .perform(get("/api/horoscopematching/{id}", horoscopeMatchingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(horoscopeMatching.getText())));
    }

    @Test
    void shouldCreateNewHoroscopeMatching() throws Exception {
        HoroscopeMatching horoscopeMatching = new HoroscopeMatching(null, "New HoroscopeMatching");
        this.mockMvc
                .perform(
                        post("/api/horoscopematching")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(horoscopeMatching)))
                .andExpect(status().isCreated())
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
        HoroscopeMatching horoscopeMatching = horoscopeMatchingList.get(0);
        horoscopeMatching.setText("Updated HoroscopeMatching");

        this.mockMvc
                .perform(
                        put("/api/horoscopematching/{id}", horoscopeMatching.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(horoscopeMatching)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(horoscopeMatching.getText())));
    }

    @Test
    void shouldDeleteHoroscopeMatching() throws Exception {
        HoroscopeMatching horoscopeMatching = horoscopeMatchingList.get(0);

        this.mockMvc
                .perform(delete("/api/horoscopematching/{id}", horoscopeMatching.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(horoscopeMatching.getText())));
    }
}
