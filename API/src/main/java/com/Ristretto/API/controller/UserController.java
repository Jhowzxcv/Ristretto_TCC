package com.Ristretto.API.controller;

import com.Ristretto.API.domain.user.User;
import com.Ristretto.API.domain.user.UserDataDetails;
import com.Ristretto.API.domain.user.UserRegistrationData;
import com.Ristretto.API.domain.user.UserRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "gerenciamento de usuarios")
public class UserController {

    @Autowired
    private UserRepository repository;

    @PostMapping
    @Transactional
    @Operation(
            summary = "Criar registrar usuario",
            description = "Registra um novo usuario no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDataDetails.class)
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
    public ResponseEntity register(
            @RequestBody @Valid
            @Parameter(description = "Dados para criação do usuario", required = true)
            UserRegistrationData data,
            UriComponentsBuilder uriBuilder
    ) {
        var user = new User(data);
        repository.save(user);

        var uri = uriBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(uri).body(new UserDataDetails(user));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar usuario",
            description = "Busca um usuario do sistema pelo ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "usuario encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDataDetails.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "usuario não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Usuario não encontrado\" }")
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
    public ResponseEntity find(
            @PathVariable
            @Parameter(description = "ID do usuario a ser encontrado", required = true, example = "1")
            Long id
    ){
        var user = repository.getReferenceById(id);
        return ResponseEntity.ok(new UserDataDetails(user));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(
            summary = "Deletar usuário",
            description = "Remove um usuário do sistema pelo ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Usuário não encontrado\" }")
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> delete(
            @PathVariable
            @Parameter(description = "ID do usuário a ser deletado", required = true, example = "1")
            Long id
    ) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}