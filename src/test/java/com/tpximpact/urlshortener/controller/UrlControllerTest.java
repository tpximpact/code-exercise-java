package com.tpximpact.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpximpact.urlshortener.dto.UrlListResponse;
import com.tpximpact.urlshortener.dto.UrlRequest;
import com.tpximpact.urlshortener.dto.UrlResponse;
import com.tpximpact.urlshortener.exception.NotFoundException;
import com.tpximpact.urlshortener.service.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // SB 4.0 standard
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UrlService urlService;

    @Test
    @DisplayName("POST /shorten - Success")
    void shortenUrl_Returns201() throws Exception {
        // Given
        UrlRequest request = new UrlRequest("https://example.com", "my-alias");
        UrlResponse response = new UrlResponse("http://localhost:8080/my-alias");

        when(urlService.shorten(any(UrlRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/my-alias"));

        verify(urlService, times(1)).shorten(any(UrlRequest.class));
    }

    @Test
    @DisplayName("GET /{alias} - Redirects to full URL")
    void redirect_Returns302() throws Exception {
        // Given
        String alias = "tpx";
        String fullUrl = "https://tpximpact.com";
        when(urlService.getFullUrl(alias)).thenReturn(fullUrl);

        // When & Then
        mockMvc.perform(get("/{alias}", alias))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", fullUrl))
                .andExpect(content().string("")); // Asserting empty body as requested

        verify(urlService).getFullUrl(alias);
    }

    @Test
    @DisplayName("GET /{alias} - Redirects returns 404 if alias not exist")
    void redirect_Returns404_If_Alias_Not_Exist() throws Exception {
        // Given
        String aliasNotExist = "notExist";
        when(urlService.getFullUrl(aliasNotExist)).thenThrow(NotFoundException.class);

        // When & Then
        mockMvc.perform(get("/{alias}", aliasNotExist))
                .andExpect(status().isNotFound());

        verify(urlService).getFullUrl(aliasNotExist);
    }

    @Test
    @DisplayName("GET /urls - Returns list of mappings")
    void getAll_Returns200() throws Exception {
        // Given
        List<UrlListResponse> mockList = List.of(
                new UrlListResponse("alias1", "https://site1.com", "http://short/1"),
                new UrlListResponse("alias2", "https://site2.com", "http://short/2")
        );
        when(urlService.getAll()).thenReturn(mockList);

        // When & Then
        mockMvc.perform(get("/urls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].alias").value("alias1"));
    }

    @Test
    @DisplayName("DELETE /{alias} - Success")
    void delete_Returns204() throws Exception {
        // When & Then
        mockMvc.perform(delete("/{alias}", "old-alias"))
                .andExpect(status().isNoContent());

        verify(urlService).delete("old-alias");
    }

    @Test
    @DisplayName("POST /shorten - Fails on invalid input")
    void shortenUrl_BlankUrl_Returns400() throws Exception {
        UrlRequest invalidRequest = new UrlRequest("", "alias");

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(urlService);
    }
}