package com.Ristretto.API.controller;

import com.Ristretto.API.domain.calendarEvent.*;
import com.Ristretto.API.domain.task.Task;
import com.Ristretto.API.domain.user.User;
import com.Ristretto.API.infra.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


// 1. CORREÇÃO PRINCIPAL: REMOVER todas as anotações Spring no topo (ex: @WebMvcTest)
class CalendarEventControllerTest {

    // O MockMvc não é mais injetado pelo Spring, então não precisa de @Autowired
    private MockMvc mockMvc;

    // 2. DEPENDÊNCIAS: Usamos as anotações puras do Mockito
    @Mock
    private CalendarEventService service;

    // 3. CONTROLADOR: Injetamos o mock do serviço no controlador
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
        // Inicializa os mocks (service e controller)
        MockitoAnnotations.openMocks(this);

        // Configuração ObjectMapper com suporte a LocalDateTime
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();

        // 4. MOCKMVC MANUAL: Configuração Standalone (TESTE UNITÁRIO PURO)
        // Isso configura o MockMvc sem depender de um contexto completo do Spring
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(pageableResolver)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        // Inicialização dos mocks de dados
        User mockUser = new User();
        Task mockTask = new Task();
        mockCalendarEvent = new CalendarEvent();
        mockCalendarEvent.setId(calendarEventId);
        mockCalendarEvent.setTitle("Test Title");
        mockCalendarEvent.setDescription("Test Description");
        mockCalendarEvent.setUser(mockUser);


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

    @Test
    void register_ShouldReturnCreated_WhenValidDataProvided() throws Exception {
        when(service.convertToEntity(any(CalendarEventRegistrationData.class))).thenReturn(mockCalendarEvent);
        when(service.save(any(CalendarEvent.class))).thenReturn(mockCalendarEvent);

        mockMvc.perform(post("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(service, times(1)).convertToEntity(any(CalendarEventRegistrationData.class));
        verify(service, times(1)).save(any(CalendarEvent.class));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenTitleIsEmpty() throws Exception {
        CalendarEventRegistrationData invalidData = new CalendarEventRegistrationData(
                "",
                "Description",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                "America/Sao_Paulo",
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
        verify(service, never()).save(any());
    }

    @Test
    void listing_ShouldReturnPageOfCalendarEvents_WhenUserIdProvided() throws Exception {
        var events = Collections.singletonList(mockCalendarEvent);
        var page = new PageImpl<>(events, PageRequest.of(0, 20), 1);

        when(service.findByUserId(any(), eq(userId))).thenReturn(page);

        mockMvc.perform(get("/calendar_event")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service, times(1)).findByUserId(any(), eq(userId));
    }

    @Test
    void update_ShouldReturnUpdatedCalendarEvent_WhenValidDataProvided() throws Exception {
        when(service.findByid(calendarEventId)).thenReturn(mockCalendarEvent);
        when(service.save(any(CalendarEvent.class))).thenReturn(mockCalendarEvent);

        mockMvc.perform(put("/calendar_event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk());

        verify(service, times(1)).findByid(calendarEventId);
        verify(service, times(1)).save(any(CalendarEvent.class));
    }

    @Test
    void delete_ShouldReturnNoContent_WhenCalendarEventExists() throws Exception {
        doNothing().when(service).deleteById(calendarEventId);

        mockMvc.perform(delete("/calendar_event/{id}", calendarEventId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteById(calendarEventId);
    }

    @Test
    void delete_ShouldReturnInternalServerError_WhenCalendarEventNotFound() throws Exception {
        doThrow(new RuntimeException("CalendarEvent not found")).when(service).deleteById(calendarEventId);

        mockMvc.perform(delete("/calendar_event/{id}", calendarEventId))
                .andExpect(status().isInternalServerError());
        verify(service, times(1)).deleteById(calendarEventId);
    }
}
