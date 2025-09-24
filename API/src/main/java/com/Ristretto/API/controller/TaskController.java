package com.Ristretto.API.controller;

import com.Ristretto.API.domain.task.*;
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
@RequestMapping("/task")
@Tag(name = "Task", description = "gerenciamento de tarefas do usuario")
public class TaskController {

    @Autowired
    private TaskService service;

    @PostMapping
    @Transactional
    @Operation(
            summary = "Criar nova tarefa",
            description = "Cria uma nova tarefa no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tarefa criada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskDataDetails.class)
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
    public ResponseEntity register(@RequestBody @Valid TaskRegistrationData data, UriComponentsBuilder uriBuilder) {
        var task = service.createTask(data);

        var uri = uriBuilder.path("/task/{id}").buildAndExpand(task.getId()).toUri();

        return ResponseEntity.created(uri).body(new TaskDataDetails(task));
    }

    @GetMapping
    @Operation(
            summary = "Listar tarefas",
            description = "Retorna uma lista paginada de tarefas"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de tarefas recuperada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskDataListing.class)
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
    public ResponseEntity<Page<TaskDataListing>> listing(
            @PageableDefault(sort = {"priority", "dueDate"}, direction = Sort.Direction.ASC)
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            Pageable pageable
    ){
        var page = service.findAll(pageable).map(TaskDataListing::new);

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
    public ResponseEntity<TaskDataDetails> update(
            @RequestBody @Valid TaskUpdateData data
    ) {
        var task = service.findById(data.id());
        task.update(data);
        service.save(task);
        return ResponseEntity.ok(new TaskDataDetails(task));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(
            summary = "Deletar tarefa",
            description = "Remove uma tarefa do sistema pelo ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Tarefa deletada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tarefa não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Tarefa não encontrada\" }")
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
            @Parameter(description = "ID da tarefa a ser deletada", required = true, example = "1")
            Long id
    ){
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}