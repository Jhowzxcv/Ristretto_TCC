package com.Ristretto.API.controller;

import com.Ristretto.API.domain.taskList.*;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;



@RestController
@RequestMapping("/tasklists")
@RequiredArgsConstructor
@Tag(name = "TaskList", description = "Gerenciamento de listas de tarefas do usuário")
public class TaskListController {

    private final TaskListService service;

    @GetMapping
    @Operation(
            summary = "Listar todas as listas de tarefas",
            description = "Retorna uma lista paginada de listas de tarefas"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listas de tarefas recuperadas com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskList.class)
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
    public ResponseEntity<Page<TaskList>> listing(Pageable pageable) {
        var page = service.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar lista de tarefas por ID",
            description = "Retorna uma lista de tarefas específica pelo seu ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de tarefas encontrada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskList.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lista de tarefas não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Lista de tarefas não encontrada\" }")
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
    public ResponseEntity<TaskList> details(
            @PathVariable
            @Parameter(description = "ID da lista de tarefas", required = true, example = "1")
            Long id
    ) {
        var taskList = service.findById(id);
        return ResponseEntity.ok(taskList);
    }

    @PostMapping
    @Transactional
    @Operation(
            summary = "Criar nova lista de tarefas",
            description = "Cria uma nova lista de tarefas no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Lista de tarefas criada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskList.class)
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
    public ResponseEntity<TaskList> register(
            @RequestBody @Valid TaskListRegistrationData data,
            UriComponentsBuilder uriBuilder
    ) {
        var taskList = service.convertToEntity(data);
        service.save(taskList);

        var uri = uriBuilder.path("/tasklists/{id}").buildAndExpand(taskList.getId()).toUri();
        return ResponseEntity.created(uri).body(taskList);
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(
            summary = "Atualizar lista de tarefas",
            description = "Atualiza uma lista de tarefas existente pelo ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de tarefas atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskList.class)
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
                    responseCode = "404",
                    description = "Lista de tarefas não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Lista de tarefas não encontrada\" }")
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
    public ResponseEntity update(
            @RequestBody @Valid TaskListUpdateData data
    ) {
        var taskList = service.findById(data.id());
        taskList.update(data);
        service.save(taskList);
        return ResponseEntity.ok(new TaskListDataDetails(taskList));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(
            summary = "Deletar lista de tarefas",
            description = "Remove uma lista de tarefas do sistema pelo ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Lista de tarefas deletada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lista de tarefas não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Lista de tarefas não encontrada\" }")
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
            @Parameter(description = "ID da lista de tarefas a ser deletada", required = true, example = "1")
            Long id
    ) {
        if (!service.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada");
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}