package com.Ristretto.API.controller;

import com.Ristretto.API.domain.subTask.*;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/subtask")
@Tag(name = "subTask", description = "gerenciamento de subtarefas do usuario")
public class SubTaskController {

    @Autowired
    private SubTaskService service;

    @PostMapping
    @Transactional
    @Operation(
            summary = "Criar nova subtarefa",
            description = "Cria uma nova subtarefa no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "subtarefa criada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SubTaskDataDetails.class)
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
    public ResponseEntity register(@RequestBody @Valid SubTaskRegistrationData data, UriComponentsBuilder uriBuilder) {
        var subtask = service.convertToEntity(data);
        service.save(subtask);
        var uri = uriBuilder.path("/task/{id}").buildAndExpand(subtask.getId()).toUri();

        return ResponseEntity.created(uri).body(new SubTaskDataDetails(subtask));
    }

    @GetMapping
    @Operation(
            summary = "Listar subtarefas",
            description = "Retorna uma lista paginada de subtarefas relacionadas a uma Tarefa"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de subtarefas recuperada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SubTaskDataDetails.class)
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
    public ResponseEntity<Page<SubTaskDataListing>> listing(
            @PageableDefault(sort = {"dueDate"}, direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam Long taskId
    ){
        var page = service.findByTaskId(pageable, taskId).map(SubTaskDataListing::new);
        return ResponseEntity.ok(page);
    }


    @DeleteMapping("/{id}")
    @Transactional
    @Operation(
            summary = "Deletar subtarefa",
            description = "Remove uma subtarefa do sistema pelo ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "subtarefa deletada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "subtarefa não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Subtarefa não encontrada\" }")
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
            @Parameter(description = "ID da subtarefa a ser deletada", required = true, example = "1")
            Long id
    ){
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}