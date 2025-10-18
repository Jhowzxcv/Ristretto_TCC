package com.Ristretto.API.controller;

import com.Ristretto.API.domain.goal.*;
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
@RequestMapping("/goal")
@Tag(name = "Goal", description = "Gerenciamento de metas registradas pelo usuário")
public class GoalController {

    @Autowired
    private GoalService service;

    @PostMapping
    @Transactional
    @Operation(summary = "Registrar nova meta", description = "Registra uma nova meta para o usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Meta registrada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GoalDataDetails.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Dados de entrada inválidos\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<GoalDataDetails> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para registrar a meta",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GoalRegistrationData.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Apenas obrigatórios",
                                            summary = "Exemplo com campos mínimos necessários",
                                            value = """
                                            {
                                              "title": "Finalizar relatório",
                                              "userId": 1
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Completo",
                                            summary = "Exemplo com todos os campos preenchidos",
                                            value = """
                                            {
                                              "title": "Finalizar relatório",
                                              "description": "Finalizar o relatório financeiro até o fim do mês",
                                              "dueDate": "2025-10-31T18:00:00",
                                              "userId": 1
                                            }
                                            """
                                    )
                            }
                    )
            )
            @RequestBody @Valid
            GoalRegistrationData data,
            UriComponentsBuilder uriBuilder
    ) {
        var goal = service.convertToEntity(data);
        service.save(goal);

        var uri = uriBuilder.path("/goal/{id}")
                .buildAndExpand(goal.getId())
                .toUri();

        return ResponseEntity.created(uri).body(new GoalDataDetails(goal));
    }

    @GetMapping
    @Operation(summary = "Listar metas", description = "Retorna uma lista paginada de metas de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de metas recuperada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GoalDataListing.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Parâmetros inválidos\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<Page<GoalDataListing>> listing(
            @RequestParam(defaultValue = "0")
            @Parameter(description = "Número da página (>= 0)", example = "0")
            int page,
            @RequestParam(defaultValue = "20")
            @Parameter(description = "Tamanho da página (> 0)", example = "20")
            int size,
            @RequestParam
            @Parameter(description = "ID do usuário para filtrar metas", required = true, example = "1")
            Long userId
    ) {
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        var result = service.findByUserId(pageable, userId).map(GoalDataListing::new);
        return ResponseEntity.ok(result);
    }

    @PutMapping
    @Transactional
    @Operation(summary = "Atualizar meta", description = "Atualiza as informações de uma meta existente pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meta atualizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GoalDataDetails.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Dados inválidos\" }"))),
            @ApiResponse(responseCode = "404", description = "Meta não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Meta não encontrada\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<GoalDataDetails> update(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para atualizar a meta",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GoalUpdateData.class),
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
                                              "title": "Finalizar relatório atualizado",
                                              "description": "Nova descrição da meta",
                                              "dueDate": "2025-11-30T18:00:00"
                                            }
                                            """
                                    )
                            }
                    )
            )
            @RequestBody @Valid
            GoalUpdateData data
    ) {
        var goal = service.findById(data.id());
        goal.update(data);
        service.save(goal);
        return ResponseEntity.ok(new GoalDataDetails(goal));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Deletar meta", description = "Remove uma meta do sistema pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Meta deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Meta não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Meta não encontrada\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<Void> delete(
            @PathVariable
            @Parameter(description = "ID da meta a ser deletada", required = true, example = "1")
            Long id
    ) {
        if (!service.existsById(id)) {
            throw new ResourceNotFoundException("Meta não encontrada");
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}