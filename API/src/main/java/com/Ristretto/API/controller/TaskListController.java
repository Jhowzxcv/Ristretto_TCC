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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/tasklist")
@Tag(name = "Task List", description = "Gerenciamento das listas de tarefas do usuário")
public class TaskListController {

    @Autowired
    private TaskListService service;

    @PostMapping
    @Transactional
    @Operation(summary = "Criar nova lista de tarefas", description = "Cria uma nova lista de tarefas para o usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lista criada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskListDataDetails.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Dados de entrada inválidos\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<TaskListDataDetails> register(@RequestBody @Valid TaskListRegistrationData data,
                                                        UriComponentsBuilder uriBuilder) {
        var taskList = service.convertToEntity(data);
        service.save(taskList);
        var uri = uriBuilder.path("/tasklist/{id}").buildAndExpand(taskList.getId()).toUri();
        return ResponseEntity.created(uri).body(new TaskListDataDetails(taskList));
    }

    @GetMapping
    @Operation(summary = "Listar listas de tarefas", description = "Retorna uma lista paginada de listas de tarefas de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listas de tarefas recuperadas com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskListDataListing.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Parâmetros inválidos\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<Page<TaskListDataListing>> listing(
            @RequestParam(defaultValue = "0")
            @Parameter(description = "Número da página (>= 0)", example = "0")
            int page,
            @RequestParam(defaultValue = "20")
            @Parameter(description = "Tamanho da página (> 0)", example = "20")
            int size,
            @RequestParam
            @Parameter(description = "ID do usuário para filtrar listas", required = true, example = "1")
            Long userId
    ) {
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        var result = service.findAllByUserId(pageable, userId).map(TaskListDataListing::new);
        return ResponseEntity.ok(result);
    }

    @PutMapping
    @Transactional
    @Operation(summary = "Atualizar lista de tarefas", description = "Atualiza as informações de uma lista existente pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tarefas atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Dados inválidos\" }"))),
            @ApiResponse(responseCode = "404", description = "Lista de tarefas não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Lista não encontrada\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<TaskListDataDetails> update(@RequestBody @Valid TaskListUpdateData data) {
        if (!service.existsById(data.id())) {
            throw new ResourceNotFoundException("Lista não encontrada");
        }
        var taskList = service.findByid(data.id());
        taskList.update(data);
        service.save(taskList);
        return ResponseEntity.ok(new TaskListDataDetails(taskList));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Deletar lista de tarefas", description = "Remove uma lista de tarefas do sistema pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Lista de tarefas deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Lista de tarefas não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Lista não encontrada\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<Void> delete(
            @PathVariable
            @Parameter(description = "ID da lista de tarefas a ser deletada", required = true, example = "1")
            Long id
    ) {
        if (!service.existsById(id)) {
            throw new ResourceNotFoundException("Lista não encontrada");
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}