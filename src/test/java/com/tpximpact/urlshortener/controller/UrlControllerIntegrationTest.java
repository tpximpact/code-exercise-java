package com.tpximpact.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpximpact.urlshortener.dto.UrlRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UrlControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Full integration flow: Create, Redirect, List, and Delete")
    void testUrlLifecycle() throws Exception {
        String longUrl = "https://www.tpximpact.com";
        String alias = "tpx";
        UrlRequest request = new UrlRequest(longUrl, alias);

        // 1. POST /shorten - Create a new mapping
        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").exists());

        // 2. GET /{alias} - Test redirection logic
        mockMvc.perform(get("/{alias}", alias))
                .andExpect(status().isFound()) // 302
                .andExpect(header().string("Location", longUrl))
                .andExpect(content().string(""));

        // 3. GET /urls - Verify the alias appears in the list
        mockMvc.perform(get("/urls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.alias == '" + alias + "')]").exists());

        // 4. DELETE /{alias} - Remove the mapping
        mockMvc.perform(delete("/{alias}", alias))
                .andExpect(status().isNoContent());

        // 5. GET /{alias} - Verify it no longer exists (assuming service throws 404)
        mockMvc.perform(get("/{alias}", alias))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /shorten - Should return 400 when fullUrl is blank")
    void shouldReturn400ForInvalidInput() throws Exception {
        UrlRequest invalidRequest = new UrlRequest("", "short");

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}