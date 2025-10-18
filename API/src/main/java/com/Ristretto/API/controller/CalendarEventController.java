package com.Ristretto.API.controller;

import com.Ristretto.API.domain.calendarEvent.*;
import com.Ristretto.API.infra.exception.Domain.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/calendar_event")
@Tag(name = "Calendar Event", description = "Gerenciamento dos eventos registrados no calendário pelo usuário")
public class CalendarEventController {

    @Autowired
    private CalendarEventService service;

    @PostMapping
    @Transactional
    @Operation(summary = "Registrar novo evento", description = "Registra um novo evento no calendário do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evento registrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CalendarEventDataDetails.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Dados de entrada inválidos\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<CalendarEventDataDetails> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do evento a ser registrado",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CalendarEventRegistrationData.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Apenas obrigatórios",
                                            summary = "Exemplo com campos mínimos necessários",
                                            value = """
                                            {
                                              "title": "Reunião de trabalho",
                                              "startDate": "2025-10-20T14:00:00",
                                              "endDate": "2025-10-20T15:00:00",
                                              "userId": 1
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Completo",
                                            summary = "Exemplo com todos os campos preenchidos",
                                            value = """
                                            {
                                              "title": "Reunião de trabalho",
                                              "description": "Discutir metas do mês",
                                              "startDate": "2025-10-20T14:00:00",
                                              "endDate": "2025-10-20T15:00:00",
                                              "timezone": "America/Sao_Paulo",
                                              "color": "#1A2B3C",
                                              "userId": 1,
                                              "taskId": 2,
                                              "repetitive": true,
                                              "repetitionType": "DAILY",
                                              "repetitionRule": "FREQ=WEEKLY;BYDAY=MO",
                                              "tactileReminder": false
                                            }
                                            """
                                    )
                            }
                    )
            )
            @Valid
            @RequestBody
            CalendarEventRegistrationData data,
            UriComponentsBuilder uriBuilder) {

        var calendarEvent = service.convertToEntity(data);
        service.save(calendarEvent);

        var uri = uriBuilder.path("/calendar_event/{id}")
                .buildAndExpand(calendarEvent.getId())
                .toUri();

        return ResponseEntity.created(uri).body(new CalendarEventDataDetails(calendarEvent));
    }

    @GetMapping
    @Operation(summary = "Listar eventos", description = "Retorna uma lista paginada de eventos de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos recuperada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CalendarEventDataListing.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Parâmetros inválidos\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<Page<CalendarEventDataListing>> listing(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam Long userId
    ) {
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        var result = service.findByUserId(pageable, userId)
                .map(CalendarEventDataListing::new);
        return ResponseEntity.ok(result);
    }

    @PutMapping
    @Transactional
    @Operation(summary = "Atualizar evento", description = "Atualiza as informações de um evento existente pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Dados inválidos\" }"))),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Evento não encontrado\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CalendarEventDataDetails> update(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para atualizar o evento",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CalendarEventUpdateData.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Apenas obrigatórios",
                                            summary = "Atualização mínima (apenas ID)",
                                            value = """
                                            {
                                              "id": 1
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Completo",
                                            summary = "Atualização com todos os campos",
                                            value = """
                                            {
                                              "id": 1,
                                              "title": "Reunião de trabalho atualizada",
                                              "description": "Discutir novas metas",
                                              "startDate": "2025-10-21T14:00:00",
                                              "endDate": "2025-10-21T15:00:00",
                                              "timezone": "America/Sao_Paulo",
                                              "color": "#FF5733",
                                              "repetitive": false,
                                              "repetitionType": "WEEKLY",
                                              "repeitionRule": "FREQ=DAILY",
                                              "tactileReminder": true
                                            }
                                            """
                                    )
                            }
                    )
            )
            @Valid
            @RequestBody
            CalendarEventUpdateData data) {

        if (!service.existsById(data.id())) {
            throw new ResourceNotFoundException("Evento não encontrado");
        }

        var calendarEvent = service.findByid(data.id());
        calendarEvent.update(data);
        service.save(calendarEvent);

        return ResponseEntity.ok(new CalendarEventDataDetails(calendarEvent));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Deletar evento", description = "Remove um evento do sistema pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evento deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Evento não encontrado\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<Void> delete(
            @PathVariable
            @Parameter(description = "ID do evento a ser deletado", required = true, example = "1")
            Long id
    ) {
        if (!service.existsById(id)) {
            throw new ResourceNotFoundException("Evento não encontrado");
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}