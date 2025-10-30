package com.Ristretto.API.controller;

import com.Ristretto.API.domain.user.*;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "Gerenciamento de usuários do sistema")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping
    @Transactional
    @Operation(summary = "Registrar novo usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDataDetails.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Dados de entrada inválidos\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<UserDataDetails> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do usuário a ser registrado",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRegistrationData.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Apenas obrigatórios",
                                            summary = "Exemplo com campos mínimos necessários",
                                            value = """
                                            {
                                              "name": "Jhonathan Dourado",
                                              "email": "jhonathan@example.com",
                                              "password": "123456"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Completo",
                                            summary = "Exemplo com todos os campos preenchidos",
                                            value = """
                                            {
                                              "name": "Jhonathan Dourado",
                                              "email": "jhonathan@example.com",
                                              "password": "123456",
                                              "theme": "DARK",
                                              "defaultReminderMin": 10,
                                              "avatarUrl": "https://example.com/avatar.jpg"
                                            }
                                            """
                                    )
                            }
                    )
            )
            @RequestBody @Valid UserRegistrationData data,
            UriComponentsBuilder uriBuilder
    ) {
        var user = service.createUser(data);
        service.save(user);
        var uri = uriBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(new UserDataDetails(user));
    }

    @GetMapping("/profile")
    @Operation(summary = "Buscar usuário autenticado", description = "Retorna as informações do usuário autenticado baseado no token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDataDetails.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido ou não fornecido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Token inválido ou não fornecido\" }"))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Usuário não encontrado\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<UserDataDetails> getCurrentUser(
            @Parameter(hidden = true) Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("Usuário não autenticado");
        }

        String email = authentication.getName();
        var user = service.findByEmail(email);

        if (user == null) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        return ResponseEntity.ok(new UserDataDetails(user));
    }

    @PutMapping
    @Transactional
    @Operation(summary = "Atualizar usuário", description = "Atualiza as informações de um usuário existente pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDataDetails.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Dados inválidos\" }"))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Usuário não encontrado\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<UserDataDetails> update(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para atualizar o usuário",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserUpdateData.class),
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
                                              "name": "Jhonathan Dourado Atualizado",
                                              "theme": "LIGHT",
                                              "defaultReminderMin": 15,
                                              "avatarUrl": "https://example.com/new-avatar.jpg"
                                            }
                                            """
                                    )
                            }
                    )
            )
            @RequestBody @Valid UserUpdateData data
    ) {
        if (!service.existsById(data.id())) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
        var user = service.findById(data.id());
        user.update(data);
        service.save(user);
        return ResponseEntity.ok(new UserDataDetails(user));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Usuário não encontrado\" }"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Erro interno do servidor\" }")))
    })
    public ResponseEntity<Void> delete(
            @PathVariable
            @Parameter(description = "ID do usuário a ser deletado", required = true, example = "1")
            Long id
    ) {
        if (!service.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}