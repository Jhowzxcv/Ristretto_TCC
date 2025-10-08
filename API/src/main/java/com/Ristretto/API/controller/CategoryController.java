package com.Ristretto.API.controller;

import com.Ristretto.API.domain.category.*;
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
@RequestMapping("/category")
@Tag(name = "Category", description = "Gerenciamento das categorias criadas pelo usuário")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @PostMapping
    @Transactional
    @Operation(summary = "Criar nova categoria", description = "Cria uma nova categoria para o usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDataDetails.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Dados de entrada inválidos\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity register(@RequestBody @Valid CategoryRegistrationData data,
                                   UriComponentsBuilder uriBuilder) {
        var category = service.convertToEntity(data);
        service.save(category);
        var uri = uriBuilder.path("/category/{id}").buildAndExpand(category.getId()).toUri();
        return ResponseEntity.created(uri).body(new CategoryDataDetails(category));
    }

    @GetMapping
    @Operation(summary = "Listar categorias", description = "Retorna uma lista paginada de categorias de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de categorias recuperada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDataListing.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<Page<CategoryDataListing>> listing(
            @PageableDefault Pageable pageable,
            @RequestParam
            @Parameter(description = "ID do usuário para filtrar categorias", required = true, example = "1")
            Long userId
    ) {
        var page = service.findByUserId(pageable, userId).map(CategoryDataListing::new);
        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    @Operation(summary = "Atualizar categoria", description = "Atualiza as informações de uma categoria existente pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Dados inválidos\" }"))),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Categoria não encontrada\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CategoryDataDetails> update(@RequestBody @Valid CategoryUpdateData data) {
        var category = service.findByid(data.id());
        category.update(data);
        service.save(category);
        return ResponseEntity.ok(new CategoryDataDetails(category));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Deletar categoria", description = "Remove uma categoria do sistema pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Categoria não encontrada\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<Void> delete(
            @PathVariable
            @Parameter(description = "ID da categoria a ser deletada", required = true, example = "1")
            Long id
    ) {
        if (!service.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada");
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}