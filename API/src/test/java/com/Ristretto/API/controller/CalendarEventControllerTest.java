package com.Ristretto.API.controller;

import com.Ristretto.API.domain.calendarEvent.*;
import com.Ristretto.API.domain.task.Task;
import com.Ristretto.API.domain.user.User;
import com.Ristretto.API.infra.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("CalendarEvent Controller Tests")
class CalendarEventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CalendarEventService service;

    @InjectMocks
    private CalendarEventController controller;

    private CalendarEvent mockCalendarEvent;
    private CalendarEventRegistrationData registrationData;
    private CalendarEventUpdateData updateData;
    private final Long calendarEventId = 1L;
    private final Long userId = 1L;
    private final Long taskId = 2L;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(pageableResolver)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        User mockUser = new User();
        mockUser.setId(userId);

        Task mockTask = new Task();
        mockTask.setId(taskId);

        mockCalendarEvent = new CalendarEvent();
        mockCalendarEvent.setId(calendarEventId);
        mockCalendarEvent.setTitle("Test Title");
        mockCalendarEvent.setDescription("Test Description");
        mockCalendarEvent.setStartDate(LocalDateTime.now().plusDays(1));
        mockCalendarEvent.setEndDate(LocalDateTime.now().plusDays(1).plusHours(1));
        mockCalendarEvent.setTimezone("America/Sao_Paulo");
        mockCalendarEvent.setColor("#FF5733");
        mockCalendarEvent.setUser(mockUser);
        mockCalendarEvent.setTask(mockTask);
        mockCalendarEvent.setRepetitive(false);
        mockCalendarEvent.setRepetitionType(null);
        mockCalendarEvent.setRepetitionRule(null);
        mockCalendarEvent.setTactileReminder(false);

        registrationData = new CalendarEventRegistrationData(
                "Team Meeting",
                "Weekly team sync meeting",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                "America/Sao_Paulo",
                "#FF5733",
                userId,
                taskId,
                true,
                RepetitionType.WEEKLY,
                "FREQ=WEEKLY;BYDAY=MO",
                true
        );

        updateData = new CalendarEventUpdateData(
                calendarEventId,
                "Updated Meeting",
                "Updated description",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1),
                "America/Sao_Paulo",
                "#33FF57",
                false,
                null,
                null,
                false
        );
    }

    // ---------- REGISTRO ----------
    @Test
    @DisplayName("Deve registrar evento válido e retornar 201")
    void register_ShouldReturnCreated_WhenValidDataProvided() throws Exception {
        when(service.convertToEntity(any(CalendarEventRegistrationData.class))).thenReturn(mockCalendarEvent);
        when(service.save(any(CalendarEvent.class))).thenReturn(mockCalendarEvent);

        mockMvc.perform(post("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(service, times(1)).convertToEntity(any());
        verify(service, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve retornar 400 quando corpo está vazio")
    void register_ShouldReturnBadRequest_WhenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    @Test
    @DisplayName("Deve retornar 400 quando JSON está malformado")
    void register_ShouldReturnBadRequest_WhenJsonMalformed() throws Exception {
        String malformedJson = "{\"title\":\"Meeting\",\"startDate\":}";

        mockMvc.perform(post("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    @Test
    @DisplayName("Deve retornar 400 quando timezone é inválido")
    void register_ShouldReturnBadRequest_WhenTimezoneIsInvalid() throws Exception {
        CalendarEventRegistrationData invalidData = new CalendarEventRegistrationData(
                "Meeting",
                "Description",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                "Invalid/Zone",
                "#FF5733",
                userId,
                null,
                false,
                null,
                null,
                false
        );

        mockMvc.perform(post("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    @Test
    @DisplayName("Deve retornar 400 quando repetição é inconsistente")
    void register_ShouldReturnBadRequest_WhenRepetitionIsInconsistent() throws Exception {
        when(service.convertToEntity(any(CalendarEventRegistrationData.class)))
                .thenReturn(mockCalendarEvent);
        when(service.save(any(CalendarEvent.class)))
                .thenReturn(mockCalendarEvent);
        CalendarEventRegistrationData invalidData = new CalendarEventRegistrationData(
                "Meeting",
                "Description",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                "America/Sao_Paulo",
                "#FF5733",
                userId,
                null,
                true,
                null, // repetitionType ausente
                null, // repetitionRule ausente
                false
        );

        mockMvc.perform(post("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenEndDateBeforeStartDate() throws Exception {
        CalendarEventRegistrationData invalidData = new CalendarEventRegistrationData(
                "Meeting", "Desc",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1), // fim antes do início
                "America/Sao_Paulo", "#FF5733", userId, null,
                false, null, null, false
        );

        mockMvc.perform(post("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());
    }


    // ---------- LISTAGEM ----------
    @Test
    @DisplayName("Deve listar eventos de um usuário")
    void listing_ShouldReturnPageOfCalendarEvents_WhenUserIdProvided() throws Exception {
        var page = new PageImpl<>(Collections.singletonList(mockCalendarEvent), PageRequest.of(0, 20), 1);

        when(service.findByUserId(any(), eq(userId))).thenReturn(page);

        mockMvc.perform(get("/calendar_event")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service, times(1)).findByUserId(any(), eq(userId));
    }

    @Test
    @DisplayName("Deve retornar 200 e uma página de eventos quando paginação é válida")
    void listing_ShouldReturnOk_WhenPaginationValid() throws Exception {
        var page = new PageImpl<>(
                Collections.singletonList(mockCalendarEvent),
                PageRequest.of(0, 20),
                1
        );

        when(service.findByUserId(any(), eq(userId))).thenReturn(page);

        mockMvc.perform(get("/calendar_event")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service, times(1)).findByUserId(any(), eq(userId));
    }



    // ---------- ATUALIZAÇÃO ----------
    @Test
    @DisplayName("Deve atualizar evento válido e retornar 200")
    void update_ShouldReturnUpdatedCalendarEvent_WhenValidDataProvided() throws Exception {
        when(service.existsById(calendarEventId)).thenReturn(true);
        when(service.findByid(calendarEventId)).thenReturn(mockCalendarEvent);
        when(service.save(any(CalendarEvent.class))).thenReturn(mockCalendarEvent);

        mockMvc.perform(put("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk());

        verify(service, times(1)).existsById(calendarEventId);
        verify(service, times(1)).findByid(calendarEventId);
        verify(service, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve retornar 404 quando evento não existe na atualização")
    void update_ShouldReturnNotFound_WhenCalendarEventDoesNotExist() throws Exception {
        when(service.existsById(calendarEventId)).thenReturn(false);

        mockMvc.perform(put("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());

        verify(service, times(1)).existsById(calendarEventId);
        verify(service, never()).findByid(any());
        verify(service, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar 400 quando ID é inválido no update")
    void update_ShouldReturnBadRequest_WhenIdIsInvalidType() throws Exception {
        String invalidJson = """
            {"id":"abc","title":"Meeting","description":"Desc"}
            """;

        mockMvc.perform(put("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(service, never()).existsById(any());
    }

    @Test
    @DisplayName("Deve permitir atualização parcial (apenas título)")
    void update_ShouldAllowPartialUpdate_WhenOnlyTitleProvided() throws Exception {
        CalendarEventUpdateData partialUpdate = new CalendarEventUpdateData(
                calendarEventId,
                "New Title",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(service.existsById(calendarEventId)).thenReturn(true);
        when(service.findByid(calendarEventId)).thenReturn(mockCalendarEvent);
        when(service.save(any(CalendarEvent.class))).thenReturn(mockCalendarEvent);

        mockMvc.perform(put("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk());

        verify(service, times(1)).existsById(calendarEventId);
        verify(service, times(1)).findByid(calendarEventId);
        verify(service, times(1)).save(any());
    }

    // ---------- DELETE ----------
    @Test
    @DisplayName("Deve deletar evento existente e retornar 204")
    void delete_ShouldReturnNoContent_WhenCalendarEventExists() throws Exception {
        when(service.existsById(calendarEventId)).thenReturn(true);
        doNothing().when(service).deleteById(calendarEventId);

        mockMvc.perform(delete("/calendar_event/{id}", calendarEventId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).existsById(calendarEventId);
        verify(service, times(1)).deleteById(calendarEventId);
    }

    @Test
    @DisplayName("Deve retornar 404 quando evento não existe no delete")
    void delete_ShouldReturnNotFound_WhenCalendarEventNotFound() throws Exception {
        when(service.existsById(calendarEventId)).thenReturn(false);

        mockMvc.perform(delete("/calendar_event/{id}", calendarEventId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).existsById(calendarEventId);
        verify(service, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve retornar 400 quando ID é inválido no delete")
    void delete_ShouldReturnBadRequest_WhenIdIsInvalidType() throws Exception {
        mockMvc.perform(delete("/calendar_event/{id}", "abc"))
                .andExpect(status().isBadRequest());

        verify(service, never()).deleteById(any());
    }
}