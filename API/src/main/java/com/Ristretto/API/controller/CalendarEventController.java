package com.Ristretto.API.controller;

import com.Ristretto.API.domain.calendarEvent.*;
import com.Ristretto.API.domain.category.CategoryDataDetails;
import com.Ristretto.API.domain.category.CategoryUpdateData;
import com.Ristretto.API.infra.exception.Domain.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/calendar_event")
@Tag(name = "Calendar Event", description = "gerenciamento dos eventos registrados no calendario pelo usuario")
public class CalendarEventController {

    @Autowired
    private CalendarEventService service;

    @PostMapping
    @Transactional
    @Operation(
            summary = "Registrar novo evento no calendario",
            description = "Registra novo evento no calendario para aquele usuario"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Evento Registrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CalendarEventDataDetails.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Dados de entrada inválidos\" }")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")
                    )
            )
    })
    public ResponseEntity register(@RequestBody @Valid CalendarEventRegistrationData data,
                                   UriComponentsBuilder uriBuilder) {
        try {
            var calendarEvent = service.convertToEntity(data);
            service.save(calendarEvent);

            var uri = uriBuilder
                    .path("/calendar_event/{id}")
                    .buildAndExpand(calendarEvent.getId())
                    .toUri();

            return ResponseEntity.created(uri).body(new CalendarEventDataDetails(calendarEvent));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping
    @Operation(
            summary = "Listar eventos relacionadas a um usuario",
            description = "Retorna uma lista paginada de eventos"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de eventos recuperada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CalendarEventDataListing.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")
                    )
            )
    })
    public ResponseEntity<Page<CalendarEventDataListing>> listing(
            @PageableDefault(size = 20, page = 0, sort = "id") Pageable pageable,
            @RequestParam Long userId
    ) {
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        var page = service.findByUserId(pageable, userId).map(CalendarEventDataListing::new);
        return ResponseEntity.ok(page);
    }



    @PutMapping
    @Transactional
    @Operation(
            summary = "Atualizar tarefa",
            description = "Atualiza as informações de uma tarefa existente pelo ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Dados inválidos\" }")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Tarefa não encontrada\" }")
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CalendarEventDataDetails> update(
            @RequestBody @Valid CalendarEventUpdateData data
    ) {
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
    @Operation(
            summary = "Deletar evento",
            description = "Remove um evento do sistema pelo ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "evento deletado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "evento não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"evento não encontrado\" }")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")
                    )
            )
    })
    public ResponseEntity<Void> delete(
            @PathVariable
            @Parameter(description = "ID do evento a ser deletado", required = true, example = "1")
            Long id
    ){
        if (!service.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada");
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}