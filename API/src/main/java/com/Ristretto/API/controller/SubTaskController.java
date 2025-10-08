package com.Ristretto.API.controller;

import com.Ristretto.API.domain.subTask.*;
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
import jakarta.validation.constraints.NotNull;
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
@Tag(name = "SubTask", description = "gerenciamento de subtarefas do usuario")
public class SubTaskController {

    @Autowired
    private SubTaskService service;

    @PostMapping
    @Transactional
    @Operation(summary = "Criar nova subtarefa", description = "Cria uma nova subtarefa no sistema")
    public ResponseEntity<SubTaskDataDetails> register(
            @RequestBody @Valid SubTaskRegistrationData data,
            UriComponentsBuilder uriBuilder
    ) {
        var subtask = service.convertToEntity(data);
        service.save(subtask);
        var uri = uriBuilder.path("/subtask/{id}").buildAndExpand(subtask.getId()).toUri();
        return ResponseEntity.created(uri).body(new SubTaskDataDetails(subtask));
    }

    @GetMapping
    @Operation(summary = "Listar subtarefas", description = "Retorna uma lista paginada de subtarefas relacionadas a uma Tarefa")
    public ResponseEntity<Page<SubTaskDataListing>> listing(
            @PageableDefault(sort = {"dueDate"}, direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam @NotNull(message = "taskId é obrigatório") Long taskId
    ){
        var page = service.findByTaskId(pageable, taskId).map(SubTaskDataListing::new);
        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    @Operation(summary = "Atualizar subtarefa", description = "Atualiza as informações de uma subtarefa existente pelo ID")
    public ResponseEntity<SubTaskDataDetails> update(
            @RequestBody @Valid SubTaskUpdateData data
    ) {
        var subTask = service.findByid(data.id());
        subTask.update(data);
        service.save(subTask);
        return ResponseEntity.ok(new SubTaskDataDetails(subTask));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Deletar subtarefa", description = "Remove uma subtarefa do sistema pelo ID")
    public ResponseEntity<Void> delete(
            @PathVariable @NotNull(message = "ID é obrigatório") Long id
    ){
        if (!service.existsById(id)) {
            throw new ResourceNotFoundException("Subtarefa não encontrada");
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
