package com.Ristretto.API.controller;

import com.Ristretto.API.domain.user.*;
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
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "gerenciamento de usuarios")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping
    @Transactional
    @Operation(summary = "Criar registrar usuario", description = "Registra um novo usuario no sistema")
    public ResponseEntity<UserDataDetails> register(
            @RequestBody @Valid UserRegistrationData data,
            UriComponentsBuilder uriBuilder
    ) {
        var user = service.createUser(data);
        if (user == null) {
            throw new IllegalStateException("Erro ao criar usuário"); // isso dispara 500, só para garantir
        }
        service.save(user);
        var uri = uriBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(new UserDataDetails(user));
    }



    @GetMapping("/{id}")
    public ResponseEntity<UserDataDetails> find(
            @PathVariable @NotNull(message = "ID é obrigatório")
            Long id
    ){
        var user = service.findById(id);
        return ResponseEntity.ok(new UserDataDetails(user));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<UserDataDetails> update(
            @RequestBody @Valid UserUpdateData data
    ){
        var user = service.findById(data.id());
        user.update(data);
        service.save(user);
        return ResponseEntity.ok(new UserDataDetails(user));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(
            @PathVariable @NotNull(message = "ID é obrigatório") Long id
    ) {
        if (!service.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
