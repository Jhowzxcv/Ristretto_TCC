package com.Ristretto.API.controller;

import com.Ristretto.API.domain.task.*;
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
@RequestMapping("/task")
@Tag(name = "Task", description = "Gerenciamento de tarefas do usuário")
public class TaskController {

    @Autowired
    private TaskService service;

    @PostMapping
    @Transactional
    @Operation(summary = "Criar nova tarefa", description = "Cria uma nova tarefa no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDataDetails.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Dados de entrada inválidos\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<TaskDataDetails> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da tarefa a ser criada",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskRegistrationData.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Apenas obrigatórios",
                                            summary = "Exemplo com campos mínimos necessários",
                                            value = """
                                            {
                                              "title": "Estudar Java",
                                              "priority": "HIGH",
                                              "status": "PENDING",
                                              "userId": 1
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Completo",
                                            summary = "Exemplo com todos os campos preenchidos",
                                            value = """
                                            {
                                              "title": "Estudar Java",
                                              "description": "Revisar módulos de OOP",
                                              "dueDate": "2025-10-20T15:00:00",
                                              "priority": "HIGH",
                                              "status": "PENDING",
                                              "userId": 1,
                                              "categoryId": 2,
                                              "goalId": 3,
                                              "taskListId": 4,
                                              "focusMode": false,
                                              "isDone": false
                                            }
                                            """
                                    )
                            }
                    )
            )
            @RequestBody @Valid TaskRegistrationData data,
            UriComponentsBuilder uriBuilder
    ) {
        var task = service.convertToEntity(data);
        var saved = service.save(task);
        var uri = uriBuilder.path("/task/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(uri).body(new TaskDataDetails(saved));
    }

    @GetMapping
    @Operation(summary = "Listar tarefas", description = "Retorna uma lista paginada de tarefas de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tarefas recuperada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDataListing.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Parâmetros inválidos\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<Page<TaskDataListing>> listing(
            @RequestParam(defaultValue = "0")
            @Parameter(description = "Número da página (>= 0)", example = "0")
            int page,
            @RequestParam(defaultValue = "20")
            @Parameter(description = "Tamanho da página (> 0)", example = "20")
            int size,
            @RequestParam
            @Parameter(description = "ID do usuário para filtrar tarefas", required = true, example = "1")
            Long userId
    ) {
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        var result = service.findAllByUserId(userId, pageable).map(TaskDataListing::new);
        return ResponseEntity.ok(result);
    }

    @PutMapping
    @Transactional
    @Operation(summary = "Atualizar tarefa", description = "Atualiza as informações de uma tarefa existente pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDataDetails.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Dados inválidos\" }"))),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Tarefa não encontrada\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<TaskDataDetails> update(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para atualizar a tarefa",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskUpdateData.class),
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
                                              "title": "Estudar Java Avançado",
                                              "description": "Revisar Spring Boot",
                                              "dueDate": "2025-10-25T15:00:00",
                                              "priority": "MEDIUM",
                                              "status": "IN_PROGRESS",
                                              "categoryId": 3,
                                              "goalId": 4,
                                              "taskListId": 5,
                                              "focusMode": true
                                            }
                                            """
                                    )
                            }
                    )
            )
            @RequestBody @Valid TaskUpdateData data
    ) {
        if (!service.existsById(data.id())) {
            throw new ResourceNotFoundException("Tarefa não encontrada");
        }
        var task = service.findById(data.id());
        task.update(data);
        var saved = service.save(task);
        return ResponseEntity.ok(new TaskDataDetails(saved));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Deletar tarefa", description = "Remove uma tarefa do sistema pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Tarefa não encontrada\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<Void> delete(
            @PathVariable
            @Parameter(description = "ID da tarefa a ser deletada", required = true, example = "1")
            Long id
    ) {
        if (!service.existsById(id)) {
            throw new ResourceNotFoundException("Tarefa não encontrada");
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}