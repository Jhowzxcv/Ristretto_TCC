package com.Ristretto.API.controller;

import com.Ristretto.API.domain.calendarEvent.*;
import com.Ristretto.API.infra.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("CalendarEvent Controller Tests (Completo e Revisado)")
class CalendarEventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CalendarEventService service;

    @InjectMocks
    private CalendarEventController controller;

    private ObjectMapper objectMapper;
    private final Long userId = 1L;
    private final Long eventId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    // ---------- REGISTRO ----------
    @Test
    void register_ShouldReturnCreated_WhenValidData() throws Exception {
        CalendarEvent mockEvent = new CalendarEvent();
        mockEvent.setId(eventId);
        mockEvent.setTitle("Meeting");

        when(service.convertToEntity(any(CalendarEventRegistrationData.class))).thenReturn(mockEvent);
        when(service.save(any(CalendarEvent.class))).thenReturn(mockEvent);

        CalendarEventRegistrationData data = new CalendarEventRegistrationData(
                "Meeting", "Desc",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                "America/Sao_Paulo",
                "#1A2B3C",
                userId,
                null,
                false,
                null,
                null,
                false
        );

        mockMvc.perform(post("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.title").value("Meeting"));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenTitleIsBlank() throws Exception {
        CalendarEventRegistrationData invalid = new CalendarEventRegistrationData(
                "", // viola @NotBlank
                "Desc",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "America/Sao_Paulo",
                "#1A2B3C",
                userId,
                null,
                false,
                null,
                null,
                false
        );

        mockMvc.perform(post("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
        verify(service, never()).save(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenStartDateIsInPast() throws Exception {
        CalendarEventRegistrationData invalid = new CalendarEventRegistrationData(
                "Meeting",
                "Desc",
                LocalDateTime.now().minusDays(1), // viola @Future
                LocalDateTime.now().plusDays(1),
                "America/Sao_Paulo",
                "#1A2B3C",
                userId,
                null,
                false,
                null,
                null,
                false
        );

        mockMvc.perform(post("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
        verify(service, never()).save(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenColorIsInvalid() throws Exception {
        CalendarEventRegistrationData invalid = new CalendarEventRegistrationData(
                "Meeting",
                "Desc",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "America/Sao_Paulo",
                "123456", // viola @Pattern
                userId,
                null,
                false,
                null,
                null,
                false
        );

        mockMvc.perform(post("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
        verify(service, never()).save(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenUserIdIsNull() throws Exception {
        CalendarEventRegistrationData invalid = new CalendarEventRegistrationData(
                "Meeting",
                "Desc",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "America/Sao_Paulo",
                "#1A2B3C",
                null, // viola @NotNull
                null,
                false,
                null,
                null,
                false
        );

        mockMvc.perform(post("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
        verify(service, never()).save(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenJsonMalformed() throws Exception {
        String malformedJson = "{\"title\":\"Meeting\",\"startDate\":}";

        mockMvc.perform(post("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    // ---------- LISTAGEM ----------
    @Test
    void listing_ShouldReturnPageOfEvents_WhenUserIdProvided() throws Exception {
        CalendarEvent mockEvent = new CalendarEvent();
        mockEvent.setId(eventId);
        mockEvent.setTitle("Meeting");

        var page = new PageImpl<>(Collections.singletonList(mockEvent), PageRequest.of(0, 10), 1);
        when(service.findByUserId(any(), eq(userId))).thenReturn(page);

        mockMvc.perform(get("/calendar_event")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void listing_ShouldReturnEmptyPage_WhenNoEventsFound() throws Exception {
        var page = new PageImpl<CalendarEvent>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(service.findByUserId(any(), eq(userId))).thenReturn(page);

        mockMvc.perform(get("/calendar_event")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void listing_ShouldReturnBadRequest_WhenPaginationInvalid() throws Exception {
        // Evita NPE ou UnsupportedOperationException na serialização
        when(service.findByUserId(any(), any())).thenReturn(Page.empty(PageRequest.of(0, 1)));

        mockMvc.perform(get("/calendar_event")
                        .param("userId", userId.toString())
                        .param("page", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/calendar_event")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    // ---------- UPDATE ----------
    @Test
    void update_ShouldReturnOk_WhenValidData() throws Exception {
        CalendarEvent mockEvent = new CalendarEvent();
        mockEvent.setId(eventId);
        mockEvent.setTitle("Updated Meeting");

        when(service.existsById(eventId)).thenReturn(true);
        when(service.findByid(eventId)).thenReturn(mockEvent);
        when(service.save(any(CalendarEvent.class))).thenReturn(mockEvent);

        CalendarEventUpdateData update = new CalendarEventUpdateData(
                eventId,
                "Updated Meeting",
                "Desc",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1),
                "America/Sao_Paulo",
                "#1A2B3C",
                false,
                null,
                null,
                false
        );

        mockMvc.perform(put("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.title").value("Updated Meeting"));
    }

    @Test
    void update_ShouldReturnNotFound_WhenEventNotFound() throws Exception {
        when(service.existsById(eventId)).thenReturn(false);

        CalendarEventUpdateData update = new CalendarEventUpdateData(
                eventId,
                "Updated Meeting",
                "Desc",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1),
                "America/Sao_Paulo",
                "#1A2B3C",
                false,
                null,
                null,
                false
        );

        mockMvc.perform(put("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Recurso não encontrado\""));
    }

    @Test
    void update_ShouldReturnBadRequest_WhenIdIsNull() throws Exception {
        CalendarEventUpdateData invalid = new CalendarEventUpdateData(
                null, // viola @NotNull
                "Title",
                "Desc",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1),
                "America/Sao_Paulo",
                "#1A2B3C",
                false,
                null,
                null,
                false
        );

        mockMvc.perform(put("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(service, never()).save(any());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenIdIsNegative() throws Exception {
        CalendarEventUpdateData invalid = new CalendarEventUpdateData(
                -1L, // viola @Positive
                "Title",
                "Desc",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1),
                "America/Sao_Paulo",
                "#1A2B3C",
                false,
                null,
                null,
                false
        );

        mockMvc.perform(put("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(service, never()).save(any());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenJsonMalformed() throws Exception {
        String malformedJson = "{\"id\":1,\"title\":\"Updated Meeting\",\"startDate\":}";

        mockMvc.perform(put("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    // ---------- DELETE ----------
    @Test
    void delete_ShouldReturnNoContent_WhenEventExists() throws Exception {
        when(service.existsById(eventId)).thenReturn(true);
        doNothing().when(service).deleteById(eventId);

        mockMvc.perform(delete("/calendar_event/{id}", eventId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).existsById(eventId);
        verify(service, times(1)).deleteById(eventId);
    }

    @Test
    void delete_ShouldReturnNotFound_WhenEventNotFound() throws Exception {
        when(service.existsById(eventId)).thenReturn(false);

        mockMvc.perform(delete("/calendar_event/{id}", eventId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Recurso não encontrado\""));

        verify(service, times(1)).existsById(eventId);
        verify(service, never()).deleteById(any());
    }
}