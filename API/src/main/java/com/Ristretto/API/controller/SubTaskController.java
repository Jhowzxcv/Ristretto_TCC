package com.Ristretto.API.controller;

import com.Ristretto.API.domain.subTask.*;
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
@RequestMapping("/subtask")
@Tag(name = "SubTask", description = "Gerenciamento de subtarefas do usuário")
public class SubTaskController {

    @Autowired
    private SubTaskService service;

    @PostMapping
    @Transactional
    @Operation(summary = "Criar nova subtarefa", description = "Cria uma nova subtarefa no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subtarefa criada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubTaskDataDetails.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Dados de entrada inválidos\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<SubTaskDataDetails> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da subtarefa a ser criada",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SubTaskRegistrationData.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Apenas obrigatórios",
                                            summary = "Exemplo com campos mínimos necessários",
                                            value = """
                                            {
                                              "title": "Finalizar relatório",
                                              "taskId": 1
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Completo",
                                            summary = "Exemplo com todos os campos preenchidos",
                                            value = """
                                            {
                                              "title": "Finalizar relatório",
                                              "description": "Incluir todas as métricas do trimestre",
                                              "dueDate": "2025-12-31T23:59:00",
                                              "taskId": 1
                                            }
                                            """
                                    )
                            }
                    )
            )
            @RequestBody @Valid SubTaskRegistrationData data,
            UriComponentsBuilder uriBuilder
    ) {
        var subtask = service.convertToEntity(data);
        service.save(subtask);
        var uri = uriBuilder.path("/subtask/{id}").buildAndExpand(subtask.getId()).toUri();
        return ResponseEntity.created(uri).body(new SubTaskDataDetails(subtask));
    }

    @GetMapping
    @Operation(summary = "Listar subtarefas", description = "Retorna uma lista paginada de subtarefas relacionadas a uma tarefa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de subtarefas recuperada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubTaskDataListing.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Parâmetros inválidos\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<Page<SubTaskDataListing>> listing(
            @RequestParam(defaultValue = "0")
            @Parameter(description = "Número da página (>= 0)", example = "0")
            int page,
            @RequestParam(defaultValue = "20")
            @Parameter(description = "Tamanho da página (> 0)", example = "20")
            int size,
            @RequestParam
            @Parameter(description = "ID da tarefa para filtrar subtarefas", required = true, example = "1")
            Long taskId
    ) {
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        var result = service.findByTaskId(pageable, taskId).map(SubTaskDataListing::new);
        return ResponseEntity.ok(result);
    }

    @PutMapping
    @Transactional
    @Operation(summary = "Atualizar subtarefa", description = "Atualiza as informações de uma subtarefa existente pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subtarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Dados inválidos\" }"))),
            @ApiResponse(responseCode = "404", description = "Subtarefa não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Subtarefa não encontrada\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<SubTaskDataDetails> update(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para atualizar a subtarefa",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SubTaskUpdateData.class),
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
                                              "title": "Finalizar relatório revisado",
                                              "isDone": true,
                                              "description": "Revisar métricas do trimestre",
                                              "dueDate": "2025-12-31T23:59:00"
                                            }
                                            """
                                    )
                            }
                    )
            )
            @RequestBody @Valid SubTaskUpdateData data
    ) {
        var subTask = service.findById(data.id());
        subTask.update(data);
        service.save(subTask);
        return ResponseEntity.ok(new SubTaskDataDetails(subTask));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Deletar subtarefa", description = "Remove uma subtarefa do sistema pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subtarefa deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Subtarefa não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Subtarefa não encontrada\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<Void> delete(
            @PathVariable
            @Parameter(description = "ID da subtarefa a ser deletada", required = true, example = "1")
            Long id
    ) {
        if (!service.existsById(id)) {
            throw new ResourceNotFoundException("Subtarefa não encontrada");
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}