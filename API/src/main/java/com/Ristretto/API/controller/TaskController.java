package com.Ristretto.API.controller;

import com.Ristretto.API.domain.task.*;
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
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity register(@RequestBody @Valid TaskRegistrationData data, UriComponentsBuilder uriBuilder) {
        var task = new Task(data);
        repository.save(task);

        var uri = uriBuilder.path("/task/{id}").buildAndExpand(task.getId()).toUri();

        return ResponseEntity.created(uri).body(new TaskDataDetails(task));
    }

    @GetMapping
    public ResponseEntity<Page<TaskDataListing>> listing(@PageableDefault(size = 10, sort = {"name"}) Pageable pageable){
        var page = repository.findAllByAtivoTrue(pageable).map(TaskDataListing::new);

        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id){
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


